/*
 * This file is part of Worldgen Indicators.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.portality.proxy.client.render;

import com.buuz135.portality.Portality;
import com.buuz135.portality.block.BlockController;
import com.buuz135.portality.tile.TileController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TESRPortal extends TileEntityRenderer<TileController> {

    @Override
    public void render(TileController te, double x, double y, double z, float partialTicks, int destroyStage) {
        //super.render(te, x, y, z, partialTicks, destroyStage);
        if (!te.isFormed()) return;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
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
        bindTexture(new ResourceLocation(Portality.MOD_ID, "textures/blocks/portal_render.png"));
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.translated(x, y, z);
        //ROTATE Z TO COMPLETE TUNNEL ROTATE Y TO ROTATE FACING
        EnumFacing facing = te.getWorld().getBlockState(te.getPos()).get(BlockController.FACING);
        if (facing == EnumFacing.SOUTH) {
            GlStateManager.translated(1, 0, 1);
            GlStateManager.rotatef(-180, 0, 1, 0);
        }
        if (facing == EnumFacing.EAST) {
            GlStateManager.translated(1, 0, 0);
            GlStateManager.rotatef(-90, 0, 1, 0);
        }
        if (facing == EnumFacing.WEST) {
            GlStateManager.translated(0, 0, 1);
            GlStateManager.rotatef(90, 0, 1, 0);
        }
        double frame = (te.getWorld().getGameTime() % 60) / 60D;
        //frame = 0.4;
        //TOP
        GlStateManager.translated(0.1 - te.getWidth() + 2, te.getHeight() - 5, 0);
        renderTop(tessellator, buffer, te, frame, j, k, 0.4, te.getWidth() * 2);
        GlStateManager.translated(-0.1 - (-te.getWidth() + 2), -(te.getHeight() - 5), 0);
        //RIGHT
        GlStateManager.translated(3 - te.getWidth() + 2, 2.1, 0);
        GlStateManager.rotatef(90, 0, 0, 1);
        renderTop(tessellator, buffer, te, frame, j, k, 0.2, te.getHeight() - 1);
        GlStateManager.rotatef(-90, 0, 0, 1);
        GlStateManager.translated(-3 - (-te.getWidth() + 2), -2.1, 0);
        //LEFT
        GlStateManager.translated(-2 + te.getWidth() - 2, te.getHeight() - 2.1, 0);
        GlStateManager.rotatef(-90, 0, 0, 1);
        renderTop(tessellator, buffer, te, frame, j, k, 0, te.getHeight() - 1);
        GlStateManager.rotatef(90, 0, 0, 1);
        GlStateManager.translated(2 - (te.getWidth() - 2), -(te.getHeight() - 2.1), 0);
        //BOTTOM
        GlStateManager.translated(0.9 + te.getWidth() - 2, 5, 0);
        GlStateManager.rotatef(-180, 0, 0, 1);
        renderTop(tessellator, buffer, te, frame, j, k, 0.6, te.getWidth() * 2);
        GlStateManager.rotatef(190, 0, 0, 1);
        GlStateManager.translated(-0.9 - (+te.getWidth() - 2), -5, 0);

        buffer.setTranslation(0, 0, 0);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        if (te.isActive() && te.getLinkData() != null && te.isDisplayNameEnabled()) {
            this.setLightmapDisabled(true);
            drawNameplate(te, te.getLinkData().getName(), x, y, z, 16);
            this.setLightmapDisabled(false);
        }
    }

    public void renderTop(Tessellator tessellator, BufferBuilder buffer, TileController te, double frame, int j, int k, double offset, int width) {
        double scale = 0.9335;
        float y = 3.99f;
        float off = /*0.0278*/ 4 - y;
        //GlStateManager.scale(scale, 1, 1);
        GlStateManager.enableAlphaTest();
        for (double posX = 0; posX < width; ++posX) {
            for (int posZ = 0; posZ < te.getLength(); ++posZ) {
                GlStateManager.translated(posX - 2.1 + frame + off, 0, posZ);
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
                double pX1 = 1;
                double u = 1;
                double pX2 = 0;
                double u2 = 0;
                if (posX == 0) {
                    pX2 = 1 - frame;
                    u2 = 1 - frame;
                }
                if (posX == 1 && frame < 0) {
                    pX2 = -frame;
                    u2 = -frame;
                }
                if (posX == width - 1) {
                    pX1 = Math.max(1 - frame, 0);
                    u = 1 - 1 * frame;
                }
                int alpha = 100;
                buffer.pos(pX2, y, 0).tex(u2, 0).lightmap(j, k).color(Color.CYAN.getRed(), Color.CYAN.getGreen(), Color.CYAN.getBlue(), alpha).endVertex();
                buffer.pos(pX1, y, 0).tex(u, 0).lightmap(j, k).color(Color.CYAN.getRed(), Color.CYAN.getGreen(), Color.CYAN.getBlue(), alpha).endVertex();
                buffer.pos(pX1, y, 1).tex(u, 1).lightmap(j, k).color(Color.CYAN.getRed(), Color.CYAN.getGreen(), Color.CYAN.getBlue(), alpha).endVertex();
                buffer.pos(pX2, y, 1).tex(u2, 1).lightmap(j, k).color(Color.CYAN.getRed(), Color.CYAN.getGreen(), Color.CYAN.getBlue(), alpha).endVertex();
                tessellator.draw();

                GlStateManager.translated(-(posX - 2.1 + frame + off), 0, -posZ);

            }
        }
        //GlStateManager.scale(1 / scale, 1, 1);
    }
}
