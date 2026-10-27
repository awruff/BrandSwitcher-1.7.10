package com.github.awruff.brandswitcher;

import com.github.awruff.brandswitcher.BrandSwitcherConfig.Preset;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

public class BrandSwitcherGui extends GuiScreen {
    private final GuiScreen parent;
    private GuiButton enabledButton;
    private GuiButton presetButton;
    private GuiButton antiForgeButton;
    private GuiTextField brandField;

    public BrandSwitcherGui(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        int x = width / 2 - 100;
        int y = height / 4;
        buttonList.clear();
        buttonList.add(enabledButton = new GuiButton(0, x, y, ""));
        buttonList.add(presetButton = new GuiButton(1, x, y + 24, ""));
        buttonList.add(antiForgeButton = new GuiButton(2, x, y + 72, ""));
        buttonList.add(new GuiButton(3, x, y + 120, "Done"));
        brandField = new GuiTextField(fontRendererObj, x + 1, y + 49, 198, 18);
        brandField.setMaxStringLength(256);
        brandField.setText(BrandSwitcherConfig.brand);
        updateLabels();
    }

    private void updateLabels() {
        enabledButton.displayString = "Enabled: " + (BrandSwitcherConfig.enabled ? "ON" : "OFF");
        presetButton.displayString = "Preset: " + Preset.of(BrandSwitcherConfig.brand).label;
        antiForgeButton.displayString = "Anti-Forge: " + (BrandSwitcherConfig.antiForge ? "ON" : "OFF");
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                BrandSwitcherConfig.enabled = !BrandSwitcherConfig.enabled;
                break;
            case 1:
                Preset[] presets = Preset.values();
                Preset next = presets[(Preset.of(BrandSwitcherConfig.brand).ordinal() + 1) % presets.length];
                if (next == Preset.CUSTOM) next = presets[0];
                BrandSwitcherConfig.brand = next.brand;
                brandField.setText(next.brand);
                break;
            case 2:
                BrandSwitcherConfig.antiForge = !BrandSwitcherConfig.antiForge;
                break;
            case 3:
                mc.displayGuiScreen(parent);
                return;
        }
        updateLabels();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (brandField.textboxKeyTyped(typedChar, keyCode)) {
            BrandSwitcherConfig.brand = brandField.getText();
            updateLabels();
        } else if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        brandField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        brandField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, BrandSwitcher.NAME, width / 2, height / 4 - 24, 0xFFFFFF);
        drawString(fontRendererObj, "Brand", width / 2 - 100, height / 4 + 38, 0xA0A0A0);
        brandField.drawTextBox();
        if (BrandSwitcherConfig.needsRejoin()) {
            drawCenteredString(fontRendererObj, "You must rejoin for changes to take effect!", width / 2, height / 4 + 100, 0xFFAA00);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        BrandSwitcherConfig.save();
    }
}
