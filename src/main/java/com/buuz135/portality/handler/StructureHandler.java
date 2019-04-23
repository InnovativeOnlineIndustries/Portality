package com.buuz135.portality.handler;

import com.buuz135.portality.block.BlockController;
import com.buuz135.portality.block.module.IPortalModule;
import com.buuz135.portality.proxy.PortalityConfig;
import com.buuz135.portality.tile.TileController;
import com.buuz135.portality.tile.TileFrame;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class StructureHandler {

    private int length;
    private int width;
    private int height;
    private TileController controller;
    private List<BlockPos> modules;
    private List<BlockPos> frameBlocks;
    private boolean shouldCheckForStructure;

    public StructureHandler(TileController tileController) {
        this.modules = new ArrayList<>();
        this.frameBlocks = new ArrayList<>();
        this.length = width = height = 0;
        this.controller = tileController;
        this.shouldCheckForStructure = true;
    }

    public boolean checkArea() {
        checkPortalSize();
        if (length < 3) return false;
        EnumFacing facing = this.controller.getWorld().getBlockState(this.controller.getPos()).get(BlockController.FACING);
        modules.clear();
        if (!checkFramesInTheBox(this.controller.getPos().offset(facing.rotateY(), width), this.controller.getPos().offset(facing.rotateYCCW(), width).offset(facing.getOpposite(), length - 1), false)) { //BOTTOM
            return false;
        }
        if (!checkFramesInTheBox(this.controller.getPos().offset(facing.rotateY(), width).offset(EnumFacing.UP, height - 1), this.controller.getPos().offset(facing.rotateYCCW(), width).offset(facing.getOpposite(), length - 1).offset(EnumFacing.UP, height - 1), false)) { //TOP
            return false;
        }
        if (!checkFramesInTheBox(this.controller.getPos().offset(facing.rotateY(), width).offset(EnumFacing.UP, 1), this.controller.getPos().offset(facing.rotateY(), width).offset(EnumFacing.UP, height - 2).offset(facing.getOpposite(), length - 1), false)) { //LEFT
            return false;
        }
        if (!checkFramesInTheBox(this.controller.getPos().offset(facing.rotateYCCW(), width).offset(EnumFacing.UP, 1), this.controller.getPos().offset(facing.rotateYCCW(), width).offset(EnumFacing.UP, height - 2).offset(facing.getOpposite(), length - 1), false)) { //LEFT
            return false;
        }
        checkFramesInTheBox(this.controller.getPos().offset(facing.rotateY(), width), this.controller.getPos().offset(facing.rotateYCCW(), width).offset(facing.getOpposite(), length - 1), true);
        checkFramesInTheBox(this.controller.getPos().offset(facing.rotateY(), width).offset(EnumFacing.UP, height - 1), this.controller.getPos().offset(facing.rotateYCCW(), width).offset(facing.getOpposite(), length - 1).offset(EnumFacing.UP, height - 1), true);
        checkFramesInTheBox(this.controller.getPos().offset(facing.rotateY(), width).offset(EnumFacing.UP, 1), this.controller.getPos().offset(facing.rotateY(), width).offset(EnumFacing.UP, height - 2).offset(facing.getOpposite(), length - 1), true);
        checkFramesInTheBox(this.controller.getPos().offset(facing.rotateYCCW(), width).offset(EnumFacing.UP, 1), this.controller.getPos().offset(facing.rotateYCCW(), width).offset(EnumFacing.UP, height - 2).offset(facing.getOpposite(), length - 1), true);
        return true;
    }

    public boolean checkFramesInTheBox(BlockPos point1, BlockPos point2, boolean save) {
        for (BlockPos blockPos : BlockPos.getAllInBox(point1, point2)) {
            if (!blockPos.equals(this.controller.getPos()) && !isValidFrame(blockPos)) {
                return false;
            } else if (save) {
                frameBlocks.add(blockPos);
                if (this.controller.getWorld().getBlockState(blockPos).getBlock() instanceof IPortalModule) {
                    modules.add(blockPos);
                }
                TileEntity entity = this.controller.getWorld().getTileEntity(blockPos);
                if (entity instanceof TileFrame) {
                    ((TileFrame) entity).setControllerPos(this.controller.getPos());
                    entity.markDirty();
                }
            }
        }
        return true;
    }

    private void checkPortalSize() {
        EnumFacing controllerFacing = this.controller.getWorld().getBlockState(this.controller.getPos()).get(BlockController.FACING);
        if (controllerFacing.getAxis().isVertical()) return;
        //Checking width
        EnumFacing widthFacing = controllerFacing.rotateY();
        int width = 1;
        while (isValidFrame(this.controller.getPos().offset(widthFacing, width)) && !isValidFrame(this.controller.getPos().offset(widthFacing, width).offset(EnumFacing.UP)) && width <= PortalityConfig.COMMON.MAX_PORTAL_WIDTH.get()) {
            ++width;
        }
        //Checking height
        int height = 1;
        while (isValidFrame(this.controller.getPos().offset(widthFacing, width).offset(EnumFacing.UP, height)) && height <= PortalityConfig.COMMON.MAX_PORTAL_HEIGHT.get()) {
            ++height;
        }
        EnumFacing lengthChecking = controllerFacing.getOpposite();
        int length = 1;
        while (isValidFrame(this.controller.getPos().offset(lengthChecking, length)) && length <= PortalityConfig.COMMON.MAX_PORTAL_LENGTH.get()) {
            ++length;
        }
        this.width = width;
        this.height = height;
        this.length = length;
    }

    private boolean isValidFrame(BlockPos pos) {
        return this.controller.getWorld().getTileEntity(pos) instanceof TileFrame && (((TileFrame) this.controller.getWorld().getTileEntity(pos)).getControllerPos() == null
                || ((TileFrame) this.controller.getWorld().getTileEntity(pos)).getControllerPos().equals(this.controller.getPos()));
    }

    public void cancelFrameBlocks() {
        for (BlockPos frameBlock : frameBlocks) {
            TileEntity entity = this.controller.getWorld().getTileEntity(frameBlock);
            if (entity instanceof TileFrame) {
                ((TileFrame) entity).setControllerPos(null);
                entity.markDirty();
            }
        }
        frameBlocks.clear();
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean shouldCheckForStructure() {
        return shouldCheckForStructure;
    }

    public void setShouldCheckForStructure(boolean shouldCheckForStructure) {
        this.shouldCheckForStructure = shouldCheckForStructure;
    }

    public List<BlockPos> getModules() {
        return modules;
    }
}
