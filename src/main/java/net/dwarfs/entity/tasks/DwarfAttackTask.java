package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;

import net.dwarfs.entity.DwarfEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

public class DwarfAttackTask extends Task<DwarfEntity> {
    private final int distance;
    private final float forwardMovement;

    public DwarfAttackTask(int distance, float forwardMovement) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET,
                MemoryModuleState.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT));
        this.distance = distance;
        this.forwardMovement = forwardMovement;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        return this.isAttackTargetVisible(dwarfEntity) && this.isNearAttackTarget(dwarfEntity);
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        // ((LivingEntity) mobEntity).getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(this.getAttackTarget(mobEntity), true));
        // ((MobEntity) mobEntity).getMoveControl().strafeTo(-this.forwardMovement, 0.0f);
        // ((Entity) mobEntity).setYaw(MathHelper.clampAngle(((Entity) mobEntity).getYaw(), ((MobEntity) mobEntity).headYaw, 0.0f));

        // if (PanicTask.wasHurt(villagerEntity) || PanicTask.isHostileNearby(villagerEntity)) {
        Brain<DwarfEntity> brain = dwarfEntity.getBrain();
        if (!brain.hasActivity(Activity.FIGHT)) {
            brain.forget(MemoryModuleType.PATH);
            brain.forget(MemoryModuleType.WALK_TARGET);
            brain.forget(MemoryModuleType.LOOK_TARGET);
            brain.forget(MemoryModuleType.BREED_TARGET);
            brain.forget(MemoryModuleType.INTERACTION_TARGET);
        }
        System.out.println("ATTACK JONGE");
        brain.doExclusively(Activity.FIGHT);
    }

    private boolean isAttackTargetVisible(DwarfEntity entity) {
        return ((LivingEntity) entity).getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get().contains(this.getAttackTarget(entity));
    }

    private boolean isNearAttackTarget(DwarfEntity entity) {
        return this.getAttackTarget(entity).isInRange((Entity) entity, this.distance);
    }

    private LivingEntity getAttackTarget(DwarfEntity entity) {
        return ((LivingEntity) entity).getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
