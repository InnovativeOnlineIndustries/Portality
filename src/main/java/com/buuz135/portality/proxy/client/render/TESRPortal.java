package com.buuz135.portality.proxy.client.render;

import com.buuz135.portality.Portality;
import com.buuz135.portality.block.BlockController;
import com.buuz135.portality.proxy.client.ClientProxy;
import com.buuz135.portality.tile.TileController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRPortal extends TileEntitySpecialRenderer<TileController> {

    @Override
    public void render(TileController te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        if (!te.isFormed()) return;
        GlStateManager.pushMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int j = 240;
        int k = 224;
        RenderHelper.disableStandardItemLighting();
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }
        bindTexture(new ResourceLocation(Portality.MOD_ID, "textures/blocks/frame/frame.png"));
        GlStateManager.translate(x, y, z);
        //ROTATE Z TO COMPLETE TUNNEL ROTATE Y TO ROTATE FACING
        EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(BlockController.FACING);
        if (facing == EnumFacing.SOUTH) {
            GlStateManager.translate(1, 0, 1);
            GlStateManager.rotate(-180, 0, 1, 0);
        }
        if (facing == EnumFacing.EAST) {
            GlStateManager.translate(1, 0, 0);
            GlStateManager.rotate(-90, 0, 1, 0);
        }
        if (facing == EnumFacing.WEST) {
            GlStateManager.translate(0, 0, 1);
            GlStateManager.rotate(90, 0, 1, 0);
        }
        double frame = ClientProxy.TICK / 80D;
        //TOP
        renderTop(tessellator, buffer, te, x, y, z, frame, j, k);
        //RIGHT
        GlStateManager.translate(3, 2, 0);
        GlStateManager.rotate(90, 0, 0, 1);
        renderTop(tessellator, buffer, te, x, y, z, frame, j, k);
        GlStateManager.rotate(-90, 0, 0, 1);
        GlStateManager.translate(-3, -2, 0);
        //LEFT
        GlStateManager.translate(-2, 3, 0);
        GlStateManager.rotate(-90, 0, 0, 1);
        renderTop(tessellator, buffer, te, x, y, z, frame, j, k);
        GlStateManager.rotate(90, 0, 0, 1);
        GlStateManager.translate(2, -3, 0);
        //BOTTOM
        GlStateManager.translate(1, 5, 0);
        GlStateManager.rotate(-180, 0, 0, 1);
        renderTop(tessellator, buffer, te, x, y, z, frame, j, k);
        GlStateManager.rotate(190, 0, 0, 1);
        GlStateManager.translate(-1, -5, 0);

        buffer.setTranslation(0, 0, 0);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
        if (te.isActive() && te.getLinkData() != null && te.isDisplayNameEnabled()) {
            this.setLightmapDisabled(true);
            drawNameplate(te, te.getLinkData().getName(), x, y, z, 16);
        }
    }

    public void renderTop(Tessellator tessellator, BufferBuilder buffer, TileController te, double x, double y, double z, double frame, int j, int k) {
        for (int posX = 0; posX < 4; ++posX) {
            for (int posZ = 0; posZ < te.getLength(); ++posZ) {
                GlStateManager.translate(posX - 2 + frame, 0, posZ);
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
                double pX1 = 1;
                double u = 1;
                double pX2 = 0;
                double u2 = 0;
                if (posX == 3) {
                    pX1 = 1 - frame - 0.2;
                    u = 1 - 1 * frame - 0.2;
                }
                if (posX == 0) {
                    pX2 = 1 - frame + 0.2;
                    u2 = 1 - frame + 0.2;
                }
                buffer.pos(pX2, 3.8, 0).tex(u2, 0).lightmap(j, k).color(255, 255, 255, 255).endVertex();
                buffer.pos(pX1, 3.8, 0).tex(u, 0).lightmap(j, k).color(255, 255, 255, 255).endVertex();
                buffer.pos(pX1, 3.8, 1).tex(u, 1).lightmap(j, k).color(255, 255, 255, 255).endVertex();
                buffer.pos(pX2, 3.8, 1).tex(u2, 1).lightmap(j, k).color(255, 255, 255, 255).endVertex();
                tessellator.draw();
                GlStateManager.translate(-(posX - 2 + frame), 0, -posZ);
            }
        }
    }
}
