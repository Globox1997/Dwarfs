package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;

import net.dwarfs.entity.DwarfEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class WorkStationCompetitionTask extends Task<DwarfEntity> {
    final VillagerProfession profession;

    public WorkStationCompetitionTask(VillagerProfession profession) {
        super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.MOBS, MemoryModuleState.VALUE_PRESENT));
        this.profession = profession;
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        GlobalPos globalPos = dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
        serverWorld.getPointOfInterestStorage().getType(globalPos.getPos())
                .ifPresent(pointOfInterestType -> DwarfTaskListProvider
                        .streamSeenDwarfs(dwarfEntity, dwarfEntity2 -> this.isUsingWorkStationAt(globalPos, (PointOfInterestType) pointOfInterestType, (DwarfEntity) dwarfEntity2))
                        .reduce(dwarfEntity, WorkStationCompetitionTask::keepJobSiteForMoreExperiencedVillager));
    }

    // @Override
    // protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
    // GlobalPos globalPos = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
    // serverWorld.getPointOfInterestStorage().getType(globalPos.getPos())
    // .ifPresent(pointOfInterestType -> LookTargetUtil
    // .streamSeenVillagers(villagerEntity, villagerEntity -> this.isUsingWorkStationAt(globalPos, (PointOfInterestType) pointOfInterestType, (VillagerEntity) villagerEntity))
    // .reduce(villagerEntity, WorkStationCompetitionTask::keepJobSiteForMoreExperiencedVillager));
    // }

    private static DwarfEntity keepJobSiteForMoreExperiencedVillager(DwarfEntity first, DwarfEntity second) {
        DwarfEntity villagerEntity2;
        DwarfEntity villagerEntity;
        if (first.getExperience() > second.getExperience()) {
            villagerEntity = first;
            villagerEntity2 = second;
        } else {
            villagerEntity = second;
            villagerEntity2 = first;
        }
        villagerEntity2.getBrain().forget(MemoryModuleType.JOB_SITE);
        return villagerEntity;
    }

    private boolean isUsingWorkStationAt(GlobalPos pos, PointOfInterestType poiType, DwarfEntity dwarfEntity) {
        return this.hasJobSite(dwarfEntity) && pos.equals(dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get())
                && this.isCompletedWorkStation(poiType, dwarfEntity.getVillagerData().getProfession());
    }

    private boolean isCompletedWorkStation(PointOfInterestType poiType, VillagerProfession profession) {
        return profession.getWorkStation().getCompletionCondition().test(poiType);
    }

    private boolean hasJobSite(DwarfEntity dwarfEntity) {
        return dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent();
    }
}
