package com.buuz135.portality.proxy.client.render;

import com.buuz135.portality.Portality;
import com.hrznstudio.titanium.reward.storage.ClientRewardStorage;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class AuraRender extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private PlayerModel model = new PlayerModel(0.4f, false);

    public AuraRender(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> entityRendererIn) {
        super(entityRendererIn);
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
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(type.getResourceLocation(), false, false)).setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
            RenderSystem.enableBlend();
            if (type.isEnableBlend())
                RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        })).setTexturingState(new RenderStateShard.OffsetTexturingStateShard(0, f * 0.01F))
                .setAlphaState(new RenderStateShard.AlphaStateShard(0.003921569F))
                .createCompositeState(true);
        entitymodel.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        entitymodel.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.create("custom_cutout", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, rendertype$state)), 100, 100, 0.5F, 0.5F, 0.5F, 0.5F);
    }

}
