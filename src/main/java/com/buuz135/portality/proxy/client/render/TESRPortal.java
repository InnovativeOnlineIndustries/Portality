package com.buuz135.portality.proxy.client.render;

import com.buuz135.portality.Portality;
import com.buuz135.portality.tile.ControllerTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TESRPortal implements BlockEntityRenderer<ControllerTile> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Portality.MOD_ID, "textures/blocks/portal_render.png");

    public TESRPortal(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ControllerTile tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!tile.isFormed() || !tile.isDisplayNameEnabled() || !tile.isActive() || tile.getLinkData() == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        String name = tile.getLinkData().getName();
        float x = -Minecraft.getInstance().font.width(name) / 2.0F;
        Minecraft.getInstance().font.drawInBatch(Component.literal(name), x, 0, -1, false, poseStack.last().pose(), bufferSource, net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();
    }
}
