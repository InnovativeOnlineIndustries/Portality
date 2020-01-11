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
package com.buuz135.portality.network;

import com.buuz135.portality.tile.ControllerTile;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class PortalRenameMessage extends Message {

    private BlockPos tileLocation;
    private String name;

    public PortalRenameMessage() {

    }

    public PortalRenameMessage(String name, BlockPos tile) {
        this.name = name;
        this.tileLocation = tile;
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        ServerPlayerEntity serverPlayer = context.getSender();
        context.enqueueWork(() -> {
            World world = serverPlayer.world;
            if (world.getTileEntity(tileLocation) != null && world.getTileEntity(tileLocation) instanceof ControllerTile) {
                ControllerTile controller = (ControllerTile) world.getTileEntity(tileLocation);
                if (controller.getOwner().equals(serverPlayer.getUniqueID())) controller.setDisplayName(name);
            }
        });
    }

}
