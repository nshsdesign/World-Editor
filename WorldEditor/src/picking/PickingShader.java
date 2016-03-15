package picking;

import java.io.File;

import shaders.ShaderProgram;

public class PickingShader extends ShaderProgram {
	
	private static final File VERTEX_SHADER = new File("picking", "pickingVertex.txt");
	private static final File FRAGMENT_SHADER = new File("picking", "pickingFragment.txt");


	public PickingShader() {
		super(VERTEX_SHADER.getName(), FRAGMENT_SHADER.getName());
		super.storeAllUniformLocations();
	}

}
