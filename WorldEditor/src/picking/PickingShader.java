package picking;

import shaders.ShaderProgram;

public class PickingShader extends ShaderProgram {
	
	private static final String VERTEX_SHADER = "src/picking/pickingVertex.txt";
	private static final String FRAGMENT_SHADER = "src/picking/pickingFragment.txt";


	public PickingShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
		super.storeAllUniformLocations();
	}


	@Override
	protected void getAllUniformLocations() {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void bindAttributes() {
		// TODO Auto-generated method stub
		
	}

}
