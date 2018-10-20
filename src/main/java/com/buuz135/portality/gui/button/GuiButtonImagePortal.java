/*
 * This file is part of Worldgen Indicators.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.portality.gui.button;

import com.buuz135.portality.Portality;
import com.buuz135.portality.data.PortalInformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiButtonImagePortal extends GuiButtonImage {

    private PortalInformation information;

    public GuiButtonImagePortal(PortalInformation information, int id, int x, int y, int xSize, int ySize, int textureX, int textureY, int offset, ResourceLocation location) {
        super(id, x, y, xSize, ySize, textureX, textureY, offset, location);
        this.information = information;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mc, mouseX, mouseY, partialTicks);
        drawButtonForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
        super.drawButtonForegroundLayer(mouseX, mouseY);
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(information.getDisplay(), x + 5, y + 3);
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        TextFormatting color = TextFormatting.RESET;
        if (information.isPrivate()) color = TextFormatting.GOLD;
        if (information.isActive()) color = TextFormatting.RED;
        fontRenderer.drawString(color + information.getName().substring(0, Math.min(information.getName().length(), 28)), x + 28, 7 + y, isMouseOver() ? 16777120 : 0xFFFFFFFF);
        //fontRenderer.drawString(color + (information.isPrivate() ? I18n.format("portality.display.private") : I18n.format("portality.display.public")), x + 40, 10 + (fontRenderer.FONT_HEIGHT + 1) * 1 + y, 0xFFFFFF);
        GlStateManager.color(1, 1, 1, 1);
        if (information.isPrivate()) {
            GlStateManager.disableDepth();
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(Portality.MOD_ID, "textures/gui/lock.png"));
            drawModalRectWithCustomSizedTexture(x + 4, y + 14, 0, 0, 8, 8, 8, 8);
            GlStateManager.enableDepth();
        }
        GlStateManager.popMatrix();
    }

    public PortalInformation getInformation() {
        return information;
    }
}
