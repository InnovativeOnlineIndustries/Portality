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
package com.buuz135.portality.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class PortalLinkData {

    private ResourceLocation dimension;
    private BlockPos pos;
    private boolean caller;
    private String name;

    public PortalLinkData(ResourceLocation dimension, BlockPos pos, boolean caller) {
        this(dimension, pos, caller, "");
    }

    public PortalLinkData(ResourceLocation dimension, BlockPos pos, boolean caller, String name) {
        this.dimension = dimension;
        this.pos = pos;
        this.caller = caller;
        this.name = name;
    }

    public static PortalLinkData readFromNBT(NBTTagCompound compound) {
        return new PortalLinkData(new ResourceLocation(compound.getString("Dimension")), BlockPos.fromLong(compound.getLong("Position")), compound.getBoolean("Caller"), compound.getString("Name"));
    }

    public ResourceLocation getDimension() {
        return dimension;
    }

    public BlockPos getPos() {
        return pos;
    }

    public boolean isCaller() {
        return caller;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setString("Dimension", dimension.toString());
        tagCompound.setLong("Position", pos.toLong());
        tagCompound.setBoolean("Caller", caller);
        tagCompound.setString("Name", name);
        return tagCompound;
    }

    public enum PortalCallType {
        NORMAL,
        SINGLE,
        FORCE;
    }
}
