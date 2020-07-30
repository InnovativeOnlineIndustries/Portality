package com.buuz135.portality.proxy.client.render;

import com.buuz135.portality.Portality;
import com.hrznstudio.titanium.reward.storage.ClientRewardStorage;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class AuraRender extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private PlayerModel model = new PlayerModel(0.4f, false);

    public AuraRender(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!ClientRewardStorage.REWARD_STORAGE.getRewards().containsKey(entityIn.getUniqueID())) return;
        if (!ClientRewardStorage.REWARD_STORAGE.getRewards().get(entityIn.getUniqueID()).getEnabled().containsKey(new ResourceLocation(Portality.MOD_ID, "aura")))
            return;
        Portality.AuraType type = Portality.AuraType.valueOf(ClientRewardStorage.REWARD_STORAGE.getRewards().get(entityIn.getUniqueID()).getEnabled().get(new ResourceLocation(Portality.MOD_ID, "aura")));
        float f = (float) entityIn.ticksExisted + partialTicks;
        EntityModel<AbstractClientPlayerEntity> entitymodel = this.getEntityModel();
        entitymodel.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
        this.getEntityModel().copyModelAttributesTo(entitymodel);
        RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(type.getResourceLocation(), false, false)).transparency(new RenderState.TransparencyState("translucent_transparency", () -> {
            RenderSystem.enableBlend();
            if (type.isEnableBlend())
                RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        })).texturing(new RenderState.OffsetTexturingState(0, f * 0.01F))
                .alpha(new RenderState.AlphaState(0.003921569F))
                .build(true);
        entitymodel.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        entitymodel.render(matrixStackIn, bufferIn.getBuffer(RenderType.makeType("custom_cutout", DefaultVertexFormats.ENTITY, 7, 256, true, false, rendertype$state)), 100, 100, 0.5F, 0.5F, 0.5F, 0.5F);
    }

}
