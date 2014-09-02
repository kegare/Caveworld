package com.kegare.caveworld.client.gui;

import java.awt.Desktop;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.network.RegenerateMessage;
import com.kegare.caveworld.world.WorldProviderCaveworld;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.HoverChecker;

public class GuiRegenerate extends GuiScreen implements GuiYesNoCallback
{
	private boolean backup;

	private GuiButton regenButton;
	private GuiButton browseButton;
	private GuiButton cancelButton;
	private GuiCheckBox backupCheckBox;

	private HoverChecker backupHoverChecker;

	public GuiRegenerate() {}

	public GuiRegenerate(boolean backup)
	{
		this.backup = backup;
	}

	@Override
	public void initGui()
	{
		regenButton = new GuiButtonExt(0, width / 2 - 100, height / 4 + 82, I18n.format("caveworld.regenerate.gui.regenerate"));
		browseButton = new GuiButtonExt(1, regenButton.xPosition, regenButton.yPosition, I18n.format("caveworld.regenerate.gui.backup.open"));
		browseButton.visible = false;
		cancelButton = new GuiButtonExt(2, regenButton.xPosition, height / 4 + 106, I18n.format("gui.cancel"));
		backupCheckBox = new GuiCheckBox(3, 10, height - 21, I18n.format("caveworld.regenerate.gui.backup"), backup);

		buttonList.clear();
		buttonList.add(regenButton);
		buttonList.add(browseButton);
		buttonList.add(cancelButton);
		buttonList.add(backupCheckBox);

		backupHoverChecker = new HoverChecker(backupCheckBox, 800);
	}

	@Override
	public void handleKeyboardInput()
	{
		super.handleKeyboardInput();

		if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT || Keyboard.getEventKey() == Keyboard.KEY_RSHIFT)
		{
			browseButton.visible = Keyboard.getEventKeyState();
			regenButton.visible = !browseButton.visible;
		}
	}

	@Override
	protected void keyTyped(char c, int code)
	{
		if (code == Keyboard.KEY_ESCAPE)
		{
			mc.displayGuiScreen(null);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		switch (button.id)
		{
			case 0:
				Caveworld.network.sendToServer(new RegenerateMessage(backupCheckBox.isChecked()));
				regenButton.enabled = false;
				cancelButton.visible = false;
				break;
			case 1:
				try
				{
					Desktop.getDesktop().open(WorldProviderCaveworld.getDimDir().getParentFile());
				}
				catch (Exception e) {}

				break;
			case 2:
				mc.displayGuiScreen(null);
				break;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		drawGradientRect(0, 0, width, height, Integer.MIN_VALUE, Integer.MAX_VALUE);
		GL11.glPushMatrix();
		GL11.glScalef(1.5F, 1.5F, 1.5F);
		drawCenteredString(fontRendererObj, I18n.format("caveworld.regenerate.gui.title"), width / 3, 30, 0xFFFFFF);
		GL11.glPopMatrix();
		drawCenteredString(fontRendererObj, I18n.format("caveworld.regenerate.gui.info"), width / 2, 90, 0xEEEEEE);

		super.drawScreen(mouseX, mouseY, ticks);

		if (backupHoverChecker.checkHover(mouseX, mouseY))
		{
			func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format("caveworld.regenerate.gui.backup.tooltip"), 300), mouseX, mouseY);
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	public void updateProgress(int task)
	{
		regenButton.enabled = false;
		cancelButton.visible = false;

		if (task < 0)
		{
			regenButton.visible = false;
			cancelButton.visible = true;
		}
		else switch (task)
		{
			case 0:
				regenButton.displayString = I18n.format("caveworld.regenerate.gui.progress.regenerating");
				break;
			case 1:
				regenButton.displayString = I18n.format("caveworld.regenerate.gui.progress.backingup");
				break;
			case 2:
				regenButton.displayString = I18n.format("caveworld.regenerate.gui.progress.regenerated");
				cancelButton.displayString = I18n.format("gui.done");
				cancelButton.visible = true;
				break;
			case 3:
				regenButton.displayString = I18n.format("caveworld.regenerate.gui.progress.failed");
				cancelButton.visible = true;
		}
	}
}