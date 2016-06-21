package caveworld.client.gui;

import java.awt.Desktop;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import caveworld.network.CaveNetworkRegistry;
import caveworld.network.client.RegenerationGuiMessage.EnumType;
import caveworld.network.server.RegenerationMessage;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.HoverChecker;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.DimensionManager;

public class GuiRegeneration extends GuiScreen
{
	public static boolean backup = true;

	private boolean caveworld = true;
	private boolean cavern = true;
	private boolean aquaCavern = true;
	private boolean caveland = true;
	private boolean cavenia = true;

	protected GuiButton regenButton, openButton, cancelButton;
	protected GuiCheckBox caveworldCheckBox, cavernCheckBox, aquaCavernCheckBox, cavelandCheckBox, caveniaCheckBox, backupCheckBox;

	private HoverChecker backupHoverChecker;

	public GuiRegeneration() {}

	public GuiRegeneration(boolean caveworld, boolean cavern, boolean aquaCavern, boolean caveland, boolean cavenia)
	{
		this.caveworld = caveworld;
		this.cavern = cavern;
		this.aquaCavern = aquaCavern;
		this.caveland = caveland;
		this.cavenia = cavenia;
	}

	@Override
	public void initGui()
	{
		if (regenButton == null)
		{
			regenButton = new GuiButtonExt(0, 0, 0, I18n.format("caveworld.regeneration.gui.regenerate"));
		}

		regenButton.xPosition = width / 2 - 100;
		regenButton.yPosition = height / 4 + regenButton.height + 65;

		if (openButton == null)
		{
			openButton = new GuiButtonExt(1, 0, 0, I18n.format("caveworld.regeneration.gui.backup.open"));
			openButton.visible = false;
		}

		openButton.xPosition = regenButton.xPosition;
		openButton.yPosition = regenButton.yPosition;

		if (cancelButton == null)
		{
			cancelButton = new GuiButtonExt(2, 0, 0, I18n.format("gui.cancel"));
		}

		cancelButton.xPosition = regenButton.xPosition;
		cancelButton.yPosition = regenButton.yPosition + regenButton.height + 5;

		if (caveworldCheckBox == null)
		{
			caveworldCheckBox = new GuiCheckBox(3, 10, 8, "Caveworld", caveworld);
		}

		if (cavernCheckBox == null)
		{
			cavernCheckBox = new GuiCheckBox(4, 10, caveworldCheckBox.yPosition + caveworldCheckBox.height + 5, "Cavern", cavern);
		}

		if (aquaCavernCheckBox == null)
		{
			aquaCavernCheckBox = new GuiCheckBox(5, 10, cavernCheckBox.yPosition + cavernCheckBox.height + 5, "Aqua Cavern", aquaCavern);
		}

		if (cavelandCheckBox == null)
		{
			cavelandCheckBox = new GuiCheckBox(6, 10, aquaCavernCheckBox.yPosition + aquaCavernCheckBox.height + 5, "Caveland", caveland);
		}

		if (caveniaCheckBox == null)
		{
			caveniaCheckBox = new GuiCheckBox(7, 10, cavelandCheckBox.yPosition + cavelandCheckBox.height + 5, "Cavenia", cavenia);
		}

		if (backupCheckBox == null)
		{
			backupCheckBox = new GuiCheckBox(8, 10, 0, I18n.format("caveworld.regeneration.gui.backup"), backup);
		}

		backupCheckBox.yPosition = height - 20;

		buttonList.clear();
		buttonList.add(regenButton);
		buttonList.add(openButton);
		buttonList.add(cancelButton);
		buttonList.add(caveworldCheckBox);
		buttonList.add(cavernCheckBox);
		buttonList.add(aquaCavernCheckBox);
		buttonList.add(cavelandCheckBox);
		buttonList.add(caveniaCheckBox);
		buttonList.add(backupCheckBox);

		if (backupHoverChecker == null)
		{
			backupHoverChecker = new HoverChecker(backupCheckBox, 800);
		}
	}

	@Override
	public void handleKeyboardInput()
	{
		super.handleKeyboardInput();

		if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT || Keyboard.getEventKey() == Keyboard.KEY_RSHIFT)
		{
			openButton.visible = Keyboard.getEventKeyState();
			regenButton.visible = !openButton.visible;
		}
	}

	@Override
	protected void keyTyped(char c, int code)
	{
		if (code == Keyboard.KEY_ESCAPE)
		{
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					boolean caveworld = caveworldCheckBox.isChecked();
					boolean cavern = cavernCheckBox.isChecked();
					boolean aquaCavern = aquaCavernCheckBox.isChecked();
					boolean caveland = cavelandCheckBox.isChecked();
					boolean cavenia = caveniaCheckBox.isChecked();

					if (!caveworld && !cavern && !aquaCavern && !caveland && !cavenia)
					{
						break;
					}

					CaveNetworkRegistry.sendToServer(new RegenerationMessage(backupCheckBox.isChecked(), caveworld, cavern, aquaCavern, caveland, cavenia));

					regenButton.enabled = false;
					cancelButton.visible = false;
					break;
				case 1:
					try
					{
						Desktop.getDesktop().open(DimensionManager.getCurrentSaveRootDirectory());
					}
					catch (Exception e) {}

					break;
				case 2:
					mc.displayGuiScreen(null);
					mc.setIngameFocus();
					break;
				case 8:
					backup = backupCheckBox.isChecked();
					break;
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		drawGradientRect(0, 0, width, height, Integer.MIN_VALUE, Integer.MAX_VALUE);

		GL11.glPushMatrix();
		GL11.glScalef(1.5F, 1.5F, 1.0F);
		drawCenteredString(fontRendererObj, I18n.format("caveworld.regeneration.gui.title"), width / 3, 30, 0xFFFFFF);
		GL11.glPopMatrix();

		drawCenteredString(fontRendererObj, I18n.format("caveworld.regeneration.gui.info"), width / 2, 90, 0xEEEEEE);

		super.drawScreen(mouseX, mouseY, ticks);

		if (backupHoverChecker.checkHover(mouseX, mouseY))
		{
			func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format("caveworld.regeneration.gui.backup.tooltip"), 300), mouseX, mouseY);
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	public void updateProgress(EnumType type)
	{
		regenButton.enabled = false;
		cancelButton.visible = false;
		caveworldCheckBox.visible = false;
		cavernCheckBox.visible = false;
		aquaCavernCheckBox.visible = false;
		cavelandCheckBox.visible = false;
		caveniaCheckBox.visible = false;
		backupCheckBox.visible = false;

		if (type == null)
		{
			regenButton.visible = false;
			cancelButton.visible = true;
		}
		else switch (type)
		{
			case START:
				regenButton.displayString = I18n.format("caveworld.regeneration.gui.progress.start");
				break;
			case BACKUP:
				regenButton.displayString = I18n.format("caveworld.regeneration.gui.progress.backup");
				break;
			case SUCCESS:
				regenButton.displayString = I18n.format("caveworld.regeneration.gui.progress.success");
				cancelButton.displayString = I18n.format("gui.done");
				cancelButton.visible = true;
				break;
			case FAILED:
				regenButton.displayString = I18n.format("caveworld.regeneration.gui.progress.failed");
				cancelButton.visible = true;
			default:
		}
	}
}