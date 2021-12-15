package net.dwarfs.init;

import net.dwarfs.entity.DwarfEntity;
import net.dwarfs.entity.extra.DwarfProfession;
import net.dwarfs.entity.extra.DwarfPointOfInterestType;
import net.dwarfs.mixin.accessor.RegistryAccess;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

public class EntityInit {

    public static final EntityType<DwarfEntity> DWARF = FabricEntityTypeBuilder.create(SpawnGroup.MISC, DwarfEntity::new).dimensions(EntityDimensions.fixed(0.7F, 1.2F)).build();

    public static final RegistryKey<Registry<DwarfProfession>> DWARF_PROFESSION_KEY = RegistryAccess.callCreateRegistryKey("dwarf_profession");
    public static final RegistryKey<Registry<DwarfPointOfInterestType>> DWARF_POINT_OF_INTEREST_TYPE_KEY = RegistryAccess.callCreateRegistryKey("dwarf_point_of_interest_type");

    public static final DefaultedRegistry<DwarfProfession> DWARF_PROFESSION = RegistryAccess.create(DWARF_PROFESSION_KEY, "none", () -> DwarfProfession.NONE);
    public static final DefaultedRegistry<DwarfPointOfInterestType> DWARF_POINT_OF_INTEREST_TYPE = RegistryAccess.create(DWARF_POINT_OF_INTEREST_TYPE_KEY, "unemployed_dwarf",
            () -> DwarfPointOfInterestType.UNEMPLOYED);

    // public static final RegistryKey<Registry<VillagerProfession>> VILLAGER_PROFESSION_KEY = Registry.createRegistryKey("villager_profession");

    public static void init() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier("dwarfs", "dwarf"), DWARF);
        FabricDefaultAttributeRegistry.register(DWARF, DwarfEntity.createDwarfAttributes());
        Registry.register(Registry.ITEM, new Identifier("dwarfs", "spawn_dwarf"), new SpawnEggItem(DWARF, 2956072, 1445648, new Item.Settings().group(ItemGroup.MISC)));
        // DefaultedRegistry.register(registry, id, entry)

        // Registry.register(Registry., id, entry)

    }

}
