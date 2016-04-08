package entities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import picking.AABB;
import picking.BoundingBox;
import renderEngine.Loader;
import textures.ModelTexture;
import toolbox.Maths;

public class Entity {

	private TexturedModel model;
	protected Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	private BoundingBox boundingBox;
	private boolean isStatic;
	private String name;
	private Matrix4f modelMatrix = new Matrix4f();
	
	private int textureIndex = 0;

	public Entity(Loader loader, String name, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.name = name;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.boundingBox = new BoundingBox(this);
		

		RawModel rawModel = OBJFileLoader.loadOBJ(name, loader);
		ModelTexture tex = new ModelTexture(loader.loadTexture("textureFiles", name));
		this.model = new TexturedModel(rawModel, tex);
	}

	public Entity(Loader loader, String name, int index, Vector3f position, float rotX, float rotY, float rotZ,
			float scale) {
		this.name = name;
		this.textureIndex = index;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.boundingBox = new BoundingBox(this);

		RawModel rawModel = OBJFileLoader.loadOBJ(name, loader);
		ModelTexture tex = new ModelTexture(loader.loadTexture("textureFiles", name));
		this.model = new TexturedModel(rawModel, tex);
	}

	public Entity(Loader loader, String name, Vector3f position, Vector3f rot, float scale) {
		this.name = name;
		this.position = position;
		this.rotX = rot.x;
		this.rotY = rot.y;
		this.rotZ = rot.z;
		this.scale = scale;

		RawModel rawModel = OBJFileLoader.loadOBJ(name, loader);
		ModelTexture tex = new ModelTexture(loader.loadTexture("textureFiles", name));
		this.model = new TexturedModel(rawModel, tex);

		this.boundingBox = new BoundingBox(this);
	}

	public Entity(Entity e) {
		this.name = e.getName();
		this.position = e.getPosition();
		this.rotX = e.getRotX();
		this.rotY = e.getRotY();
		this.rotZ = e.getRotZ();
		this.scale = e.getScale();
		this.boundingBox = e.getBoundingBox();
		this.model = e.getModel();
	}

	public float getTextureXOffset() {
		int column = textureIndex % model.getTexture().getNumberOfRows();
		return (float) column / (float) model.getTexture().getNumberOfRows();
	}

	public float getTextureYOffset() {
		int row = textureIndex / model.getTexture().getNumberOfRows();
		return (float) row / (float) model.getTexture().getNumberOfRows();
	}

	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}

	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public boolean getIsStatic() {
		return isStatic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setModel(Loader loader, String name) {
		RawModel rawModel = OBJFileLoader.loadOBJ(name, loader);
		ModelTexture tex = new ModelTexture(loader.loadTexture("textureFiles", name));
		this.model = new TexturedModel(rawModel, tex);
		this.name = name;
	}

	public AABB getAABB() {
		return boundingBox.getAABB();
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public Matrix4f getModelMatrix(){
		Maths.updateTransformationMatrix(modelMatrix, position.x, position.y, position.z, rotX, rotY, rotZ, scale);
		return modelMatrix;
	}
	
	public void updateModelMatrix(Vector3f up, Vector3f forward) {
		Matrix4f rotation = Maths.getRotationMatrix(up, forward);
		modelMatrix.setIdentity();
		Matrix4f.translate(position, modelMatrix, modelMatrix);
		Matrix4f.mul(modelMatrix, rotation, modelMatrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), modelMatrix, modelMatrix);
	}
	
	public void update(){
		Vector3f up = new Vector3f((float) Math.cos(Math.toRadians(rotZ)), (float) Math.sin(Math.toRadians(rotZ)), (float) Math.sin(Math.toRadians(rotY)));
		Vector3f forward = new Vector3f((float) Math.cos(Math.toRadians(rotX)), (float) Math.sin(Math.toRadians(rotY)), (float) Math.tan(Math.toRadians(rotZ)));
		updateModelMatrix(up, forward);
	}
	
}
