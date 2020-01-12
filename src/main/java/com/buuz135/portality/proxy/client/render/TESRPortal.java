/**
 * MIT License
 *
 * Copyright (c) 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.buuz135.portality.proxy.client.render;

import com.buuz135.portality.Portality;
import com.buuz135.portality.block.ControllerBlock;
import com.buuz135.portality.tile.ControllerTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.Random;

public class TESRPortal extends TileEntityRenderer<ControllerTile> {

    private static final Random RANDOM = new Random(31100L);
    public static RenderType TYPE = createRenderType();

    public TESRPortal(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    public static RenderType createRenderType() {
        RenderType.State state = RenderType.State.builder().texture(new RenderState.TextureState(new ResourceLocation(Portality.MOD_ID, "textures/blocks/portal_render.png"), false, false)).transparency(new RenderState.TransparencyState("translucent_transparency", () -> {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            RenderSystem.enableAlphaTest();
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.disableAlphaTest();
        })).build(true);
        return RenderType.of("portal_render", DefaultVertexFormats.POSITION_TEX_COLOR, 7, 256, false, true, state);
    }

    public void renderTop(MatrixStack stack, IVertexBuilder buffer, ControllerTile te, float frame, float xTrans, float yTrans, float zTrans, double offset, int width, Color color) {
        double scale = 0.9335;
        float y = 3.999f;
        float off = /*0.0278*/ 4 - y;
        //RenderSystem.scale(scale, 1, 1);
        RenderSystem.enableAlphaTest();
        for (int posX = 0; posX < width; ++posX) {
            for (int posZ = 0; posZ < te.getLength(); ++posZ) {
                //RenderSystem.translated(posX - 2.1 + frame + off, 0, posZ);
                //buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                float pX1 = 1;
                float u = 1;
                float pX2 = 0;
                float u2 = 0;
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
                int alpha = 150;
                float xOffset = posX - 2f + frame + off + xTrans;
                float yOffset = yTrans - off;
                float zOffset = posZ + zTrans;
                Matrix4f matrix = stack.peek().getModel();
                buffer.vertex(matrix, pX2 + xOffset, yOffset, 0 + zOffset).texture(u2, 0).color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
                buffer.vertex(matrix, pX1 + xOffset, yOffset, 0 + zOffset).texture(u, 0).color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
                buffer.vertex(matrix, pX1 + xOffset, yOffset, 1 + zOffset).texture(u, 1).color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
                buffer.vertex(matrix, pX2 + xOffset, yOffset, 1 + zOffset).texture(u2, 1).color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
                //tessellator.draw();
                //RenderSystem.translated(-(posX - 2.1 + frame + off), 0, -posZ);

            }
        }
        //RenderSystem.scale(1 / scale, 1, 1);
    }

    //@Override
    //public void func_225616_a_(TileController te, float p_225616_2_, MatrixStack matrixStack, IRenderTypeBuffer typeBuffer, int p_225616_5_, int p_225616_6_) {
    //    //super.render(te, x, y, z, partialTicks, destroyStage);
    //    if (!te.isFormed()) return;
    //    RenderSystem.pushMatrix();
    //    //matrixStack.func_227860_a_();
    //    //RenderSystem.enableBlend();
    //    //BufferBuilder buffer = tessellator.getBuffer();
    //    ResourceLocation texture = new ResourceLocation("textures/entity/beacon_beam.png");
    //    IVertexBuilder buffer = typeBuffer.getBuffer(TYPE);
    //    int j = 240;
    //    int k = 224;
    //    int x = te.getPos().getX();
    //    int y = te.getPos().getY();
    //    int z = te.getPos().getZ();
    //    RenderHelper.disableStandardItemLighting();
    //    if (Minecraft.isAmbientOcclusionEnabled()) {
    //        RenderSystem.shadeModel(GL11.GL_SMOOTH);
    //    } else {
    //        RenderSystem.shadeModel(GL11.GL_FLAT);
    //    }
    //    //Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(Portality.MOD_ID, "blocks/portal_render.png"));
    //    //Minecraft.getInstance().getTextureManager().bindTexture(;
    //    //RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
    //    //matrixStack.func_227861_a_(x, y, z);
    //    //ROTATE Z TO COMPLETE TUNNEL ROTATE Y TO ROTATE FACINGB
    //    Direction facing = te.getWorld().getBlockState(te.getPos()).get(BlockController.FACING);
    //    if (facing == Direction.SOUTH) {
    //        RenderSystem.translated(1, 0, 1);
    //        RenderSystem.rotatef(-180, 0, 1, 0);
    //    }
    //    if (facing == Direction.EAST) {
    //        RenderSystem.translated(1, 0, 0);
    //        RenderSystem.rotatef(-90, 0, 1, 0);
    //    }
    //    if (facing == Direction.WEST) {
    //        RenderSystem.translated(0, 0, 1);
    //        RenderSystem.rotatef(90, 0, 1, 0);
    //    }
    //    float frame = (te.getWorld().getGameTime() % 60) / 60f;
    //    //frame = 0.4;
    //    //TOP
    //    RenderSystem.translated(0.1 - te.getWidth() + 2, te.getHeight() - 5, 0);
    //    renderTop(matrixStack, buffer, te, frame, j, k, 0.4, te.getWidth() * 2);
    //    RenderSystem.translated(-0.1 - (-te.getWidth() + 2), -(te.getHeight() - 5), 0);
    //    //RIGHT
    //    RenderSystem.translated(3 - te.getWidth() + 2, 2.1, 0);
    //    RenderSystem.rotatef(90, 0, 0, 1);
    //    renderTop(matrixStack, buffer, te, frame, j, k, 0.2, te.getHeight() - 1);
    //    RenderSystem.rotatef(-90, 0, 0, 1);
    //    RenderSystem.translated(-3 - (-te.getWidth() + 2), -2.1, 0);
    //    //LEFT
    //    RenderSystem.translated(-2 + te.getWidth() - 2, te.getHeight() - 2.1, 0);
    //    RenderSystem.rotatef(-90, 0, 0, 1);
    //    renderTop(matrixStack, buffer, te, frame, j, k, 0, te.getHeight() - 1);
    //    RenderSystem.rotatef(90, 0, 0, 1);
    //    RenderSystem.translated(2 - (te.getWidth() - 2), -(te.getHeight() - 2.1), 0);
    //    //BOTTOM
    //    RenderSystem.translated(0.9 + te.getWidth() - 2, 5, 0);
    //    RenderSystem.rotatef(-180, 0, 0, 1);
    //    renderTop(matrixStack, buffer, te, frame, j, k, 0.6, te.getWidth() * 2);
    //    RenderSystem.rotatef(190, 0, 0, 1);
    //    RenderSystem.translated(-0.9 - (+te.getWidth() - 2), -5, 0);
