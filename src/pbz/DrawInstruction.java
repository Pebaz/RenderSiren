package pbz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
Couples both an `Image` and the position, rotation, and scale it should have on
the resulting image.
*/
public class DrawInstruction
{
    private Image image;
    private float x;
    private float y;
    private float rot;
    private float sclx;
    private float scly;

    /**
	Create a `DrawInstruction`.
    */
    public DrawInstruction(Image image, float x, float y, float rot, float sclx, float scly)
    {
        this.setImage(image);
        this.setX(x);
        this.setY(y);
        this.setRot(rot);
        this.setSclx(sclx);
        this.setScly(scly);
    }

    /**
	Write the image and the drawing instructions to the socket (in this case).
    */
    public void write(DataOutputStream out) throws IOException
    {
        getImage().write(out);
        out.writeFloat(getX());
        out.writeFloat(getY());
        out.writeFloat(getRot());
        out.writeFloat(getSclx());
        out.writeFloat(getScly());
    }

    /**
	Create a `DrawInstruction` and return it by reading the data from the
	network socket (in this case).
    */
    public static DrawInstruction read(DataInputStream in) throws IOException
    {
        DrawInstruction di = new DrawInstruction(
                Image.read(in),
                in.readFloat(),
                in.readFloat(),
                in.readFloat(),
                in.readFloat(),
                in.readFloat()
        );
        return di;
    }

    /**
	Getter.
    */
    public Image getImage()
    {
        return image;
    }

    /**
	Setter.
    */
    public void setImage(Image image)
    {
        this.image = image;
    }

    /**
	Getter.
    */
    public float getX()
    {
        return x;
    }

    /**
	Setter.
    */
    public void setX(float x)
    {
        this.x = x;
    }

    /**
	Getter.
    */
    public float getY()
    {
        return y;
    }

    /**
	Setter.
    */
    public void setY(float y)
    {
        this.y = y;
    }

    /**
	Getter.
    */
    public float getRot()
    {
        return rot;
    }

    /**
	Setter.
    */
    public void setRot(float rot)
    {
        this.rot = rot;
    }

    /**
	Getter.
    */
    public float getSclx()
    {
        return sclx;
    }

    /**
	Setter.
    */
    public void setSclx(float sclx)
    {
        this.sclx = sclx;
    }

    /**
	Getter.
    */
    public float getScly()
    {
        return scly;
    }

    /**
	Setter.
    */
    public void setScly(float scly)
    {
        this.scly = scly;
    }
}
