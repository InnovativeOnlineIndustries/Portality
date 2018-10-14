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
package com.buuz135.portality.gui;

import com.buuz135.portality.Portality;
import com.buuz135.portality.data.PortalLinkData;
import com.buuz135.portality.gui.button.IHasTooltip;
import com.buuz135.portality.gui.button.TooltipGuiButton;
import com.buuz135.portality.network.PortalCloseMessage;
import com.buuz135.portality.network.PortalDisplayToggleMessage;
import com.buuz135.portality.network.PortalNetworkMessage;
import com.buuz135.portality.network.PortalPrivacyToggleMessage;
import com.buuz135.portality.proxy.CommonProxy;
import com.buuz135.portality.util.BlockPosUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class GuiController extends GuiContainer {

    public static final ResourceLocation BG = new ResourceLocation(Portality.MOD_ID, "textures/gui/controller.png");

    public GuiController(ContainerController inventorySlotsIn) {
        super(inventorySlotsIn);
        this.xSize = 175;
        this.ySize = 110;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.addButton(new GuiButton(0, this.guiLeft + 5, this.guiTop + 90, 80, 16, I18n.format("portality.display.call_portal")) {
            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                if (isMouseOver()) {
                    ContainerController containerController = (ContainerController) GuiController.this.inventorySlots;
                    BlockPos pos = containerController.getController().getPos();
                    mc.player.openGui(Portality.INSTANCE, 2, containerController.getController().getWorld(), pos.getX(), pos.getY(), pos.getZ());
                    Portality.NETWORK.sendToServer(new PortalNetworkMessage.Request(containerController.getController().isInterdimensional(), containerController.getController().getPos(), BlockPosUtils.getMaxDistance(containerController.getController().getLength())));

                }
                return super.mousePressed(mc, mouseX, mouseY);
            }

            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
                FontRenderer fontrenderer = mc.fontRenderer;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                int j = 14737632;
                if (packedFGColour != 0) j = packedFGColour;
                else if (!this.enabled) j = 10526880;
                else if (this.hovered) j = 16777120;
                this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
            }
        });
        this.addButton(new GuiButton(1, this.guiLeft + 10 + 80, this.guiTop + 90, 80, 16, I18n.format("portality.display.close_portal")) {
            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                if (isMouseOver()) {
                    ContainerController containerController = (ContainerController) GuiController.this.inventorySlots;
                    BlockPos pos = containerController.getController().getPos();
                    Portality.NETWORK.sendToServer(new PortalCloseMessage(new PortalLinkData(containerController.getController().getWorld().provider.getDimension(), pos, false)));
                }
                return super.mousePressed(mc, mouseX, mouseY);
            }

            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
                FontRenderer fontrenderer = mc.fontRenderer;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                int j = 14737632;
                if (packedFGColour != 0) j = packedFGColour;
                else if (!this.enabled) j = 10526880;
                else if (this.hovered) j = 16777120;
                this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
            }
        });
        this.addButton(new TooltipGuiButton(3, this.guiLeft - 22, this.guiTop + 14, 20, 20, "Aa") {
            @Override
            public Rectangle getTexture() {
                return new Rectangle(176, 0, 20, 20);
            }

            @Override
            public List<String> getTooltip() {
                return Arrays.asList(I18n.format("portality.display.change_name"));
            }

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                if (isMouseOver()) {
                    ContainerController containerController = (ContainerController) GuiController.this.inventorySlots;
                    BlockPos pos = containerController.getController().getPos();
                    mc.player.openGui(Portality.INSTANCE, 1, containerController.getController().getWorld(), pos.getX(), pos.getY(), pos.getZ());
                }
                return super.mousePressed(mc, mouseX, mouseY);
            }
        });
        this.addButton(new TooltipGuiButton(4, this.guiLeft - 22, this.guiTop + 14 + 22, 20, 20, "") {

            @Override
            public Rectangle getTexture() {
                ContainerController containerController = (ContainerController) GuiController.this.inventorySlots;
                if (!containerController.getController().isPrivate()) return new Rectangle(176, 21, 20, 20);
                return new Rectangle(197, 21, 20, 20);
            }

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                if (isMouseOver()) {
                    ContainerController containerController = (ContainerController) GuiController.this.inventorySlots;
                    Portality.NETWORK.sendToServer(new PortalPrivacyToggleMessage(containerController.getController().getPos().toLong()));
                }
                return super.mousePressed(mc, mouseX, mouseY);
            }

            @Override
            public List<String> getTooltip() {
                ContainerController containerController = (ContainerController) GuiController.this.inventorySlots;
                return containerController.getController().isPrivate() ? Arrays.asList(I18n.format("portality.display.make_public")) : Arrays.asList(I18n.format("portality.display.make_private"));
            }
        });
        this.addButton(new TooltipGuiButton(5, this.guiLeft - 22, this.guiTop + 14 + 22 * 2, 20, 20, "") {
            @Override
            public Rectangle getTexture() {
                ContainerController containerController = (ContainerController) GuiController.this.inventorySlots;
                if (containerController.getController().isDisplayNameEnabled()) return new Rectangle(176, 42, 20, 20);
                return new Rectangle(197, 42, 20, 20);
            }

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                if (isMouseOver()) {
                    ContainerController containerController = (ContainerController) GuiController.this.inventorySlots;
                    Portality.NETWORK.sendToServer(new PortalDisplayToggleMessage(containerController.getController().getPos().toLong()));
                }
                return super.mousePressed(mc, mouseX, mouseY);
            }

            @Override
            public List<String> getTooltip() {
                ContainerController containerController = (ContainerController) GuiController.this.inventorySlots;
                return containerController.getController().isDisplayNameEnabled() ? Arrays.asList(I18n.format("portality.display.hide_name")) : Arrays.asList(I18n.format("portality.display.show_name"));
            }
        });
