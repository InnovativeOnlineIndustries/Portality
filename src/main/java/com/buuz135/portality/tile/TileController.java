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
package com.buuz135.portality.tile;

import com.buuz135.portality.block.BlockController;
import com.buuz135.portality.block.module.IPortalModule;
import com.buuz135.portality.data.PortalDataManager;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.data.PortalLinkData;
import com.buuz135.portality.handler.ChunkLoaderHandler;
import com.buuz135.portality.handler.CustomEnergyStorageHandler;
import com.buuz135.portality.handler.StructureHandler;
import com.buuz135.portality.handler.TeleportHandler;
import com.buuz135.portality.proxy.PortalityConfig;
import com.buuz135.portality.proxy.PortalitySoundHandler;
import com.buuz135.portality.proxy.client.TickeableSound;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TileController extends TileBase implements ITickable {

    private static String NBT_FORMED = "Formed";
    private static String NBT_LENGTH = "Length";
    private static String NBT_WIDTH = "Width";
    private static String NBT_HEIGHT = "Height";
    private static String NBT_PORTAL = "Portal";
    private static String NBT_LINK = "Link";
    private static String NBT_DISPLAY = "Display";
    private static String NBT_ONCE = "Once";

    private boolean isFormed;
    private boolean onceCall;
    private boolean display;
    private PortalInformation information;
    private PortalLinkData linkData;
    private CustomEnergyStorageHandler energy;

    @SideOnly(Side.CLIENT)
    private TickeableSound sound;

    private TeleportHandler teleportHandler;
    private StructureHandler structureHandler;

    public TileController() {
        this.isFormed = false;
        this.onceCall = false;
        this.display = true;
        this.teleportHandler = new TeleportHandler(this);
        this.structureHandler = new StructureHandler(this);
        this.energy = new CustomEnergyStorageHandler(PortalityConfig.MAX_PORTAL_POWER, PortalityConfig.MAX_PORTAL_POWER_IN, 0, 0);
    }

    @Override
    public void update() {
        if (isActive()) {
            teleportHandler.tick();
            if (linkData != null) {
                for (Entity entity : this.world.getEntitiesWithinAABB(Entity.class, getPortalArea())) {
                    teleportHandler.addEntityToTeleport(entity, linkData);
                }
            }
            if (!world.isRemote) {
                workModules();
            }
        }
        if (world.isRemote) {
            tickSound();
            return;
        }
        if (isActive() && linkData != null) {
            energy.extractEnergyInternal((linkData.isCaller() ? 2 : 1) * structureHandler.getLength() * PortalityConfig.POWER_PORTAL_TICK, false);
            if (energy.getEnergyStored() == 0 || !isFormed) {
                closeLink();
            }
        }
        if (this.world.getTotalWorldTime() % 10 == 0) {
            if (structureHandler.shouldCheckForStructure()) {
                this.isFormed = structureHandler.checkArea();
                if (this.isFormed) {
                    structureHandler.setShouldCheckForStructure(false);
                } else {
                    structureHandler.cancelFrameBlocks();
                }
            }
            if (isFormed) {
                getPortalInfo();
                if (linkData != null) {
                    ChunkLoaderHandler.addPortalAsChunkloader(this);
                    TileEntity tileEntity = this.world.getMinecraftServer().getWorld(linkData.getDimension()).getTileEntity(linkData.getPos());
                    if (!(tileEntity instanceof TileController) || ((TileController) tileEntity).getLinkData() == null || ((TileController) tileEntity).getLinkData().getDimension() != this.world.provider.getDimension() || !((TileController) tileEntity).getLinkData().getPos().equals(this.pos)) {
                        this.closeLink();
                    }
                }
            }
            markForUpdate();
        }
    }

    @SideOnly(Side.CLIENT)
    private void tickSound() {
        if (isActive()) {
            if (sound == null) {
                FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound = new TickeableSound(this.world, this.pos, PortalitySoundHandler.PORTAL));
            } else {
                sound.increase();
            }
        } else if (sound != null) {
            if (sound.getPitch() > 0) {
                sound.decrease();
            } else {
                sound.setDone();
                sound = null;
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setBoolean(NBT_FORMED, isFormed);
        compound.setInteger(NBT_LENGTH, structureHandler.getLength());
        compound.setInteger(NBT_WIDTH, structureHandler.getWidth());
        compound.setInteger(NBT_HEIGHT, structureHandler.getHeight());
        energy.writeToNBT(compound);
        compound.setBoolean(NBT_ONCE, onceCall);
        compound.setBoolean(NBT_DISPLAY, display);
        if (information != null) compound.setTag(NBT_PORTAL, information.writetoNBT());
        if (linkData != null) compound.setTag(NBT_LINK, linkData.writeToNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        isFormed = compound.getBoolean(NBT_FORMED);
        structureHandler.setLength(compound.getInteger(NBT_LENGTH));
        structureHandler.setWidth(compound.getInteger(NBT_WIDTH));
        structureHandler.setHeight(compound.getInteger(NBT_HEIGHT));
        if (compound.hasKey(NBT_PORTAL))
            information = PortalInformation.readFromNBT(compound.getCompoundTag(NBT_PORTAL));
        if (compound.hasKey(NBT_LINK))
            linkData = PortalLinkData.readFromNBT(compound.getCompoundTag(NBT_LINK));
        energy.readFromNBT(compound);
        onceCall = compound.getBoolean(NBT_ONCE);
        display = compound.getBoolean(NBT_DISPLAY);
        super.readFromNBT(compound);
    }

    public void breakController() {
        closeLink();
        structureHandler.cancelFrameBlocks();
    }

    private void workModules() {
        boolean interdimensional = false;
        for (BlockPos pos : structureHandler.getModules()) {
            Block block = this.world.getBlockState(pos).getBlock();
            if (block instanceof IPortalModule) {
                if (((IPortalModule) block).allowsInterdimensionalTravel()) interdimensional = true;
                ((IPortalModule) block).work(this, pos);
            }
        }
        PortalDataManager.setPortalInterdimensional(this.world, this.pos, interdimensional);
    }

    public AxisAlignedBB getPortalArea() {
        if (!(world.getBlockState(this.pos).getBlock() instanceof BlockController))
            return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
        EnumFacing facing = world.getBlockState(this.pos).getValue(BlockController.FACING);
        BlockPos corner1 = this.pos.offset(facing.rotateY(), structureHandler.getWidth() - 1).offset(EnumFacing.UP);
        BlockPos corner2 = this.pos.offset(facing.rotateY(), -structureHandler.getWidth() + 1).offset(EnumFacing.UP, structureHandler.getHeight() - 1).offset(facing.getOpposite(), structureHandler.getLength() - 1);
        return new AxisAlignedBB(corner1, corner2);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return getPortalArea();
    }

    private void getPortalInfo() {
        information = PortalDataManager.getInfoFromPos(this.world, this.pos);
    }

    public void togglePrivacy() {
        PortalDataManager.setPortalPrivacy(this.world, this.pos, !information.isPrivate());
        getPortalInfo();
        markForUpdate();
    }

    public boolean isFormed() {
        return isFormed;
    }

    public boolean isPrivate() {
        return information != null && information.isPrivate();
    }

    public UUID getOwner() {
        if (information != null) return information.getOwner();
        return null;
    }

    public UUID getID() {
        if (information != null) return information.getId();
        return null;
    }

    public String getName() {
        if (information != null) return information.getName();
        return "";
    }

    public void setName(String name) {
        PortalDataManager.setPortalName(this.world, this.getPos(), name);
        getPortalInfo();
        markForUpdate();
    }

    public boolean isInterdimensional() {
        return information != null && information.isInterdimensional();
    }

    public ItemStack getDisplay() {
        if (information != null) return information.getDisplay();
        return ItemStack.EMPTY;
    }

    public void setDisplayNameEnabled(ItemStack display) {
        PortalDataManager.setPortalDisplay(this.world, this.pos, display);
        getPortalInfo();
        markForUpdate();
    }

    public void linkTo(PortalLinkData data, PortalLinkData.PortalCallType type) {
        if (type == PortalLinkData.PortalCallType.FORCE) {
            closeLink();
        }
        if (linkData != null) return;
        if (type == PortalLinkData.PortalCallType.SINGLE) onceCall = true;
        if (data.isCaller()) {
            World world = this.world.getMinecraftServer().getWorld(data.getDimension());
            TileEntity entity = world.getTileEntity(data.getPos());
            if (entity instanceof TileController) {
                data.setName(((TileController) entity).getName());
                ((TileController) entity).linkTo(new PortalLinkData(this.world.provider.getDimension(), this.pos, false, this.getName()), type);
                int power = PortalityConfig.PORTAL_POWER_OPEN_INTERDIMENSIONAL;
                if (entity.getWorld().equals(this.world)) {
                    power = (int) this.pos.getDistance(entity.getPos().getX(), entity.getPos().getZ(), entity.getPos().getY()) * structureHandler.getLength();
                }
                energy.extractEnergyInternal(power, false);
            }
        }
        PortalDataManager.setActiveStatus(this.world, this.pos, true);
        this.linkData = data;
    }

    public void closeLink() {
        if (linkData != null) {
            PortalDataManager.setActiveStatus(this.world, this.pos, false);
            World world = this.world.getMinecraftServer().getWorld(linkData.getDimension());
            TileEntity entity = world.getTileEntity(linkData.getPos());
            linkData = null;
            if (entity instanceof TileController) {
                ((TileController) entity).closeLink();
            }
        }
        ChunkLoaderHandler.removePortalAsChunkloader(this);
    }

    public boolean isActive() {
        return information != null && information.isActive();
    }

    public PortalLinkData getLinkData() {
        return linkData;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    public CustomEnergyStorageHandler getEnergy() {
        return energy;
    }

    public boolean isDisplayNameEnabled() {
        return display;
    }

    public void setDisplayNameEnabled(boolean display) {
        this.display = display;
    }

    public List<BlockPos> getModules() {
        return structureHandler.getModules();
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) return (T) energy;
        return super.getCapability(capability, facing);
    }

    public boolean teleportedEntity() {
        if (onceCall) {
            onceCall = false;
            closeLink();
            return true;
        }
        return false;
    }

    public boolean isShouldCheckForStructure() {
        return structureHandler.shouldCheckForStructure();
    }

    public void setShouldCheckForStructure(boolean shouldCheckForStructure) {
        structureHandler.setShouldCheckForStructure(shouldCheckForStructure);
    }

    public int getWidth() {
        return structureHandler.getWidth();
    }

    public int getHeight() {
        return structureHandler.getHeight();
    }

    public int getLength() {
        return structureHandler.getLength();
    }
}
