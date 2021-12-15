package net.dwarfs.entity.tasks.sensor;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleLivingEntitySensor;

public class DwarfHostilesSensor extends NearestVisibleLivingEntitySensor {
    private static final ImmutableMap<EntityType<?>, Float> SQUARED_DISTANCES_FOR_DANGER = ImmutableMap.<EntityType<?>, Float>builder().put(EntityType.DROWNED, Float.valueOf(8.0f))
            .put(EntityType.EVOKER, Float.valueOf(12.0f)).put(EntityType.HUSK, Float.valueOf(8.0f)).put(EntityType.ILLUSIONER, Float.valueOf(12.0f)).put(EntityType.PILLAGER, Float.valueOf(15.0f))
            .put(EntityType.RAVAGER, Float.valueOf(12.0f)).put(EntityType.VEX, Float.valueOf(8.0f)).put(EntityType.VINDICATOR, Float.valueOf(10.0f)).put(EntityType.ZOGLIN, Float.valueOf(10.0f))
            .put(EntityType.ZOMBIE, Float.valueOf(8.0f)).put(EntityType.CREEPER, Float.valueOf(8.0f)).put(EntityType.CAVE_SPIDER, Float.valueOf(8.0f))
            .put(EntityType.WITHER_SKELETON, Float.valueOf(10.0f)).put(EntityType.SKELETON, Float.valueOf(10.0f)).put(EntityType.ENDERMAN, Float.valueOf(8.0f))
            .put(EntityType.SPIDER, Float.valueOf(10.0f)).put(EntityType.SLIME, Float.valueOf(8.0f)).put(EntityType.WITCH, Float.valueOf(10.0f)).put(EntityType.ZOMBIE_VILLAGER, Float.valueOf(8.0f))
            .build();

    @Override
    protected boolean matches(LivingEntity entity, LivingEntity target) {
        return this.isHostile(target) && this.isCloseEnoughForDanger(entity, target);
    }

    private boolean isCloseEnoughForDanger(LivingEntity dwarf, LivingEntity target) {
        float f = SQUARED_DISTANCES_FOR_DANGER.get(target.getType()).floatValue();
        return target.squaredDistanceTo(dwarf) <= (double) (f * f);
    }

    @Override
    protected MemoryModuleType<LivingEntity> getOutputMemoryModule() {
        return MemoryModuleType.NEAREST_HOSTILE;
    }

    private boolean isHostile(LivingEntity entity) {
        return SQUARED_DISTANCES_FOR_DANGER.containsKey(entity.getType());
    }
}
