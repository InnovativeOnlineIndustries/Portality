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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class PortalCloseMessage extends Message {

    private ResourceLocation dimension;
    private BlockPos pos;

    public PortalCloseMessage(RegistryKey<World> worldRegistryKey, BlockPos pos) {
        this.dimension = worldRegistryKey.func_240901_a_();
        this.pos = pos;
    }

    public PortalCloseMessage() {
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        World world = context.getSender().world.getServer().getWorld(RegistryKey.func_240903_a_(Registry.WORLD_KEY, dimension));
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof ControllerTile) {
            ((ControllerTile) tileEntity).closeLink();
        }
    }

}
