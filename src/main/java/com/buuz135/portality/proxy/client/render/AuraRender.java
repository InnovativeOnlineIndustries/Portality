package com.buuz135.portality.proxy.client.render;

import com.buuz135.portality.Portality;
import com.hrznstudio.titanium.reward.storage.ClientRewardStorage;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class AuraRender extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public static PlayerModel model;

    public AuraRender(LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
        model = renderer.getModel();
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!ClientRewardStorage.REWARD_STORAGE.getRewards().containsKey(entityIn.getUUID())) return;
        if (!ClientRewardStorage.REWARD_STORAGE.getRewards().get(entityIn.getUUID()).getEnabled().containsKey(new ResourceLocation(Portality.MOD_ID, "aura")))
            return;
        Portality.AuraType type = Portality.AuraType.valueOf(ClientRewardStorage.REWARD_STORAGE.getRewards().get(entityIn.getUUID()).getEnabled().get(new ResourceLocation(Portality.MOD_ID, "aura")));
        float f = (float) entityIn.tickCount + partialTicks;
        EntityModel<AbstractClientPlayer> entitymodel = this.getParentModel();
        entitymodel.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTicks);
        this.getParentModel().copyPropertiesTo(entitymodel);
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEnergySwirlShader))
                .setTextureState(new RenderStateShard.TextureStateShard(type.getResourceLocation(), false, false))
                .setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
                    RenderSystem.enableBlend();
                    if (type.isEnableBlend())
                        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                }, () -> {
                    RenderSystem.disableBlend();
                    RenderSystem.defaultBlendFunc();
                })).setOverlayState(new RenderStateShard.OverlayStateShard(true))
                .setCullState(new RenderStateShard.CullStateShard(false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard(0, f * 0.01F))
                //.setTransparencyState(new RenderStateShard.TransparencyStateShard(0.003921569F))
                .createCompositeState(true);
        entitymodel.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        entitymodel.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.create("portality_aura", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state)), 100, 100, 0.5F, 0.5F, 0.5F, 0.5F);
    }

}
