package net.ditto.content;

import net.ditto.ability.Ability;
import net.ditto.ability.IgnitionAbility;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class DevilsFootIgnition implements IgnitionAbility {

    private final List<Ability> abilities = new ArrayList<>();

    public DevilsFootIgnition() {
        abilities.add(new FlashStepAbility());
        abilities.add(new InfernoKickAbility());
    }

    @Override
    public List<Ability> getAbilitySet() {
        return abilities;
    }

    @Override
    public void onEquip(PlayerEntity player) {
        // Passive: Speed Boost and Jump Boost while in this mode
        player.sendMessage(Text.literal("Ignition Style: Devil's Foot").formatted(Formatting.RED, Formatting.BOLD), true);
        player.playSound(SoundEvents.ITEM_FIRECHARGE_USE, 1.0f, 0.5f);
    }

    @Override
    public void onUnequip(PlayerEntity player) {
        player.sendMessage(Text.literal("Flames Extinguished...").formatted(Formatting.GRAY), true);
    }

    @Override
    public void onUpdate(PlayerEntity player) {
        // Constant particle effect at feet
        if (player.getWorld().getTime() % 5 == 0) {
            player.getWorld().addParticle(ParticleTypes.FLAME, player.getX(), player.getY(), player.getZ(), 0, 0.05, 0);
        }
    }

    // --- Sub-Abilities ---

    private static class FlashStepAbility implements Ability {
        @Override
        public Identifier getId() {
            return new Identifier("ignition", "flash_step");
        }

        @Override
        public void onActivate(PlayerEntity player) {
            Vec3d look = player.getRotationVector();
            player.addVelocity(look.x * 1.5, 0.2, look.z * 1.5);
            player.velocityModified = true;
            player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0f, 2.0f);

            // Add particles along the path
            for(int i=0; i<5; i++) {
                player.getWorld().addParticle(ParticleTypes.SMOKE, player.getX(), player.getY() + i*0.2, player.getZ(), 0, 0, 0);
            }
        }
    }

    private static class InfernoKickAbility implements Ability {
        @Override
        public Identifier getId() {
            return new Identifier("ignition", "inferno_kick");
        }

        @Override
        public void onActivate(PlayerEntity player) {
            // Instant activation logic (if not charging)
        }

        @Override
        public void onHold(PlayerEntity player, int ticksHeld) {
            if (ticksHeld % 5 == 0) {
                player.getWorld().addParticle(ParticleTypes.FLAME, player.getX(), player.getY() + 0.5, player.getZ(), 0.1, 0.1, 0.1);
            }
        }

        @Override
        public void onRelease(PlayerEntity player, int ticksHeld) {
            float power = Math.min(ticksHeld / 20f, 3.0f); // Max power at 3 seconds

            Vec3d look = player.getRotationVector();
            // Launch player forward
            player.addVelocity(look.x * power, 0.5, look.z * power);
            player.velocityModified = true;

            // Sound and Visuals
            player.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 0.5f, 1.2f);
            player.getWorld().addParticle(ParticleTypes.EXPLOSION, player.getX(), player.getY(), player.getZ(), 0, 0, 0);
        }
    }
}
