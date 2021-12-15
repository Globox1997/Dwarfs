package net.dwarfs.init;

import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;

import net.dwarfs.entity.extra.DwarfProfession;
import net.dwarfs.entity.tasks.sensor.DwarfBabiesSensor;
import net.dwarfs.entity.tasks.sensor.DwarfHostilesSensor;
import net.dwarfs.entity.tasks.sensor.SecondaryPointsOfInterestSensor;
import net.dwarfs.mixin.accessor.RegistryAccess;
import net.dwarfs.mixin.accessor.SensorTypeAccess;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.poi.PointOfInterestType;

public class PointOfInterestsInit {

    public static final RegistryKey<Registry<DwarfProfession>> DWARF_PROFESSION_KEY = RegistryAccess.callCreateRegistryKey("dwarf_profession");

    public static final DefaultedRegistry<DwarfProfession> DWARF_PROFESSION = RegistryAccess.create(DWARF_PROFESSION_KEY, "none", () -> DwarfProfession.NONE);

    private static final Supplier<Set<PointOfInterestType>> DWARF_WORKSTATIONS = Suppliers.memoize(() -> DWARF_PROFESSION.stream().map(DwarfProfession::getWorkStation).collect(Collectors.toSet()));
    public static final Predicate<PointOfInterestType> IS_USED_BY_DWARF_PROFESSION = poiType -> DWARF_WORKSTATIONS.get().contains(poiType);

    public static final PointOfInterestType NOTHING = PointOfInterestHelper.register(new Identifier("dwarfs", "nothing"), 1, IS_USED_BY_DWARF_PROFESSION, 1, ImmutableSet.of());
    public static final PointOfInterestType BARTENDER = PointOfInterestHelper.register(new Identifier("dwarfs", "bartender"), 1, 1, Blocks.DRAGON_EGG);
    // public static final PointOfInterestType BARTENDER = PointOfInterestHelper.register(new Identifier("dwarfs", "bartender"), 1, 1,
    // ImmutableSet.copyOf(Blocks.DRAGON_EGG.getStateManager().getStates()));

    // public static final SensorType<HurtBySensor> HURT_BY = SensorType.register("hurt_by", HurtBySensor::new);
    public static final SensorType<DwarfBabiesSensor> DWARF_BABIES_SENSOR = SensorTypeAccess.register("dwarf_babies_sensor", DwarfBabiesSensor::new);
    public static final SensorType<DwarfHostilesSensor> DWARF_HOSTILES_SENSOR = SensorTypeAccess.register("dwarf_hostile_sensor", DwarfHostilesSensor::new);
    public static final SensorType<SecondaryPointsOfInterestSensor> DWARF_SECONDARY_POI_SENSOR = SensorTypeAccess.register("dwarf_secondary_poi_sensor", SecondaryPointsOfInterestSensor::new);

    public static void init() {
        TrackedDataHandlerRegistry.register(DwarfProfession.DWARF_DATA);
        System.out.println(BARTENDER.getCompletionCondition());
    }
}
