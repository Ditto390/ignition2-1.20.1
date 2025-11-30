package net.ditto.mixin.client;

import net.ditto.client.IgnitionInputHandler;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"), cancellable = true)
    private void interceptScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        // Check if our Modifier Key (R) is currently pressed
        if (IgnitionInputHandler.handleScroll(vertical)) {
            // If true, we handled the event, so stop the hotbar from scrolling
            ci.cancel();
        }
    }
}