//
    //    matrixStack.func_227861_a_(0,0,0);
    //    //buffer.setTranslation(0, 0, 0);
    //    RenderSystem.setupGui3DDiffuseLighting();
    //    RenderSystem.disableBlend();
    //    RenderSystem.popMatrix();
    //    if (te.isActive() && te.getLinkData() != null && te.isDisplayNameEnabled()) {
    //        //this.setLightmapDisabled(true);
    //        //drawNameplate(te, te.getLinkData().getName(), x, y, z, 16);
    //        //this.setLightmapDisabled(false);
    //    }
    //}

    @Override
    public void render(ControllerTile te, float p_225616_2_, MatrixStack matrixStack, IRenderTypeBuffer typeBuffer, int p_225616_5_, int p_225616_6_) {
        if (!te.isFormed()) return;
        RenderSystem.pushMatrix();
        float frame = (te.getWorld().getGameTime() % 60) / 60f;
        //Color color = Color.getHSBColor((te.getWorld().getGameTime() % 360)/ 360f , 01f ,1f);
        Color color = Color.CYAN;
        //renderTop(stack, p_225616_4_.getBuffer(TYPE), te, frame,0,0, -0.6, te.getWidth() * 2);
        //this.func_228883_a_(te, lvt_10_1_, 0.15F, lvt_11_1_, p_225616_4_.getBuffer((RenderType)field_228881_e_.get(0)));
        int x = 0;
        int y = 0;
        int z = 0;
        //RenderSystem.translated(x,y,z);
        if (te.isDisplayNameEnabled() && te.isActive()) {
            matrixStack.push();
            String name = te.getLinkData().getName();
            matrixStack.translate(0.5, (double) 1.5f, 0.5D);
            matrixStack.multiply(Minecraft.getInstance().getRenderManager().getRotation());
            matrixStack.scale(-0.025F, -0.025F, 0.025F);
            float f1 = Minecraft.getInstance().gameSettings.func_216840_a(0.25F);
            int j = (int) (f1 * 255.0F) << 24;

            Minecraft.getInstance().fontRenderer.draw(name, -Minecraft.getInstance().fontRenderer.getStringWidth(name) / 2f, 0, -1, false, matrixStack.peek().getModel(), typeBuffer, false, j, 15728880);
            matrixStack.pop();
        }
        Direction facing = te.getWorld().getBlockState(te.getPos()).get(ControllerBlock.FACING_HORIZONTAL);
        if (facing == Direction.SOUTH) {
            //RenderSystem.translated(1, 0, 1);
            //RenderSystem.rotatef(-180, 0, 1, 0);
            z = -1;
            x = -1;
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-180f));
        }
        if (facing == Direction.EAST) {
            //RenderSystem.translated(1, 0, 0);
            //RenderSystem.rotatef(-90, 0, 1, 0);
            z = -1;
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90f));
        }
        if (facing == Direction.WEST) {
            //RenderSystem.translated(0, 0, 1);
            x = -1;
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90f));
            //RenderSystem.rotatef(90, 0, 1, 0);
        }
        if (facing == Direction.NORTH) {
            //x = 1;
        }
        //TOP
        //RenderSystem.translated(0.1 - te.getWidth() + 2, te.getHeight() - 5, 0);
        IVertexBuilder buffer = typeBuffer.getBuffer(TYPE);
        renderTop(matrixStack, buffer, te, frame, -te.getWidth() + 2f + x, te.getHeight() + y - 1f, z, 0.4, te.getWidth() * 2, color);
        //RenderSystem.translated(-0.1 - (-te.getWidth() + 2), -(te.getHeight() - 5), 0);
        //RIGHT
        //RenderSystem.translated(3 - te.getWidth() + 2, 2.1, 0);
        //RenderSystem.rotatef(90, 0, 0, 1);
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90f));
        renderTop(matrixStack, buffer, te, frame, 2 + y, te.getWidth() - 1 - x, z, 0.2, te.getHeight() - 1, color);
        matrixStack.multiply(Vector3f.NEGATIVE_Z.getDegreesQuaternion(90f));
        //RenderSystem.rotatef(-90, 0, 0, 1);
        //RenderSystem.translated(-3 - (-te.getWidth() + 2), -2.1, 0);
        //LEFT
        //RenderSystem.translated(-2 + te.getWidth() - 2, te.getHeight() - 2.1, 0);
        //RenderSystem.rotatef(-90, 0, 0, 1);
        matrixStack.multiply(Vector3f.NEGATIVE_Z.getDegreesQuaternion(90f));
        renderTop(matrixStack, buffer, te, frame, 2 - te.getHeight() + y, te.getWidth() + x, z, 0, te.getHeight() - 1, color);
        //RenderSystem.rotatef(90, 0, 0, 1);
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90f));
        //RenderSystem.translated(2 - (te.getWidth() - 2), -(te.getHeight() - 2.1), 0);
        //BOTTOM
        //RenderSystem.translated(0.9 + te.getWidth() - 2, 5, 0);
        //RenderSystem.rotatef(-180, 0, 0, 1);
        matrixStack.multiply(Vector3f.NEGATIVE_Z.getDegreesQuaternion(180f));
        renderTop(matrixStack, buffer, te, frame, -te.getWidth() - x + 1, -1 - y, z, 0.6, te.getWidth() * 2, color);
        //RenderSystem.rotatef(190, 0, 0, 1);180f));
        //RenderSystem.translated(-0.9 - (+te.getWidth() - 2), -5, 0);
        RenderSystem.popMatrix();

    }
}
