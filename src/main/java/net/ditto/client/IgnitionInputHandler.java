package net.ditto.client;

import net.ditto.Ignition;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

public class IgnitionInputHandler {

    public static KeyBinding KEY_CYCLE_MODIFIER;
    public static KeyBinding KEY_ACTIVATE_ABILITY;

    public static void register() {
        KEY_CYCLE_MODIFIER = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ignition.cycle_modifier", // Translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R, // Default key 'R'
                "category.ignition" // Category
        ));

        KEY_ACTIVATE_ABILITY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ignition.activate",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z, // Default key 'Z'
                "category.ignition"
        ));
    }

    /**
     * Called by MouseMixin.
     * @return true if we processed the scroll (cancels vanilla behavior)
     */
    public static boolean handleScroll(double verticalAmount) {
        if (KEY_CYCLE_MODIFIER.isPressed() && verticalAmount != 0) {
            // Send packet to server
            // verticalAmount is usually 1.0 (up) or -1.0 (down)
            // We flip it so scrolling UP cycles UP the list (index - 1)
            int direction = verticalAmount > 0 ? -1 : 1;

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(direction);
            ClientPlayNetworking.send(Ignition.PACKET_CYCLE_ABILITY, buf);

            return true; // We handled it
        }
        return false;
    }
}
