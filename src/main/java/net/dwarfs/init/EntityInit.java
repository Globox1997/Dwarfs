package net.dwarfs.init;

import net.dwarfs.entity.DwarfEntity;
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
import net.minecraft.util.registry.Registry;

public class EntityInit {

    public static final EntityType<DwarfEntity> DWARF = FabricEntityTypeBuilder.create(SpawnGroup.MISC, DwarfEntity::new).dimensions(EntityDimensions.fixed(0.7F, 1.2F)).build();

    public static void init() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier("dwarfs", "dwarf"), DWARF);
        FabricDefaultAttributeRegistry.register(DWARF, DwarfEntity.createDwarfAttributes());
        Registry.register(Registry.ITEM, new Identifier("dwarfs", "spawn_dwarf"), new SpawnEggItem(DWARF, 2956072, 1445648, new Item.Settings().group(ItemGroup.MISC)));
    }

}
