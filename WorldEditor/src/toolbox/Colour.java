package toolbox;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

public class Colour {

	private float r = 0;
	private float g = 0;
	private float b = 0;
	private float a = 1;

	public Colour(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Colour(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Colour(float r, float g, float b, boolean convert) {
		if (convert) {
			this.r = r / 255f;
			this.g = g / 255f;
			this.b = b / 255f;
		} else {
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}

	public Colour() {
	}

	public Vector3f toVector() {
		return new Vector3f(r, g, b);
	}

	public FloatBuffer getAsFloatBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(new float[] { r, g, b, a });
		buffer.flip();
		return buffer;
	}

	public float getR() {
		return r;
	}

	public float getG() {
		return g;
	}

	public float getB() {
		return b;
	}

	public Colour duplicate() {
		return new Colour(r, g, b);
	}

	public void multiplyBy(Colour colour) {
		this.r *= colour.r;
		this.g *= colour.g;
		this.b *= colour.b;
	}

	public void setColour(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void setColour(Colour colour){
		this.r = colour.r;
		this.g = colour.g;
		this.b = colour.b;
	}

	public void setColour(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public void setR(float r) {
		this.r = r;
	}

	public void setG(float g) {
		this.g = g;
	}

	public void setB(float b) {
		this.b = b;
	}

	public boolean isEqualTo(Colour colour) {
		if (this.r == colour.r && this.g == colour.g && this.b == colour.b) {
			return true;
		} else {
			return false;
		}
	}

	public Colour scale(float value) {
		this.r *= value;
		this.g *= value;
		this.b *= value;
		return this;
	}

	public String toString() {
		return ("(" + r + ", " + g + ", " + b + ")");
	}

	public static Colour sub(Colour colLeft, Colour colRight, Colour dest){
		if (dest == null) {
			return new Colour(colLeft.r - colRight.r, colLeft.g - colRight.g, colLeft.b - colRight.b);
		} else {
			dest.r = colLeft.r - colRight.r;
			dest.g = colLeft.g - colRight.g;
			dest.b = colLeft.b - colRight.b;
			return dest;
		}
	}
	
	public Colour getUnit(){
		Colour colour = new Colour();
		if(r == 0 && g == 0 && b == 0){
			return colour;
		}
		colour.setColour(this);
		colour.scale(1f/length());
		return colour;
	}
	
	public float length(){
		return (float) Math.sqrt(lengthSquared());
	}
	
	public float lengthSquared(){
		return (r*r) + (g*g) + (b*b);
	}
	
	public static Colour interpolateColours(Colour colour1, Colour colour2, float blend, Colour dest) {
		float colour1Weight = 1 - blend;
		float r = (colour1Weight * colour1.r) + (blend * colour2.r);
		float g = (colour1Weight * colour1.g) + (blend * colour2.g);
		float b = (colour1Weight * colour1.b) + (blend * colour2.b);
		if (dest == null) {
			return new Colour(r, g, b);
		} else {
			dest.setColour(r, g, b);
			return dest;
		}
	}

	public static Colour add(Colour colour1, Colour colour2, Colour dest) {
		if (dest == null) {
			return new Colour(colour1.r + colour2.r, colour1.g + colour2.g, colour1.b + colour2.b);
		} else {
			dest.r = colour1.r + colour2.r;
			dest.g = colour1.g + colour2.g;
			dest.b = colour1.b + colour2.b;
			return dest;
		}
	}

}
