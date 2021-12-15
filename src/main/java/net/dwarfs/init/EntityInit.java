package net.dwarfs.init;

import com.google.common.collect.ImmutableSet;

import net.dwarfs.entity.DwarfEntity;
import net.dwarfs.entity.extra.DwarfProfession;
import net.dwarfs.mixin.accessor.RegistryAccess;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.PiglinEntity;
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
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class EntityInit {

    public static final EntityType<DwarfEntity> DWARF = FabricEntityTypeBuilder.create(SpawnGroup.MISC, DwarfEntity::new).dimensions(EntityDimensions.changing(0.7F, 1.2F)).build();

    public static void init() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier("dwarfs", "dwarf"), DWARF);
        FabricDefaultAttributeRegistry.register(DWARF, DwarfEntity.createDwarfAttributes());
        Registry.register(Registry.ITEM, new Identifier("dwarfs", "spawn_dwarf"), new SpawnEggItem(DWARF, 2956072, 1445648, new Item.Settings().group(ItemGroup.MISC)));
    }

}
