package caveworld.client.config;

import caveworld.config.Config;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.config.Configuration;

@SideOnly(Side.CLIENT)
public class GeneralEntry extends CaveCategoryEntry
{
	public GeneralEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return Config.generalCfg;
	}

	@Override
	protected String getEntryName()
	{
		return Configuration.CATEGORY_GENERAL;
	}
}