package caveworld.client.config;

import java.util.List;

import com.google.common.collect.Lists;

import caveworld.config.Config;
import caveworld.core.Caveworld;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

@SideOnly(Side.CLIENT)
public class MobsEntry extends CaveCategoryEntry
{
	public MobsEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return Config.mobsCfg;
	}

	@Override
	protected String getEntryName()
	{
		return "mobs";
	}

	@Override
	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();

		for (String category : getConfig().getCategoryNames())
		{
			list.add(new EntityElement(getConfig().getCategory(category)));
		}

		return list;
	}

	private class EntityElement extends ConfigElement
	{
		public EntityElement(ConfigCategory category)
		{
			super(category);
		}

		@Override
		public String getName()
		{
			return I18n.format("entity.caveworld." + super.getName() + ".name");
		}

		@Override
		public String getComment()
		{
			return I18n.format(Caveworld.CONFIG_LANG + getEntryName() +".entry.tooltip", getName());
		}
	}
}