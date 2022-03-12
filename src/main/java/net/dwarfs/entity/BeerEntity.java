package net.dwarfs.entity;

import net.dwarfs.init.EntityInit;
import net.dwarfs.init.ItemInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class BeerEntity extends ThrownItemEntity {
    public BeerEntity(EntityType<? extends BeerEntity> entityType, World world) {
        super((EntityType<? extends ThrownItemEntity>) entityType, world);
    }

    public BeerEntity(World world, LivingEntity owner) {
        super((EntityType<? extends ThrownItemEntity>) EntityInit.BEER_ENTITY, owner, world);
    }

    public BeerEntity(World world, double x, double y, double z) {
        super((EntityType<? extends ThrownItemEntity>) EntityInit.BEER_ENTITY, x, y, z, world);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 3) {
            for (int i = 0; i < 8; ++i) {
                this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()), this.getX(), this.getY(), this.getZ(), ((double) this.random.nextFloat() - 0.5) * 0.08,
                        ((double) this.random.nextFloat() - 0.5) * 0.08, ((double) this.random.nextFloat() - 0.5) * 0.08);
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().damage(DamageSource.thrownProjectile(this, this.getOwner()), 3.0f);
        if (entityHitResult.getEntity() instanceof LivingEntity)
            ((LivingEntity) entityHitResult.getEntity()).addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 2));
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient) {
            if (this.random.nextInt(8) == 0) {
                int i = 1;
                if (this.random.nextInt(32) == 0) {
                    i = 4;
                }
                // for (int j = 0; j < i; ++j) {
                // ChickenEntity chickenEntity = EntityType.CHICKEN.create(this.world);
                // chickenEntity.setBreedingAge(-24000);
                // chickenEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0f);
                // this.world.spawnEntity(chickenEntity);
                // }
            }
            this.world.sendEntityStatus(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ItemInit.BEER_ITEM;
    }
}
