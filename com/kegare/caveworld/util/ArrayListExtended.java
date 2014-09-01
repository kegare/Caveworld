/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class ArrayListExtended<E> extends ArrayList<E>
{
	public ArrayListExtended()
	{
		super();
	}

	public ArrayListExtended(Collection<? extends E> c)
	{
		super(c);
	}

	public boolean addObject(Object obj)
	{
		return obj == null ? false : add((E)obj);
	}

	public ArrayListExtended<E> addAllObject(Collection c)
	{
		for (Object obj : c)
		{
			addObject(obj);
		}

		return this;
	}

	public ArrayListExtended<E> addAllObject(Iterable iterable)
	{
		for (Object obj : iterable)
		{
			addObject(obj);
		}

		return this;
	}

	public ArrayListExtended<E> addAllObject(E... objects)
	{
		for (Object obj : objects)
		{
			addObject(obj);
		}

		return this;
	}

	public E get(int index, E value)
	{
		return index < 0 || index >= size() || super.get(index) == null ? value : super.get(index);
	}

	public ArrayListExtended<E> sort(Comparator<? super E> comparator)
	{
		Collections.sort(this, comparator);

		return this;
	}
}