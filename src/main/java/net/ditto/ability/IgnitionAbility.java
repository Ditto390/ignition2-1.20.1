package net.ditto.ability;

import net.minecraft.entity.player.PlayerEntity;
import java.util.List;

public interface IgnitionAbility {
    /**
     * @return The list of specific abilities (Z Key actions) this Ignition provides.
     */
    List<Ability> getAbilitySet();

    /**
     * Called when this Ignition is applied to the player.
     * Use this for permanent stat boosts or initial effects.
     */
    void onEquip(PlayerEntity player);

    /**
     * Called when this Ignition is removed/replaced.
     * Clean up stat boosts here.
     */
    void onUnequip(PlayerEntity player);

    /**
     * Called every tick while this Ignition is active.
     */
    void onUpdate(PlayerEntity player);
}
