package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;

import net.dwarfs.entity.DwarfEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

public class DwarfMeleeAttackTask extends Task<DwarfEntity> {
    private final int interval;

    public DwarfMeleeAttackTask(int interval) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.ATTACK_COOLING_DOWN,
                MemoryModuleState.VALUE_ABSENT));
        this.interval = interval;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity mobEntity) {
        LivingEntity livingEntity = this.getAttackTarget(mobEntity);
        return !this.isHoldingUsableRangedWeapon(mobEntity) && LookTargetUtil.isVisibleInMemory(mobEntity, livingEntity) && LookTargetUtil.isTargetWithinMeleeRange(mobEntity, livingEntity);
    }

    private boolean isHoldingUsableRangedWeapon(MobEntity entity) {
        return entity.isHolding(stack -> {
            Item item = stack.getItem();
            return item instanceof RangedWeaponItem && entity.canUseRangedWeapon((RangedWeaponItem) item);
        });
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity mobEntity, long l) {
        LivingEntity livingEntity = this.getAttackTarget(mobEntity);
        LookTargetUtil.lookAt(mobEntity, livingEntity);
        mobEntity.swingHand(Hand.MAIN_HAND);
        mobEntity.tryAttack(livingEntity);
        System.out.println("JONGE");
        mobEntity.getBrain().remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, this.interval);
    }

    private LivingEntity getAttackTarget(MobEntity entity) {
        return entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
