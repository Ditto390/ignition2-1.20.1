package net.ditto.client;

import net.ditto.Ignition;
import net.ditto.ability.IgnitionAbility;
import net.ditto.impl.PlayerAbilityManager;
import net.ditto.registry.IgnitionRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.util.Identifier;

public class IgnitionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 1. Register Keybinds
        IgnitionInputHandler.register();

        // 2. Register HUD Overlay
        HudRenderCallback.EVENT.register(new IgnitionHud());

        // 3. Register Sync Packet Receiver
        // This listens for when the server says "Hey, this player changed their ability!"
        ClientPlayNetworking.registerGlobalReceiver(Ignition.PACKET_SYNC_IGNITION, (client, handler, buf, responseSender) -> {
            Identifier id = buf.readIdentifier();

            client.execute(() -> {
                if (client.player instanceof PlayerAbilityManager manager) {
                    IgnitionAbility ability = IgnitionRegistry.get(id);
                    // Update the client-side player without sending another packet
                    manager.setIgnitionAbility(ability);
                }
            });
        });
    }
}