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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.UUID;

public class PortalInformation {

    private UUID id;
    private UUID owner;
    private String name;
    private boolean isActive;
    private boolean isPrivate;
    private RegistryKey<World> dimension;
    private BlockPos location;
    private ItemStack display;
    private boolean interdimensional;
    private boolean isToken;

    public PortalInformation(UUID id, UUID owner, boolean isActive, boolean isPrivate, RegistryKey<World> dimension, BlockPos location, String name, ItemStack display, boolean interdimensional) {
        this.id = id;
        this.owner = owner;
        this.isActive = isActive;
        this.isPrivate = isPrivate;
        this.dimension = dimension;
        this.location = location;
        this.name = name;
        this.display = display;
        this.interdimensional = interdimensional;
        this.isToken = false;
    }

    public static PortalInformation readFromNBT(CompoundNBT info) {
        return new PortalInformation(info.getUniqueId("ID"), info.getUniqueId("Owner"), info.getBoolean("Active"), info.getBoolean("Private"),
                RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(info.getString("Dimension"))), BlockPos.fromLong(info.getLong("Position")), info.getString("Name"), ItemStack.read(info.getCompound("Display")), info.getBoolean("Interdimensional")).setToken(info.getBoolean("Token"));
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public RegistryKey<World> getDimension() {
        return dimension;
    }

    public BlockPos getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemStack getDisplay() {
        return display;
    }

    public void setDisplay(ItemStack display) {
        this.display = display;
    }

    public boolean isInterdimensional() {
        return interdimensional;
    }

    public void setInterdimensional(boolean interdimensional) {
        this.interdimensional = interdimensional;
    }

    public boolean isToken() {
        return isToken;
    }

    public PortalInformation setToken(boolean token) {
        isToken = token;
        return this;
    }

    public CompoundNBT writetoNBT() {
        CompoundNBT infoTag = new CompoundNBT();
        infoTag.putUniqueId("ID", getId());
        infoTag.putUniqueId("Owner", getOwner());
        infoTag.putBoolean("Active", isActive());
        infoTag.putBoolean("Private", isPrivate());
        infoTag.putString("Dimension", getDimension().getLocation().toString());
        infoTag.putLong("Position", getLocation().toLong());
        infoTag.putString("Name", getName());
        infoTag.put("Display", display.serializeNBT());
        infoTag.putBoolean("Interdimensional", interdimensional);
        infoTag.putBoolean("Token", isToken());
        return infoTag;
    }

    @Override
    public String toString() {
        return "PortalInformation{" +
                "id=" + id +
                ", owner=" + owner +
                ", isActive=" + isActive +
                ", isPrivate=" + isPrivate +
                ", dimension=" + dimension +
                ", location=" + location +
                '}';
    }
}
