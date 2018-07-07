package pbz;

/**
Contains convenience methods for creating integers that contain four color
channels: Alpha, Red, Green, and Blue.
*/
class Pixel
{
	/**
	Returns the 8-bit (1 byte) alpha channel from the given color.
	*/
	public static int getChannelAlpha(int color)
	{
		return (color >> 24) & 255;
	}

	/**
	Returns the 8-bit (1 byte) red channel from the given color.
	*/
	public static int getChannelRed(int color)
	{
		return (color >> 16) & 255;
	}

	/**
	Returns the 8-bit (1 byte) green channel from the given color.
	*/
	public static int getChannelGreen(int color)
	{
		return (color >> 8) & 255;
	}

	/**
	Returns the 8-bit (1 byte) blue channel from the given color.
	*/
	public static int getChannelBlue(int color)
	{
		return color & 255;
	}

	/**
	Converts 4 integers into the 4 channels of a color.
	A safety check is performed by bitwise ANDing all channels with 255. This
	will cap them at 255 rather than allow overflowing values (greater than
	255) to be entered.
	*/
	public static int createColor(int a, int r, int g, int b)
	{
		int aa = (a & 0xFF) << 24;
		int rr = (r & 0xFF) << 16;
		int gg = (g & 0xFF) << 8;
		int bb = b & 0xFF;
		return aa | rr | gg | bb;
	}

	/**
	Blends 2 colors together if the top one even slightly transparent.
	*/
	public static int blendColor(int a, int b)
	{
		int balpha = getChannelAlpha(b);

		// Exit early?
		if (balpha == 255) return b;

		int bred 	= getChannelRed(b);
		int bgreen 	= getChannelGreen(b);
		int bblue 	= getChannelBlue(b);
		int aalpha 	= getChannelAlpha(a);
		int ared 	= getChannelRed(a);
		int agreen 	= getChannelGreen(a);
		int ablue 	= getChannelBlue(a);
		int falpha, fred, fgreen, fblue;
		double barf = balpha / 255.0f;
		double brem = (255 - balpha) / 255.0f;

		fred   = (int)(bred   * barf + ared   * brem);
		fgreen = (int)(bgreen * barf + agreen * brem);
		fblue  = (int)(bblue  * barf + ablue  * brem);
		falpha = Math.min(aalpha + balpha, 255);

		return createColor(falpha, fred, fgreen, fblue);
	}
}
