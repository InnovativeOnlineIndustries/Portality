package com.buuz135.portality.gui.button;

import com.buuz135.portality.Portality;
import com.buuz135.portality.data.PortalLinkData;
import com.buuz135.portality.gui.GuiPortals;
import com.buuz135.portality.network.PortalLinkMessage;
import com.buuz135.portality.tile.TileController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class PortalCallButton extends GuiButtonImage {

    private final CallAction action;
    private final TileController controller;
    private final GuiPortals guiPortals;

    public PortalCallButton(int id, int x, int y, TileController tile, CallAction action, GuiPortals guiPortals) {
        super(id, x, y, 51, 22, 0, 187, 0, new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png"));
        this.action = action;
        this.controller = tile;
        this.guiPortals = guiPortals;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (isMouseOver() && guiPortals.getSelectedPortal() != null) {
            Portality.NETWORK.sendToServer(new PortalLinkMessage(action.getId(), new PortalLinkData(controller.getWorld().provider.getDimension(), controller.getPos(), true), new PortalLinkData(guiPortals.getSelectedPortal().getDimension(), guiPortals.getSelectedPortal().getLocation(), false)));
            mc.player.closeScreen();
        }
        return super.mousePressed(mc, mouseX, mouseY);
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
        super.drawButtonForegroundLayer(mouseX, mouseY);

    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mc, mouseX, mouseY, partialTicks);
        drawCenteredString(Minecraft.getMinecraft().fontRenderer, I18n.format(action.getName()), x + 25, y + 7, isMouseOver() ? 16777120 : 0xFFFFFFFF);
        GlStateManager.color(1, 1, 1, 1);
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
