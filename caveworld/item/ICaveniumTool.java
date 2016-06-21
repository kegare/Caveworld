package caveworld.item;

import java.util.List;
import java.util.Set;

import caveworld.api.BlockEntry;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ICaveniumTool extends IModeItem
{
	public String getToolClass();

	public List<BlockEntry> getBreakableBlocks();

	public boolean setBreakableToNBT(ItemStack itemstack);

	public int getRefined(ItemStack itemstack);

	public boolean canBreak(ItemStack itemstack, Block block, int metadata);

	public boolean breakAll(ItemStack itemstack, World world, int x, int y, int z, EntityLivingBase entity);

	public Item getBase(ItemStack itemstack);

	public IBreakMode getMode(ItemStack itemstack);

	public boolean setMode(ItemStack itemstack, int id);

	public IBreakMode toggleMode(ItemStack itemstack);

	public Set<Item> getBaseableItems();
}