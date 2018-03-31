package com.buuz135.portality.gui;

import com.buuz135.portality.tile.TileController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    public static final int CONTROLLER = 0;
    public static final int CONTROLLER_RENAME = 1;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case CONTROLLER:
                return new ContainerController((TileController) world.getTileEntity(new BlockPos(x, y, z)));
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case CONTROLLER:
                return new GuiController((ContainerController) getServerGuiElement(ID, player, world, x, y, z));
            case CONTROLLER_RENAME:
                return new GuiControllerRename((ContainerController) getServerGuiElement(CONTROLLER, player, world, x, y, z));
            default:
                return null;
        }
    }
}
