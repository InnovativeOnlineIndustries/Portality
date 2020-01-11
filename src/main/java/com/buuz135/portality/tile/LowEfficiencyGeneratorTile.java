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
import com.hrznstudio.titanium.block.tile.GeneratorTile;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;

public class LowEfficiencyGeneratorTile extends GeneratorTile<LowEfficiencyGeneratorTile> {

    @Save
    private SidedInventoryComponent<LowEfficiencyGeneratorTile> fuel;

    public LowEfficiencyGeneratorTile() {
        super(CommonProxy.BLOCK_GENERATOR);
        this.addInventory(fuel = (SidedInventoryComponent<LowEfficiencyGeneratorTile>) new SidedInventoryComponent<LowEfficiencyGeneratorTile>("fuel", 46, 22, 1, 0)
                .setColor(DyeColor.ORANGE)
                .setColorGuiEnabled(false)
                .setInputFilter((itemStack, integer) -> FurnaceTileEntity.isFuel(itemStack))
                .setComponentHarness(this)
        );
    }

    @Override
    public ActionResultType onActivated(PlayerEntity playerIn, Hand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) != ActionResultType.SUCCESS) {
            openGui(playerIn);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public IAssetProvider getAssetProvider() {
        return TileAssetProvider.PROVIDER;
    }

    @Nonnull
    @Override
    public LowEfficiencyGeneratorTile getSelf() {
        return this;
    }

    @Override
    public int consumeFuel() {
        int time = FurnaceTileEntity.getBurnTimes().getOrDefault(fuel.getStackInSlot(0).getItem(), 100);
        fuel.getStackInSlot(0).shrink(1);
        return time;
    }

    @Override
    public boolean canStart() {
        return !fuel.getStackInSlot(0).isEmpty() && FurnaceTileEntity.getBurnTimes().get(fuel.getStackInSlot(0).getItem()) != null;
    }

    @Override
    public int getEnergyProducedEveryTick() {
        return 40;
    }

    @Override
    public ProgressBarComponent<LowEfficiencyGeneratorTile> getProgressBar() {
        return new ProgressBarComponent<LowEfficiencyGeneratorTile>(30, 20, 0, 100)
                .setComponentHarness(this)
                .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP)
                .setColor(DyeColor.CYAN);
    }

    @Override
    public int getEnergyCapacity() {
        return 100000;
    }

    @Override
    public int getExtractingEnergy() {
        return 100000;
    }
}
