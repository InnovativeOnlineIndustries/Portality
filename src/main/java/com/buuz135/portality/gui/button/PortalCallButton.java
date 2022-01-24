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

import com.buuz135.portality.Portality;
import com.buuz135.portality.data.PortalLinkData;
import com.buuz135.portality.gui.PortalsScreen;
import com.buuz135.portality.network.PortalLinkMessage;
import com.buuz135.portality.tile.ControllerTile;
import com.hrznstudio.titanium.client.screen.ITileContainer;
import com.hrznstudio.titanium.client.screen.ScreenAddonScreen;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class PortalCallButton extends BasicScreenAddon {

    private final CallAction action;
    private final ControllerTile controller;
    private final PortalsScreen guiPortals;
    private int guiX;
    private int guiY;

    public PortalCallButton(int x, int y, ControllerTile tile, CallAction action, PortalsScreen guiPortals) {
        super(x, y);
        this.action = action;
        this.controller = tile;
        this.guiPortals = guiPortals;
        this.guiX = 0;
        this.guiY = 0;
    }

    @Override
    public void drawBackgroundLayer(PoseStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0, new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png"));
        screen.blit(stack, this.getPosX(), this.getPosY(), 0, 187, this.getXSize(), this.getYSize());
        this.guiX = guiX;
        this.guiY = guiY;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX + guiX, mouseY + guiY);
    }

    @Override
    public int getXSize() {
        return 51;
    }

    @Override
    public int getYSize() {
        return 22;
    }

    @Override
    public void drawForegroundLayer(PoseStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partial) {
        screen.drawCenteredString(stack, Minecraft.getInstance().font, new TranslatableComponent(action.getName()).getString(), this.getPosX() + 25, this.getPosY() + 7, isMouseOver(mouseX - guiX, mouseY - guiY) ? 16777120 : 0xFFFFFFFF);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (guiPortals.getSelectedPortal() != null) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof ScreenAddonScreen) {
                if (!isMouseOver(mouseX - ((ScreenAddonScreen) screen).x, mouseY - ((ScreenAddonScreen) screen).y))
                    return false;
                Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.2f, 1f, Minecraft.getInstance().player.blockPosition()));
                Portality.NETWORK.get().sendToServer(new PortalLinkMessage(action.getId(), new PortalLinkData(controller.getLevel().dimension(), controller.getBlockPos(), true, guiPortals.getSelectedPortal().getName(),guiPortals.getSelectedPortal().isToken()), new PortalLinkData(guiPortals.getSelectedPortal().getDimension(), guiPortals.getSelectedPortal().getLocation(), false,guiPortals.getSelectedPortal().getName(), guiPortals.getSelectedPortal().isToken())));
                Minecraft.getInstance().setScreen(null);
                return true;
            }
        }
        return false;
    }

    public static enum CallAction {
        OPEN(0, "portality.display.dial"), ONCE(1, "portality.display.dial_once"), FORCE(2, "portality.display.force");

        private int id;
        private String name;

        private CallAction(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
