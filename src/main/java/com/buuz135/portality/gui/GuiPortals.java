package com.buuz135.portality.gui;

import com.buuz135.portality.Portality;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.gui.button.GuiButtonImagePortal;
import com.buuz135.portality.gui.button.PortalCallButton;
import com.buuz135.portality.tile.TileController;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IGuiAddon;
import com.hrznstudio.titanium.client.gui.GuiAddonScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiPortals extends GuiAddonScreen {

    private final int guiHeight;
    private final int guiWidth;
    private List<PortalInformation> informationList;
    private TextFieldWidget textField;
    private double scrolling;
    private double lastScrolling;
    private boolean isDragging;
    private int visiblePortalInformations;
    private List<GuiButtonImagePortal> portalButtons;
    private PortalInformation selectedPortal;
    private TileController controller;

    public GuiPortals(TileController controller) {
        super(PortalityAssetProvider.PROVIDER, false);
        this.guiWidth = 200;
        this.guiHeight = 186;
        this.scrolling = 0;
        this.lastScrolling = 0;
        this.portalButtons = new ArrayList<>();
        this.controller = controller;
    }

    @Override
    public void init() {
        super.init();
        this.x = this.width / 2 - guiWidth / 2;
        this.y = this.height / 2 - guiWidth / 2;
        if (informationList != null && !informationList.isEmpty()) addPortalButtons();
        textField = new TextFieldWidget(Minecraft.getInstance().fontRenderer, this.x + guiWidth - 131, this.y + 3, 100, 10, "");
        textField.setFocused2(true);
        textField.setVisible(true);
        textField.setEnableBackgroundDrawing(true);
        getAddons().add(new PortalCallButton(x + 9, y + guiHeight + 2, controller, PortalCallButton.CallAction.OPEN, this));
        getAddons().add(new PortalCallButton(x + 53 + 9, y + guiHeight + 2, controller, PortalCallButton.CallAction.ONCE, this));
        getAddons().add(new PortalCallButton(x + 53 * 2 + 9, y + guiHeight + 2, controller, PortalCallButton.CallAction.FORCE, this));
    }

    private void addPortalButtons() {
        if (this.informationList == null) return;
        List<PortalInformation> informationList = new ArrayList<>(this.informationList);
        informationList.removeIf(information -> information.isPrivate() && !information.getOwner().equals(Minecraft.getInstance().player.getUniqueID()));
        informationList.sort((o1, o2) -> Boolean.compare(o2.isPrivate(), o1.isPrivate()));
        if (!textField.getText().isEmpty())
            informationList.removeIf(portalInformation -> !portalInformation.getName().toLowerCase().contains(textField.getText()));
        this.buttons.removeIf(guiButton -> portalButtons.contains(guiButton));
        this.portalButtons.clear();
        this.visiblePortalInformations = informationList.size();
        int pointer = (int) ((informationList.size() / 7D) * scrolling);
        for (int i = pointer; i < pointer + 7; i++) {
            if (informationList.size() > i) {
                int finalI = i;
                GuiButtonImagePortal buttonImage = new GuiButtonImagePortal(this, informationList.get(finalI), this.x + 9, this.y + 19 + 23 * (finalI - pointer), 157, 22, 0, 234, 0, new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png")) {
                    @Override
                    public void onClick(double mouseX, double mouseY) {
                        if (isMouseOver(mouseX, mouseY)) {
                            selectedPortal = informationList.get(finalI);
                        }
                        super.onClick(mouseX, mouseY);
                    }
                };
                this.addButton(buttonImage);
                this.portalButtons.add(buttonImage);
            }
        }
    }

    public void refresh(List<PortalInformation> informationList) {
        this.informationList = informationList;
        addPortalButtons();
    }

    @Override
    public void renderBackground(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png"));
        blit(x, y, 0, 0, guiWidth, guiHeight);
        super.renderBackground(mouseX, mouseY, partialTicks);
        textField.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public List<IFactory<IGuiAddon>> guiAddons() {
        return Collections.emptyList();
    }

    @Override
    public void tick() {
        super.tick();
        textField.tick();
    }

    public PortalInformation getSelectedPortal() {
        return selectedPortal;
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
