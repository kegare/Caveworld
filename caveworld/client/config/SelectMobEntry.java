package caveworld.client.config;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.ArrayEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SelectMobEntry extends ArrayEntry
{
	public SelectMobEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
	{
		super(owningScreen, owningEntryList, configElement);
	}

	@Override
	public void valueButtonPressed(int index)
	{
		if (GuiConfig.isShiftKeyDown())
		{
			super.valueButtonPressed(index);
		}
		else if (btnValue.enabled)
		{
			btnValue.func_146113_a(mc.getSoundHandler());

			mc.displayGuiScreen(new GuiSelectMob(owningScreen, this));
		}
	}
}