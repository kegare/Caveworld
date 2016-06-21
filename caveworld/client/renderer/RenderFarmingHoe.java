package caveworld.client.renderer;

import org.lwjgl.opengl.GL11;

import caveworld.item.ItemFarmingHoe;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class RenderFarmingHoe implements IItemRenderer
{
	private final Minecraft mc = FMLClientHandler.instance().getClient();
	private final RenderItem itemRender = RenderItem.getInstance();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return type == ItemRenderType.INVENTORY;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		FontRenderer renderer = item.getItem().getFontRenderer(item);

		if (renderer == null)
		{
			renderer = mc.fontRenderer;
		}

		itemRender.renderItemIntoGUI(renderer, mc.getTextureManager(), item, 0, 0, true);

		Item base = ((ItemFarmingHoe)item.getItem()).getBase(item);

		if (base != item.getItem() && base.getIconIndex(item) != item.getIconIndex())
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(-9.0F, 14.0F, -1.0F);
			GL11.glScalef(0.6F, 0.6F, 1.0F);
			GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
			itemRender.renderItemIntoGUI(renderer, mc.getTextureManager(), new ItemStack(base), 0, item.isItemDamaged() ? 13 : 16, true);
			GL11.glPopMatrix();
		}
	}
}