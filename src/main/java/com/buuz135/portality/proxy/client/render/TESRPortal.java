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
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class TESRPortal implements BlockEntityRenderer<ControllerTile> {

    public static ResourceLocation TEXTURE = new ResourceLocation(Portality.MOD_ID, "textures/blocks/portal_render.png");

    private static final Random RANDOM = new Random(31100L);
    public static RenderType TYPE = createRenderType();

    public TESRPortal(BlockEntityRendererProvider.Context context) {

    }

    public static RenderType createRenderType() {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                //.setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexShader))
                .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, true))
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexShader))
                .setTextureState(new RenderStateShard.TextureStateShard(TEXTURE, false, false)).setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
            RenderSystem.enableBlend();
            //RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        }, () -> {
                    RenderSystem.disableBlend();
                    RenderSystem.defaultBlendFunc();
        })).setLayeringState(new RenderStateShard.LayeringStateShard("view_offset_z_layering", () -> {
                    PoseStack posestack = RenderSystem.getModelViewStack();
                    posestack.pushPose();
                    posestack.scale(0.99975586F, 0.99975586F, 0.99975586F);
                    RenderSystem.applyModelViewMatrix();
                }, () -> {
                    PoseStack posestack = RenderSystem.getModelViewStack();
                    posestack.popPose();
                    RenderSystem.applyModelViewMatrix();
                })).setCullState(new RenderStateShard.CullStateShard(false)).createCompositeState(true);
        return RenderType.create("portal_render", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, false, state);
    }

    public void renderTop(PoseStack stack, VertexConsumer buffer, ControllerTile te, float frame, float xTrans, float yTrans, float zTrans, double offset, int width, int color) {
        double scale = 0.9335;
        float y = 3.999f;
        float off = /*0.0278*/ 4 - y;
        float red = FastColor.ARGB32.red(color) / 256F;
        float green = FastColor.ARGB32.green(color)  / 256F;
        float blue = FastColor.ARGB32.blue(color)  / 256F;
        //RenderSystem.scale(scale, 1, 1);
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
                int alpha = 1;
                float xOffset = posX - 2f + frame + off + xTrans;
                float yOffset = yTrans - off;
                float zOffset = posZ + zTrans;
                Matrix4f matrix = stack.last().pose();
    

                buffer.vertex(matrix, pX2 + xOffset, yOffset, 0 + zOffset).color(red, green, blue, alpha).uv(u2, 0).endVertex();
                buffer.vertex(matrix, pX1 + xOffset, yOffset, 0 + zOffset).color(red, green, blue, alpha).uv(u, 0).endVertex();
                buffer.vertex(matrix, pX1 + xOffset, yOffset, 1 + zOffset).color(red, green, blue, alpha).uv(u, 1).endVertex();
                buffer.vertex(matrix, pX2 + xOffset, yOffset, 1 + zOffset).color(red, green, blue, alpha).uv(u2, 1).endVertex();
                //tessellator.draw();
                //RenderSystem.translated(-(posX - 2.1 + frame + off), 0, -posZ);

            }
        }
        //RenderSystem.scale(1 / scale, 1, 1);
    }

    @Override
    public void render(ControllerTile te, float p_225616_2_, PoseStack matrixStack, MultiBufferSource typeBuffer, int p_225616_5_, int p_225616_6_) {
        if (!te.isFormed()) return;
        matrixStack.pushPose();
        float frame = (te.getLevel().getGameTime() % 60) / 60f;
        //Color color = Color.getHSBColor((te.getWorld().getGameTime() % 360)/ 360f , 01f ,1f);
        //renderTop(stack, p_225616_4_.getBuffer(TYPE), te, frame,0,0, -0.6, te.getWidth() * 2);
        //this.renderCube(te, lvt_10_1_, 0.15F, lvt_11_1_, p_225616_4_.getBuffer((RenderType)RENDER_TYPES.get(0)));
        int x = 0;
        int y = 0;
        int z = 0;
        //RenderSystem.translated(x,y,z);
        if (te.isDisplayNameEnabled() && te.isActive()) {
            matrixStack.pushPose();
            String name = te.getLinkData().getName();
            matrixStack.translate(0.5, (double) 1.5f, 0.5D);
            matrixStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            matrixStack.scale(-0.025F, -0.025F, 0.025F);
            float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int j = (int) (f1 * 255.0F) << 24;

            Minecraft.getInstance().font.drawInBatch(name, -Minecraft.getInstance().font.width(name) / 2f, 0, -1, false, matrixStack.last().pose(), typeBuffer, false, j, 15728880);
            matrixStack.popPose();
        }
        BlockState blockState = te.getLevel().getBlockState(te.getBlockPos());
        // Apparently with Optifine, this code path gets run after the Portal block
        // has been destroyed, causing the BlockState to be an air block,
        // which is missing the below property, causing a crash. If this property is missing,
        // let's just silently fail.
        if (!blockState.hasProperty(ControllerBlock.FACING_HORIZONTAL)) {
            return;
        }
        Direction facing = blockState.getValue(ControllerBlock.FACING_HORIZONTAL);
        if (facing == Direction.SOUTH) {
            //RenderSystem.translated(1, 0, 1);
            //RenderSystem.rotatef(-180, 0, 1, 0);
            z = -1;
            x = -1;
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-180f));
        }
        if (facing == Direction.EAST) {
            //RenderSystem.translated(1, 0, 0);
            //RenderSystem.rotatef(-90, 0, 1, 0);
            z = -1;
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90f));
        }
        if (facing == Direction.WEST) {
            //RenderSystem.translated(0, 0, 1);
            x = -1;
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(90f));
            //RenderSystem.rotatef(90, 0, 1, 0);
        }
        if (facing == Direction.NORTH) {
            //x = 1;
        }
        //TOP
        //RenderSystem.translated(0.1 - te.getWidth() + 2, te.getHeight() - 5, 0);
        VertexConsumer buffer = typeBuffer.getBuffer(TYPE);
        renderTop(matrixStack, buffer, te, frame, -te.getWidth() + 2f + x, te.getHeight() + y - 1f, z, 0.4, te.getWidth() * 2, te.getColor());
        //RenderSystem.translated(-0.1 - (-te.getWidth() + 2), -(te.getHeight() - 5), 0);
        //RIGHT
        //RenderSystem.translated(3 - te.getWidth() + 2, 2.1, 0);
        //RenderSystem.rotatef(90, 0, 0, 1);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90f));
        renderTop(matrixStack, buffer, te, frame, 2 + y, te.getWidth() - 1 - x, z, 0.2, te.getHeight() - 1, te.getColor());
        matrixStack.mulPose(Vector3f.ZN.rotationDegrees(90f));
        //RenderSystem.rotatef(-90, 0, 0, 1);
        //RenderSystem.translated(-3 - (-te.getWidth() + 2), -2.1, 0);
        //LEFT
        //RenderSystem.translated(-2 + te.getWidth() - 2, te.getHeight() - 2.1, 0);
        //RenderSystem.rotatef(-90, 0, 0, 1);
        matrixStack.mulPose(Vector3f.ZN.rotationDegrees(90f));
        renderTop(matrixStack, buffer, te, frame, 2 - te.getHeight() + y, te.getWidth() + x, z, 0, te.getHeight() - 1, te.getColor());
        //RenderSystem.rotatef(90, 0, 0, 1);
        matrixStack.mulPose(Vector3f.ZN.rotationDegrees(90f));
        //RenderSystem.translated(2 - (te.getWidth() - 2), -(te.getHeight() - 2.1), 0);
        //BOTTOM
        //RenderSystem.translated(0.9 + te.getWidth() - 2, 5, 0);
        //RenderSystem.rotatef(-180, 0, 0, 1);
        //matrixStack.rotate(Vector3f.ZN.rotationDegrees(180f));
        renderTop(matrixStack, buffer, te, frame, -te.getWidth() - x + 1, -1 - y, z, 0.6, te.getWidth() * 2, te.getColor());
        //RenderSystem.rotatef(190, 0, 0, 1);180f));
        //RenderSystem.translated(-0.9 - (+te.getWidth() - 2), -5, 0);
        matrixStack.popPose();

    }
}
