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
package com.buuz135.portality.gui;

import com.buuz135.portality.proxy.CommonProxy;
import com.buuz135.portality.tile.ControllerTile;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.client.assets.types.IBackgroundAsset;
import com.hrznstudio.titanium.client.screen.ITileContainer;
import com.hrznstudio.titanium.client.screen.ScreenAddonScreen;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ControllerScreen extends ScreenAddonScreen implements ITileContainer<ControllerTile> {

    private ControllerTile controller;

    public ControllerScreen(ControllerTile controller) {
        super(PortalityAssetProvider.PROVIDER, true);
        this.controller = controller;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderBackground(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        IBackgroundAsset background = (IBackgroundAsset) IAssetProvider.getAsset(PortalityAssetProvider.PROVIDER, AssetTypes.BACKGROUND);
        String name = Component.translatable(CommonProxy.BLOCK_CONTROLLER.block().get().getDescriptionId()).getString();
        Font fontRenderer = Minecraft.getInstance().font;
        graphics.drawString(fontRenderer, ChatFormatting.DARK_AQUA + name, this.x + background.getArea().width / 2 - fontRenderer.width(name) / 2, this.y + 3, 0x000000, true);
        graphics.drawString(fontRenderer, I18n.get("portality.gui.controller") + " " + controller.getPortalDisplayName().substring(0, Math.min(controller.getPortalDisplayName().length(), 26)), this.x + 10, this.y + 21, 0xFFFFFF, true);
        graphics.drawString(fontRenderer, I18n.get("portality.gui.controller.private") + " " + controller.isPrivate(), this.x + 10, this.y + 21 + (fontRenderer.lineHeight + 1), 0xFFFFFF, true);
        graphics.drawString(fontRenderer, I18n.get("portality.gui.controller.interdimensional") + " " + controller.isInterdimensional(), this.x + 10, this.y + 21 + (fontRenderer.lineHeight + 1) * 2, 0xFFFFFF, true);
        graphics.drawString(fontRenderer, I18n.get("portality.gui.controller.power") + " " + new DecimalFormat().format(controller.getEnergyStorage().getEnergyStored()) + " FE", this.x + 10, this.y + 21 + (fontRenderer.lineHeight + 1) * 3, 0xFFFFFF, true);
        graphics.drawString(fontRenderer, I18n.get("portality.gui.controller.link") + " " + (controller.isActive() ? I18n.get("portality.gui.controller.link_active") : I18n.get("portality.gui.controller.link_missing")), this.x + 10, this.y + 21 + (fontRenderer.lineHeight + 1) * 4, 0xFFFFFF, true);
        super.renderForeground(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public List<IFactory<IScreenAddon>> guiAddons() {
        List<IFactory<IScreenAddon>> addons = new ArrayList<>();
        addons.add(() -> new BasicScreenAddon(x - 25, y + 9) {
            @Override
            public int getXSize() {
                return 0;
            }

            @Override
            public int getYSize() {
                return 0;
            }

            @Override
            public void drawBackgroundLayer(GuiGraphics graphics, Screen guiScreen, IAssetProvider iAssetProvider, int i, int i1, int i2, int i3, float v) {
                IBackgroundAsset background = (IBackgroundAsset) IAssetProvider.getAsset(PortalityAssetProvider.PROVIDER, AssetTypes.BACKGROUND);
                RenderSystem.setShaderTexture(0, background.getResourceLocation());
                graphics.blit(background.getResourceLocation(), x - 25, y + 9, 0, 110, 25, 97, 256, 256);
            }

            @Override
            public void drawForegroundLayer(GuiGraphics graphics, Screen guiScreen, IAssetProvider iAssetProvider, int i, int i1, int i2, int i3, float partial) {

            }
        });
        controller.getScreenAddons().forEach(iFactory -> addons.add((IFactory<IScreenAddon>) iFactory));
        return addons;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public ControllerTile getTile() {
        return controller;
    }
}
