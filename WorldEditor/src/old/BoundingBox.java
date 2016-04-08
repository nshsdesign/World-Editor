package old;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;

public class BoundingBox{

	private float baseScale;
	private Vector3f scale = new Vector3f();
	private Entity entity;

	public BoundingBox(Entity entity) {
		this.baseScale = entity.getScale();
		this.entity = entity;
	}

	public Vector3f getSizes() {
//		if (needsRecalculating()) {
//			recalculate();
//		}
		return scale;
	}
//
//	public Matrix4f getModelMatrix() {
//		if (needsRecalculating()) {
//			recalculate();
//		}
//		return modelMatrix;
//	}

	public float getHeight() {
		AABB aabb = entity.getAABB();
		return (aabb.getScale().y * 0.5f + aabb.getOffset().y) * baseScale;
	}

	public float getMaxWidth() {
		//mesh.getCurrentModelStage().getAABB();
		AABB aabb = entity.getAABB();
		return aabb.getMaxWidth() * baseScale;
	}

//	private boolean needsRecalculating() {
//		return dirty || mesh.getCurrentStageNumber() != calculatedStage;
//	}

//	private void recalculate() {
//		AABB aabb = mesh.getCurrentModelStage().getAABB();
//		calculatedStage = mesh.getCurrentStageNumber();
//		Vector4f position = Matrix4f.transform(transform.getModelMatrix(), aabb.getOffset(), null);
//		scale.set(aabb.getScale());
//		scale.scale(transform.getScale());
//		Maths.updateModelMatrix(modelMatrix, new Vector3f(position), transform.getRotX(), transform.getRotY(),
//				transform.getRotZ(), scale);
//		dirty = false;
//	}

}
