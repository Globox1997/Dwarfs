package net.dwarfs.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DwarfEntityModel<T extends Entity> extends SinglePartEntityModel<T> implements ModelWithHead, ModelWithHat, ModelWithArms {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart hatRim;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    protected final ModelPart nose;

    public DwarfEntityModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild(EntityModelPartNames.HEAD);
        this.hat = this.head.getChild(EntityModelPartNames.HAT);
        this.hatRim = this.hat.getChild(EntityModelPartNames.HAT_RIM);
        this.nose = this.head.getChild(EntityModelPartNames.NOSE);
        this.rightLeg = root.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftLeg = root.getChild(EntityModelPartNames.LEFT_LEG);
    }

    public static TexturedModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f), ModelTransform.NONE);
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, new Dilation(0.5f)),
                ModelTransform.NONE);
        modelPartData3.addChild(EntityModelPartNames.HAT_RIM, ModelPartBuilder.create().uv(30, 47).cuboid(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f), ModelTransform.rotation(-1.5707964f, 0.0f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.NOSE, ModelPartBuilder.create().uv(24, 0).cuboid(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f), ModelTransform.pivot(0.0f, -2.0f, 0.0f));
        ModelPartData modelPartData4 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(16, 20).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f), ModelTransform.NONE);
        modelPartData4.addChild(EntityModelPartNames.JACKET, ModelPartBuilder.create().uv(0, 38).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, new Dilation(0.5f)), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.ARMS, ModelPartBuilder.create().uv(44, 22).cuboid(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f).uv(44, 22)
                .cuboid(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, true).uv(40, 38).cuboid(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f), ModelTransform.of(0.0f, 3.0f, -1.0f, -0.75f, 0.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 22).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.pivot(-2.0f, 12.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.pivot(2.0f, 12.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        boolean bl = false;
        if (entity instanceof MerchantEntity) {
            bl = ((MerchantEntity) entity).getHeadRollingTimeLeft() > 0;
        }
        this.head.yaw = headYaw * ((float) Math.PI / 180);
        this.head.pitch = headPitch * ((float) Math.PI / 180);
        if (bl) {
            this.head.roll = 0.3f * MathHelper.sin(0.45f * animationProgress);
            this.head.pitch = 0.4f;
        } else {
            this.head.roll = 0.0f;
        }
        this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance * 0.5f;
        this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float) Math.PI) * 1.4f * limbDistance * 0.5f;
        this.rightLeg.yaw = 0.0f;
        this.leftLeg.yaw = 0.0f;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void setHatVisible(boolean visible) {
        this.head.visible = visible;
        this.hat.visible = visible;
        this.hatRim.visible = visible;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        this.getArm(arm).rotate(matrices);
    }

    protected ModelPart getArm(Arm arm) {
    //    if (arm == Arm.LEFT) {
    //        return this.leftArm;
   //    }
      //  return this.rightArm;
        return this.hatRim;
    }
}
