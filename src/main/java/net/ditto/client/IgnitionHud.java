package net.ditto.client;

import net.ditto.ability.Ability;
import net.ditto.ability.IgnitionAbility;
import net.ditto.impl.PlayerAbilityManager;
import net.ditto.registry.IgnitionRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class IgnitionHud implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || !(player instanceof PlayerAbilityManager manager)) return;

        IgnitionAbility activeIgnition = manager.getIgnitionAbility();
        if (activeIgnition == null) return;

        List<Ability> abilities = manager.getActiveAbilities();
        Ability selected = manager.getSelectedAbility();

        TextRenderer textRenderer = client.textRenderer;
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // --- HUD Configuration ---
        int xOffset = width - 100; // 100 pixels from right
        int yOffset = height - 40; // 40 pixels from bottom
        int lineHeight = 12;

        // --- Draw Ignition Title (Header) ---
        Identifier ignitionId = IgnitionRegistry.getId(activeIgnition);
        String header = ignitionId != null ? formatName(ignitionId.getPath()) : "Unknown Style";

        // Draw a semi-transparent background box
        context.fill(xOffset - 10, yOffset - (abilities.size() * lineHeight) - 15, width, height - 10, 0x90000000);

        // Draw Header Text with Shadow
        context.drawTextWithShadow(textRenderer, header, xOffset, yOffset - (abilities.size() * lineHeight) - 10, 0xFFAA00); // Gold color

        // --- Draw Ability List ---
        for (int i = 0; i < abilities.size(); i++) {
            Ability ability = abilities.get(i);
            boolean isSelected = (ability == selected);

            String abilityName = formatName(ability.getId().getPath());
            int yPos = yOffset - ((abilities.size() - 1 - i) * lineHeight);

            // Draw selection indicator
            if (isSelected) {
                context.drawTextWithShadow(textRenderer, "> " + abilityName, xOffset - 8, yPos, 0xFF5555); // Red for selected
            } else {
                context.drawTextWithShadow(textRenderer, abilityName, xOffset, yPos, 0xFFFFFF); // White for unselected
            }
        }
    }

    // Helper to turn "flash_step" into "Flash Step"
    private String formatName(String input) {
        String[] words = input.split("_");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (builder.length() > 0) builder.append(" ");
            builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return builder.toString();
    }
}
