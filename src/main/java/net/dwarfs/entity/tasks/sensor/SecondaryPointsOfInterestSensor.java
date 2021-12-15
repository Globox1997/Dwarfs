package net.dwarfs.entity.tasks.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Set;

import net.dwarfs.entity.DwarfEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class SecondaryPointsOfInterestSensor extends Sensor<DwarfEntity> {
    private static final int RUN_TIME = 40;

    public SecondaryPointsOfInterestSensor() {
        super(40);
    }

    @Override
    protected void sense(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        RegistryKey<World> registryKey = serverWorld.getRegistryKey();
        BlockPos blockPos = dwarfEntity.getBlockPos();
        ArrayList<GlobalPos> list = Lists.newArrayList();
        int i = 4;
        for (int j = -4; j <= 4; ++j) {
            for (int k = -2; k <= 2; ++k) {
                for (int l = -4; l <= 4; ++l) {
                    BlockPos blockPos2 = blockPos.add(j, k, l);
                    if (!dwarfEntity.getDwarfData().getProfession().getSecondaryJobSites().contains(serverWorld.getBlockState(blockPos2).getBlock()))
                        continue;
                    list.add(GlobalPos.create(registryKey, blockPos2));
                }
            }
        }
        Brain<DwarfEntity> j = dwarfEntity.getBrain();
        if (!list.isEmpty()) {
            j.remember(MemoryModuleType.SECONDARY_JOB_SITE, list);
        } else {
            j.forget(MemoryModuleType.SECONDARY_JOB_SITE);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
    }
}
