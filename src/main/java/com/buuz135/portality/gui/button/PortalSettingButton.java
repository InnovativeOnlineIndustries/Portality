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
package com.buuz135.portality.gui.button;

import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.GenericAssetType;
import com.hrznstudio.titanium.api.client.IAsset;
import com.hrznstudio.titanium.api.client.IAssetType;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.ITileContainer;
import com.hrznstudio.titanium.client.screen.ScreenAddonScreen;
import com.hrznstudio.titanium.client.screen.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.network.locator.ILocatable;
import com.hrznstudio.titanium.network.locator.instance.TileEntityLocatorInstance;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class PortalSettingButton extends ButtonComponent {

    public static final IAssetType<IAsset> RENAME = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> PRIVATE = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> PUBLIC = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> NAME_HIDDEN = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> NAME_SHOWN = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> BOTH_DIRECTION = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> SEND = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> RECEIVE = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> CHANGE_COLOR = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);

    private StateButtonInfo[] infos;
    private Supplier<Runnable> supplier;

    public PortalSettingButton(int posX, int posY, Supplier<Runnable> runnableSupplier, StateButtonInfo... infos) {
        super(posX, posY, 20, 20);
        this.infos = infos;
        this.supplier = runnableSupplier;
    }

    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        return Collections.singletonList(() -> new StateButtonAddon(this, infos) {
            @Override
            public int getState() {
                return PortalSettingButton.this.getState();
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                Screen screen = Minecraft.getInstance().screen;
                if (screen instanceof ScreenAddonScreen && screen instanceof ITileContainer) {
                    if (!isMouseOver(mouseX - ((ScreenAddonScreen) screen).x, mouseY - ((ScreenAddonScreen) screen).y))
                        return false;
                    Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.2f, 1f, Minecraft.getInstance().player.blockPosition()));
                    Titanium.NETWORK.get().sendToServer(new ButtonClickNetworkMessage(new TileEntityLocatorInstance(((ITileContainer) screen).getTile().getBlockPos()), getId(), new CompoundTag()));
                    supplier.get().run();
                    return true;
                }
                return false;
            }
        });
    }

    public abstract int getState();

    public StateButtonInfo[] getInfos() {
        return infos;
    }

}
