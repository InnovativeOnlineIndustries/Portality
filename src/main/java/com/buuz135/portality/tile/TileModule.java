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

import com.buuz135.portality.block.module.BlockCapabilityModule;
import com.buuz135.portality.gui.TileAssetProvider;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IGuiAddon;
import com.hrznstudio.titanium.block.BlockTileBase;
import com.hrznstudio.titanium.block.tile.button.PosButton;
import com.hrznstudio.titanium.client.gui.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.gui.addon.StateButtonInfo;
import com.hrznstudio.titanium.client.gui.asset.IAssetProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import java.util.Collections;
import java.util.List;

public class TileModule extends TileFrame {

    @Save
    private boolean input;
    private PosButton button;

    public TileModule(BlockTileBase base) {
        super(base);
        this.input = true;
        this.addButton(button = new PosButton(153, 84, 18, 18) {
            @Override
            public List<IFactory<? extends IGuiAddon>> getGuiAddons() {
                return Collections.singletonList(() -> new StateButtonAddon(button,
                        new StateButtonInfo(0, TileAssetProvider.AA_BUTTON_IO_INPUT, "module.type.input"),
                        new StateButtonInfo(1, TileAssetProvider.AA_BUTTON_IO_OUTPUT, "module.type.output")) {
                    @Override
                    public int getState() {
                        return input ? 0 : 1;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> changeMode()));
    }

    @Override
    public boolean onActivated(PlayerEntity playerIn, Hand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ)) return true;
        openGui(playerIn);
        return true;
    }

    public void changeMode() {
        input = !input;
        this.world.setBlockState(this.pos, this.getBlockState().with(BlockCapabilityModule.INPUT, input));
        markForUpdate();
    }

    @Override
    public IAssetProvider getAssetProvider() {
        return TileAssetProvider.PROVIDER;
    }

    public boolean isInput() {
        return input;
    }
}
