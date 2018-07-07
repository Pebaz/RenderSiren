package pbz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
`DrawInstructions` should be added in the order that they should be drawn on
the server. Used to store and then send/recieve a set of drawing instructions.
*/
public class InstructionSet
{
    private Vector<DrawInstruction> instructions = new Vector<>();
    private int canvasWidth;
    private int canvasHeight;

    /**
    Constructor.
    */
    public InstructionSet(int width, int height)
    {
        canvasWidth = width;
        canvasHeight = height;
    }

    /**
    Getter.
    */
    public int getCanvasWidth()
    {
        return canvasWidth;
    }

    /**
    Setter.
    */
    public int getCanvasHeight()
    {
        return canvasHeight;
    }

    /**
    Getter.
    */
    public Vector<DrawInstruction> getInstructions()
    {
        return instructions;
    }

    /**
    Adds an instruction to the list of instructions to be sent to the server.
    Instructions should be added in the same order that they should be drawn.
    */
    public void addInstruction(DrawInstruction drawInstruction)
    {
        instructions.add(drawInstruction);
    }

    /**
    Write all the `DrawInstructions` to the network socket (as of right now).
    */
    public void write(DataOutputStream out) throws IOException
    {
        out.writeInt(canvasWidth);
        out.writeInt(canvasHeight);
        out.writeInt(instructions.size());

        for (DrawInstruction inst : instructions)
        {
            System.out.print("Writing Instruction...");
            inst.write(out);
            System.out.println("\rWrote Instruction");
        }
    }

    /**
    Create, populate and then return a new `InstructionSet` obtained from the
    server.
    */
    public static InstructionSet read(DataInputStream in) throws IOException
    {
        InstructionSet set = new InstructionSet(in.readInt(), in.readInt());
        int numInstructions = in.readInt();
        for (int i = 0; i < numInstructions; i++)
        {
            System.out.print("Reading Instruction...");
            set.addInstruction(DrawInstruction.read(in));
            System.out.println("\rRead Instruction!");
        }
        return set;
    }
}
