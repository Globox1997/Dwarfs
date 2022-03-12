package net.dwarfs.init;

import net.dwarfs.block.screen.BrewingBarrelScreen;
import net.dwarfs.entity.model.BeerEntityModel;
import net.dwarfs.entity.model.DwarfEntityModel;
import net.dwarfs.entity.render.BeerEntityRenderer;
import net.dwarfs.entity.render.DwarfEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RenderInit {

    public static final EntityModelLayer DWARF_LAYER = new EntityModelLayer(new Identifier("dwarfs:dwarf_layer"), "dwarf_layer");
    public static final EntityModelLayer BEER_LAYER = new EntityModelLayer(new Identifier("dwarfs:beer_layer"), "beer_layer");

    public static void init() {
        EntityRendererRegistry.register(EntityInit.DWARF, DwarfEntityRenderer::new);
        EntityRendererRegistry.register(EntityInit.BEER_ENTITY, BeerEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(DWARF_LAYER, DwarfEntityModel::getModelData);
        EntityModelLayerRegistry.registerModelLayer(BEER_LAYER, BeerEntityModel::getTexturedModelData);

        ScreenRegistry.register(BlockInit.BREWING_BARREL, BrewingBarrelScreen::new);
    }

}
