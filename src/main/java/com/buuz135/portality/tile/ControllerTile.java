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

import com.buuz135.portality.block.ControllerBlock;
import com.buuz135.portality.block.module.IPortalModule;
import com.buuz135.portality.data.PortalDataManager;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.data.PortalLinkData;
import com.buuz135.portality.gui.ControllerScreen;
import com.buuz135.portality.gui.PortalsScreen;
import com.buuz135.portality.gui.RenameControllerScreen;
import com.buuz135.portality.gui.button.PortalSettingButton;
import com.buuz135.portality.gui.button.TextPortalButton;
import com.buuz135.portality.handler.ChunkLoaderHandler;
import com.buuz135.portality.handler.StructureHandler;
import com.buuz135.portality.handler.TeleportHandler;
import com.buuz135.portality.network.PortalNetworkMessage;
import com.buuz135.portality.proxy.CommonProxy;
import com.buuz135.portality.proxy.PortalityConfig;
import com.buuz135.portality.proxy.PortalitySoundHandler;
import com.buuz135.portality.proxy.client.TickeableSound;
import com.buuz135.portality.util.BlockPosUtils;
import com.hrznstudio.titanium.block.tile.PoweredTile;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class ControllerTile extends PoweredTile<ControllerTile> {

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

    @OnlyIn(Dist.CLIENT)
    private TickeableSound sound;

    private TeleportHandler teleportHandler;
    private StructureHandler structureHandler;

    public ControllerTile() {
        super(CommonProxy.BLOCK_CONTROLLER);
        this.isFormed = false;
        this.onceCall = false;
        this.display = true;
        this.teleportHandler = new TeleportHandler(this);
        this.structureHandler = new StructureHandler(this);

        this.addButton(new PortalSettingButton(-22, 12, () -> () -> {
            OpenGui.open(1, ControllerTile.this);
        }, new StateButtonInfo(0, PortalSettingButton.RENAME, "portality.display.change_name")) {
            @Override
            public int getState() {
                return 0;
            }
        }.setId(1));

        this.addButton(new PortalSettingButton(-22, 12 + 22, () -> () -> {
        }, new StateButtonInfo(0, PortalSettingButton.PUBLIC, "portality.display.make_private"), new StateButtonInfo(1, PortalSettingButton.PRIVATE, "portality.display.make_public")) {
            @Override
            public int getState() {
                return information != null && information.isPrivate() ? 1 : 0;
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            if (information.getOwner().equals(playerEntity.getUniqueID())) togglePrivacy();
        }).setId(2));

        this.addButton(new PortalSettingButton(-22, 12 + 22 * 2, () -> () -> {
        }, new StateButtonInfo(0, PortalSettingButton.NAME_SHOWN, "portality.display.hide_name"), new StateButtonInfo(1, PortalSettingButton.NAME_HIDDEN, "portality.display.show_name")) {
            @Override
            public int getState() {
                return display ? 0 : 1;
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            if (information.getOwner().equals(playerEntity.getUniqueID()))
                setDisplayNameEnabled(!isDisplayNameEnabled());
        }).setId(3));
        this.addButton(new TextPortalButton(5, 90, 80, 16, "portality.display.call_portal")
                .setClientConsumer(() -> screen -> {
                    OpenGui.open(2, ControllerTile.this);
                })
                .setId(4)
                .setPredicate((playerEntity, compoundNBT) -> PortalNetworkMessage.sendInformationToPlayer((ServerPlayerEntity) playerEntity, isInterdimensional(), getPos(), BlockPosUtils.getMaxDistance(this.getLength())))
        );
        this.addButton(new TextPortalButton(90, 90, 80, 16, "portality.display.close_portal").setPredicate((playerEntity, compoundNBT) -> closeLink()).setId(5));
    }

    @Override
    public void tick() {
        if (isActive()) {
            teleportHandler.tick();
            if (linkData != null) {
                for (Entity entity : this.world.getEntitiesWithinAABB(Entity.class, getPortalArea())) {
                    teleportHandler.addEntityToTeleport(entity, linkData);
                }
            }
        }
        if (!world.isRemote) {
            workModules();
        }
        if (world.isRemote) {
            tickSound();
            return;
        }
        if (isActive() && linkData != null) {
            this.getEnergyStorage().extractEnergy((linkData.isCaller() ? 2 : 1) * structureHandler.getLength() * PortalityConfig.POWER_PORTAL_TICK, false);
            if (this.getEnergyStorage().getEnergyStored() == 0 || !isFormed) {
                closeLink();
            }
        }
        if (this.world.getGameTime() % 10 == 0) {
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
                    TileEntity tileEntity = this.world.getServer().getWorld(linkData.getDimension()).getTileEntity(linkData.getPos());
                    if (!(tileEntity instanceof ControllerTile) || ((ControllerTile) tileEntity).getLinkData() == null || !((ControllerTile) tileEntity).getLinkData().getDimension().equals(this.world.func_234923_W_()) || !((ControllerTile) tileEntity).getLinkData().getPos().equals(this.pos)) {
                        this.closeLink();
                    }
                }
            }
            markForUpdate();
        }
    }

    @Nonnull
    @Override
    public ControllerTile getSelf() {
        return this;

    }

    @OnlyIn(Dist.CLIENT)
    private void tickSound() {
        if (isActive()) {
            if (sound == null) {
                Minecraft.getInstance().getSoundHandler().play(sound = new TickeableSound(this.world, this.pos, PortalitySoundHandler.PORTAL));
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
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);
        compound.putBoolean(NBT_FORMED, isFormed);
        compound.putInt(NBT_LENGTH, structureHandler.getLength());
        compound.putInt(NBT_WIDTH, structureHandler.getWidth());
        compound.putInt(NBT_HEIGHT, structureHandler.getHeight());
        compound.putBoolean(NBT_ONCE, onceCall);
        compound.putBoolean(NBT_DISPLAY, display);
        if (information != null) compound.put(NBT_PORTAL, information.writetoNBT());
        if (linkData != null) compound.put(NBT_LINK, linkData.writeToNBT());
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        isFormed = compound.getBoolean(NBT_FORMED);
        structureHandler.setLength(compound.getInt(NBT_LENGTH));
        structureHandler.setWidth(compound.getInt(NBT_WIDTH));
        structureHandler.setHeight(compound.getInt(NBT_HEIGHT));
        if (compound.contains(NBT_PORTAL))
            information = PortalInformation.readFromNBT(compound.getCompound(NBT_PORTAL));
        if (compound.contains(NBT_LINK))
            linkData = PortalLinkData.readFromNBT(compound.getCompound(NBT_LINK));
        onceCall = compound.getBoolean(NBT_ONCE);
        display = compound.getBoolean(NBT_DISPLAY);
        super.read(state, compound);
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
                if (isActive()) ((IPortalModule) block).work(this, pos);
            }
        }
        PortalDataManager.setPortalInterdimensional(this.world, this.pos, interdimensional);
    }

    public AxisAlignedBB getPortalArea() {
        if (!(world.getBlockState(this.pos).getBlock() instanceof ControllerBlock))
            return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
        Direction facing = world.getBlockState(this.pos).get(ControllerBlock.FACING_HORIZONTAL);
        BlockPos corner1 = this.pos.offset(facing.rotateY(), structureHandler.getWidth()).offset(Direction.UP);
        BlockPos corner2 = this.pos.offset(facing.rotateYCCW(), structureHandler.getWidth()).offset(Direction.UP, structureHandler.getHeight() - 1).offset(facing.getOpposite(), structureHandler.getLength() - 1);
        return new AxisAlignedBB(corner1, corner2);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return getPortalArea();
    }

    private void getPortalInfo() {
        information = PortalDataManager.getInfoFromPos(this.world, this.pos);
        markForUpdate();
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

    public String getPortalDisplayName() {
        if (information != null) return information.getName();
        return "";
    }

    public void setDisplayName(String name) {
        if (information != null) information.setName(name);
    }

    public boolean isInterdimensional() {
        return information != null && information.isInterdimensional();
    }

    public ItemStack getDisplay() {
        if (information != null) return information.getDisplay();
        return ItemStack.EMPTY;
    }

    public void linkTo(PortalLinkData data, PortalLinkData.PortalCallType type) {
        if (type == PortalLinkData.PortalCallType.FORCE) {
            closeLink();
        }
        if (linkData != null) return;
        if (type == PortalLinkData.PortalCallType.SINGLE) onceCall = true;
        if (data.isCaller()) {
            World world = this.world.getServer().getWorld(data.getDimension());
            TileEntity entity = world.getTileEntity(data.getPos());
            if (entity instanceof ControllerTile) {
                data.setName(((ControllerTile) entity).getPortalDisplayName());
                ((ControllerTile) entity).linkTo(new PortalLinkData(this.world.func_234923_W_(), this.pos, false, this.getPortalDisplayName()), type);
                int power = PortalityConfig.PORTAL_POWER_OPEN_INTERDIMENSIONAL;
                if (entity.getWorld().equals(this.world)) {
                    power = (int) this.pos.distanceSq(new Vector3i(entity.getPos().getX(), entity.getPos().getZ(), entity.getPos().getY())) * structureHandler.getLength();
                }
                this.getEnergyStorage().extractEnergy(power, false);
            }
        }
        PortalDataManager.setActiveStatus(this.world, this.pos, true);
        this.linkData = data;
    }

    public void closeLink() {
        if (linkData != null) {
            PortalDataManager.setActiveStatus(this.world, this.pos, false);
            World world = this.world.getServer().getWorld(linkData.getDimension());
            TileEntity entity = world.getTileEntity(linkData.getPos());
            linkData = null;
            if (entity instanceof ControllerTile) {
                ((ControllerTile) entity).closeLink();
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

    public boolean isDisplayNameEnabled() {
        return display;
    }

    public void setDisplayNameEnabled(ItemStack display) {
        PortalDataManager.setPortalDisplay(this.world, this.pos, display);
        getPortalInfo();
        markForUpdate();
    }

    public void setDisplayNameEnabled(boolean display) {
        this.display = display;
        markForUpdate();
    }

    public List<BlockPos> getModules() {
        return structureHandler.getModules();
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

    public PortalInformation getInformation() {
        getPortalInfo();
        return information;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ActionResultType onActivated(PlayerEntity playerIn, Hand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) != ActionResultType.SUCCESS) {
            if (!world.isRemote()) {
                Minecraft.getInstance().deferTask(() -> {
                    OpenGui.open(0, this);
                });
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    protected EnergyStorageComponent<ControllerTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(PortalityConfig.MAX_PORTAL_POWER, PortalityConfig.MAX_PORTAL_POWER_IN, 10, 20);
    }

    @OnlyIn(Dist.CLIENT)
    public static class OpenGui {
        public static void open(int id, ControllerTile controller) {
            switch (id) {
                case 0:
                    Minecraft.getInstance().displayGuiScreen(new ControllerScreen(controller));
                    return;
                case 1:
                    Minecraft.getInstance().displayGuiScreen(new RenameControllerScreen(controller));
                    return;
                case 2:
                    Minecraft.getInstance().displayGuiScreen(new PortalsScreen(controller));
                    return;
            }
        }
    }
}
