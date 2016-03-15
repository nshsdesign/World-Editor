package entities;

import org.lwjgl.util.vector.Vector3f;

public class BoundingBox {

	public Vector3f pos, size;

	public BoundingBox(Vector3f pos, Vector3f size) {
		this.pos = pos;
		this.size = size;
	}

	public boolean collides(BoundingBox b) {
		if (Math.abs(pos.x - b.pos.x) < size.x + b.size.x) {
			if (Math.abs(pos.y - b.pos.y) < size.y + b.size.y) {
				if (Math.abs(pos.z - b.pos.z) < size.z + b.size.z) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean contains(Vector3f b) {
		if (Math.abs(pos.x - b.x) < size.x) {
			if (Math.abs(pos.y - b.y) < size.y) {
				if (Math.abs(pos.z - b.z) < size.z) {
					return true;
				}
			}
		}
		return false;
	}

	public void update(Vector3f pos) {
		this.pos = pos;
	}
}
