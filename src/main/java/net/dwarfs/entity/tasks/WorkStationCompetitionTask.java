package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;

import net.dwarfs.entity.DwarfEntity;
import net.dwarfs.entity.extra.DwarfProfession;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.world.poi.PointOfInterestType;

public class WorkStationCompetitionTask extends Task<DwarfEntity> {
    final DwarfProfession profession;

    public WorkStationCompetitionTask(DwarfProfession profession) {
        super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.MOBS, MemoryModuleState.VALUE_PRESENT));
        this.profession = profession;
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        GlobalPos globalPos = dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
        serverWorld.getPointOfInterestStorage().getType(globalPos.getPos())
                .ifPresent(pointOfInterestType -> DwarfTaskListProvider
                        .streamSeenDwarfs(dwarfEntity, dwarfEntity2 -> this.isUsingWorkStationAt(globalPos, (PointOfInterestType) pointOfInterestType, (DwarfEntity) dwarfEntity2))
                        .reduce(dwarfEntity, WorkStationCompetitionTask::keepJobSiteForMoreExperiencedDwarf));
    }

    // @Override
    // protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
    // GlobalPos globalPos = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
    // serverWorld.getPointOfInterestStorage().getType(globalPos.getPos())
    // .ifPresent(pointOfInterestType -> LookTargetUtil
    // .streamSeenVillagers(villagerEntity, villagerEntity -> this.isUsingWorkStationAt(globalPos, (PointOfInterestType) pointOfInterestType, (VillagerEntity) villagerEntity))
    // .reduce(villagerEntity, WorkStationCompetitionTask::keepJobSiteForMoreExperiencedVillager));
    // }

    private static DwarfEntity keepJobSiteForMoreExperiencedDwarf(DwarfEntity first, DwarfEntity second) {
        DwarfEntity dwarfEntity2;
        DwarfEntity dwarfEntity;
        if (first.getExperience() > second.getExperience()) {
            dwarfEntity = first;
            dwarfEntity2 = second;
        } else {
            dwarfEntity = second;
            dwarfEntity2 = first;
        }
        dwarfEntity2.getBrain().forget(MemoryModuleType.JOB_SITE);
        return dwarfEntity;
    }

    private boolean isUsingWorkStationAt(GlobalPos pos, PointOfInterestType poiType, DwarfEntity dwarfEntity) {
        return this.hasJobSite(dwarfEntity) && pos.equals(dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get())
                && this.isCompletedWorkStation(poiType, dwarfEntity.getDwarfData().getProfession());
    }

    private boolean isCompletedWorkStation(PointOfInterestType poiType, DwarfProfession profession) {
        return profession.getWorkStation().getCompletionCondition().test(poiType);
    }

    private boolean hasJobSite(DwarfEntity dwarfEntity) {
        return dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent();
    }
}
