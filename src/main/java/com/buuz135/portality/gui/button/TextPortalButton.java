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
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.ITileContainer;
import com.hrznstudio.titanium.client.screen.addon.BasicButtonAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.network.locator.instance.TileEntityLocatorInstance;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TextPortalButton extends ButtonComponent {

    private final String display;
    private Supplier<Consumer<Screen>> screenConsumer;

    public TextPortalButton(int posX, int posY, int sizeX, int sizeY, String display) {
        super(posX, posY, sizeX, sizeY);
        this.display = display;
        this.screenConsumer = () -> screen -> {
        };
    }

    public String getDisplay() {
        return display;
    }

    public TextPortalButton setClientConsumer(Supplier<Consumer<Screen>> screenConsumer) {
        this.screenConsumer = screenConsumer;
        return this;
    }

    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        return Collections.singletonList(() -> new TextButtonAddon(this, display, screenConsumer.get()));
    }

    public class TextButtonAddon extends BasicButtonAddon {

        private String text;
        private Consumer<Screen> supplier;

        public TextButtonAddon(ButtonComponent posButton, String text, Consumer<Screen> supplier) {
            super(posButton);
            this.text = text;
            this.supplier = supplier;
        }

        @Override
        public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
            super.drawBackgroundLayer(stack, screen, provider, guiX, guiY, mouseX, mouseY, partialTicks);
            String string = new TranslationTextComponent(text).getString();
            TextFormatting color = isInside(screen, mouseX - guiX, mouseY - guiY) ? TextFormatting.YELLOW : TextFormatting.WHITE;
            Minecraft.getInstance().fontRenderer.drawString(stack, color + string, guiX + this.getPosX() + this.getXSize() / 2 - Minecraft.getInstance().fontRenderer.getStringWidth(string) / 2, guiY + this.getPosY() + this.getYSize() / 2f - 3.5f, 0xFFFFFF);
        }

        @Override
        public void handleClick(Screen tile, int guiX, int guiY, double mouseX, double mouseY, int button) {
            Minecraft.getInstance().getSoundHandler().play(new SimpleSound(SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1f, 1f, Minecraft.getInstance().player.getPosition()));
            if (tile instanceof ITileContainer) {
                Titanium.NETWORK.get().sendToServer(new ButtonClickNetworkMessage(new TileEntityLocatorInstance(((ITileContainer) tile).getTile().getPos()), getId(), new CompoundNBT()));
            }
            supplier.accept(tile);
        }
    }
}