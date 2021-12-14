package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;

import net.dwarfs.entity.DwarfEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

public class LoseJobOnSiteLossTask extends Task<DwarfEntity> {
    public LoseJobOnSiteLossTask() {
        super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_ABSENT));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        VillagerData villagerData = dwarfEntity.getVillagerData();
        return villagerData.getProfession() != VillagerProfession.NONE && villagerData.getProfession() != VillagerProfession.NITWIT && dwarfEntity.getExperience() == 0
                && villagerData.getLevel() <= 1;
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        dwarfEntity.setVillagerData(dwarfEntity.getVillagerData().withProfession(VillagerProfession.NONE));
        dwarfEntity.reinitializeBrain(serverWorld);
    }
}
