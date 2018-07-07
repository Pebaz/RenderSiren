package pbz;


/**
Proof of concept implementation of a way to store and blit arrays of integers
interpreted as pixels.
*/
class Bitmap
{
	protected int width;
	protected int height;
	protected int size;
	protected int[] pixels;

	/**
	Create a new Bitmap instance and allocate an integer buffer for storing the
	pixels in.
	*/
	public Bitmap(int width, int height)
	{
		this.setWidth(width);
		this.setHeight(height);
		setSize(width * height);
		setPixels(new int[getSize()]);
	}

	/**
	Sets every pixel to the same color.
	*/
	public void clear(int color)
	{
		for (int i = 0; i < getSize(); i++)
		{
			getPixels()[i] = color;
		}
	}

	/**
	Draws a pixel using a given x and y value to determine an index.
	*/
	public void drawPixel(int x, int y, int color)
	{
		if (x >= getWidth() || x < 0 || y >= getHeight()) return;

		int index = x + y * getWidth();
		if (0 <= index && index < getSize())

		// If the color is completely opaque, don't blend it.
		if (Pixel.getChannelAlpha(color) == 255)
			getPixels()[index] = color;
		else
			getPixels()[index] = Pixel.blendColor(getPixels()[index], color);
	}

	/**
	Draws a pixel using an index into the pixel array rather than x and y.
	*/
	public void drawPixel(int index, int color)
	{
		if (index >= 0 && index > getSize())
		getPixels()[index] = Pixel.blendColor(getPixels()[index], color);
	}

	/**
	Skips blending and sets the pixel color at the specified coordinates.
	*/
	public void setPixel(int x, int y, int color)
	{
		// Dont' draw this pixel if it is not even on the destination bitmap.
		if (x >= getWidth() || x < 0 || y >= getHeight()) return;
		int index = x + y * getWidth();
		if (0 <= index && index < getSize()) getPixels()[index] = color;
	}

	/**
	Returns the color of the pixel at the specified coordinates.
	*/
	public int getPixel(int x, int y)
	{
		int index = x + y * getWidth();
		return index >= 0 && index < getSize() ? getPixels()[index] : 0;
	}

	/**
	Draws a bitmap onto this bitmap at the specified position.
	*/
	private void blitPos(Bitmap inbmp, double x, double y)
	{
		int pixel = 0;

		x -= inbmp.getWidth() * 0.5f;
		y -= inbmp.getHeight() * 0.5f;

		// Bounds checking:
		if (x + inbmp.getWidth() <= 0 || x >= getWidth() ||
			y + inbmp.getHeight() <= 0 || y >= getHeight())
			return;

		for (int yy = 0; yy < inbmp.getHeight(); yy++)
		{
			int row = (int)(y + yy);
			for (int xx = 0; xx < inbmp.getWidth(); xx++)
			{
				drawPixel((int)(x + xx), row, inbmp.getPixels()[pixel]);
				pixel++;
			}
		}
	}

	/**
	Blits a bitmap using a rotation angle in radians.
	*/
	private void blitRot(Bitmap inbmp, double x, double y, double rot)
	{
		// Store the rotated angle
		double ssin = Math.sin(-rot);
		double ccos = Math.cos(-rot);

		int hw = (int)(inbmp.getWidth() * 0.5f);
		int hh = (int)(inbmp.getHeight() * 0.5f);

		// Get the bounding rect

		int left   = -hw;
		int right  =  hw;
		int top    =  hh;
		int bottom = -hh;

		// Get the points of the rotated image

		int p1x = (int)(left 	* ssin + top 	* ccos);
		int p1y = (int)(left 	* ccos - top 	* ssin);
		int p2x = (int)(right 	* ssin + top 	* ccos);
		int p2y = (int)(right 	* ccos - top 	* ssin);
		int p3x = (int)(left 	* ssin + bottom * ccos);
		int p3y = (int)(left 	* ccos - bottom * ssin);
		int p4x = (int)(right 	* ssin + bottom * ccos);
		int p4y = (int)(right 	* ccos - bottom * ssin);

		// Get the furthest and closest point at each axis

		int minx = Math.min(p1x, Math.min(p2x, Math.min(p3x, p4x)));
		int miny = Math.min(p1y, Math.min(p2y, Math.min(p3y, p4y)));
		int maxx = Math.max(p1x, Math.max(p2x, Math.max(p3x, p4x)));
		int maxy = Math.max(p1y, Math.max(p2y, Math.max(p3y, p4y)));

		// Bounds checking

		if (x + maxx <= 0 || x + minx >= getWidth() ||
		y + maxx <= 0 || y + miny >= getHeight())
		return;

		// Use those points to draw the rotated pixels

		for (int yy = miny - p1y; yy < maxy - p1y + 1; yy++)
		{
			for (int xx = minx - p1x; xx < maxx - p1x + 1; xx++)
			{
				int srcx = (int)(xx * ssin + yy * ccos);
				int srcy = (int)(yy * ssin - xx * ccos);
				int destx = xx - p4x;
				int desty = yy - p4y;

				if (srcx < inbmp.getWidth() && srcx >= 0 &&
				srcy < inbmp.getHeight() && srcy >= 0)
				{
					int index = srcx + srcy * inbmp.getWidth();
					int sample = inbmp.getPixels()[index];
					drawPixel((int)(x + destx), (int)(y + desty), sample);
				}
			}
		}
	}

