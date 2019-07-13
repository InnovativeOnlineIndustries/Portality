package com.buuz135.portality.tile;

import com.buuz135.portality.block.module.BlockCapabilityModule;
import com.buuz135.portality.gui.TileAssetProvider;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BlockTileBase;
import com.hrznstudio.titanium.block.tile.button.PosButton;
import com.hrznstudio.titanium.client.gui.asset.IAssetProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public class TileModule extends TileFrame {

    @Save
    public boolean input;

    public TileModule(BlockTileBase base) {
        super(base);
        this.addButton(new PosButton(0, 0, 18, 18).setPredicate((playerEntity, compoundNBT) -> changeMode()));
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
}
