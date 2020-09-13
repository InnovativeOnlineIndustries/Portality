package com.buuz135.portality.gui;

import com.buuz135.portality.Portality;
import com.buuz135.portality.gui.button.PortalSaveButton;
import com.buuz135.portality.tile.ControllerTile;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.ScreenAddonScreen;
import com.hrznstudio.titanium.client.screen.addon.color.ColorPickerAddon;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class ChangeColorScreen extends ScreenAddonScreen {

    private ControllerTile controllerTile;
    private TextFieldWidget textField;
    private int color;
    private ColorPickerAddon colorPickerAddon;
    private boolean textChangedManually;

    public ChangeColorScreen(ControllerTile tile) {
        super(PortalityAssetProvider.PROVIDER, false);
        this.controllerTile = tile;
        this.color = tile.getColor();
        this.textChangedManually = false;
    }

    @Override
    public void init() {
        super.init();
        textField = new TextFieldWidget(Minecraft.getInstance().fontRenderer, this.x + 14, this.y + 120, 80, 16, new StringTextComponent(""));
        //textField.setFocused2(true);
        textField.setVisible(true);
        textField.setEnableBackgroundDrawing(true);
        textField.setMaxStringLength(6);
        textField.setResponder(s -> {
            if (textChangedManually) {
                textChangedManually = false;
                return;
            }
            if (s.length() > 0) {
                try {
                    int tempColor = 0xff000000 | Integer.parseInt(s, 16);
                    if (tempColor != color) {
                        color = tempColor;
                        colorPickerAddon.setColor(color);
                    }
                } catch (NumberFormatException e) {
                }
            }
        });
        updateColor(color);
        addListener(textField);
        this.getAddons().add(new PortalSaveButton(this.x + 110, this.y + 116, controllerTile, "Save", this));
    }

    @Override
    public void renderBackground(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(Portality.MOD_ID, "textures/gui/color_change.png"));
        Minecraft.getInstance().currentScreen.blit(stack, x, y, 0, 0, 175, 146);
        AbstractGui.fill(stack, x + 13, y + 9, x + 15 + 100, y + 91, -16739073);
        AbstractGui.fill(stack, x + 123, y + 9, x + 121 + 40, y + 91, -16739073);
        AbstractGui.fill(stack, x + 13, y + 99, x + 13 + 148, y + 109, -16739073);
        super.renderBackground(stack, mouseX, mouseY, partialTicks);
        textField.renderButton(stack, mouseX, mouseY, partialTicks);
        AssetUtil.drawHorizontalLine(stack, textField.x - 1, textField.x + textField.getWidth(), textField.y - 1, -16739073);
        AssetUtil.drawHorizontalLine(stack, textField.x - 1, textField.x + textField.getWidth(), textField.y + textField.getHeightRealms(), -16739073);
        AssetUtil.drawVerticalLine(stack, textField.x - 1, textField.y - 1, textField.y + textField.getHeightRealms(), -16739073);
        AssetUtil.drawVerticalLine(stack, textField.x + textField.getWidth(), textField.y - 1, textField.y + textField.getHeightRealms(), -16739073);
    }

    @Override
    public void tick() {
        super.tick();
        textField.tick();
    }

    @Override
    public List<IFactory<IScreenAddon>> guiAddons() {
        List<IFactory<IScreenAddon>> addons = new ArrayList<>();
        addons.add(() -> this.colorPickerAddon = new ColorPickerAddon(14, 10, this.color, this::updateColor));
        return addons;
    }

    public void updateColor(int color) {
        this.color = color;
        if (textField != null) {
            this.textChangedManually = true;
            textField.setText(Integer.toHexString(color).substring(2));
        }
    }

    public int getColor() {
        return color;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return textField.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
