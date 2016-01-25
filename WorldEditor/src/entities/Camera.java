package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	public static final int YAW = 0;
	
	private static final float OFFSET_Y = 7;

	private Vector3f position;
	private float pitch;
	private float yaw;
	private float roll;
	private Player player;
	private float distanceFromPlayer = 200;
	
	public Camera(Player player){
		this.player = player;
		position = new Vector3f(player.getPosition().x, player.getPosition().y + 100, player.getPosition().z - 70);
		pitch = 45;
		yaw = 180;
	}
	
	public void move(){
		calculateZoom();
        float horizontalDistance = calculateHorizontalDistance();
        float verticleDistance = calculateVerticleDistance();
        calculateCameraPosition(horizontalDistance, verticleDistance);
		
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
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		if(distanceFromPlayer-zoomLevel>=150 && distanceFromPlayer-zoomLevel<=300)distanceFromPlayer -= zoomLevel;
	}
	
	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticleDistance(){
		float vD = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
		if(vD<0){
			vD = 0;
			pitch = 0;
		}
		return vD;
	}
	
	private void calculateCameraPosition(float hDistance, float vDistance){
		position.x = player.getPosition().x;
		position.z = player.getPosition().z - hDistance;
		position.y = player.getPosition().y + vDistance + OFFSET_Y;
	}
	
}