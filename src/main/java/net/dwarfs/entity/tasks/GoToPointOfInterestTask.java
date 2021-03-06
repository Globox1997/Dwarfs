package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;

import net.dwarfs.entity.DwarfEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestStorage;

public class GoToPointOfInterestTask extends Task<DwarfEntity> {
    private final float speed;
    private final int completionRange;

    public GoToPointOfInterestTask(float speed, int completionRange) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
        this.speed = speed;
        this.completionRange = completionRange;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        return !serverWorld.isNearOccupiedPointOfInterest(dwarfEntity.getBlockPos());
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
        int i = pointOfInterestStorage.getDistanceFromNearestOccupied(ChunkSectionPos.from(dwarfEntity.getBlockPos()));
        Vec3d vec3d = null;
        for (int j = 0; j < 5; ++j) {
            Vec3d vec3d2 = FuzzyTargeting.find(dwarfEntity, 15, 7, blockPos -> -pointOfInterestStorage.getDistanceFromNearestOccupied(ChunkSectionPos.from(blockPos)));
            if (vec3d2 == null)
                continue;
            int k = pointOfInterestStorage.getDistanceFromNearestOccupied(ChunkSectionPos.from(new BlockPos(vec3d2)));
            if (k < i) {
                vec3d = vec3d2;
                break;
            }
            if (k != i)
                continue;
            vec3d = vec3d2;
        }
        if (vec3d != null) {
            dwarfEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.speed, this.completionRange));
        }
    }
}
