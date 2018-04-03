package com.buuz135.portality.tile;

import com.buuz135.portality.block.BlockController;
import com.buuz135.portality.block.BlockFrame;
import com.buuz135.portality.block.module.IPortalModule;
import com.buuz135.portality.data.PortalDataManager;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.data.PortalLinkData;
import com.buuz135.portality.handler.ChunkLoaderHandler;
import com.buuz135.portality.handler.CustomEnergyStorageHandler;
import com.buuz135.portality.handler.TeleportHandler;
import com.buuz135.portality.proxy.PortalityConfig;
import com.buuz135.portality.util.BlockPosUtils;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TileController extends TileBase implements ITickable {


    private static String NBT_FORMED = "Formed";
    private static String NBT_TICK = "Tick";
    private static String NBT_LENGTH = "Length";
    private static String NBT_PORTAL = "Portal";
    private static String NBT_LINK = "Link";
    private static String NBT_DISPLAY = "Display";
    private static String NBT_ONCE = "Once";

    private boolean isFormed;
    private int tick;
    private int length;
    private boolean onceCall;
    private boolean display;
    private PortalInformation information;
    private PortalLinkData linkData;
    private CustomEnergyStorageHandler energy;

    private TeleportHandler teleportHandler;
    private List<BlockPos> modules;

    public TileController() {
        this.length = 0;
        this.tick = 0;
        this.isFormed = false;
        this.onceCall = false;
        this.display = true;
        this.teleportHandler = new TeleportHandler(this);
        this.modules = new ArrayList<>();
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
        }
        if (world.isRemote) return;
        if (isActive() && linkData != null) {
            energy.extractEnergyInternal((linkData.isCaller() ? 2 : 1) * length * PortalityConfig.POWER_PORTAL_TICK, false);
            if (energy.getEnergyStored() == 0) {
                closeLink();
            }
        }
        ++tick;
        if (tick >= 10) {
            tick = 0;
            length = checkArea();
            workModules();
            getPortalInfo();
            markForUpdate();
            if (linkData != null) {
                ChunkLoaderHandler.addPortalAsChunkloader(this);
                TileEntity tileEntity = this.world.getMinecraftServer().getWorld(linkData.getDimension()).getTileEntity(linkData.getPos());
                if (!(tileEntity instanceof TileController) || ((TileController) tileEntity).getLinkData().getDimension() != this.world.provider.getDimension() || !((TileController) tileEntity).getLinkData().getPos().equals(this.pos)) {
                    this.closeLink();
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setBoolean(NBT_FORMED, isFormed);
        compound.setInteger(NBT_TICK, tick);
        compound.setInteger(NBT_LENGTH, length);
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
        tick = compound.getInteger(NBT_TICK);
        length = compound.getInteger(NBT_LENGTH);
        if (compound.hasKey(NBT_PORTAL))
            information = PortalInformation.readFromNBT(compound.getCompoundTag(NBT_PORTAL));
        if (compound.hasKey(NBT_LINK))
            linkData = PortalLinkData.readFromNBT(compound.getCompoundTag(NBT_LINK));
        energy.readFromNBT(compound);
        onceCall = compound.getBoolean(NBT_ONCE);
        display = compound.getBoolean(NBT_DISPLAY);
        super.readFromNBT(compound);
    }

    private int checkArea() {
        BlockPos center = this.pos.offset(EnumFacing.UP, 2);
        EnumFacing facing = world.getBlockState(this.pos).getValue(BlockController.FACING);
        boolean isSouthNorth = facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH;
        int length = 0;
        modules.clear();
        while (true) {
            AxisAlignedBB box = new AxisAlignedBB(center.getX() + (isSouthNorth ? 2 : 0), center.getY() + 2, center.getZ() + (!isSouthNorth ? 2 : 0),
                    center.getX() + (isSouthNorth ? -2 : 0), center.getY() - 2, center.getZ() + (!isSouthNorth ? -2 : 0));
            List<BlockPos> blocks = BlockPosUtils.getBlockPosInAABB(box);
            BlockPos finalCenter = center;
            blocks.removeIf(pos1 -> BlockPosUtils.getBlockPosInAABB(new AxisAlignedBB(finalCenter.getX() + (isSouthNorth ? 1 : 0), finalCenter.getY() + 1, finalCenter.getZ() + (!isSouthNorth ? 1 : 0),
                    finalCenter.getX() + (isSouthNorth ? -1 : 0), finalCenter.getY() - 1, finalCenter.getZ() + (!isSouthNorth ? -1 : 0))).contains(pos1));
            for (BlockPos pos : blocks) {
                if (world.getBlockState(pos).getBlock() instanceof IPortalModule) {
                    modules.add(pos);
                    continue;
                }
                if (length == 0 && (pos.getX() == center.getX() && pos.getY() == center.getY() - 2 && pos.getZ() == center.getZ()) && this.world.getBlockState(pos).getBlock() instanceof BlockController)
                    continue;
                if (this.world.getBlockState(pos).getBlock() instanceof BlockFrame) continue;
                return length;
            }
            ++length;
            if (length > PortalityConfig.MAX_PORTAL_LENGTH) return length;
            center = center.offset(facing.getOpposite());
        }
    }

    private void workModules() {
        boolean interdimensional = false;
        for (BlockPos pos : modules) {
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
        BlockPos center = this.pos.offset(EnumFacing.UP, 2);
        EnumFacing facing = world.getBlockState(this.pos).getValue(BlockController.FACING);
        boolean isSouthNorth = facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH;
        BlockPos corner1 = new BlockPos(center.getX() + (isSouthNorth ? 2 : 0), center.getY() + 2, center.getZ() + (!isSouthNorth ? 2 : 0)).offset(facing.getOpposite(), length - 1);
        BlockPos corner2 = new BlockPos(center.getX() + (isSouthNorth ? -2 : 0), center.getY() - 2, center.getZ() + (!isSouthNorth ? -2 : 0));
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
        return length >= 3;
    }

    public int getTick() {
        return tick;
    }

    public int getLength() {
        return length;
    }

    public boolean isPrivate() {
        return information != null && information.isPrivate();
    }

    public UUID getOwner() {
        if (information != null) return information.getOwner();
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
                    power = (int) this.pos.getDistance(entity.getPos().getX(), entity.getPos().getZ(), entity.getPos().getY()) * length;
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
        return modules;
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
}
