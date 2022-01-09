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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.network.NetworkEvent;

public class PortalCloseMessage extends Message {

    private ResourceLocation dimension;
    private BlockPos pos;

    public PortalCloseMessage(ResourceKey<Level> worldRegistryKey, BlockPos pos) {
        this.dimension = worldRegistryKey.location();
        this.pos = pos;
    }

    public PortalCloseMessage() {
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        Level world = context.getSender().level.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dimension));
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ControllerTile) {
            ((ControllerTile) tileEntity).closeLink();
        }
    }

}
