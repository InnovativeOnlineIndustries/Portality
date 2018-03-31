package com.buuz135.portality.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class PortalInformation {

    private UUID id;
    private UUID owner;
    private String name;
    private boolean isActive;
    private boolean isPrivate;
    private int dimension;
    private BlockPos location;

    public PortalInformation(UUID id, UUID owner, boolean isActive, boolean isPrivate, int dimension, BlockPos location, String name) {
        this.id = id;
        this.owner = owner;
        this.isActive = isActive;
        this.isPrivate = isPrivate;
        this.dimension = dimension;
        this.location = location;
        this.name = name;
    }

    public static PortalInformation readFromNBT(NBTTagCompound info) {
        return new PortalInformation(info.getUniqueId("ID"), info.getUniqueId("Owner"), info.getBoolean("Active"), info.getBoolean("Private"),
                info.getInteger("Dimension"), BlockPos.fromLong(info.getLong("Position")), info.getString("Name"));
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

    public int getDimension() {
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

    public NBTTagCompound writetoNBT() {
        NBTTagCompound infoTag = new NBTTagCompound();
        infoTag.setUniqueId("ID", getId());
        infoTag.setUniqueId("Owner", getOwner());
        infoTag.setBoolean("Active", isActive());
        infoTag.setBoolean("Private", isPrivate());
        infoTag.setInteger("Dimension", getDimension());
        infoTag.setLong("Position", getLocation().toLong());
        infoTag.setString("Name", getName());
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
