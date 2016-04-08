package toolbox;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class OpenglUtils {

	private static boolean cullingBackFace = false;
	private static boolean inWireframe = false;
	private static boolean isAlphaBlending = false;
	private static boolean additiveBlending = false;
	private static boolean antialiasing = false;

	public static void prepareNewRenderParse(Colour skyColour) {
		GL11.glClearColor(skyColour.getR(), skyColour.getG(), skyColour.getB(), 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		disableBlending();
		cullBackFaces(true);
		enableDepthTesting();
	}
	
	public static void prepareNewRenderParse(Colour skyColour, float alpha) {
		GL11.glClearColor(skyColour.getR(), skyColour.getG(), skyColour.getB(), alpha);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		disableBlending();
		enableDepthTesting();
	}
	
	public static void antialias(boolean enable){
		if(!GraphicsOptions.ANTI_ALIASING){
			return;
		}
		if(enable && !antialiasing){
			GL11.glEnable(GL13.GL_MULTISAMPLE);
			antialiasing = true;
		}else if(!enable && antialiasing){
			GL11.glDisable(GL13.GL_MULTISAMPLE);
			antialiasing = false;
		}
	}

	public static void enableAlphaBlending() {
		if (!isAlphaBlending) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			isAlphaBlending = true;
			additiveBlending = false;
		}
	}

	public static void enableAdditiveBlending() {
		if (!additiveBlending) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			additiveBlending = true;
			isAlphaBlending = false;
		}
	}

	public static void disableBlending() {
		if (isAlphaBlending||additiveBlending) {
			GL11.glDisable(GL11.GL_BLEND);
			isAlphaBlending = false;
			additiveBlending = false;
		}
	}

	public static void disableDepthTesting() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	public static void enableDepthTesting() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static void bindVAO(int vaoID, int... attributes) {
		GL30.glBindVertexArray(vaoID);
		for (int i : attributes) {
			GL20.glEnableVertexAttribArray(i);
		}
	}

	public static void unbindVAO(int... attributes) {
		for (int i : attributes) {
			GL20.glDisableVertexAttribArray(i);
		}
		GL30.glBindVertexArray(0);
	}

	public static void bindTextureToBank(int textureID, int bankID) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + bankID);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}

	public static void bindTextureToBank(int textureID, int bankID, int lodBias) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + bankID);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, lodBias);
		GL13.glActiveTexture(0);
	}

	public static void cullBackFaces(boolean cull) {
		if (cull && !cullingBackFace) {
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
			cullingBackFace = true;
		} else if (!cull && cullingBackFace) {
			GL11.glDisable(GL11.GL_CULL_FACE);
			cullingBackFace = false;
		}
	}

	public static void goWireframe(boolean goWireframe) {
		if (goWireframe && !inWireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			inWireframe = true;
		} else if (!goWireframe && inWireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			inWireframe = false;
		}
	}

}
