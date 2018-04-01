package com.buuz135.portality.gui;

import com.buuz135.portality.Portality;
import com.buuz135.portality.data.PortalInformation;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class GuiPortals extends GuiContainer {

    private ContainerController controller;
    private int pointer;
    private List<PortalInformation> informationList;


    public GuiPortals(ContainerController controller) {
        super(controller);
        this.controller = controller;
        pointer = 0;
    }

    @Override
    public void initGui() {
        super.initGui();
        if (informationList != null && !informationList.isEmpty()) addPortalButtons();
    }

    public void refresh(List<PortalInformation> informationList) {
        this.informationList = informationList;
        pointer = 0;
        informationList.removeIf(information -> information.isPrivate() && !information.getOwner().equals(mc.player.getUniqueID()));
        informationList.sort((o1, o2) -> Boolean.compare(o2.isPrivate(), o1.isPrivate()));
        this.buttonList.clear();
        addPortalButtons();
    }

    private void addPortalButtons() {
        buttonList.clear();
        for (int i = 0; i < 4; i++) {
            GuiButtonImagePortal buttonImage = new GuiButtonImagePortal(informationList.get(pointer + i), i + 3, this.guiLeft + 5, this.guiTop + 6 + 39 * i, 166, 36, 0, 166, 0, new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png"));
            this.addButton(buttonImage);
        }
        this.addButton(new GuiButton(0, this.guiLeft, this.guiTop - 20, 20, 20, "<"));
        this.addButton(new GuiButton(1, this.guiLeft + this.xSize - 20, this.guiTop - 20, 20, 20, ">"));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String name = "PAGE: " + (pointer + 1) + "/" + ((int) Math.ceil(informationList.size() / 4D));
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, -14, 0xFFFFFF);


        for (GuiButton button : this.buttonList) {
            if (button instanceof IHasTooltip && button.isMouseOver()) {
                this.drawHoveringText(((IHasTooltip) button).getTooltip(), mouseX - this.guiLeft, mouseY - this.guiTop);
            }
        }
    }


}
