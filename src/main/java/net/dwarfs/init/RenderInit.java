package net.dwarfs.init;

import net.dwarfs.entity.model.DwarfEntityModel;
import net.dwarfs.entity.render.DwarfEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class RenderInit {

    public static final EntityModelLayer DWARF_LAYER = new EntityModelLayer(new Identifier("dwarfs:dwarf_layer"), "dwarf_layer");

    public static void init() {
        EntityRendererRegistry.register(EntityInit.DWARF, DwarfEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(DWARF_LAYER, DwarfEntityModel::getModelData);
    }

}
