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
                        new StateButtonInfo(0, TileAssetProvider.AA_BUTTON_IO_INPUT, "input"),
                        new StateButtonInfo(1, TileAssetProvider.AA_BUTTON_IO_OUTPUT, "output")) {
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
        if (!super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ)) {
            openGui(playerIn);
            return true;
        }
        return false;
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
