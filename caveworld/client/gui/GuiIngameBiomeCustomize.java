package caveworld.client.gui;

import caveworld.api.CaveworldAPI;
import caveworld.client.config.GuiBiomesEntry;
import caveworld.core.Caveworld;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiIngameBiomeCustomize extends GuiScreen
{
	private GuiButton backButton;
	private GuiButton caveworldButton, cavernButton, aquaCavernButton;

	@Override
	public void initGui()
	{
		if (backButton == null)
		{
			backButton = new GuiButtonExt(0, 0, 0, I18n.format("menu.returnToGame"));
		}

		backButton.xPosition = width / 2 - 100;
		backButton.yPosition = height / 4 + 8;

		if (caveworldButton == null)
		{
			caveworldButton = new GuiButtonExt(1, 0, 0, "Caveworld");
		}

		caveworldButton.xPosition = backButton.xPosition;
		caveworldButton.yPosition = backButton.yPosition + backButton.height + 5;

		if (cavernButton == null)
		{
			cavernButton = new GuiButtonExt(2, 0, 0, "Cavern");
		}

		cavernButton.xPosition = caveworldButton.xPosition;
		cavernButton.yPosition = caveworldButton.yPosition + caveworldButton.height + 5;

		if (aquaCavernButton == null)
		{
			aquaCavernButton = new GuiButtonExt(3, 0, 0, "Aqua Cavern");
		}

		aquaCavernButton.xPosition = cavernButton.xPosition;
		aquaCavernButton.yPosition = cavernButton.yPosition + cavernButton.height + 5;

		buttonList.clear();
		buttonList.add(backButton);
		buttonList.add(caveworldButton);
		buttonList.add(cavernButton);
		buttonList.add(aquaCavernButton);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					mc.displayGuiScreen(null);
					mc.setIngameFocus();
					break;
				case 1:
					mc.displayGuiScreen(new GuiBiomesEntry(this, CaveworldAPI.biomeManager));
					break;
				case 2:
					mc.displayGuiScreen(new GuiBiomesEntry(this, CaveworldAPI.biomeCavernManager));
					break;
				case 3:
					mc.displayGuiScreen(new GuiBiomesEntry(this, CaveworldAPI.biomeAquaCavernManager));
					break;
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		drawGradientRect(0, 0, width, height, Integer.MIN_VALUE, Integer.MAX_VALUE);
		drawCenteredString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "biomes"), width / 2, 40, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}