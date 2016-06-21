package caveworld.world;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class CaveniaSaveHandler extends CaveSaveHandler
{
	public static final int CAVENIC_SKELETON = 0;

	protected int bossType;
	protected boolean bossAlive;

	public CaveniaSaveHandler(String name)
	{
		super(name);
	}

	public int getBossType()
	{
		return bossType;
	}

	public void setBossType(int type)
	{
		bossType = type;

		if (data != null)
		{
			data.setInteger("BossType", type);
		}
	}

	public boolean getBossAlive()
	{
		return bossAlive;
	}

	public void setBossAlive(boolean flag)
	{
		bossAlive = flag;

		if (data != null)
		{
			data.setBoolean("BossAlive", flag);
		}
	}

	@Override
	public void readFromBuffer(ByteBuf buffer)
	{
		super.readFromBuffer(buffer);

		bossType = buffer.readInt();
		bossAlive = buffer.readBoolean();
	}

	@Override
	public void writeToBuffer(ByteBuf buffer)
	{
		super.writeToBuffer(buffer);

		buffer.writeInt(bossType);
		buffer.writeBoolean(bossAlive);
	}

	@Override
	public void loadFromNBT(NBTTagCompound nbt)
	{
		if (nbt == null)
		{
			return;
		}

		super.loadFromNBT(nbt);

		if (!nbt.hasKey("BossType"))
		{
			nbt.setInteger("BossType", 0);
		}

		if (!nbt.hasKey("BossAlive"))
		{
			nbt.setBoolean("BossAlive", true);
		}

		bossType = nbt.getInteger("BossType");
		bossAlive = nbt.getBoolean("BossAlive");
	}
}