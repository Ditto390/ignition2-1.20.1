package net.ditto.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ModularProjectileEntity extends ProjectileEntity {

    private ProjectileBehavior behavior;

    public ModularProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setBehavior(ProjectileBehavior behavior) {
        this.behavior = behavior;
    }

    @Override
    public void tick() {
        super.tick();

        // Custom Movement Logic
        if (behavior != null) behavior.onTick(this);

        // Basic Collision Logic (Simplified)
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);
        }

        this.checkBlockCollision();
        this.setPosition(this.getX() + this.getVelocity().x, this.getY() + this.getVelocity().y, this.getZ() + this.getVelocity().z);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (behavior != null) behavior.onHitEntity(this, entityHitResult);
    }

    @Override
    protected void initDataTracker() {} // Add sync data here if needed

    // --- The Modular Interface ---
    public interface ProjectileBehavior {
        void onTick(ModularProjectileEntity projectile);
        void onHitEntity(ModularProjectileEntity projectile, EntityHitResult hit);
        // Add onHitBlock, etc.
    }
}
