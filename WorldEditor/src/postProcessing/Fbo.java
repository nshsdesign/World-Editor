package postProcessing;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import postProcessing.FboBuilder.DepthBufferType;

public class Fbo {

	private final int width;
	private final int height;

	private final boolean alpha;

	private int frameBuffer;

	private int colourTexture;
	private int depthTexture;

	private int depthBuffer;
	private int colourBuffer;

	private final boolean antialiased;

	public static FboBuilder newFbo(int width, int height) {
		return new FboBuilder(width, height);
	}

	protected Fbo(int width, int height, DepthBufferType depthType, boolean useColourBuffer, boolean linear,
			boolean clamp, boolean alpha, boolean antialiased, int samples) {
		this.width = width;
		this.height = height;
		this.alpha = alpha;
		this.antialiased = antialiased;
		initialiseFrameBuffer(depthType, useColourBuffer, linear, clamp, samples);
	}

	/**
	 * Deletes the frame buffer and shadow map when the game closes.
	 */
	public void cleanUp() {
		GL30.glDeleteFramebuffers(frameBuffer);
		GL11.glDeleteTextures(colourTexture);
		GL11.glDeleteTextures(depthTexture);
		GL30.glDeleteRenderbuffers(depthBuffer);
		GL30.glDeleteRenderbuffers(colourBuffer);
	}

	/**
	 * Binds the frame buffer, setting it as the current render target.
	 */
	public void bindFrameBuffer() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);
		GL11.glViewport(0, 0, width, height);
	}

	/**
	 * Unbinds the frame buffer, setting the default frame buffer as the current
	 * render target.
	 */
	public void unbindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	public void bindToRead() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
	}

	/**
	 * @return The ID of the texture containing the colour buffer of the FBO.
	 */
	public int getColourTexture() {
		return colourTexture;
	}

	/**
	 * @return The texture containing the FBOs depth buffer.
	 */
	public int getDepthTexture() {
		return depthTexture;
	}

	/**
	 * After rendering to a multisampled FBO the FBO needs to be resolved
	 * (combine the multiple samples into single texels). This method resolves
	 * the multisampled FBO into another FBO.
	 * 
	 * @param outputFbo
	 *            - The FBO to which the multisampled FBO should be resolved.
	 */
	public void resolveMultisampledFbo(Fbo outputFbo) {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, outputFbo.frameBuffer);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);

		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, outputFbo.width, outputFbo.height,
				GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		unbindFrameBuffer();
	}

	/**
	 * Copies the contents of this FBO to the screen's frame buffer, applying
	 * antialiasing if multisampling was used necessary.
	 */
	public void blitToScreen() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL11.glDrawBuffer(GL11.GL_BACK);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, Display.getWidth(), Display.getHeight(),
				GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
	}

	/**
	 * Creates the FBO along with its necessary attachments.
	 * 
	 * @param type
	 *            - the type of depth buffer being used.
	 * @param useColourBuffer
	 *            - whether a colour buffer is needed.
	 * @param linear
	 *            - whether the colour buffer texture should use linear
	 *            sampling.
	 * @param clamp
	 *            - whether the edges of the colour buffer texture should be
	 *            clamped.
	 * @param samples
	 *            - the number of samples to be used if this FBO uses
	 *            multisampling.
	 */
	private void initialiseFrameBuffer(DepthBufferType type, boolean useColourBuffer, boolean linear, boolean clamp,
			int samples) {
		createFrameBuffer(useColourBuffer);
		if (!antialiased) {
			if (useColourBuffer) {
				createTextureAttachment(linear, clamp);
			}
			if (type == DepthBufferType.RENDER_BUFFER) {
				createDepthBufferAttachment(samples);
			} else if (type == DepthBufferType.TEXTURE) {
				createDepthTextureAttachment();
			}
		} else {
			attachMutlisampleColourBuffer(samples);
			createDepthBufferAttachment(samples);
		}
		unbindFrameBuffer();
	}

	/**
	 * Creates a new frame buffer object and sets the buffer to which drawing
	 * will occur.
	 * 
	 * @param useColourBuffer
	 *            - whether a colour buffer is being used.
	 */
	private void createFrameBuffer(boolean useColourBuffer) {
		frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		GL11.glDrawBuffer(useColourBuffer ? GL30.GL_COLOR_ATTACHMENT0 : GL11.GL_NONE);
	}

	/**
	 * Creates a colour buffer texture attachment.
	 * 
	 * @param linear
	 *            - whether the texture should use linear sampling.
	 * @param clamp
	 *            - whether the edges of the texture should be clamped.
	 */
	private void createTextureAttachment(boolean linear, boolean clamp) {
		colourTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, alpha ? GL11.GL_RGBA8 : GL11.GL_RGB8, width, height, 0,
				alpha ? GL11.GL_RGBA : GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, linear ? GL11.GL_LINEAR : GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, linear ? GL11.GL_LINEAR : GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
				clamp ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
				clamp ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colourTexture,
				0);
	}

	/**
	 * Adds a depth buffer to the FBO in the form of a texture, which can later
	 * be sampled.
	 */
	private void createDepthTextureAttachment() {
		depthTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0, GL11.GL_DEPTH_COMPONENT,
				GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
	}

	/**
	 * Attaches a depth buffer to the FBO. If antialiasing is on it will add a
	 * multisampling render buffer.
	 * 
	 * @param samples
	 *            - the number of samples to be used if antialiasing is on.
	 */
	private void createDepthBufferAttachment(int samples) {
		depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		if (antialiased) {
			GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, GL14.GL_DEPTH_COMPONENT24, width,
					height);
		} else {
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, width, height);
		}
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,
				depthBuffer);
	}

	/**
	 * Attaches a colour multisampling render buffer to the FBO.
	 * 
	 * @param samples
	 *            - the numbr of samples to be used in the multisampling
	 *            process.
	 */
	private void attachMutlisampleColourBuffer(int samples) {
		colourBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBuffer);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, alpha ? GL11.GL_RGBA8 : GL11.GL_RGB8,
				width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER,
				colourBuffer);
	}

}
