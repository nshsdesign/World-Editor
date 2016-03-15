package picking;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import toolbox.Maths;

public class AABB {
	
	private Vector3f scale;
	private Vector4f offset;
	
	public AABB(Vector3f min, Vector3f max) {
		this.scale = Vector3f.sub(max, min, null);
		Vector3f middle = Vector3f.add(min, new Vector3f(scale.x/2f, scale.y/2f, scale.z/2f), null);
		this.offset = new Vector4f(middle.x, middle.y, middle.z, 1f);
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

}
