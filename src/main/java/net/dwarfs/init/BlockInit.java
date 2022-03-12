package net.dwarfs.init;

import net.dwarfs.block.BrewingBarrelBlock;
import net.dwarfs.block.entity.BrewingBarrelBlockEntity;
import net.dwarfs.block.screen.BrewingBarrelScreenHandler;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockInit {

    // Block
    public static final BrewingBarrelBlock BREWING_BARREL_BLOCK = new BrewingBarrelBlock(FabricBlockSettings.copy(Blocks.BARREL));
    // Block Entity
    public static BlockEntityType<BrewingBarrelBlockEntity> BREWING_BARREL_ENTITY;
    // ScreenHandler
    public static final ScreenHandlerType<BrewingBarrelScreenHandler> BREWING_BARREL = ScreenHandlerRegistry.registerSimple(new Identifier("dwarfs", "brewing_barrel"),
            BrewingBarrelScreenHandler::new);

    public static void init() {
        Registry.register(Registry.ITEM, new Identifier("dwarfs", "brewing_barrel"), new BlockItem(BREWING_BARREL_BLOCK, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("dwarfs", "brewing_barrel"), BREWING_BARREL_BLOCK);

        BREWING_BARREL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "dwarfs:brewing_barrel_entity",
                FabricBlockEntityTypeBuilder.create(BrewingBarrelBlockEntity::new, BREWING_BARREL_BLOCK).build(null));
    }

}
