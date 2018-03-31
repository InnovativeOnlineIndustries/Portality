package com.buuz135.portality.gui;

import com.buuz135.portality.Portality;
import com.buuz135.portality.network.PortalRenameMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
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
        this.addButton(new GuiButton(2, this.guiLeft, this.guiTop + 25, this.xSize, 20, "Rename") {

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
