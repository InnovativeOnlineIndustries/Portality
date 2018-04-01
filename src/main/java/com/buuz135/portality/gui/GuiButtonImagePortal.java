package com.buuz135.portality.gui;

import com.buuz135.portality.data.PortalInformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;

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
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(information.getDisplay(), x + 13, 10 + y);
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawString(information.getName().substring(0, Math.min(information.getName().length(), 28)), x + 40, 10 + y, 0xFFFFFF);
        fontRenderer.drawString(information.isPrivate() ? "Private" : "Public", x + 40, 10 + (fontRenderer.FONT_HEIGHT + 1) * 1 + y, 0xFFFFFF);
    }
}
