/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class GuiButtonVolume extends GuiButton
{
	private final String title;
	public float volume;
	public boolean changed;

	private IVolume handler;

	public GuiButtonVolume(int id, int x, int y, String title, float volume, IVolume handler)
	{
		super(id, x, y, "");
		this.title = title;
		this.volume = volume;
		this.displayString = title + ": " + getVolumeString();
		this.handler = handler;
	}

	public String getVolumeString()
	{
		return volume == 0.0F ? I18n.format("options.off") : (int)(volume * 100.0F) + "%";
	}

	@Override
	public int getHoverState(boolean flag)
	{
		return 0;
	}

	@Override
	protected void mouseDragged(Minecraft mc, int x, int y)
	{
		if (visible)
		{
			if (changed)
			{
				volume = (float)(x - (xPosition + 4)) / (float)(width - 8);

				if (volume < 0.0F)
				{
					volume = 0.0F;
				}

				if (volume > 1.0F)
				{
					volume = 1.0F;
				}

				if (handler != null)
				{
					handler.onVolumeChanged(id, volume);
				}

				displayString = title + ": " + getVolumeString();
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(xPosition + (int)(volume * (width - 8)), yPosition, 0, 66, 4, 20);
			drawTexturedModalRect(xPosition + (int)(volume * (width - 8)) + 4, yPosition, 196, 66, 4, 20);
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int x, int y)
	{
		if (super.mousePressed(mc, x, y))
		{
			volume = (float)(x - (xPosition + 4)) / (float)(width - 8);

			if (volume < 0.0F)
			{
				volume = 0.0F;
			}

			if (volume > 1.0F)
			{
				volume = 1.0F;
			}

			if (handler != null)
			{
				handler.onVolumeChanged(id, volume);
			}

			displayString = title + ": " + getVolumeString();
			changed = true;

			return true;
		}

		return false;
	}

	@Override
	public void func_146113_a(SoundHandler handler) {}

	@Override
	public void mouseReleased(int x, int y)
	{
		changed = false;
	}

	public interface IVolume
	{
		public void onVolumeChanged(int id, float volume);
	}
}