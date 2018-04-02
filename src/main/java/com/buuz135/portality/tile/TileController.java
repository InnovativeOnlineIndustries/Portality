package com.buuz135.portality.tile;

import com.buuz135.portality.block.BlockController;
import com.buuz135.portality.block.BlockFrame;
import com.buuz135.portality.data.PortalDataManager;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.data.PortalLinkData;
import com.buuz135.portality.handler.CustomEnergyStorageHandler;
import com.buuz135.portality.handler.TeleportHandler;
import com.buuz135.portality.util.BlockPosUtils;
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
import java.util.List;
import java.util.UUID;

public class TileController extends TileBase implements ITickable {

    private static String NBT_FORMED = "Formed";
    private static String NBT_TICK = "Tick";
    private static String NBT_LENGTH = "Length";
    private static String NBT_PORTAL = "Portal";
    private static String NBT_LINK = "Link";
    private static String NBT_ENERGY = "Energy";

    private boolean isFormed;
    private int tick;
    private int length;
    private PortalInformation information;
    private PortalLinkData linkData;
    private CustomEnergyStorageHandler energy;

    private TeleportHandler teleportHandler;

    public TileController() {
        this.length = 0;
        this.tick = 0;
        this.isFormed = false;
        this.teleportHandler = new TeleportHandler(this);
        this.energy = new CustomEnergyStorageHandler(100000, 2000, 0, 0);
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
        if (isActive()) {
            energy.extractEnergyInternal((linkData.isCaller() ? 2 : 1) * length, false);
            if (energy.getEnergyStored() == 0) {
                closeLink();
            }
        }
        ++tick;
        if (tick >= 10) {
            tick = 0;
            length = checkArea();
            getPortalInfo();
            markForUpdate();
            if (linkData != null) {
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
        super.readFromNBT(compound);
    }

    private int checkArea() {
        BlockPos center = this.pos.offset(EnumFacing.UP, 2);
        EnumFacing facing = world.getBlockState(this.pos).getValue(BlockController.FACING);
        boolean isSouthNorth = facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH;
        int length = 0;
        while (true) {
            AxisAlignedBB box = new AxisAlignedBB(center.getX() + (isSouthNorth ? 2 : 0), center.getY() + 2, center.getZ() + (!isSouthNorth ? 2 : 0),
                    center.getX() + (isSouthNorth ? -2 : 0), center.getY() - 2, center.getZ() + (!isSouthNorth ? -2 : 0));
            List<BlockPos> blocks = BlockPosUtils.getBlockPosInAABB(box);
            BlockPos finalCenter = center;
            blocks.removeIf(pos1 -> BlockPosUtils.getBlockPosInAABB(new AxisAlignedBB(finalCenter.getX() + (isSouthNorth ? 1 : 0), finalCenter.getY() + 1, finalCenter.getZ() + (!isSouthNorth ? 1 : 0),
                    finalCenter.getX() + (isSouthNorth ? -1 : 0), finalCenter.getY() - 1, finalCenter.getZ() + (!isSouthNorth ? -1 : 0))).contains(pos1));
            for (BlockPos pos : blocks) {
                if (length == 0 && (pos.getX() == center.getX() && pos.getY() == center.getY() - 2 && pos.getZ() == center.getZ()) && this.world.getBlockState(pos).getBlock() instanceof BlockController)
                    continue;
                if (this.world.getBlockState(pos).getBlock() instanceof BlockFrame) continue;
                return length;
            }
            ++length;
            if (length > 16) return length;
            center = center.offset(facing.getOpposite());
        }
    }

    public AxisAlignedBB getPortalArea() {
        BlockPos center = this.pos.offset(EnumFacing.UP, 2);
        EnumFacing facing = world.getBlockState(this.pos).getValue(BlockController.FACING);
        boolean isSouthNorth = facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH;
        BlockPos corner1 = new BlockPos(center.getX() + (isSouthNorth ? 2 : 0), center.getY() + 2, center.getZ() + (!isSouthNorth ? 2 : 0)).offset(facing.getOpposite(), length);
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

    public ItemStack getDisplay() {
        if (information != null) return information.getDisplay();
        return ItemStack.EMPTY;
    }

    public void setDisplay(ItemStack display) {
        PortalDataManager.setPortalDisplay(this.world, this.pos, display);
        getPortalInfo();
        markForUpdate();
    }

    public void linkTo(PortalLinkData data, PortalLinkData.PortalCallType type) {
        if (type == PortalLinkData.PortalCallType.FORCE) {
            closeLink();
        }
        if (linkData != null) return;
        if (data.isCaller()) {
            World world = this.world.getMinecraftServer().getWorld(data.getDimension());
            TileEntity entity = world.getTileEntity(data.getPos());
            if (entity instanceof TileController) {
                data.setName(((TileController) entity).getName());
                ((TileController) entity).linkTo(new PortalLinkData(this.world.provider.getDimension(), this.pos, false, this.getName()), type);
                int power = 50000;
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
    }

    public boolean isActive() {
        if (information != null) return information.isActive();
        return false;
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

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) return (T) energy;
        return super.getCapability(capability, facing);
    }
}
