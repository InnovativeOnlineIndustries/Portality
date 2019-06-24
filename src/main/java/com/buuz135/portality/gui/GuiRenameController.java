package com.buuz135.portality.gui;

import com.buuz135.portality.network.PortalRenameMessage;
import com.buuz135.portality.tile.TileController;
import com.hrznstudio.titanium.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiRenameController extends Screen {

    private final TileController controller;
    private TextFieldWidget textFieldWidget;
    private Button confirm;

    public GuiRenameController(TileController controller) {
        super(new TranslationTextComponent("portality.gui.controller.rename"));
        this.controller = controller;
    }

    @Override
    protected void init() {
        super.init();
        int textFieldWidth = 140;
        textFieldWidget = new TextFieldWidget(Minecraft.getInstance().fontRenderer, width / 2 - textFieldWidth / 2, height / 2 - 10, textFieldWidth, 18, "");
        textFieldWidget.setCanLoseFocus(false);
        textFieldWidget.setMaxStringLength(28);
        textFieldWidget.setSelectionPos(0);
        textFieldWidget.setText(this.controller.getPortalDisplayName());
        textFieldWidget.setFocused2(true);
        children.add(textFieldWidget);
        this.setFocused(this.textFieldWidget);

        confirm = new Button(width / 2 + textFieldWidth / 2 + 5, height / 2 - 10, 50, 18, "Confirm", button -> {
            NetworkHandler.NETWORK.sendToServer(new PortalRenameMessage(textFieldWidget.getText(), controller.getPos()));
            onClose();
        });
        addButton(confirm);
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        renderBackground();
        super.render(p_render_1_, p_render_2_, p_render_3_);
        textFieldWidget.render(p_render_1_, p_render_2_, p_render_3_);
        String rename = new TranslationTextComponent("portality.gui.controller.rename").getFormattedText();
        font.drawString(rename, width / 2 - font.getStringWidth(rename) / 2, height / 2 - 30, 0xFFFFFF);
    }

    @Override
    public void tick() {
        super.tick();
        textFieldWidget.tick();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().displayGuiScreen(new GuiController(controller));
    }
}
