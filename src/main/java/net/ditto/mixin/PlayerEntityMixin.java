package net.ditto.mixin;

import net.ditto.Ignition;
import net.ditto.ability.Ability;
import net.ditto.ability.IgnitionAbility;
import net.ditto.impl.PlayerAbilityManager;
import net.ditto.registry.IgnitionRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerAbilityManager {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique private final List<Ability> activeAbilities = new ArrayList<>();
    @Unique private int selectedAbilityIndex = 0;
    @Unique private IgnitionAbility activeIgnition;

    @Override
    public void setIgnitionAbility(IgnitionAbility newIgnition) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        // 1. Cleanup Old
        if (this.activeIgnition != null) {
            this.activeIgnition.onUnequip(self);
        }

        // 2. Reset State
        this.activeAbilities.clear();
        this.selectedAbilityIndex = 0;

        // 3. Set New
        this.activeIgnition = newIgnition;

        // 4. Initialize New
        if (this.activeIgnition != null) {
            this.activeIgnition.onEquip(self);
            List<Ability> newSet = this.activeIgnition.getAbilitySet();
            if (newSet != null) {
                this.activeAbilities.addAll(newSet);
            }
        }

        // --- SYNC TO CLIENT (The Fix) ---
        if (!self.getWorld().isClient && self instanceof ServerPlayerEntity serverPlayer) {
            // Find the ID of the new ability
            Identifier id = IgnitionRegistry.getId(newIgnition);
            if (id != null) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeIdentifier(id);
                ServerPlayNetworking.send(serverPlayer, Ignition.PACKET_SYNC_IGNITION, buf);
            }
        }
    }

    @Override
    public IgnitionAbility getIgnitionAbility() { return this.activeIgnition; }

    @Override
    public List<Ability> getActiveAbilities() { return this.activeAbilities; }

    @Override
    public Ability getSelectedAbility() {
        if (activeAbilities.isEmpty()) return null;
        if (selectedAbilityIndex >= activeAbilities.size()) selectedAbilityIndex = 0;
        return activeAbilities.get(selectedAbilityIndex);
    }

    @Override
    public void cycleAbility(int direction) {
        if (activeAbilities.isEmpty()) return;
        selectedAbilityIndex += direction;
        if (selectedAbilityIndex < 0) selectedAbilityIndex = activeAbilities.size() - 1;
        else if (selectedAbilityIndex >= activeAbilities.size()) selectedAbilityIndex = 0;

        // Optional: Send ActionBar message
        Ability current = getSelectedAbility();
        if (current != null) {
            ((PlayerEntity)(Object)this).sendMessage(Text.literal(current.getId().getPath()), true);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickAbilities(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (activeIgnition != null) activeIgnition.onUpdate(self);
    }

    // NBT Read/Write stubs...
    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    public void writeIgnitionData(NbtCompound nbt, CallbackInfo ci) {}

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void readIgnitionData(NbtCompound nbt, CallbackInfo ci) {}
}