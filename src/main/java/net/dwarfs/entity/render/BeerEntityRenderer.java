package net.dwarfs.entity.render;

import net.dwarfs.entity.BeerEntity;
import net.dwarfs.entity.model.BeerEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BeerEntityRenderer extends EntityRenderer<BeerEntity> {

    private static final Identifier TEXTURE = new Identifier("adventurez:textures/entity/beer.png");
    private final BeerEntityModel<BeerEntity> model = new BeerEntityModel<>(BeerEntityModel.getTexturedModelData().createModel());

    public BeerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public int getBlockLight(BeerEntity amethystShardEntity, BlockPos blockPos) {
        return amethystShardEntity.world.getLightLevel(blockPos);
    }

    @Override
    public void render(BeerEntity amethystShardEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        float h = MathHelper.lerpAngle(amethystShardEntity.prevYaw, amethystShardEntity.getYaw(), g);
        float j = MathHelper.lerp(g, amethystShardEntity.prevPitch, amethystShardEntity.getPitch());
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        matrixStack.translate(0.0D, -1.55D, 0.0D);
        this.model.setAngles(amethystShardEntity, 0.0F, 0.0F, 0.0F, h, j);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();
        super.render(amethystShardEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(BeerEntity amethystShardEntity) {
        return TEXTURE;
    }

}