package caveworld.util;

import org.apache.commons.lang3.StringUtils;

public class Roman
{
	private static final int[] values = {1, 10, 100, 1000};
	private static final char[] ones = {'I', 'X', 'C', 'M'};
	private static final char[] fives = {'V', 'L', 'D'};

	public static String toRoman(int num)
	{
		if (num <= 0 || num >= 4000)
		{
			return "";
		}

		StringBuilder ret = new StringBuilder();

		for (int i = 3; i >= 0; --i)
		{
			int r = num / values[i];
			num %= values[i];

			if (r == 4)
			{
				ret.append(ones[i]).append(fives[i]);
				continue;
			}

			if (r == 9)
			{
				ret.append(ones[i]).append(ones[i + 1]);
				continue;
			}

			if (r >= 5)
			{
				ret.append(fives[i]);
				r -= 5;
			}

			ret.append(StringUtils.repeat(ones[i], r));
		}

		return ret.toString();
	}
}