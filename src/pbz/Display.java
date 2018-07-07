package pbz;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
Simple JPanel subclass that renders its `Bitmap` backbuffer after a brief pause
to attempt to ensure that reading and writing to the buffer does not happen at
the same time.
*/
class Display extends JPanel
{
	protected Image buffer;

	/**
	Create an Image that has the same dimensions as the top-level window.
	*/
	public Display(int width, int height)
	{
		buffer = new Image(width, height);
	}

	/**
	Can be overridden to provide custom rendering without messing with the
	timing loop.
	*/
	public void render()
	{
		// Normally clear the screen but it's neat to see the squares add
		// up on screen:
		//buffer.clear(0xFFFFBA44);

		Random rand = new Random();

		// Create an Image with a random color
		Image square = new Image(64, 64);
		square.clear(Pixel.createColor(
				100 + rand.nextInt(155),
				rand.nextInt(255),
				rand.nextInt(255),
				rand.nextInt(255)
		));

		// Draw it at a random position
		int tX = rand.nextInt(buffer.getWidth());
		int tY = rand.nextInt(buffer.getHeight());
		buffer.blitBitmap(square, tX, tY, Math.toRadians(rand.nextInt(360)), 1, 1);
	}

	/**
	Enter an infinite rendering loop that can only be killed by closing the
	window. Renders a randomly colored Image onto the buffer. The buffer will
	later get copied to the background of the JFrame.
	*/
	private void run()
	{
		while (true)
		{
			render();

			try
			{
				// Sleep the rendering thread so that the GUI has enough time
				// to render the image before it is modified next.
				Thread.sleep(60);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			// Ask Java to repaint the screen and do this all over again
			repaint();
		}
	}

	/**
	Inherited method to render the buffer onto the JFrame.
	*/
	protected void paintComponent(Graphics graphics)
	{
		Graphics2D gdd = (Graphics2D)graphics;
		gdd.drawImage(buffer.asBufferedImage(), 0, 0, null);
	}

	/**
	Create a JFrame, add this Display to it and then start the render loop.
	*/
	public void start()
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(buffer.getWidth(), buffer.getHeight()));
		frame.setResizable(false);
		frame.add(this);
		frame.setVisible(true);
		run();
	}

	/**
	Merely provided as a test.
	*/
	public static void main(String[] args)
	{
		new Display(800, 600).start();
	}
}
