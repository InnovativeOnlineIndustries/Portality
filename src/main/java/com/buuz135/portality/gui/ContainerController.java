package com.buuz135.portality.gui;

import com.buuz135.portality.tile.TileController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class ContainerController extends Container {

    private TileController controller;

    public ContainerController(TileController controller) {
        this.controller = controller;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        if (!controller.isFormed()) {
            playerIn.sendStatusMessage(new TextComponentTranslation(TextFormatting.RED + "ERROR: Portal needs to be 3 blocks deep"), true);
            return false;
        }
        return !this.controller.isPrivate() || this.controller.getOwner() == null || playerIn.getUniqueID().equals(this.controller.getOwner());
    }

    public TileController getController() {
        return controller;
    }
}
