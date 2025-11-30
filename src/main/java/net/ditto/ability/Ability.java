package net.ditto.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public interface Ability {
    Identifier getId();

    // Called when the key (Z) is pressed
    void onActivate(PlayerEntity player);

    // For Charge abilities: called every tick while Z is held
    default void onHold(PlayerEntity player, int ticksHeld) {}

    // For Charge abilities: called when Z is released
    default void onRelease(PlayerEntity player, int ticksHeld) {}
}
