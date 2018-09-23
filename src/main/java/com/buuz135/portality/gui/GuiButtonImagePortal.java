package com.buuz135.portality.gui;

import com.buuz135.portality.Portality;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.data.PortalLinkData;
import com.buuz135.portality.network.PortalLinkMessage;
import com.buuz135.portality.tile.TileController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;

public class GuiButtonImagePortal extends GuiButtonImage implements IHasTooltip {

    private PortalInformation information;
    private TileController controller;

    public GuiButtonImagePortal(PortalInformation information, int id, int x, int y, int xSize, int ySize, int textureX, int textureY, int offset, ResourceLocation location, TileController tile) {
        super(id, x, y, xSize, ySize, textureX, textureY, offset, location);
        this.information = information;
        this.controller = tile;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mc, mouseX, mouseY, partialTicks);
        drawButtonForegroundLayer(mouseX, mouseY);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (isMouseOver()) {
            if (information.getDimension() == controller.getWorld().provider.getDimension() && information.getLocation().equals(controller.getPos()))
                return super.mousePressed(mc, mouseX, mouseY);
            int type = 0;
            if (GuiScreen.isShiftKeyDown()) {// One Call
                type = 1;
            }
            if (GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown()) {// Force call
                type = 2;
            }
            if (!information.isActive() || type == 2) {
                Portality.NETWORK.sendToServer(new PortalLinkMessage(type, new PortalLinkData(controller.getWorld().provider.getDimension(), controller.getPos(), true), new PortalLinkData(information.getDimension(), information.getLocation(), false)));
                mc.player.closeScreen();
            }
        }
        return super.mousePressed(mc, mouseX, mouseY);
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
        super.drawButtonForegroundLayer(mouseX, mouseY);
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(information.getDisplay(), x + 5, y + 3);
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        TextFormatting color = TextFormatting.WHITE;
        if (information.isPrivate()) color = TextFormatting.GOLD;
        if (information.isActive()) color = TextFormatting.RED;
        fontRenderer.drawString(color + information.getName().substring(0, Math.min(information.getName().length(), 28)), x + 28, 7 + y, 0xFFFFFF);
        //fontRenderer.drawString(color + (information.isPrivate() ? I18n.format("portality.display.private") : I18n.format("portality.display.public")), x + 40, 10 + (fontRenderer.FONT_HEIGHT + 1) * 1 + y, 0xFFFFFF);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.popMatrix();
    }


    @Override
    public List<String> getTooltip() {
        return Arrays.asList(I18n.format("portality.display.dial"), I18n.format("portality.display.dial_once"), I18n.format("portality.display.force"));
    }
}
