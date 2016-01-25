package entities;

import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import terrain.Terrain;

@SuppressWarnings("unused")
public class Player extends Entity{

	public Player(TexturedModel model, Vector3f position, float rotX,
			float rotY, float rotZ, float scale, Terrain[][] terrains) {
		super(model, position, rotX, rotY, rotZ, scale, terrains);
	}
	
}
