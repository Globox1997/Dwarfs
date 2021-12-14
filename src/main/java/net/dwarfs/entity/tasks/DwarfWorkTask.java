package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

import net.dwarfs.entity.DwarfEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;

public class DwarfWorkTask extends Task<DwarfEntity> {
    private static final int RUN_TIME = 300;
    private static final double MAX_DISTANCE = 1.73;
    private long lastCheckedTime;

    public DwarfWorkTask() {
        super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        if (serverWorld.getTime() - this.lastCheckedTime < 300L) {
            return false;
        }
        if (serverWorld.random.nextInt(2) != 0) {
            return false;
        }
        this.lastCheckedTime = serverWorld.getTime();
        GlobalPos globalPos = dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
        return globalPos.getDimension() == serverWorld.getRegistryKey() && globalPos.getPos().isWithinDistance(dwarfEntity.getPos(), 1.73);
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        Brain<DwarfEntity> brain = dwarfEntity.getBrain();
        brain.remember(MemoryModuleType.LAST_WORKED_AT_POI, l);
        brain.getOptionalMemory(MemoryModuleType.JOB_SITE).ifPresent(globalPos -> brain.remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(globalPos.getPos())));
        dwarfEntity.playWorkSound();
        this.performAdditionalWork(serverWorld, dwarfEntity);
        if (dwarfEntity.shouldRestock()) {
            dwarfEntity.restock();
        }
    }

    protected void performAdditionalWork(ServerWorld world, DwarfEntity entity) {
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        Optional<GlobalPos> optional = dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
        if (!optional.isPresent()) {
            return false;
        }
        GlobalPos globalPos = optional.get();
        return globalPos.getDimension() == serverWorld.getRegistryKey() && globalPos.getPos().isWithinDistance(dwarfEntity.getPos(), 1.73);
    }

}
