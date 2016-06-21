package caveworld.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityUniversalChestFX extends EntityPortalFX
{
	public EntityUniversalChestFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ)
	{
		super(world, x, y, z, motionX, motionY, motionZ);
		float f = rand.nextFloat() * 0.5F + 0.4F;
		this.particleRed = this.particleGreen = this.particleBlue = 0.65F * f * 0.8F;
	}
}