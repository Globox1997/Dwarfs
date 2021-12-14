package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

import net.dwarfs.entity.DwarfEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;

public class WalkTowardJobSiteTask extends Task<DwarfEntity> {
    private static final int RUN_TIME = 1200;
    final float speed;

    public WalkTowardJobSiteTask(float speed) {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleState.VALUE_PRESENT), 1200);
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        return dwarfEntity.getBrain().getFirstPossibleNonCoreActivity().map(activity -> activity == Activity.IDLE || activity == Activity.WORK || activity == Activity.PLAY).orElse(true);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        return dwarfEntity.getBrain().hasMemoryModule(MemoryModuleType.POTENTIAL_JOB_SITE);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        LookTargetUtil.walkTowards((LivingEntity) dwarfEntity, dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos(), this.speed, 1);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        Optional<GlobalPos> optional = dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
        optional.ifPresent(globalPos -> {
            BlockPos blockPos = globalPos.getPos();
            ServerWorld serverWorld2 = serverWorld.getServer().getWorld(globalPos.getDimension());
            if (serverWorld2 == null) {
                return;
            }
            PointOfInterestStorage pointOfInterestStorage = serverWorld2.getPointOfInterestStorage();
            if (pointOfInterestStorage.test(blockPos, pointOfInterestType -> true)) {
                pointOfInterestStorage.releaseTicket(blockPos);
            }
            DebugInfoSender.sendPointOfInterest(serverWorld, blockPos);
        });
        dwarfEntity.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
    }

    // @Override
    // protected boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
    // return this.shouldKeepRunning(world, (DwarfEntity) entity, time);
    // }

    // @Override
    // protected void keepRunning(ServerWorld world, LivingEntity entity, long time) {
    // this.keepRunning(world, (DwarfEntity) entity, time);
    // }
}
