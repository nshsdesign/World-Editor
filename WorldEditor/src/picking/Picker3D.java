package picking;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import entities.Entity;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;

public class Picker3D {

	private static final int MAX_INSTANCES = 8000;
	private static final int INSTANCE_DATA_LENGTH = 4 + 4 + 4 + 4 + 2;
	private static final Color WHITE = new Color(1, 1, 1);
	private static final int MAX_BYTE_VAL = 255;

	private static final float[] VERTICES = { -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f,
			0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f };
	private static final int[] INDICES = { 0, 1, 3, 3, 1, 2, 3, 2, 7, 7, 2, 6, 7, 6, 4, 4, 6, 5, 4, 5, 0, 0, 5, 1, 4, 0,
			7, 7, 0, 3 };
	private static final int FBO_DOWNSCALE = 8;
	private static final int PBO_COUNT = 3;

	private PickingShader shader;

	private int vao;
	private int vbo;
	private Fbo fbo;
	private Entity entity;
	private int base;
	private float step;

	private Matrix4f projectionView = new Matrix4f();
	private Matrix4f mvpMatrix = new Matrix4f();
	private Vector2f colour = new Vector2f();
	private FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH * 4);

	private PboDataDownloader downloader;

	public Picker3D() {
		vao = Loader.createInterleavedVAO(VERTICES, INDICES, 3);
		vbo = Loader.createInterleavedInstancedVbo(vao, MAX_INSTANCES, 1, 4, 4, 4, 4, 2);
		fbo = Fbo.newFbo(DisplayManager.getWidth() / FBO_DOWNSCALE, DisplayManager.getHeight() / FBO_DOWNSCALE)
				.nearestFiltering().setDepthBuffer(DepthBufferType.RENDER_BUFFER).create();
		shader = new PickingShader();
		downloader = new PboDataDownloader(1, 1, PBO_COUNT);
	}

	public int getFboTexture() {
		return fbo.getColourTexture();
	}

	public void update(List<Entity> worldEntities) {
		float[] instanceData = getInstanceData(worldEntities);
		renderInstances(instanceData, worldEntities.size());
		byte[] result = readPixelColour();
		if (noColourFound(result)) {
			entity = null;
		} else {
			pickEntity(result, worldEntities);
		}
	}

	public Entity getPickedEntity() {
		return entity;
	}

	public void reset() {
		downloader.reset();
	}

	public void cleanUp() {
		downloader.cleanUp();
	}

	private float[] getInstanceData(List<Entity> worldEntities) {
		int totalEntities = worldEntities.size();
		float[] instanceData = new float[totalEntities * INSTANCE_DATA_LENGTH];
		int pointer = 0;
		calculateBaseAndStep(totalEntities);
		for (int i = 0; i < totalEntities; i++) {
			encodeColour(i);
			pointer = loadMvpMatrix(worldEntities.get(i), instanceData, pointer);
			pointer = storeColour(instanceData, pointer);
		}
		return instanceData;
	}

	private boolean noColourFound(byte[] colour) {
		byte blue = colour[2];
		int blueValue = convertUnsignedByte(blue);
		return blueValue > MAX_BYTE_VAL / 2;
	}

	private byte[] readPixelColour() {
		fbo.bindToRead();
		byte[] res = downloader.downloadData(Mouse.getX() / FBO_DOWNSCALE, Mouse.getY() / FBO_DOWNSCALE);
		fbo.unbindFrameBuffer();
		return res;
	}

	private void renderInstances(float[] instanceData, int total) {
		prepare();
		Loader.refillVboWithData(vbo, buffer, instanceData);
		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, INDICES.length, GL11.GL_UNSIGNED_INT, 0, total);
		endRendering();
	}
	
	private void pickEntity(byte[] result, List<Entity> worldEntities){
		int index = decodeEntityIndex(result);
		if (worldEntities.size() > index) {
			entity = worldEntities.get(index);
		}else{
			entity = null;
			System.err.println("Picker messing up!");
		}
	}

	private int storeColour(float[] instanceData, int pointer) {
		instanceData[pointer++] = colour.x;
		instanceData[pointer++] = colour.y;
		return pointer;
	}

	private int convertUnsignedByte(byte b) {
		return (b & 0xFF);
	}
	
	private void calculateBaseAndStep(int total){
		base = (int) (Math.floor(Math.sqrt(total)) + 1);
		step = 1f / (float) base;
	}

	private void encodeColour(int index) {
		int r = index / base;
		int g = index % base;
		colour.set(getValue(r, step), getValue(g, step));
	}

	private float getValue(float value, float step) {
		return (value * step) + (step * 0.5f);
	}

	private int decodeEntityIndex(byte[] colour) {
		float r = convertUnsignedByte(colour[0]) / (float) MAX_BYTE_VAL;
		float g = convertUnsignedByte(colour[1]) / (float) MAX_BYTE_VAL;
		int ri = (int) (r / step);
		int gi = (int) (g / step);
		return ri * base + gi;
	}

	private void prepare() {
		fbo.bindFrameBuffer();
		OpenglUtils.prepareNewRenderParse(WHITE);
		OpenglUtils.bindVAO(vao, 0, 1, 2, 3, 4, 5);
		calculateProjectionViewMatrix();
		shader.start();
	}

	private void endRendering() {
		shader.stop();
		fbo.unbindFrameBuffer();
		OpenglUtils.unbindVAO(0, 1, 2, 3, 4, 5);
	}

	private void calculateProjectionViewMatrix() {
		Matrix4f projection = MasterRenderer.getProjectionMatrix();
		Matrix4f view = EngineMaster.getCamera().getViewMatrix();
		Matrix4f.mul(projection, view, projectionView);
	}

	private int loadMvpMatrix(Entity entity, float[] data, int pointer) {
		Matrix4f modelMat = entity.getBoundingBox().getModelMatrix();
		Matrix4f.mul(projectionView, modelMat, mvpMatrix);
		data[pointer++] = mvpMatrix.m00;
		data[pointer++] = mvpMatrix.m01;
		data[pointer++] = mvpMatrix.m02;
		data[pointer++] = mvpMatrix.m03;
		data[pointer++] = mvpMatrix.m10;
		data[pointer++] = mvpMatrix.m11;
		data[pointer++] = mvpMatrix.m12;
		data[pointer++] = mvpMatrix.m13;
		data[pointer++] = mvpMatrix.m20;
		data[pointer++] = mvpMatrix.m21;
		data[pointer++] = mvpMatrix.m22;
		data[pointer++] = mvpMatrix.m23;
		data[pointer++] = mvpMatrix.m30;
		data[pointer++] = mvpMatrix.m31;
		data[pointer++] = mvpMatrix.m32;
		data[pointer++] = mvpMatrix.m33;
		return pointer;
	}

}
