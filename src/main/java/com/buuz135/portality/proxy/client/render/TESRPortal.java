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
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;

public class TESRPortal implements BlockEntityRenderer<ControllerTile> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Portality.MOD_ID, "textures/block/portal_render.png");
    private static final RenderType TYPE = createRenderType();
    private static final int HOLOGRAM_ALPHA = 120;

    public TESRPortal(BlockEntityRendererProvider.Context context) {
    }

    private static RenderType createRenderType() {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, true))
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader))
                .setTextureState(new RenderStateShard.TextureStateShard(TEXTURE, false, false))
                .setTransparencyState(new RenderStateShard.TransparencyStateShard("portality_additive_transparency", () -> {
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                }, () -> {
                    RenderSystem.disableBlend();
                    RenderSystem.defaultBlendFunc();
                }))
                .setCullState(new RenderStateShard.CullStateShard(false))
                .createCompositeState(true);
        return RenderType.create("portality_portal_render", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 256, false, false, state);
    }

    @Override
    public boolean shouldRenderOffScreen(ControllerTile tile) {
        return tile.isFormed();
    }

    @Override
    public void render(ControllerTile tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!tile.isFormed()) {
            return;
        }

        renderLinkedWorld(tile, poseStack, bufferSource, packedOverlay);
        renderDisplayName(tile, poseStack, bufferSource);

        BlockState blockState = tile.getLevel().getBlockState(tile.getBlockPos());
        if (!blockState.hasProperty(ControllerBlock.FACING_HORIZONTAL)) {
            return;
        }

        poseStack.pushPose();
        float frame = (tile.getLevel().getGameTime() % 60 + partialTick) / 60F;
        int x = 0;
        int y = 0;
        int z = 0;

        Direction facing = blockState.getValue(ControllerBlock.FACING_HORIZONTAL);
        if (facing == Direction.SOUTH) {
            z = -1;
            x = -1;
            poseStack.mulPose(Axis.YP.rotationDegrees(-180F));
        } else if (facing == Direction.EAST) {
            z = -1;
            poseStack.mulPose(Axis.YP.rotationDegrees(-90F));
        } else if (facing == Direction.WEST) {
            x = -1;
            poseStack.mulPose(Axis.YP.rotationDegrees(90F));
        }

        VertexConsumer buffer = bufferSource.getBuffer(TYPE);
        int color = tile.getColor();

        renderStrip(poseStack, buffer, tile, frame, -tile.getWidth() + 2F + x, tile.getHeight() + y - 1F, z, tile.getWidth() * 2, color);

        poseStack.mulPose(Axis.ZP.rotationDegrees(90F));
        renderStrip(poseStack, buffer, tile, frame, 2 + y, tile.getWidth() - 1 - x, z, tile.getHeight() - 1, color);

        poseStack.mulPose(Axis.ZN.rotationDegrees(90F));
        poseStack.mulPose(Axis.ZN.rotationDegrees(90F));
        renderStrip(poseStack, buffer, tile, frame, 2 - tile.getHeight() + y, tile.getWidth() + x, z, tile.getHeight() - 1, color);

        poseStack.mulPose(Axis.ZN.rotationDegrees(90F));
        renderStrip(poseStack, buffer, tile, frame, -tile.getWidth() - x + 1, -1 - y, z, tile.getWidth() * 2, color);

        poseStack.popPose();
    }

    private void renderLinkedWorld(ControllerTile tile, PoseStack poseStack, MultiBufferSource bufferSource, int packedOverlay) {
        if (!tile.isActive() || tile.getLinkData() == null || tile.getLinkData().isToken()) {
            return;
        }

        Level level = tile.getLevel();
        if (level == null || !level.dimension().equals(tile.getLinkData().getDimension())) {
            return;
        }

        BlockState currentState = level.getBlockState(tile.getBlockPos());
        if (!currentState.hasProperty(ControllerBlock.FACING_HORIZONTAL)) {
            return;
        }

        BlockEntity targetEntity = level.getBlockEntity(tile.getLinkData().getPos());
        if (!(targetEntity instanceof ControllerTile linkedTile) || !linkedTile.isFormed()) {
            return;
        }

        BlockState linkedState = level.getBlockState(linkedTile.getBlockPos());
        if (!linkedState.hasProperty(ControllerBlock.FACING_HORIZONTAL)) {
            return;
        }

        Direction currentFacing = currentState.getValue(ControllerBlock.FACING_HORIZONTAL);
        Direction linkedFacing = linkedState.getValue(ControllerBlock.FACING_HORIZONTAL);
        Direction currentRight = currentFacing.getClockWise();
        Direction linkedRight = linkedFacing.getClockWise();
        int halfWidth = Math.min(tile.getWidth(), linkedTile.getWidth());
        int height = Math.min(tile.getHeight(), linkedTile.getHeight());
        int depth = tile.getLength();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        RandomSource random = RandomSource.create();

        for (int lateral = -halfWidth; lateral <= halfWidth; lateral++) {
            for (int vertical = 1; vertical < height; vertical++) {
                for (int forward = 1; forward <= depth; forward++) {
                    BlockPos remotePos = linkedTile.getBlockPos()
                            .relative(linkedRight, lateral)
                            .above(vertical)
                            .relative(linkedFacing, forward);
                    if (!level.hasChunkAt(remotePos)) {
                        continue;
                    }

                    BlockState remoteState = level.getBlockState(remotePos);
                    if (remoteState.isAir()) {
                        continue;
                    }

                    BlockPos localPos = tile.getBlockPos()
                            .relative(currentRight, -lateral)
                            .above(vertical)
                            .relative(currentFacing.getOpposite(), forward - 1);
                    poseStack.pushPose();
                    poseStack.translate(
                            localPos.getX() - tile.getBlockPos().getX(),
                            localPos.getY() - tile.getBlockPos().getY(),
                            localPos.getZ() - tile.getBlockPos().getZ()
                    );
                    VertexConsumer hologramBuffer = new HolographicVertexConsumer(bufferSource.getBuffer(RenderType.translucent()), tile.getColor(), HOLOGRAM_ALPHA);
                    for (RenderType renderType : ItemBlockRenderTypes.getRenderLayers(remoteState)) {
                        blockRenderer.renderBatched(remoteState, remotePos, level, poseStack, hologramBuffer, true, random, ModelData.EMPTY, renderType);
                    }
                    poseStack.popPose();
                }
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox(ControllerTile blockEntity) {
        return blockEntity.getRenderBoundingBox();
    }

    private void renderDisplayName(ControllerTile tile, PoseStack poseStack, MultiBufferSource bufferSource) {
        if (!tile.isDisplayNameEnabled() || !tile.isActive() || tile.getLinkData() == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        String name = tile.getLinkData().getName();
        Font font = Minecraft.getInstance().font;
        float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;
        font.drawInBatch(Component.literal(name), -font.width(name) / 2F, 0, -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, backgroundColor, 15728880);
        poseStack.popPose();
    }

    private void renderStrip(PoseStack poseStack, VertexConsumer buffer, ControllerTile tile, float frame, float xTrans, float yTrans, float zTrans, int width, int color) {
        float red = FastColor.ARGB32.red(color) / 255F;
        float green = FastColor.ARGB32.green(color) / 255F;
        float blue = FastColor.ARGB32.blue(color) / 255F;
        int r = (int) (red * 255);
        int g = (int) (green * 255);
        int b = (int) (blue * 255);
        float y = 3.999F;
        float off = 4 - y;
        Matrix4f matrix = poseStack.last().pose();

        for (int posX = 0; posX < width; ++posX) {
            for (int posZ = 0; posZ < tile.getLength(); ++posZ) {
                float pX1 = 1;
                float u1 = 1;
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
                    u1 = 1 - frame;
                }

                float xOffset = posX - 2F + frame + off + xTrans;
                float yOffset = yTrans - off;
                float zOffset = posZ + zTrans;

                addVertex(buffer, matrix, pX2 + xOffset, yOffset, zOffset, r, g, b, u2, 0);
                addVertex(buffer, matrix, pX1 + xOffset, yOffset, zOffset, r, g, b, u1, 0);
                addVertex(buffer, matrix, pX1 + xOffset, yOffset, 1 + zOffset, r, g, b, u1, 1);
                addVertex(buffer, matrix, pX2 + xOffset, yOffset, 1 + zOffset, r, g, b, u2, 1);
            }
        }
    }

    private void addVertex(VertexConsumer buffer, Matrix4f matrix, float x, float y, float z, int red, int green, int blue, float u, float v) {
        buffer.addVertex(matrix, x, y, z).setColor(red, green, blue, 255).setUv(u, v);
    }

    private static class HolographicVertexConsumer implements VertexConsumer {

        private final VertexConsumer delegate;
        private final int tintRed;
        private final int tintGreen;
        private final int tintBlue;
        private final int alpha;

        private HolographicVertexConsumer(VertexConsumer delegate, int tint, int alpha) {
            this.delegate = delegate;
            this.tintRed = Math.max(80, FastColor.ARGB32.red(tint));
            this.tintGreen = Math.max(180, FastColor.ARGB32.green(tint));
            this.tintBlue = Math.max(220, FastColor.ARGB32.blue(tint));
            this.alpha = alpha;
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            delegate.addVertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer setColor(int red, int green, int blue, int alpha) {
            delegate.setColor(blend(red, tintRed), blend(green, tintGreen), blend(blue, tintBlue), Math.min(alpha, this.alpha));
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            delegate.setUv(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            delegate.setUv1(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            delegate.setUv2(u, v);
            return this;
        }

        @Override
        public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
            delegate.setNormal(normalX, normalY, normalZ);
            return this;
        }

        private int blend(int base, int tint) {
            return Math.min(255, (base + tint * 2) / 3);
        }
    }
}
