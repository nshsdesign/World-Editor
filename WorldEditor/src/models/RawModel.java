package models;

import objConverter.ModelData;

public class RawModel {

	private int vaoID;
	private int vertexCount;
	private ModelData data;

	public RawModel(int vaoID, int vertexCount, ModelData data) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.data = data;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
	public ModelData getModelData(){
		return data;
	}

}
