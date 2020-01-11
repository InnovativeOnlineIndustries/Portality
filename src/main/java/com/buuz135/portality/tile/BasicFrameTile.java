package com.buuz135.portality.tile;

import com.buuz135.portality.proxy.CommonProxy;

import javax.annotation.Nonnull;

public class BasicFrameTile extends FrameTile<BasicFrameTile> {

    public BasicFrameTile() {
        super(CommonProxy.BLOCK_FRAME);
    }

    @Nonnull
    @Override
    public BasicFrameTile getSelf() {
        return this;
    }
}
