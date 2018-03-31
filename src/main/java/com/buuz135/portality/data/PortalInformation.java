package com.buuz135.portality.data;

import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class PortalInformation {

    private UUID id;
    private UUID owner;
    private boolean isActive;
    private boolean isPrivate;
    private int dimension;
    private BlockPos location;

    public PortalInformation(UUID id, UUID owner, boolean isActive, boolean isPrivate, int dimension, BlockPos location) {
        this.id = id;
        this.owner = owner;
        this.isActive = isActive;
        this.isPrivate = isPrivate;
        this.dimension = dimension;
        this.location = location;
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
