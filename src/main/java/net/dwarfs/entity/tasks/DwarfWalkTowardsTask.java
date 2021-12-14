package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

import net.dwarfs.entity.DwarfEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class DwarfWalkTowardsTask extends Task<DwarfEntity> {
    private final MemoryModuleType<GlobalPos> destination;
    private final float speed;
    private final int completionRange;
    private final int maxRange;
    private final int maxRunTime;

    public DwarfWalkTowardsTask(MemoryModuleType<GlobalPos> destination, float speed, int completionRange, int maxRange, int maxRunTime) {
        super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleState.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, destination,
                MemoryModuleState.VALUE_PRESENT));
        this.destination = destination;
        this.speed = speed;
        this.completionRange = completionRange;
        this.maxRange = maxRange;
        this.maxRunTime = maxRunTime;
    }

    private void giveUp(DwarfEntity dwarfEntity, long time) {
        Brain<DwarfEntity> brain = dwarfEntity.getBrain();
        dwarfEntity.releaseTicketFor(this.destination);
        brain.forget(this.destination);
        brain.remember(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, time);
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        Brain<DwarfEntity> brain = dwarfEntity.getBrain();
        brain.getOptionalMemory(this.destination).ifPresent(pos -> {
            if (this.dimensionMismatches(serverWorld, (GlobalPos) pos) || this.shouldGiveUp(serverWorld, dwarfEntity)) {
                this.giveUp(dwarfEntity, l);
            } else if (this.exceedsMaxRange(dwarfEntity, (GlobalPos) pos)) {
                int i;
                Vec3d vec3d = null;
                int j = 1000;
                for (i = 0; i < 1000 && (vec3d == null || this.exceedsMaxRange(dwarfEntity, GlobalPos.create(serverWorld.getRegistryKey(), new BlockPos(vec3d)))); ++i) {
                    vec3d = NoPenaltyTargeting.findTo(dwarfEntity, 15, 7, Vec3d.ofBottomCenter(pos.getPos()), 1.5707963705062866);
                }
                if (i == 1000) {
                    this.giveUp(dwarfEntity, l);
                    return;
                }
                brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.speed, this.completionRange));
            } else if (!this.reachedDestination(serverWorld, dwarfEntity, (GlobalPos) pos)) {
                brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(pos.getPos(), this.speed, this.completionRange));
            }
        });
    }

    private boolean shouldGiveUp(ServerWorld world, DwarfEntity dwarfEntity) {
        Optional<Long> optional = dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        if (optional.isPresent()) {
            return world.getTime() - optional.get() > (long) this.maxRunTime;
        }
        return false;
    }

    private boolean exceedsMaxRange(DwarfEntity dwarfEntity, GlobalPos pos) {
        return pos.getPos().getManhattanDistance(dwarfEntity.getBlockPos()) > this.maxRange;
    }

    private boolean dimensionMismatches(ServerWorld world, GlobalPos pos) {
        return pos.getDimension() != world.getRegistryKey();
    }

    private boolean reachedDestination(ServerWorld world, DwarfEntity dwarfEntity, GlobalPos pos) {
        return pos.getDimension() == world.getRegistryKey() && pos.getPos().getManhattanDistance(dwarfEntity.getBlockPos()) <= this.completionRange;
    }
}
