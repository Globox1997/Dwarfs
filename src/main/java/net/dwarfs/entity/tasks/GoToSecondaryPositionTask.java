package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;

import net.dwarfs.entity.DwarfEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import org.jetbrains.annotations.Nullable;

public class GoToSecondaryPositionTask extends Task<DwarfEntity> {
    private final MemoryModuleType<List<GlobalPos>> secondaryPositions;
    private final MemoryModuleType<GlobalPos> primaryPosition;
    private final float speed;
    private final int completionRange;
    private final int primaryPositionActivationDistance;
    private long nextRunTime;
    @Nullable
    private GlobalPos chosenPosition;

    public GoToSecondaryPositionTask(MemoryModuleType<List<GlobalPos>> secondaryPositions, float speed, int completionRange, int primaryPositionActivationDistance,
            MemoryModuleType<GlobalPos> primaryPosition) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, secondaryPositions, MemoryModuleState.VALUE_PRESENT, primaryPosition, MemoryModuleState.VALUE_PRESENT));
        this.secondaryPositions = secondaryPositions;
        this.speed = speed;
        this.completionRange = completionRange;
        this.primaryPositionActivationDistance = primaryPositionActivationDistance;
        this.primaryPosition = primaryPosition;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        List<GlobalPos> list;
        Optional<List<GlobalPos>> optional = dwarfEntity.getBrain().getOptionalMemory(this.secondaryPositions);
        Optional<GlobalPos> optional2 = dwarfEntity.getBrain().getOptionalMemory(this.primaryPosition);
        if (optional.isPresent() && optional2.isPresent() && !(list = optional.get()).isEmpty()) {
            this.chosenPosition = list.get(serverWorld.getRandom().nextInt(list.size()));
            return this.chosenPosition != null && serverWorld.getRegistryKey() == this.chosenPosition.getDimension()
                    && optional2.get().getPos().isWithinDistance(dwarfEntity.getPos(), (double) this.primaryPositionActivationDistance);
        }
        return false;
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        if (l > this.nextRunTime && this.chosenPosition != null) {
            dwarfEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(this.chosenPosition.getPos(), this.speed, this.completionRange));
            this.nextRunTime = l + 100L;
        }
    }
}
