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
package com.buuz135.portality.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class PortalLinkData {

    private ResourceKey<Level> dimension;
    private BlockPos pos;
    private boolean caller;
    private String name;
    private boolean token;

    public PortalLinkData(ResourceKey<Level> dimension, BlockPos pos, boolean caller, String name, boolean token) {
        this.dimension = dimension;
        this.pos = pos;
        this.caller = caller;
        this.name = name;
        this.token = token;
    }

    public static PortalLinkData readFromNBT(CompoundTag compound) {
        return new PortalLinkData(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compound.getString("Dimension"))), BlockPos.of(compound.getLong("Position")), compound.getBoolean("Caller"), compound.getString("Name"), compound.getBoolean("Token"));
    }

    public ResourceKey<Level> getDimension() {
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

    public boolean isToken() {
        return token;
    }

    public CompoundTag writeToNBT() {
        CompoundTag tagCompound = new CompoundTag();
        tagCompound.putString("Dimension", dimension.location().toString());
        tagCompound.putLong("Position", pos.asLong());
        tagCompound.putBoolean("Caller", caller);
        tagCompound.putString("Name", name);
        tagCompound.putBoolean("Token", token);
        return tagCompound;
    }

    public enum PortalCallType {
        NORMAL,
        SINGLE,
        FORCE;
    }
}
