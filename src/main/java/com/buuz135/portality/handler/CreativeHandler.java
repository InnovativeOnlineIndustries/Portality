package com.buuz135.portality.handler;

import com.buuz135.portality.Portality;
import com.buuz135.portality.tile.TileController;
import com.buuz135.portality.tile.TileFrame;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Portality.MOD_ID)
public class CreativeHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        TileEntity entity = event.getWorld().getTileEntity(event.getPos());
        if (entity instanceof TileFrame) {
            BlockPos pos = ((TileFrame) entity).getControllerPos();
            if (pos != null) {
                TileEntity controller = event.getWorld().getTileEntity(pos);
                if (controller instanceof TileController) {
                    if (((TileController) controller).isCreative() && !event.getPlayer().canUseCommandBlock())
                        event.setCanceled(true);
                }
            }
        }
        if (entity instanceof TileController) {
            if (((TileController) entity).isCreative() && !event.getPlayer().canUseCommandBlock())
                event.setCanceled(true);
        }
    }
}
