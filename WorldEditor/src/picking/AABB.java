package picking;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import objConverter.ModelData;

public class AABB {
	
	private Vector3f scale;
	private Vector4f offset;
	private Vector3f min;
	private Vector3f max;
	
	public AABB(Vector3f min, Vector3f max) {
		this.scale = Vector3f.sub(max, min, null);
		Vector3f middle = Vector3f.add(min, new Vector3f(scale.x/2f, scale.y/2f, scale.z/2f), null);
		this.offset = new Vector4f(middle.x, middle.y, middle.z, 1f);
		this.min = min;
		this.max = max;
	}
	
	public Vector3f getScale() {
		return scale;
	}
	
	public Vector4f getOffset() {
		return offset;
	}
	
	public float getMaxWidth(){
		float xWidth = getMaxAxisWidth(scale.x, offset.x);
		float zWidth = getMaxAxisWidth(scale.z, offset.z);
		return Math.max(xWidth, zWidth);
	}
	
	public float getMaxAxisWidth(float scale, float offset){
		return (scale * 0.5f) + Math.abs(offset);
	}
	
	public static AABB createAABBFromData(ModelData data){
		Vector3f min = new Vector3f(data.getVertices()[0],data.getVertices()[1], data.getVertices()[2]);
		Vector3f max = new Vector3f(data.getVertices()[0],data.getVertices()[1], data.getVertices()[2]);
		for(int i=0; i<data.getVertices().length; i+=3){
			
			if(data.getVertices()[i] < min.x){
				min.x = data.getVertices()[i];
			}else if(data.getVertices()[i] > max.x){
				max.x = data.getVertices()[i];
			}
			
			if(data.getVertices()[i+1] < min.y){
				min.y = data.getVertices()[i+1];
			}else if(data.getVertices()[i+1] > max.y){
				max.y = data.getVertices()[i+1];
			}
			
			if(data.getVertices()[i+2] < min.z){
				min.z = data.getVertices()[i+2];
			}else if(data.getVertices()[i+2] > max.z){
				max.z = data.getVertices()[i+2];
			}
		}
		return new AABB(min, max);
	}
	
	public Vector3f getMin(){
		return min;
	}
	
	public Vector3f getMax(){
		return max;
	}

}
