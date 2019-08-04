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

import com.buuz135.portality.gui.TileAssetProvider;
import com.buuz135.portality.proxy.CommonProxy;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.tile.TilePowered;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import com.hrznstudio.titanium.block.tile.progress.PosProgressBar;
import com.hrznstudio.titanium.client.gui.addon.EnergyBarGuiAddon;
import com.hrznstudio.titanium.client.gui.asset.IAssetProvider;
import com.hrznstudio.titanium.energy.NBTEnergyHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileGenerator extends TilePowered {

    @Save
    private SidedInvHandler fuel;
    @Save
    private PosProgressBar bar;

    public TileGenerator() {
        super(CommonProxy.BLOCK_GENERATOR);
        this.addGuiAddonFactory(() -> new EnergyBarGuiAddon(10, 20, getEnergyStorage()));
        this.addInventory(fuel = (SidedInvHandler) new SidedInvHandler("fuel", 46, 22, 1, 0)
                .setColor(DyeColor.ORANGE)
                .setColorGuiEnabled(false)
                .setInputFilter((itemStack, integer) -> FurnaceTileEntity.isFuel(itemStack))
                .setTile(this)
        );
        this.addProgressBar(bar = new PosProgressBar(30, 20, 100, 100)
                .setTile(this)
                .setBarDirection(PosProgressBar.BarDirection.VERTICAL_UP)
                .setOnStart(() -> {
                    bar.setMaxProgress(FurnaceTileEntity.getBurnTimes().getOrDefault(fuel.getStackInSlot(0).getItem(), 100));
                    fuel.getStackInSlot(0).shrink(1);
                    markForUpdate();
                })
                .setCanIncrease(tileEntity -> true)
                .setCanReset(tileEntity -> !fuel.getStackInSlot(0).isEmpty() && FurnaceTileEntity.getBurnTimes().get(fuel.getStackInSlot(0).getItem()) != null)
                .setOnTickWork(() -> this.getEnergyStorage().receiveEnergyForced(40))
                .setColor(DyeColor.CYAN)
        );
    }

    @Override
    protected IFactory<NBTEnergyHandler> getEnergyHandlerFactory() {
        return () -> new NBTEnergyHandler(this, 100000, 0, 100000);
    }

    @Override
    public void tick() {
        super.tick();
        for (Direction facing : Direction.values()) {
            BlockPos checking = this.pos.offset(facing);
            TileEntity checkingTile = this.world.getTileEntity(checking);
            if (checkingTile != null) {
                checkingTile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite()).ifPresent(storage -> {
                    int energy = storage.receiveEnergy(Math.min(this.getEnergyStorage().getEnergyStored(), 4000), false);
                    if (energy > 0) {
                        this.getEnergyStorage().extractEnergy(energy, false);
                        return;
                    }
                });
            }
        }

    }

    @Override
    public boolean onActivated(PlayerEntity playerIn, Hand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (!super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ)) {
            openGui(playerIn);
            return true;
        }
        return false;
    }

    @Override
    public IAssetProvider getAssetProvider() {
        return TileAssetProvider.PROVIDER;
    }
}
