package postProcessing;

public class FboBuilder {

	private int width;
	private int height;
	private DepthBufferType depthBuffer = DepthBufferType.NONE;
	private boolean clampEdge = true;
	private boolean linearFiltering = true;
	private boolean colourBuffer = true;
	
	private boolean alphaChannel = false;
	private boolean antialiased = false;
	private int samples = 1;

	protected FboBuilder(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public FboBuilder setDepthBuffer(DepthBufferType type) {
		this.depthBuffer = type;
		return this;
	}

	public FboBuilder repeatTexture() {
		this.clampEdge = false;
		return this;
	}
	
	public FboBuilder antialias(int samples){
		this.antialiased = true;
		this.samples = samples;
		return this;
	}

	public FboBuilder nearestFiltering() {
		linearFiltering = false;
		return this;
	}
	
	public FboBuilder withAlphaChannel(boolean alpha){
		this.alphaChannel = alpha;
		return this;
	}

	public FboBuilder noColourBuffer() {
		colourBuffer = false;
		return this;
	}

	public Fbo create() {
		return new Fbo(width, height, depthBuffer, colourBuffer, linearFiltering, clampEdge, alphaChannel, antialiased, samples);
	}

	public static enum DepthBufferType {
		RENDER_BUFFER(), TEXTURE(), NONE();
	}

}
