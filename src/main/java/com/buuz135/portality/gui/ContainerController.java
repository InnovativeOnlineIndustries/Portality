package com.buuz135.portality.gui;

import com.buuz135.portality.tile.TileController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerController extends Container {

    private TileController controller;

    public ContainerController(TileController controller) {
        this.controller = controller;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !this.controller.isPrivate() || this.controller.getOwner() == null || playerIn.getUniqueID().equals(this.controller.getOwner());
    }

    public TileController getController() {
        return controller;
    }
}