package glRenderer;

import org.lwjgl.util.vector.Vector3f;

public class Camera {
	// Camera world position
	private Vector3f position = new Vector3f(0,0,0);
	// Camera angles
	private float pitch;
	private float pitchMax;
	private float pitchMin;
	private float yaw;
	
	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	// rotate around y-axis
	public void pitch(float angle) {
		if(pitch+angle < pitchMin)
			pitch = pitchMin;
		else if (pitch+angle > pitchMax)
			pitch = pitchMax;
		else
			pitch += angle;
	}
	
	public void resetPitch() {
		pitch = 0f;
	}
	
	public void setPitchLimit(float pitchLimit) {
		this.pitchMax = pitch + pitchLimit;
		this.pitchMin = pitch - pitchLimit;
	}

	public float getYaw() {
		return yaw;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	// rotate around x-axis
	public void yaw(float angle) {
		yaw += angle;
	}
}