	/**
	Draws a bitmap with a given scale at the specified position.
	*/
	private void blitScl(Bitmap inbmp, double x, double y, double sclx,
		double scly)
	{
		int fwidth = (int)(inbmp.getWidth() * sclx);
		int fheight = (int)(inbmp.getHeight() * scly);

		// Make sure that we put the origin at x and y, not the corner.

		x -= fwidth * 0.5f;
		y -= fheight * 0.5f;

		// Bounds checking
		if (x + fwidth <= 0 || x >= getWidth() ||
			y + fheight <= 0 || y >= getHeight())
			return;

		double stepx = (double) inbmp.getWidth() / (double)fwidth;
		double stepy = (double) inbmp.getHeight() / (double)fheight;
		double iny = 0;
		int yout = 0;

		while (yout < fheight)
		{
			double inx = 0;
			int xout = 0;
			while (xout < fwidth)
			{
				int sample = inbmp.getPixel((int)inx, (int)iny);
				drawPixel((int)(x + xout), (int)(y + yout), sample);

				inx += stepx;
				xout++;
			}
			iny += stepy;
			yout++;
		}
	}

	/**
	Blits a bitmap with rotation and scaling applied at the given position.
	*/
	private void blitRotScl(Bitmap inbmp, double x, double y, double rot,
		double sclx, double scly)
	{
		int nwidth  = (int)(inbmp.getWidth() * sclx);
		int nheight = (int)(inbmp.getHeight() * scly);
		double ssin = Math.sin(-rot);
		double ccos = Math.cos(-rot);
		int hw = (int)(nwidth  * 0.5f);
		int hh = (int)(nheight * 0.5f);
		int left   = -hw;
		int right  =  hw;
		int top    =  hh;
		int bottom = -hh;
		int p1x = (int)(left * ssin + top * ccos);
		int p1y = (int)(left * ccos - top * ssin);
		int p2x = (int)(right * ssin + top * ccos);
		int p2y = (int)(right * ccos - top * ssin);
		int p3x = (int)(left * ssin + bottom * ccos);
		int p3y = (int)(left * ccos - bottom * ssin);
		int p4x = (int)(right * ssin + bottom * ccos);
		int p4y = (int)(right * ccos - bottom * ssin);
		int minx = Math.min(p1x, Math.min(p2x, Math.min(p3x, p4x)));
		int miny = Math.min(p1y, Math.min(p2y, Math.min(p3y, p4y)));
		int maxx = Math.max(p1x, Math.max(p2x, Math.max(p3x, p4x)));
		int maxy = Math.max(p1y, Math.max(p2y, Math.max(p3y, p4y)));

		// Bounds checking

		if (x + maxx <= 0 || x + minx >= getWidth() ||
			y + maxx <= 0 || y + miny >= getHeight())
			return;

		for (int yy = miny - p1y - 1; yy < maxy - p1y + 1; yy++)
		{
			for (int xx = minx - p1x - 1; xx < maxx - p1x + 1; xx++)
			{
				int srcx = (int)((xx * ssin + yy * ccos) / sclx);
				int srcy = (int)((yy * ssin - xx * ccos) / scly);
				int destx = xx - p4x;
				int desty = yy - p4y;

				if (srcx < inbmp.getWidth() && srcx >= 0 && srcy < inbmp.getHeight() &&
					srcy >= 0)
				{
					int index = srcx + srcy * inbmp.getWidth();
					int sample = inbmp.getPixels()[index];
					drawPixel((int)(x + destx), (int)(y + desty), sample);
				}
			}
		}
	}

	/**
	Convenience method to automatically use the right method for blitting.
	*/
	public void blitBitmap(Bitmap inbmp, double x, double y, double rot,
		double sclx, double scly)
	{
		if (rot == 0 && sclx == 1 && scly == 1)
			blitPos(inbmp, x, y);

		else if (rot == 0 && sclx != 1 && scly != 1)
			blitScl(inbmp, x, y, sclx, scly);

		else if (rot != 0 && sclx == 1 && scly == 1)
			blitRot(inbmp, x, y, rot);

		else
			blitRotScl(inbmp, x, y, rot, sclx, scly);
	}

	/**
	Getter.
	*/
	public int getWidth()
	{
		return width;
	}

	/**
	Setter.
	*/
	public void setWidth(int width)
	{
		this.width = width;
	}

	/**
	Getter.
	*/
	public int getHeight()
	{
		return height;
	}

	/**
	Setter.
	*/
	public void setHeight(int height)
	{
		this.height = height;
	}

	/**
	Getter.
	*/
	public int getSize()
	{
		return size;
	}

	/**
	Setter.
	*/
	public void setSize(int size)
	{
		this.size = size;
	}

	/**
	Getter.
	*/
	public int[] getPixels()
	{
		return pixels;
	}

	/**
	Setter.
	*/
	public void setPixels(int[] pixels)
	{
		this.pixels = pixels;
	}
}
