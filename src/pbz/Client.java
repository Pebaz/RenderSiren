package pbz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
Simple proof of concept client to connect to the `RenderSiren` server and
request that a stack of `DrawInstructions` be processed and the image returned.
Takes the returned image and then displays it in a `Display` class window.
*/
public class Client	
{
	/**
	Starts the program. Loads 4 images, creates draw instructions for them, and
	then sends the `InstructionSet` to the server once a successful connection
	is made. As this is a test program, no effort is made to handle potential
	exceptions.
	*/
    public static void main(String[] args) throws IOException	
    {
    	// Load the images
        Image background = Image.load("res/Map.png");
        Image pebaz = Image.load("res/Pebaz.png");
        Image protodip = Image.load("res/Protodip.png");
        Image azimuth = Image.load("res/Azimuth.png");

        // Create an instruction set for them
        InstructionSet set = new InstructionSet(800, 600);
        set.addInstruction(new DrawInstruction(
                background,
                set.getCanvasWidth() / 2,
                set.getCanvasHeight() / 2,
                0, 1, 1
        ));
        set.addInstruction(new DrawInstruction(pebaz, 100, 256, 0, 1, 1));
        set.addInstruction(new DrawInstruction(protodip, 256, 256, 0.45f, 1, 1));
        set.addInstruction(new DrawInstruction(azimuth, 512, 256, 0, 2, 2));

        // Connect to the RenderSiren server
        Socket socket = new Socket("127.0.0.1", 8011);

        // Having a smaller send/recieve buffer size increased performance
        socket.setReceiveBufferSize(512);
        socket.setSendBufferSize(512);

        // In/Out streams
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        // Send the InstructionSet over the wire
        set.write(out);

        // Get the resulting image from the server
        System.out.println("Retrieving results from the server...");
        Image result = Image.read(in);
        System.out.println("Got em!");

        // Close the streams
        out.close();
        in.close();

        // Show the image in a JFrame
        Display display = new Display(set.getCanvasWidth(), set.getCanvasHeight()) {
            @Override
            public void render()
            {
                buffer.blitBitmap(
                        result, set.getCanvasWidth() / 2,
                        set.getCanvasHeight() / 2,
                        0, 1, 1
                );
            }
        };
        display.start();
    }
}