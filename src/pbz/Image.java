package pbz;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
Subclass of `pbz.Bitmap` that provides saving and loading methods as well as
the ability to read and write to a Data(In/Out)putStream. No attempt is made to
handle the endianness of the platform as saving/loading/reading/writing is
handled by the Java Standard Library for convenience purposes.
*/
class Image extends Bitmap
{
	/**
	Constructor. Inherits from Bitmap, so initialize parent.
	*/
	public Image(int width, int height)
	{
		super(width, height);
	}

	/**
	Hidden constructor for easier creation of bitmaps from Java's built-in
	BufferedImage class only within the confines of this Image class.
	*/
	private Image(BufferedImage image)
	{
		super(image.getWidth(), image.getHeight());

		for (int y = 0; y < getHeight(); y++)
		{
			for (int x = 0; x < getWidth(); x++)
			{
				int index = x + y * getWidth();
				getPixels()[index] = image.getRGB(x, y);
			}
		}
	}

	/**
	Load an image from disk and become it.
	*/
	public static Image load(String filename)
	{
		try
		{
			return new Image(ImageIO.read(new File(filename)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	Save contents of this image to disk as a Portable Network Graphic (PNG).
	*/
	public void save(String filename)
	{
		try
		{
			ImageIO.write(asBufferedImage(), "png", new File(filename));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	Return the contents of this Image as a BufferedImage (Java Standard Lib).
	*/
	public BufferedImage asBufferedImage()
	{
		BufferedImage img = new BufferedImage(getWidth(), getHeight(),
			BufferedImage.TYPE_INT_ARGB);


		for (int i = 0; i < getSize(); i++)
		{
			int destX = i % getWidth();
			int destY = i / getWidth();
			img.setRGB(destX, destY, getPixels()[i]);
		}

		return img;
	}

	/**
	Write the pixels contained herein to (in this case) the network socket.
	*/
	public void write(DataOutputStream out) throws IOException
	{
		out.writeInt(getWidth());
		out.writeInt(getHeight());

		for (int pixel : getPixels())
		{
			out.writeInt(pixel);
		}
	}


	/**
	Read the pixels from the network socket (in this case), populate an new
	Image instance and then return it.
	*/
	public static Image read(DataInputStream in) throws IOException
	{
		Image image = new Image(in.readInt(), in.readInt());

		for (int i = 0; i < image.getSize(); i++)
		{
			image.getPixels()[i] = in.readInt();
		}

		return image;
	}
}
