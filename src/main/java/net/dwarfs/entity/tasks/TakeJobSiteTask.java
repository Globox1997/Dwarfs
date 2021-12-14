package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

import net.dwarfs.entity.DwarfEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class TakeJobSiteTask extends Task<DwarfEntity> {
    private final float speed;

    public TakeJobSiteTask(float speed) {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.MOBS,
                MemoryModuleState.VALUE_PRESENT));
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        if (dwarfEntity.isBaby()) {
            return false;
        }
        return dwarfEntity.getVillagerData().getProfession() == VillagerProfession.NONE;
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity3, long l) {
        BlockPos blockPos = dwarfEntity3.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos();
        Optional<PointOfInterestType> optional = serverWorld.getPointOfInterestStorage().getType(blockPos);
        if (!optional.isPresent()) {
            return;
        }
        DwarfTaskListProvider.streamSeenDwarfs(dwarfEntity3, dwarfEntity -> this.canUseJobSite((PointOfInterestType) optional.get(), (DwarfEntity) dwarfEntity, blockPos)).findFirst().ifPresent(
                dwarfEntity2 -> this.claimSite(serverWorld, dwarfEntity3, (DwarfEntity) dwarfEntity2, blockPos, dwarfEntity2.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent()));
    }

    private boolean canUseJobSite(PointOfInterestType poiType, DwarfEntity dwarfEntity, BlockPos pos) {
        boolean bl = dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();
        if (bl) {
            return false;
        }
        Optional<GlobalPos> optional = dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
        VillagerProfession villagerProfession = dwarfEntity.getVillagerData().getProfession();
        if (dwarfEntity.getVillagerData().getProfession() != VillagerProfession.NONE && villagerProfession.getWorkStation().getCompletionCondition().test(poiType)) {
            if (!optional.isPresent()) {
                return this.canReachJobSite(dwarfEntity, pos, poiType);
            }
            return optional.get().getPos().equals(pos);
        }
        return false;
    }

    private void claimSite(ServerWorld world, DwarfEntity previousOwner, DwarfEntity newOwner, BlockPos pos, boolean jobSitePresent) {
        this.forgetJobSiteAndWalkTarget(previousOwner);
        if (!jobSitePresent) {
            LookTargetUtil.walkTowards((LivingEntity) newOwner, pos, this.speed, 1);
            newOwner.getBrain().remember(MemoryModuleType.POTENTIAL_JOB_SITE, GlobalPos.create(world.getRegistryKey(), pos));
            DebugInfoSender.sendPointOfInterest(world, pos);
        }
    }

    private boolean canReachJobSite(DwarfEntity dwarfEntity, BlockPos pos, PointOfInterestType poiType) {
        Path path = dwarfEntity.getNavigation().findPathTo(pos, poiType.getSearchDistance());
        return path != null && path.reachesTarget();
    }

    private void forgetJobSiteAndWalkTarget(DwarfEntity dwarfEntity) {
        dwarfEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        dwarfEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        dwarfEntity.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
    }
}
