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
import com.buuz135.portality.gui.ChangeColorScreen;
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
import com.buuz135.portality.proxy.client.IPortalColor;
import com.buuz135.portality.proxy.client.TickeableSound;
import com.buuz135.portality.util.BlockPosUtils;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.PoweredTile;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class ControllerTile extends PoweredTile<ControllerTile> implements IPortalColor {

    private static String NBT_FORMED = "Formed";
    private static String NBT_LENGTH = "Length";
    private static String NBT_WIDTH = "Width";
    private static String NBT_HEIGHT = "Height";
    private static String NBT_PORTAL = "Portal";
    private static String NBT_LINK = "Link";
    private static String NBT_DISPLAY = "Display";
    private static String NBT_ONCE = "Once";
    private static String NBT_COLOR = "Color";
    private static String NBT_TOKENS = "Tokens";

    private boolean isFormed;
    private boolean onceCall;
    private boolean display;
    private PortalInformation information;
    private PortalLinkData linkData;
    private int color;
    private HashMap<String, CompoundTag> teleportationTokens;

    @OnlyIn(Dist.CLIENT)
    private TickeableSound sound;

    private TeleportHandler teleportHandler;
    private StructureHandler structureHandler;

    public ControllerTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<ControllerTile>) CommonProxy.BLOCK_CONTROLLER.getLeft().get(),  CommonProxy.BLOCK_CONTROLLER.getRight().get(), pos, state);
        this.teleportationTokens = new LinkedHashMap<>();
        this.isFormed = false;
        this.onceCall = false;
        this.display = true;
        this.color = Integer.parseInt("0094ff", 16); //Default Blue
        this.teleportHandler = new TeleportHandler(this);
        this.structureHandler = new StructureHandler(this);
        this.setShowEnergy(false);
        this.addButton(new PortalSettingButton(-22, 12, () -> () -> {
            OpenGui.open(1, ControllerTile.this);
        }, new StateButtonInfo(0, PortalSettingButton.RENAME, "portality.display.change_name")) {
            @Override
            public int getState() {
                return 0;
            }
        }.setId(1));
        this.addButton(new PortalSettingButton(-22, 12 + 22 * 3, () -> () -> {
            OpenGui.open(3, ControllerTile.this);
        }, new StateButtonInfo(0, PortalSettingButton.CHANGE_COLOR, "portality.display.change_color")) {
            @Override
            public int getState() {
                return 0;
            }
        }.setId(5));
        this.addButton(new TextPortalButton(5, 90, 80, 16, "portality.display.call_portal")
                .setClientConsumer(() -> screen -> {
                    OpenGui.open(2, ControllerTile.this);
                })
                .setId(4)
                .setPredicate((playerEntity, compoundNBT) -> PortalNetworkMessage.sendInformationToPlayer((ServerPlayer) playerEntity, isInterdimensional(), getBlockPos(), BlockPosUtils.getMaxDistance(this.getLength()), this.teleportationTokens))
        );

        this.addButton(new PortalSettingButton(-22, 12 + 22, () -> () -> {
        }, new StateButtonInfo(0, PortalSettingButton.PUBLIC, "portality.display.make_private"), new StateButtonInfo(1, PortalSettingButton.PRIVATE, "portality.display.make_public")) {
            @Override
            public int getState() {
                return information != null && information.isPrivate() ? 1 : 0;
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            if (information.getOwner().equals(playerEntity.getUUID())) togglePrivacy();
        }).setId(2));

        this.addButton(new PortalSettingButton(-22, 12 + 22 * 2, () -> () -> {
        }, new StateButtonInfo(0, PortalSettingButton.NAME_SHOWN, "portality.display.hide_name"), new StateButtonInfo(1, PortalSettingButton.NAME_HIDDEN, "portality.display.show_name")) {
            @Override
            public int getState() {
                return display ? 0 : 1;
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            if (information.getOwner().equals(playerEntity.getUUID()))
                setDisplayNameEnabled(!isDisplayNameEnabled());
        }).setId(3));
        this.addButton(new TextPortalButton(90, 90, 80, 16, "portality.display.close_portal").setPredicate((playerEntity, compoundNBT) -> closeLink()).setId(5));
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, ControllerTile blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        if (isActive()) {
            teleportHandler.tick();
            if (linkData != null) {
                for (Entity entity : this.level.getEntitiesOfClass(Entity.class, getPortalArea())) {
                    teleportHandler.addEntityToTeleport(entity, linkData);
                }
            }
            workModules();
        }
        if (isActive() && linkData != null) {
            this.getEnergyStorage().extractEnergy((linkData.isCaller() ? 2 : 1) * structureHandler.getLength() * PortalityConfig.POWER_PORTAL_TICK, false);
            if (this.getEnergyStorage().getEnergyStored() == 0 || !isFormed) {
                closeLink();
            }
        }
        if (this.level.getGameTime() % 10 == 0) {
            if (structureHandler.shouldCheckForStructure()) {
                this.isFormed = structureHandler.checkArea();
                if (this.isFormed) {
                    structureHandler.setShouldCheckForStructure(false);
                    PortalDataManager.setPortalInterdimensional(this.level, this.worldPosition, structureHandler.getModules().stream().map(blockPos -> this.level.getBlockState(blockPos)).filter(blockState -> blockState.getBlock() instanceof IPortalModule && ((IPortalModule) blockState.getBlock()).allowsInterdimensionalTravel()).count() > 0);
                } else {
                    structureHandler.cancelFrameBlocks();
                }
            }
            if (isFormed) {
                getPortalInfo();
                if (linkData != null) {
                    ChunkLoaderHandler.addPortalAsChunkloader(this);
                    if (this.level.getServer() == null || this.level.getServer().getLevel(linkData.getDimension()) == null){
                        this.closeLink();
                    } else if (!linkData.isToken()){
                        BlockEntity tileEntity = this.level.getServer().getLevel(linkData.getDimension()).getBlockEntity(linkData.getPos());
                        if (!(tileEntity instanceof ControllerTile) || ((ControllerTile) tileEntity).getLinkData() == null || !((ControllerTile) tileEntity).getLinkData().getDimension().equals(this.level.dimension()) || !((ControllerTile) tileEntity).getLinkData().getPos().equals(this.worldPosition)) {
                            this.closeLink();
                        }
                    }
                }
            }
            markForUpdate();
        }
    }

    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state, ControllerTile blockEntity) {
        super.clientTick(level, pos, state, blockEntity);
        if (isActive()) {
            teleportHandler.tick();
            if (linkData != null) {
                for (Entity entity : this.level.getEntitiesOfClass(Entity.class, getPortalArea())) {
                    teleportHandler.addEntityToTeleport(entity, linkData);
                }
            }
        }
        tickSound();
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
                Minecraft.getInstance().getSoundManager().play(sound = new TickeableSound(this.level, this.worldPosition, PortalitySoundHandler.PORTAL.get()));
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
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean(NBT_FORMED, isFormed);
        compound.putInt(NBT_LENGTH, structureHandler.getLength());
        compound.putInt(NBT_WIDTH, structureHandler.getWidth());
        compound.putInt(NBT_HEIGHT, structureHandler.getHeight());
        compound.putBoolean(NBT_ONCE, onceCall);
        compound.putBoolean(NBT_DISPLAY, display);
        compound.putInt(NBT_COLOR, color);
        if (information != null) compound.put(NBT_PORTAL, information.writetoNBT());
        if (linkData != null) compound.put(NBT_LINK, linkData.writeToNBT());
        CompoundTag tokens = new CompoundTag();
        for (String s : teleportationTokens.keySet()) {
            tokens.put(s, teleportationTokens.get(s));
        }
        compound.put(NBT_TOKENS, tokens);
    }

    @Override
    public void load(CompoundTag compound) {
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
        if (compound.contains(NBT_COLOR))
            color = compound.getInt(NBT_COLOR);
        this.teleportationTokens = new LinkedHashMap<>();
        if (compound.contains(NBT_TOKENS)){
            CompoundTag tokens = compound.getCompound(NBT_TOKENS);
            for (String s : tokens.getAllKeys()) {
                this.teleportationTokens.put(s, tokens.getCompound(s));
            }
        }
        super.load(compound);
    }

    public void breakController() {
        closeLink();
        structureHandler.cancelFrameBlocks();
    }

    private void workModules() {
        boolean interdimensional = false;
        for (BlockPos pos : structureHandler.getModules()) {
            Block block = this.level.getBlockState(pos).getBlock();
            if (block instanceof IPortalModule) {
                if (((IPortalModule) block).allowsInterdimensionalTravel()) interdimensional = true;
                if (isActive()) ((IPortalModule) block).work(this, pos);
            }
        }
        PortalDataManager.setPortalInterdimensional(this.level, this.worldPosition, interdimensional);
    }

    public AABB getPortalArea() {
        if (!(level.getBlockState(this.worldPosition).getBlock() instanceof ControllerBlock))
            return new AABB(0, 0, 0, 0, 0, 0);
        Direction facing = level.getBlockState(this.worldPosition).getValue(ControllerBlock.FACING_HORIZONTAL);
        BlockPos corner1 = this.worldPosition.relative(facing.getClockWise(), structureHandler.getWidth()).relative(Direction.UP);
        BlockPos corner2 = this.worldPosition.relative(facing.getCounterClockWise(), structureHandler.getWidth()).relative(Direction.UP, structureHandler.getHeight() - 1).relative(facing.getOpposite(), structureHandler.getLength() - 1);
        return new AABB(corner1, corner2);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return getPortalArea();
    }

    private void getPortalInfo() {
        information = PortalDataManager.getInfoFromPos(this.level, this.worldPosition);
        markForUpdate();
    }

    public void togglePrivacy() {
        PortalDataManager.setPortalPrivacy(this.level, this.worldPosition, !information.isPrivate());
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
            if (!data.isToken()){
                Level world = this.level.getServer().getLevel(data.getDimension());
                BlockEntity entity = world.getBlockEntity(data.getPos());
                if (entity instanceof ControllerTile) {
                    data.setName(((ControllerTile) entity).getPortalDisplayName());
                    ((ControllerTile) entity).linkTo(new PortalLinkData(this.level.dimension(), this.worldPosition, false, this.getPortalDisplayName(), false), type);
                }
            }
            int power = PortalityConfig.PORTAL_POWER_OPEN_INTERDIMENSIONAL;
            if (data.getDimension().location().equals(this.level.dimension().location())) {
                power = (int) this.worldPosition.distSqr(data.getPos()) * structureHandler.getLength();
            }
            this.getEnergyStorage().extractEnergy(power, false);
        }
        PortalDataManager.setActiveStatus(this.level, this.worldPosition, true);
        this.linkData = data;
        markForUpdate();
    }

    public void closeLink() {
        if (linkData != null) {
            PortalDataManager.setActiveStatus(this.level, this.worldPosition, false);
            if (!linkData.isToken()){
                Level world = this.level.getServer().getLevel(linkData.getDimension());
                if (world != null){
                    BlockEntity entity = world.getBlockEntity(linkData.getPos());
                    if (entity instanceof ControllerTile) {
                        linkData = null;
                        ((ControllerTile) entity).closeLink();
                    }
                }
            }
            linkData = null;
        }
        ChunkLoaderHandler.removePortalAsChunkloader(this);
        markForUpdate();
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
        PortalDataManager.setPortalDisplay(this.level, this.worldPosition, display);
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        structureHandler.getModules().stream().filter(blockPos -> this.level.getBlockEntity(blockPos) instanceof IPortalColor).map(blockPos -> (IPortalColor) this.level.getBlockEntity(blockPos)).forEach(iPortalColor -> iPortalColor.setColor(color));
        structureHandler.getFrameBlocks().stream().filter(blockPos -> !(this.level.getBlockEntity(blockPos) instanceof ControllerTile)).filter(blockPos -> this.level.getBlockEntity(blockPos) instanceof IPortalColor).map(blockPos -> (IPortalColor) this.level.getBlockEntity(blockPos)).forEach(iPortalColor -> iPortalColor.setColor(color));
        markForUpdate();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public InteractionResult onActivated(Player playerIn, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) != InteractionResult.SUCCESS) {
            Minecraft.getInstance().submitAsync(() -> OpenGui.open(0, this));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
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
                    Minecraft.getInstance().setScreen(new ControllerScreen(controller));
                    return;
                case 1:
                    Minecraft.getInstance().setScreen(new RenameControllerScreen(controller));
                    return;
                case 2:
                    Minecraft.getInstance().setScreen(new PortalsScreen(controller));
                    return;
                case 3:
                    Minecraft.getInstance().setScreen(new ChangeColorScreen(controller));
                    return;
            }
        }
    }

    public boolean addTeleportationToken(ItemStack stack){
        this.teleportationTokens.put(stack.getHoverName().getString(), stack.getTag());
        markForUpdate();
        return true;
    }

    public HashMap<String, CompoundTag> getTeleportationTokens() {
        return teleportationTokens;
    }
}
