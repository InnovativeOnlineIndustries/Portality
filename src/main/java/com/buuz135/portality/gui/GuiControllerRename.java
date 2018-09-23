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
import com.buuz135.portality.network.PortalRenameMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;

public class GuiControllerRename extends GuiContainer {

    private GuiTextField field;

    public GuiControllerRename(ContainerController inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Override
    public void initGui() {
        super.initGui();
        ContainerController containerController = (ContainerController) GuiControllerRename.this.inventorySlots;
        field = new GuiTextField(0, fontRenderer, this.guiLeft, this.guiTop, this.xSize, 20);
        field.setFocused(true);
        field.setMaxStringLength(28);
        field.setText(containerController.getController().getName());
        field.setSelectionPos(0);
        this.addButton(new GuiButton(2, this.guiLeft, this.guiTop + 25, this.xSize, 20, I18n.format("portality.gui.controller.rename")) {

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                if (isMouseOver()) {
                    ContainerController containerController = (ContainerController) GuiControllerRename.this.inventorySlots;
                    BlockPos pos = containerController.getController().getPos();
                    Portality.NETWORK.sendToServer(new PortalRenameMessage(field.getText(), pos));
                    mc.player.closeScreen();
                    mc.player.openGui(Portality.INSTANCE, 0, containerController.getController().getWorld(), pos.getX(), pos.getY(), pos.getZ());
                }
                return super.mousePressed(mc, mouseX, mouseY);
            }

        });
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
        field.drawTextBox();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        field.updateCursorCounter();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (field.isFocused()) {
            field.textboxKeyTyped(typedChar, keyCode);
        }
    }


}
