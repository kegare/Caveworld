package caveworld.item;

import caveworld.core.Caveworld;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemCaveArmor extends ItemArmor
{
	private final String renderName;

	public ItemCaveArmor(String name, String texture, String renderName, ArmorMaterial material, int armorType)
	{
		super(material, 0, armorType);
		this.renderName = renderName;
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:" + texture);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
	{
		if (slot >= 0 && slot < 4)
		{
			return String.format("caveworld:textures/models/armor/%s_layer_%d.png", renderName, slot == 2 ? 2 : 1);
		}

		return super.getArmorTexture(stack, entity, slot, type);
	}
}