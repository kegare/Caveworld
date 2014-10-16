/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.util.comparator;

import java.util.Comparator;

import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.breaker.BreakPos;

public class BreakPosComparator implements Comparator<BreakPos>
{
	private final BreakPos originPos;

	public BreakPosComparator(BreakPos origin)
	{
		this.originPos = origin;
	}

	@Override
	public int compare(BreakPos pos1, BreakPos pos2)
	{
		int i = CaveUtils.compareWithNull(pos1, pos2);

		if (i == 0 && pos1 != null && pos2 != null)
		{
			int dist1 = originPos.getDistanceSq(pos1);
			int dist2 = originPos.getDistanceSq(pos2);

			i = Integer.compare(Math.abs(dist1), Math.abs(dist2));
		}

		return i;
	}
}