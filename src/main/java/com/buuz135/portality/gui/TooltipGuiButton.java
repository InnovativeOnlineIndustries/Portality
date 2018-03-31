package com.buuz135.portality.gui;

import net.minecraft.client.gui.GuiButton;

import java.util.Arrays;
import java.util.List;

public class TooltipGuiButton extends GuiButton {

    public TooltipGuiButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public TooltipGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    public List<String> getTooltip() {
        return Arrays.asList();
    }
}
