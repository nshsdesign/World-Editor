package picking;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Entity;
import objConverter.ModelData;
import toolbox.Maths;

public class BoundingBox{

	private ModelData data;
	private Matrix4f modelMatrix = new Matrix4f();
	private Vector3f scale = new Vector3f();
	private boolean dirty = true;
	private Entity e;
	private AABB aabb;

	public BoundingBox(Entity e) {
		this.e = e;
		this.data = e.getModel().getRawModel().getModelData();
		this.aabb = AABB.createAABBFromData(data);
	}

	public Vector3f getSizes() {
		if (needsRecalculating()) {
			recalculate();
		}
		return scale;
	}

	public Matrix4f getModelMatrix() {
		if (needsRecalculating()) {
			recalculate();
		}
		return modelMatrix;
	}

	public float getHeight() {
		return (aabb.getScale().y * 0.5f + aabb.getOffset().y) * e.getScale();
	}

	public float getMaxWidth() {
		return aabb.getMaxWidth() * e.getScale();
	}

	private boolean needsRecalculating() {
		return dirty;
	}

	private void recalculate() {
		Vector4f position = Matrix4f.transform(e.getModelMatrix(), aabb.getOffset(), null);
		scale.set(aabb.getScale());
		scale.scale(e.getScale());
		Maths.updateModelMatrix(modelMatrix, new Vector3f(position), e.getRotX(), e.getRotY(),
				e.getRotZ(), scale);
		dirty = false;
	}
	
	public AABB getAABB(){
		return aabb;
	}

}
