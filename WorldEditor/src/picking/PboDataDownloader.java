package picking;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;

public class PboDataDownloader {

	private static final int BYTES_PER_PIXEL = 4;

	private int[] pbos;
	private final int width, height;
	private final int byteCount;
	
	private int currentPbo = 0;
	private boolean pbosFilled = false;
	
	private ByteBuffer buffer;
	private byte[] result;

	public PboDataDownloader(int width, int height, int pboCount) {
		this.width = width;
		this.height = height;
		this.byteCount = calculateByteCount();
		buffer = BufferUtils.createByteBuffer(byteCount);
		result = new byte[byteCount];
		emptyResult();
		initPbos(pboCount);
	}

	public byte[] downloadData(int x, int y) {
		GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, pbos[currentPbo]);
		if (pbosFilled) {
			readDataFromPbo();
		}
		GL11.glReadPixels(x, y, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0);
		GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
		moveToNextPbo();
		return result;
	}

	public void reset(){
		emptyResult();
		pbosFilled = false;
		currentPbo = 0;
	}

	public void cleanUp() {
		GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
		for (int pbo : pbos) {
			GL15.glDeleteBuffers(pbo);
		}
	}
	
	private void emptyResult(){
		for(int i=0;i<result.length;i++){
			result[i] = -1;
		}
	}
	
	private void initPbos(int count) {
		pbos = new int[count];
		for (int i = 0; i < pbos.length; i++) {
			pbos[i] = createPbo();
		}
	}

	private void readDataFromPbo() {
		buffer = GL15.glMapBuffer(GL21.GL_PIXEL_PACK_BUFFER, GL15.GL_READ_ONLY, buffer);
		buffer.get(result);
		GL15.glUnmapBuffer(GL21.GL_PIXEL_PACK_BUFFER);
		buffer.flip();
	}

	private int createPbo() {
		int pbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, pbo);
		GL15.glBufferData(GL21.GL_PIXEL_PACK_BUFFER, byteCount, GL15.GL_STREAM_READ);
		GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
		return pbo;
	}

	private int calculateByteCount() {
		int pixels = width * height;
		return pixels * BYTES_PER_PIXEL;
	}

	private void moveToNextPbo() {
		currentPbo++;
		if (currentPbo == pbos.length) {
			currentPbo = 0;
			pbosFilled = true;
		}
	}

}
