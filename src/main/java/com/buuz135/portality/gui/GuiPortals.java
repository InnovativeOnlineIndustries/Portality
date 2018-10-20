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
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.gui.button.GuiButtonImagePortal;
import com.buuz135.portality.gui.button.IHasTooltip;
import com.buuz135.portality.gui.button.PortalCallButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiPortals extends GuiContainer {

    private ContainerController controller;
    private List<PortalInformation> informationList;
    private GuiTextField textField;
    private double scrolling;
    private double lastScrolling;
    private boolean isDragging;
    private int visiblePortalInformations;
    private List<GuiButtonImagePortal> portalButtons;
    private PortalInformation selectedPortal;

    public GuiPortals(ContainerController controller) {
        super(controller);
        this.xSize = 200;
        this.ySize = 186;
        this.controller = controller;
        this.scrolling = 0;
        this.lastScrolling = 0;
        this.portalButtons = new ArrayList<>();
    }

    @Override
    public void initGui() {
        super.initGui();
        if (informationList != null && !informationList.isEmpty()) addPortalButtons();
        textField = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, this.guiLeft + xSize - 131, this.guiTop + 3, 100, 10);
        textField.setFocused(true);
        textField.setVisible(true);
        textField.setEnableBackgroundDrawing(true);
        this.addButton(new PortalCallButton(135, this.guiLeft + 9, this.guiTop + ySize + 2, controller.getController(), PortalCallButton.CallAction.OPEN, this));
        this.addButton(new PortalCallButton(136, this.guiLeft + 53 + 9, this.guiTop + ySize + 2, controller.getController(), PortalCallButton.CallAction.ONCE, this));
        this.addButton(new PortalCallButton(137, this.guiLeft + 53 * 2 + 9, this.guiTop + ySize + 2, controller.getController(), PortalCallButton.CallAction.FORCE, this));
    }

    public void refresh(List<PortalInformation> informationList) {
        this.informationList = informationList;
        addPortalButtons();
    }

    private void addPortalButtons() {
        if (this.informationList == null) return;
        List<PortalInformation> informationList = new ArrayList<>(this.informationList);
        informationList.removeIf(information -> information.isPrivate() && !information.getOwner().equals(mc.player.getUniqueID()));
        informationList.sort((o1, o2) -> Boolean.compare(o2.isPrivate(), o1.isPrivate()));
        informationList.removeIf(information -> information.getDimension() == controller.getController().getWorld().provider.getDimension() && information.getLocation().equals(controller.getController().getPos()));
        if (!textField.getText().isEmpty())
            informationList.removeIf(portalInformation -> !portalInformation.getName().toLowerCase().contains(textField.getText()));
        this.buttonList.removeIf(guiButton -> portalButtons.contains(guiButton));
        this.portalButtons.clear();
        this.visiblePortalInformations = informationList.size();
        int pointer = (int) ((informationList.size() / 7D) * scrolling);
        for (int i = pointer; i < pointer + 7; i++) {
            if (informationList.size() > i) {
                int finalI = i;
                GuiButtonImagePortal buttonImage = new GuiButtonImagePortal(informationList.get(finalI), finalI + 3, this.guiLeft + 9, this.guiTop + 19 + 23 * (finalI - pointer), 157, 22, 0, 234, 0, new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png")) {
                    @Override
                    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                        if (isMouseOver()) {
                            selectedPortal = informationList.get(finalI);
                        }
                        return super.mousePressed(mc, mouseX, mouseY);
                    }
                };
                this.addButton(buttonImage);
                this.portalButtons.add(buttonImage);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawDefaultBackground();
        checkForScrolling(mouseX, mouseY);
        drawRect(this.guiLeft + 5, this.guiTop + 15, this.guiLeft + 165 + 5, this.guiTop + 168 + 15, 0xff242424);
        GlStateManager.color(1, 1, 1, 1);
        //Render Buttons
        mc.getTextureManager().bindTexture(new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png"));
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        drawTexturedModalRect(this.guiLeft + xSize - 22, (float) (this.guiTop + 10 + 140 * scrolling), 200, 9, 18, 23);

        textField.drawTextBox();
        GlStateManager.color(1, 1, 1, 1);

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        for (GuiButtonImagePortal portalButton : portalButtons) {
            if (selectedPortal != null && portalButton.getInformation().getId().equals(selectedPortal.getId())) {
                mc.getTextureManager().bindTexture(new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png"));
                drawTexturedModalRect(portalButton.x - this.guiLeft, portalButton.y - this.guiTop, 0, 210, 157, 22);
            }
        }
        for (GuiButton button : this.buttonList) {
            if (button instanceof IHasTooltip && button.isMouseOver()) {
                this.drawHoveringText(((IHasTooltip) button).getTooltip(), mouseX - this.guiLeft, mouseY - this.guiTop);
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        textField.updateCursorCounter();
        addPortalButtons();
    }

    @Override
    protected void keyTyped(char c, int keyCode) {
        if (keyCode == 1) {
            this.mc.player.closeScreen();
        }
        String text = textField.getText();
        textField.textboxKeyTyped(c, keyCode);
        if (!text.equals(textField.getText())) {
            this.scrolling = 0;
            this.lastScrolling = 0;
            addPortalButtons();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        int mouseS = Mouse.getEventDWheel();
        if (mouseS != 0) {
            double step = 1 / (visiblePortalInformations / 7D);
            if (mouseS < 0) {
                lastScrolling = scrolling;
                scrolling = Math.min(1, scrolling + step);
            }
            if (mouseS > 0) {
                lastScrolling = scrolling;
                scrolling = Math.max(0, scrolling - step);
            }
        }
        super.handleMouseInput();
    }

    private void checkForScrolling(int mouseX, int mouseY) {
        if (Mouse.isButtonDown(0)) {
            if (!isDragging && mouseX > this.guiLeft + xSize - 22 && mouseX < this.guiLeft + xSize - 22 + 18 && mouseY > this.guiTop + 10 && mouseY < this.guiTop + 10 + 151) {
                isDragging = true;
            }
            if (isDragging) {
                mouseY -= (7 + 18 + this.guiTop);
                lastScrolling = scrolling;
                scrolling = MathHelper.clamp(mouseY / 128D, 0, 1);
            }
        } else {
            isDragging = false;
        }
    }

    public PortalInformation getSelectedPortal() {
        return selectedPortal;
    }
}
