package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

import net.dwarfs.entity.DwarfEntity;
import net.dwarfs.entity.extra.DwarfProfession;
import net.dwarfs.init.EntityInit;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class GoToWorkTask extends Task<DwarfEntity> {
    public GoToWorkTask() {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleState.VALUE_PRESENT));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        BlockPos blockPos = dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos();
        return blockPos.isWithinDistance(dwarfEntity.getPos(), 2.0) || dwarfEntity.isNatural();
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        GlobalPos globalPos = dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get();
        dwarfEntity.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
        dwarfEntity.getBrain().remember(MemoryModuleType.JOB_SITE, globalPos);
        serverWorld.sendEntityStatus(dwarfEntity, (byte) 14);
        if (dwarfEntity.getDwarfData().getProfession() != DwarfProfession.NONE) {
            return;
        }
        MinecraftServer minecraftServer = serverWorld.getServer();
        Optional.ofNullable(minecraftServer.getWorld(globalPos.getDimension())).flatMap(world -> world.getPointOfInterestStorage().getType(globalPos.getPos()))
                .flatMap(poiType -> EntityInit.DWARF_PROFESSION.stream().filter(profession -> profession.getWorkStation() == poiType).findFirst()).ifPresent(profession -> {
                    dwarfEntity.setDwarfData(dwarfEntity.getDwarfData().withProfession((DwarfProfession) profession));
                    dwarfEntity.reinitializeBrain(serverWorld);
                });
    }
}
