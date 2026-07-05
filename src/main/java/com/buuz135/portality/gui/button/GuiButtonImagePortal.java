package com.buuz135.portality.gui.button;

import com.buuz135.portality.Portality;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.gui.PortalsScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GuiButtonImagePortal extends Button {

    private static final ResourceLocation PORTALS = ResourceLocation.fromNamespaceAndPath(Portality.MOD_ID, "textures/gui/portals.png");
    private static final ResourceLocation LOCK = ResourceLocation.fromNamespaceAndPath(Portality.MOD_ID, "textures/gui/lock.png");

    private final PortalInformation information;
    private final PortalsScreen portals;

    public GuiButtonImagePortal(PortalsScreen guiPortals, PortalInformation information, int x, int y, int width, int height, int textureX, int textureY, int offset, ResourceLocation location) {
        super(x, y, width, height, Component.empty(), button -> {
        }, DEFAULT_NARRATION);
        this.information = information;
        this.portals = guiPortals;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(PORTALS, getX(), getY(), 0, 234, width, height, 256, 256);
        graphics.renderItem(information.getDisplay(), getX() + 5, getY() + 3);

        ChatFormatting color = ChatFormatting.RESET;
        if (information.isPrivate()) color = ChatFormatting.GOLD;
        if (information.isActive()) color = ChatFormatting.RED;
        String name = color + information.getName().substring(0, Math.min(information.getName().length(), 25));
        graphics.drawString(Minecraft.getInstance().font, name, getX() + 28, getY() + 7, isMouseOver(mouseX, mouseY) ? 16777120 : 0xFFFFFFFF, true);

        if (information.isPrivate()) {
            graphics.blit(LOCK, getX() + 4, getY() + 14, 0, 0, 8, 8, 8, 8);
        }
        if (portals.getSelectedPortal() == information) {
            graphics.blit(PORTALS, getX(), getY(), 0, 210, 157, 22, 256, 256);
        }
    }

    public PortalInformation getInformation() {
        return information;
    }
}
