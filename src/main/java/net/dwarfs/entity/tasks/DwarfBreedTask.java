package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

import net.dwarfs.entity.DwarfEntity;
import net.dwarfs.init.EntityInit;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestType;

public class DwarfBreedTask extends Task<DwarfEntity> {
    private static final int MAX_DISTANCE = 5;
    private static final float APPROACH_SPEED = 0.5f;
    private long breedEndTime;

    public DwarfBreedTask() {
        super(ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT), 350, 350);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        return this.isReadyToBreed(dwarfEntity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        return l <= this.breedEndTime && this.isReadyToBreed(dwarfEntity);
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        PassiveEntity passiveEntity = dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.BREED_TARGET).get();
        LookTargetUtil.lookAtAndWalkTowardsEachOther(dwarfEntity, passiveEntity, 0.5f);
        serverWorld.sendEntityStatus(passiveEntity, (byte) 18);
        serverWorld.sendEntityStatus(dwarfEntity, (byte) 18);
        int i = 275 + dwarfEntity.getRandom().nextInt(50);
        this.breedEndTime = l + (long) i;
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        DwarfEntity dwarfEntity2 = (DwarfEntity) dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.BREED_TARGET).get();
        if (dwarfEntity.squaredDistanceTo(dwarfEntity2) > 5.0) {
            return;
        }
        LookTargetUtil.lookAtAndWalkTowardsEachOther(dwarfEntity, dwarfEntity2, 0.5f);
        if (l >= this.breedEndTime) {
            dwarfEntity.eatForBreeding();
            dwarfEntity2.eatForBreeding();
            this.goHome(serverWorld, dwarfEntity, dwarfEntity2);
        } else if (dwarfEntity.getRandom().nextInt(35) == 0) {
            serverWorld.sendEntityStatus(dwarfEntity2, (byte) 12);
            serverWorld.sendEntityStatus(dwarfEntity, (byte) 12);
        }
    }

    private void goHome(ServerWorld world, DwarfEntity first, DwarfEntity second) {
        Optional<BlockPos> optional = this.getReachableHome(world, first);
        if (!optional.isPresent()) {
            world.sendEntityStatus(second, (byte) 13);
            world.sendEntityStatus(first, (byte) 13);
        } else {
            Optional<DwarfEntity> optional2 = this.createChild(world, first, second);
            if (optional2.isPresent()) {
                this.setChildHome(world, optional2.get(), optional.get());
            } else {
                world.getPointOfInterestStorage().releaseTicket(optional.get());
                DebugInfoSender.sendPointOfInterest(world, optional.get());
            }
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        dwarfEntity.getBrain().forget(MemoryModuleType.BREED_TARGET);
    }

    private boolean isReadyToBreed(DwarfEntity dwarfEntity) {
        Brain<DwarfEntity> brain = dwarfEntity.getBrain();
        Optional<PassiveEntity> optional = brain.getOptionalMemory(MemoryModuleType.BREED_TARGET).filter(passiveEntity -> passiveEntity.getType() == EntityInit.DWARF);
        if (!optional.isPresent()) {
            return false;
        }
        return LookTargetUtil.canSee(brain, MemoryModuleType.BREED_TARGET, EntityInit.DWARF) && dwarfEntity.isReadyToBreed() && optional.get().isReadyToBreed();
    }

    private Optional<BlockPos> getReachableHome(ServerWorld world, DwarfEntity dwarfEntity) {
        return world.getPointOfInterestStorage().getPosition(PointOfInterestType.HOME.getCompletionCondition(), blockPos -> this.canReachHome(dwarfEntity, (BlockPos) blockPos),
                dwarfEntity.getBlockPos(), 48);
    }

    private boolean canReachHome(DwarfEntity dwarfEntity, BlockPos pos) {
        Path path = dwarfEntity.getNavigation().findPathTo(pos, PointOfInterestType.HOME.getSearchDistance());
        return path != null && path.reachesTarget();
    }

    private Optional<DwarfEntity> createChild(ServerWorld world, DwarfEntity parent, DwarfEntity partner) {
        DwarfEntity dwarfEntity = parent.createChild(world, partner);
        if (dwarfEntity == null) {
            return Optional.empty();
        }
        parent.setBreedingAge(6000);
        partner.setBreedingAge(6000);
        dwarfEntity.setBreedingAge(-24000);
        dwarfEntity.refreshPositionAndAngles(parent.getX(), parent.getY(), parent.getZ(), 0.0f, 0.0f);
        world.spawnEntityAndPassengers(dwarfEntity);
        world.sendEntityStatus(dwarfEntity, (byte) 12);
        return Optional.of(dwarfEntity);
    }

    private void setChildHome(ServerWorld world, DwarfEntity child, BlockPos pos) {
        GlobalPos globalPos = GlobalPos.create(world.getRegistryKey(), pos);
        child.getBrain().remember(MemoryModuleType.HOME, globalPos);
    }

}
