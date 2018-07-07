package pbz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
Server to accept requests from clients over a raw TCP socket for the purposes
of rendering a set of images onto a backbuffer and then returning the results.
"Rendering As A Service"!

Note: Absolutely no attention is given to the performance of this system as it
is a proof of concept only.
*/
public class RenderSiren
{
	/**
	Create a TCP socket and bind to it, listening for connections on port 8011.
	*/
    public static void main(String[] args) throws IOException
    {
        ServerSocket socket = new ServerSocket(8011);
        while (true)
        {
            Socket connection = socket.accept();
            handleInstructions(connection);
            connection.close();
        }
    }

    /**
    Takes a given TCP socket connection and then reads the desired instructions
    sent from the client. Creates an `Image` with the dimensions specified from
    the client and then renders each `Instruction` onto it in turn. Sends the
    results back to the client when finished.
    */
    private static void handleInstructions(Socket socket) throws IOException
    {
    	// Having a smaller send/receive buffer size increased performance
        socket.setReceiveBufferSize(512);
        socket.setSendBufferSize(512);

        // Create the data streams
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        // Read the instruction set from the socket
        InstructionSet set = InstructionSet.read(in);

        System.out.println("Successfully read the instruction set!");

        // Create a new `Image` as the backbuffer
        Image result = new Image(set.getCanvasWidth(), set.getCanvasHeight());

        // Draw the `Image` from each `DrawInstruction` onto the backbuffer
        for (DrawInstruction inst : set.getInstructions())
        {
            result.blitBitmap(inst.getImage(), inst.getX(), inst.getY(),
            	inst.getRot(), inst.getSclx(), inst.getScly());
        }

        // Send the client the results
        System.out.println("Starting to write the results to the client");
        result.write(out);
        System.out.println("Successfully sent!");

        // Close the streams
        in.close();
        out.flush();
        out.close();
    }
}