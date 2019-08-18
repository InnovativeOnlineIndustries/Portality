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
package com.buuz135.portality.tile;

import com.hrznstudio.titanium.block.BlockTileBase;
import com.hrznstudio.titanium.block.tile.TileActive;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class TileFrame extends TileActive {

    private BlockPos controllerPos;

    public TileFrame(BlockTileBase base) {
        super(base);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);
        if (controllerPos != null) {
            compound.putInt("X", controllerPos.getX());
            compound.putInt("Y", controllerPos.getY());
            compound.putInt("Z", controllerPos.getZ());
        }
        return compound;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        if (compound.contains("X")) {
            controllerPos = new BlockPos(compound.getInt("X"), compound.getInt("Y"), compound.getInt("Z"));
        }
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
    }
}
