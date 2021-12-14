package net.dwarfs.entity.render;

import net.dwarfs.entity.DwarfEntity;
import net.dwarfs.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DwarfEntityRenderer extends MobEntityRenderer<DwarfEntity, VillagerResemblingModel<DwarfEntity>> {
    private static final Identifier TEXTURE = new Identifier("dwarfs:textures/entity/dwarf.png");

    public DwarfEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new VillagerResemblingModel(context.getPart(RenderInit.DWARF_LAYER)), 0.5f);
        // this.addFeature(new HeadFeatureRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>>(this, context.getModelLoader()));
        // this.addFeature(new VillagerClothingFeatureRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>>(this, context.getResourceManager(), "villager"));
        // this.addFeature(new VillagerHeldItemFeatureRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>>(this));
    }

    @Override
    public Identifier getTexture(DwarfEntity dwarfEntity) {
        return TEXTURE;
    }

    @Override
    protected void scale(DwarfEntity dwarfEntity, MatrixStack matrixStack, float f) {
        float g = 0.9375f;
        if (dwarfEntity.isBaby()) {
            g = (float) ((double) g * 0.5);
            this.shadowRadius = 0.25f;
        } else {
            this.shadowRadius = 0.5f;
        }
        matrixStack.scale(g, g, g);
    }
}