//        this.addButton(new TooltipGuiButton(6, this.guiLeft - 21, this.guiTop + this.xSize-41, 20, 20, "") {
//            @Override
//            public Rectangle getTexture() {
//                return new Rectangle();
//            }
//            @Override
//            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
//                if (isMouseOver()) {
//                    ContainerController containerController = (ContainerController) GuiController.this.inventorySlots;
//                    UUID id = containerController.getController().getID();
//                    if (id != null){
//                        StringSelection stringSelection = new StringSelection(id.toString().split("-")[0]);
//                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//                        clipboard.setContents(stringSelection, null);
//                    }
//                }
//                return super.mousePressed(mc, mouseX, mouseY);
//            }
//
//            @Override
//            public List<String> getTooltip() {
//                return Arrays.asList(I18n.format("portality.display.copy_to_clipboard"));
//            }
//        });
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(BG);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        drawTexturedModalRect(x - 25, y + 9, 0, 110, 25, 97);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String name = new TextComponentTranslation(CommonProxy.BLOCK_CONTROLLER.getTranslationKey() + ".name").getFormattedText();
        fontRenderer.drawString(TextFormatting.DARK_AQUA + name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 3, 0x000000);

        ContainerController containerController = (ContainerController) this.inventorySlots;
        fontRenderer.drawString(I18n.format("portality.gui.controller.name") + " " + containerController.getController().getName().substring(0, Math.min(containerController.getController().getName().length(), 28)), 10, 21, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.private") + " " + containerController.getController().isPrivate(), 10, 21 + (fontRenderer.FONT_HEIGHT + 1) * 1, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.max_distance") + " " + BlockPosUtils.getMaxDistance(containerController.getController().getLength()), 10, 21 + (fontRenderer.FONT_HEIGHT + 1) * 2, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.interdimensional") + " " + containerController.getController().isInterdimensional(), 10, 21 + (fontRenderer.FONT_HEIGHT + 1) * 3, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.power") + " " + new DecimalFormat().format(containerController.getController().getEnergy().getEnergyStored()) + " FE", 10, 21 + (fontRenderer.FONT_HEIGHT + 1) * 4, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.link") + " " + (containerController.getController().isActive() ? I18n.format("portality.gui.controller.link_active") : I18n.format("portality.gui.controller.link_missing")), 10, 21 + (fontRenderer.FONT_HEIGHT + 1) * 5, 0xFFFFFF);

        // this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.SIGN), -19, 19);
        //this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.TRIPWIRE_HOOK), -19, 19 + 20);
//        this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.PAINTING), -19, 19 + 20 * 2 + 1);
//        this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.PAPER), -19,  this.xSize-39  );

        for (GuiButton button : this.buttonList) {
            if (button instanceof IHasTooltip && button.isMouseOver()) {
                this.drawHoveringText(((IHasTooltip) button).getTooltip(), mouseX - this.guiLeft, mouseY - this.guiTop);
            }
        }
    }
}
