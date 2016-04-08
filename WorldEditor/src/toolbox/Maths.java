package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;

public class Maths {

	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
	
	public static Matrix4f updateModelMatrix(Matrix4f matrix, Vector3f position, float rotX, float rotY, float rotZ,
			Vector3f scale) {
		matrix.setIdentity();
		Matrix4f.translate(position, matrix, matrix);
		Matrix4f.rotate(Maths.degreesToRadians(rotX), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate(Maths.degreesToRadians(rotY), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate(Maths.degreesToRadians(rotZ), new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.scale(scale, matrix, matrix);
		return matrix;
	}
	
	public static float degreesToRadians(float degrees) {
		return degrees * (float) (Math.PI / 180d);
	}

	public static Matrix4f getRotationMatrix(Vector3f up, Vector3f forwardish) {
		Vector3f right = Vector3f.cross(forwardish, up, null);
		right.normalise();
		Vector3f.cross(up, right, forwardish);
		forwardish.normalise();
		Matrix4f rotationMat = new Matrix4f();
		rotationMat.m00 = right.x;
		rotationMat.m01 = right.y;
		rotationMat.m02 = right.z;
		rotationMat.m10 = up.x;
		rotationMat.m11 = up.y;
		rotationMat.m12 = up.z;
		rotationMat.m20 = -forwardish.x;
		rotationMat.m21 = -forwardish.y;
		rotationMat.m22 = -forwardish.z;
		return rotationMat;
	}
	
	public static Matrix4f updateTransformationMatrix(Matrix4f transformationMatrix, float x, float y, float z,
			float rotX, float rotY, float rotZ, float scale) {
		transformationMatrix.setIdentity();
		Matrix4f.translate(new Vector3f(x, y, z), transformationMatrix, transformationMatrix);
		Matrix4f.rotate(Maths.degreesToRadians(rotX), new Vector3f(1, 0, 0), transformationMatrix,
				transformationMatrix);
		Matrix4f.rotate(Maths.degreesToRadians(rotY), new Vector3f(0, 1, 0), transformationMatrix,
				transformationMatrix);
		Matrix4f.rotate(Maths.degreesToRadians(rotZ), new Vector3f(0, 0, 1), transformationMatrix,
				transformationMatrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), transformationMatrix, transformationMatrix);
		return transformationMatrix;
	}
}
