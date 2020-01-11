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
package com.buuz135.portality.handler;

import com.buuz135.portality.block.ControllerBlock;
import com.buuz135.portality.block.module.IPortalModule;
import com.buuz135.portality.proxy.PortalityConfig;
import com.buuz135.portality.tile.ControllerTile;
import com.buuz135.portality.tile.FrameTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class StructureHandler {

    private int length;
    private int width;
    private int height;
    private ControllerTile controller;
    private List<BlockPos> modules;
    private List<BlockPos> frameBlocks;
    private boolean shouldCheckForStructure;

    public StructureHandler(ControllerTile controllerTile) {
        this.modules = new ArrayList<>();
        this.frameBlocks = new ArrayList<>();
        this.length = width = height = 0;
        this.controller = controllerTile;
        this.shouldCheckForStructure = true;
    }

    public boolean checkArea() {
        checkPortalSize();
        if (length < 3) return false;
        Direction facing = this.controller.getWorld().getBlockState(this.controller.getPos()).get(ControllerBlock.FACING_HORIZONTAL);
        modules.clear();
        if (!checkFramesInTheBox(this.controller.getPos().offset(facing.rotateY(), width), this.controller.getPos().offset(facing.rotateYCCW(), width).offset(facing.getOpposite(), length - 1), false)) { //BOTTOM
            return false;
        }
        if (!checkFramesInTheBox(this.controller.getPos().offset(facing.rotateY(), width).offset(Direction.UP, height - 1), this.controller.getPos().offset(facing.rotateYCCW(), width).offset(facing.getOpposite(), length - 1).offset(Direction.UP, height - 1), false)) { //TOP
            return false;
        }
        if (!checkFramesInTheBox(this.controller.getPos().offset(facing.rotateY(), width).offset(Direction.UP, 1), this.controller.getPos().offset(facing.rotateY(), width).offset(Direction.UP, height - 2).offset(facing.getOpposite(), length - 1), false)) { //LEFT
            return false;
        }
        if (!checkFramesInTheBox(this.controller.getPos().offset(facing.rotateYCCW(), width).offset(Direction.UP, 1), this.controller.getPos().offset(facing.rotateYCCW(), width).offset(Direction.UP, height - 2).offset(facing.getOpposite(), length - 1), false)) { //LEFT
            return false;
        }
        checkFramesInTheBox(this.controller.getPos().offset(facing.rotateY(), width), this.controller.getPos().offset(facing.rotateYCCW(), width).offset(facing.getOpposite(), length - 1), true);
        checkFramesInTheBox(this.controller.getPos().offset(facing.rotateY(), width).offset(Direction.UP, height - 1), this.controller.getPos().offset(facing.rotateYCCW(), width).offset(facing.getOpposite(), length - 1).offset(Direction.UP, height - 1), true);
        checkFramesInTheBox(this.controller.getPos().offset(facing.rotateY(), width).offset(Direction.UP, 1), this.controller.getPos().offset(facing.rotateY(), width).offset(Direction.UP, height - 2).offset(facing.getOpposite(), length - 1), true);
        checkFramesInTheBox(this.controller.getPos().offset(facing.rotateYCCW(), width).offset(Direction.UP, 1), this.controller.getPos().offset(facing.rotateYCCW(), width).offset(Direction.UP, height - 2).offset(facing.getOpposite(), length - 1), true);
        return true;
    }

    public boolean checkFramesInTheBox(BlockPos point1, BlockPos point2, boolean save) {
        for (BlockPos blockPos : BlockPos.getAllInBoxMutable(point1, point2)) {
            if (!blockPos.equals(this.controller.getPos()) && !isValidFrame(blockPos)) {
                return false;
            } else if (save) {
                frameBlocks.add(blockPos.toImmutable());
                if (this.controller.getWorld().getBlockState(blockPos).getBlock() instanceof IPortalModule) {
                    modules.add(blockPos.toImmutable());
                }
                TileEntity entity = this.controller.getWorld().getTileEntity(blockPos);
                if (entity instanceof FrameTile) {
                    ((FrameTile) entity).setControllerPos(this.controller.getPos());
                    entity.markDirty();
                }
            }
        }
        return true;
    }

    private void checkPortalSize() {
        Direction controllerFacing = this.controller.getWorld().getBlockState(this.controller.getPos()).get(ControllerBlock.FACING_HORIZONTAL);
        if (controllerFacing.getAxis().isVertical()) return;
        //Checking width
        Direction widthFacing = controllerFacing.rotateY();
        int width = 1;
        while (isValidFrame(this.controller.getPos().offset(widthFacing, width)) && !isValidFrame(this.controller.getPos().offset(widthFacing, width).offset(Direction.UP)) && width <= PortalityConfig.MAX_PORTAL_WIDTH) {
            ++width;
        }
        //Checking height
        int height = 1;
        while (isValidFrame(this.controller.getPos().offset(widthFacing, width).offset(Direction.UP, height)) && height <= PortalityConfig.MAX_PORTAL_HEIGHT) {
            ++height;
        }
        Direction lengthChecking = controllerFacing.getOpposite();
        int length = 1;
        while (isValidFrame(this.controller.getPos().offset(lengthChecking, length)) && length <= PortalityConfig.MAX_PORTAL_LENGTH) {
            ++length;
        }
        this.width = width;
        this.height = height;
        this.length = length;
    }

    private boolean isValidFrame(BlockPos pos) {
        return this.controller.getWorld().getTileEntity(pos) instanceof FrameTile && (((FrameTile) this.controller.getWorld().getTileEntity(pos)).getControllerPos() == null
                || ((FrameTile) this.controller.getWorld().getTileEntity(pos)).getControllerPos().equals(this.controller.getPos()));
    }

    public void cancelFrameBlocks() {
        for (BlockPos frameBlock : frameBlocks) {
            TileEntity entity = this.controller.getWorld().getTileEntity(frameBlock);
            if (entity instanceof FrameTile) {
                ((FrameTile) entity).setControllerPos(null);
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
