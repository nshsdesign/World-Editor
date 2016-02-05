package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;

public class Camera {
	
	private static final float MIN_ZOOM = 50;
	private static final float MAX_ZOOM = 500;

	private Vector3f position;
	private float speed;
	private float angleSpeed;
	private float pitch;
	private float yaw;
	private float roll;
	private float mouseWheelSensitivity;
	private float zoomDistance;
	private float dx;
	private float dz;
	
	//FreeRoam
	private static final float ANGLE_MOVE_SPEED = 50;
	private static final float MOVE_SPEED = ANGLE_MOVE_SPEED;
	
	public Camera(){
		position = new Vector3f(0,10,0);
		
		dx = 0;
		dz = 0;
		mouseWheelSensitivity = 0.1f;
		Mouse.setGrabbed(false);
		zoomDistance = 50;
		pitch = 10;
		yaw = 135;
	}
	
	public void move(){
        speed = MOVE_SPEED * DisplayManager.getFrameTimeSeconds();
        angleSpeed = ANGLE_MOVE_SPEED * DisplayManager.getFrameTimeSeconds();
        handleInputs();
        calculateZoom();
        position.y = zoomDistance;
    
	}
	
	private void handleInputs(){
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			dz = (float) (-speed * Math.cos(Math.toRadians(yaw)));
			dx = (float) (speed * Math.sin(Math.toRadians(yaw)));
			position.z+= dz;
			position.x+= dx;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			dz = (float) (speed * Math.cos(Math.toRadians(yaw)));
			dx = (float) (-speed * Math.sin(Math.toRadians(yaw)));
			position.z+= dz;
			position.x+= dx;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			dz = (float) (-speed * Math.cos(Math.toRadians(yaw+90)));
			dx = (float) (speed * Math.sin(Math.toRadians(yaw+90)));
			position.z+= dz;
			position.x+= dx;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			dz = (float) (speed * Math.cos(Math.toRadians(yaw+90)));
			dx = (float) (-speed * Math.sin(Math.toRadians(yaw+90)));
			position.z+= dz;
			position.x+= dx;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
			yaw -= angleSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_E)){
			yaw += angleSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_R)){
			pitch -= angleSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_F)){
			pitch += angleSpeed;
		}
	}
	
	public void invertPitch(){
		this.pitch = -pitch;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private void calculateZoom(){
		float zoomLevel = Mouse.getDWheel() * mouseWheelSensitivity;
		if(zoomDistance-zoomLevel>=MIN_ZOOM && zoomDistance-zoomLevel<=MAX_ZOOM)zoomDistance -= zoomLevel;
	}
	
}
