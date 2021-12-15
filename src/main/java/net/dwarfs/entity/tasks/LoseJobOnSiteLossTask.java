package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;

import net.dwarfs.entity.DwarfEntity;
import net.dwarfs.entity.extra.DwarfData;
import net.dwarfs.entity.extra.DwarfProfession;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class LoseJobOnSiteLossTask extends Task<DwarfEntity> {
    public LoseJobOnSiteLossTask() {
        super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_ABSENT));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        DwarfData dwarfData = dwarfEntity.getDwarfData();
        return dwarfData.getProfession() != DwarfProfession.NONE && dwarfData.getProfession() != DwarfProfession.NITWIT && dwarfEntity.getExperience() == 0 && dwarfData.getLevel() <= 1;
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        dwarfEntity.setDwarfData(dwarfEntity.getDwarfData().withProfession(DwarfProfession.NONE));
        dwarfEntity.reinitializeBrain(serverWorld);
    }
}
