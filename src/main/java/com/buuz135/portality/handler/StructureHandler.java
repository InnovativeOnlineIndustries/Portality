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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

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
        Direction facing = this.controller.getLevel().getBlockState(this.controller.getBlockPos()).getValue(ControllerBlock.FACING_HORIZONTAL);
        modules.clear();
        if (!checkFramesInTheBox(this.controller.getBlockPos().relative(facing.getClockWise(), width), this.controller.getBlockPos().relative(facing.getCounterClockWise(), width).relative(facing.getOpposite(), length - 1), false)) { //BOTTOM
            return false;
        }
        if (!checkFramesInTheBox(this.controller.getBlockPos().relative(facing.getClockWise(), width).relative(Direction.UP, height - 1), this.controller.getBlockPos().relative(facing.getCounterClockWise(), width).relative(facing.getOpposite(), length - 1).relative(Direction.UP, height - 1), false)) { //TOP
            return false;
        }
        if (!checkFramesInTheBox(this.controller.getBlockPos().relative(facing.getClockWise(), width).relative(Direction.UP, 1), this.controller.getBlockPos().relative(facing.getClockWise(), width).relative(Direction.UP, height - 2).relative(facing.getOpposite(), length - 1), false)) { //LEFT
            return false;
        }
        if (!checkFramesInTheBox(this.controller.getBlockPos().relative(facing.getCounterClockWise(), width).relative(Direction.UP, 1), this.controller.getBlockPos().relative(facing.getCounterClockWise(), width).relative(Direction.UP, height - 2).relative(facing.getOpposite(), length - 1), false)) { //LEFT
            return false;
        }
        checkFramesInTheBox(this.controller.getBlockPos().relative(facing.getClockWise(), width), this.controller.getBlockPos().relative(facing.getCounterClockWise(), width).relative(facing.getOpposite(), length - 1), true);
        checkFramesInTheBox(this.controller.getBlockPos().relative(facing.getClockWise(), width).relative(Direction.UP, height - 1), this.controller.getBlockPos().relative(facing.getCounterClockWise(), width).relative(facing.getOpposite(), length - 1).relative(Direction.UP, height - 1), true);
        checkFramesInTheBox(this.controller.getBlockPos().relative(facing.getClockWise(), width).relative(Direction.UP, 1), this.controller.getBlockPos().relative(facing.getClockWise(), width).relative(Direction.UP, height - 2).relative(facing.getOpposite(), length - 1), true);
        checkFramesInTheBox(this.controller.getBlockPos().relative(facing.getCounterClockWise(), width).relative(Direction.UP, 1), this.controller.getBlockPos().relative(facing.getCounterClockWise(), width).relative(Direction.UP, height - 2).relative(facing.getOpposite(), length - 1), true);
        return true;
    }

    public boolean checkFramesInTheBox(BlockPos point1, BlockPos point2, boolean save) {
        for (BlockPos blockPos : BlockPos.betweenClosed(point1, point2)) {
            if (!blockPos.equals(this.controller.getBlockPos()) && !isValidFrame(blockPos)) {
                return false;
            } else if (save) {
                frameBlocks.add(blockPos.immutable());
                if (this.controller.getLevel().getBlockState(blockPos).getBlock() instanceof IPortalModule) {
                    modules.add(blockPos.immutable());
                }
                BlockEntity entity = this.controller.getLevel().getBlockEntity(blockPos);
                if (entity instanceof FrameTile) {
                    ((FrameTile) entity).setControllerPos(this.controller.getBlockPos());
                    ((FrameTile<?>) entity).setColor(this.controller.getColor());
                    entity.setChanged();
                }
            }
        }
        return true;
    }

    private void checkPortalSize() {
        Direction controllerFacing = this.controller.getLevel().getBlockState(this.controller.getBlockPos()).getValue(ControllerBlock.FACING_HORIZONTAL);
        if (controllerFacing.getAxis().isVertical()) return;
        //Checking width
        Direction widthFacing = controllerFacing.getClockWise();
        int width = 1;
        while (isValidFrame(this.controller.getBlockPos().relative(widthFacing, width)) && !isValidFrame(this.controller.getBlockPos().relative(widthFacing, width).relative(Direction.UP)) && width <= PortalityConfig.MAX_PORTAL_WIDTH) {
            ++width;
        }
        //Checking height
        int height = 1;
        while (isValidFrame(this.controller.getBlockPos().relative(widthFacing, width).relative(Direction.UP, height)) && height <= PortalityConfig.MAX_PORTAL_HEIGHT) {
            ++height;
        }
        Direction lengthChecking = controllerFacing.getOpposite();
        int length = 1;
        while (isValidFrame(this.controller.getBlockPos().relative(lengthChecking, length)) && length <= PortalityConfig.MAX_PORTAL_LENGTH) {
            ++length;
        }
        this.width = width;
        this.height = height;
        this.length = length;
    }

    private boolean isValidFrame(BlockPos pos) {
        return this.controller.getLevel().getBlockEntity(pos) instanceof FrameTile && (((FrameTile) this.controller.getLevel().getBlockEntity(pos)).getControllerPos() == null
                || ((FrameTile) this.controller.getLevel().getBlockEntity(pos)).getControllerPos().equals(this.controller.getBlockPos()));
    }

    public void cancelFrameBlocks() {
        for (BlockPos frameBlock : frameBlocks) {
            BlockEntity entity = this.controller.getLevel().getBlockEntity(frameBlock);
            if (entity instanceof FrameTile) {
                ((FrameTile) entity).setControllerPos(null);
                entity.setChanged();
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

    public List<BlockPos> getFrameBlocks() {
        return frameBlocks;
    }
}
