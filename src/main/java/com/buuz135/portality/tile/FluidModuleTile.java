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
package com.buuz135.portality.tile;

import com.buuz135.portality.proxy.CommonProxy;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import net.minecraft.world.item.DyeColor;

import javax.annotation.Nonnull;

public class FluidModuleTile extends ModuleTile<FluidModuleTile> {

    @Save
    private SidedFluidTankComponent<FluidModuleTile> tank;

    public FluidModuleTile() {
        super(CommonProxy.BLOCK_CAPABILITY_FLUID_MODULE);
        this.addTank(tank = (SidedFluidTankComponent<FluidModuleTile>) new SidedFluidTankComponent<FluidModuleTile>("tank", 16000, 76, 20, 0).
                setColor(DyeColor.CYAN).
                setComponentHarness(this)
        );
    }

    @Nonnull
    @Override
    public FluidModuleTile getSelf() {
        return this;
    }
}
