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

import com.buuz135.portality.data.PortalLinkData;
import com.buuz135.portality.tile.ControllerTile;
import com.hrznstudio.titanium.network.CompoundSerializableDataHandler;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;


public class PortalLinkMessage extends Message {

    static {
        CompoundSerializableDataHandler.map(PortalLinkData.class, buf -> PortalLinkData.readFromNBT(buf.readNbt()), (buf, portalLinkData) -> buf.writeNbt(portalLinkData.writeToNBT()));
    }

    private int type;
    private PortalLinkData linkSender;
    private PortalLinkData linkReceiver;

    public PortalLinkMessage() {
    }

    public PortalLinkMessage(int type, PortalLinkData linkSender, PortalLinkData linkReceiver) {
        this.type = type;
        this.linkSender = linkSender;
        this.linkReceiver = linkReceiver;
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        Level world = context.getSender().level.getServer().getLevel(linkSender.getDimension());
        BlockEntity tileEntity = world.getBlockEntity(linkSender.getPos());
        if (tileEntity instanceof ControllerTile) {
            ((ControllerTile) tileEntity).linkTo(new PortalLinkData(linkReceiver.getDimension(), linkReceiver.getPos(), true, linkSender.getName(), linkReceiver.isToken()), PortalLinkData.PortalCallType.values()[type]);
        }
    }

}
