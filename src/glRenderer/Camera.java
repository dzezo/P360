package glRenderer;

import org.lwjgl.util.vector.Vector3f;

import input.InputManager;

public class Camera {
	// Camera world position
	private Vector3f position = new Vector3f(0,0,0);
	
	// Camera angles
	private float pitch;
	private float pitchMax;
	private float pitchMin;
	private float yaw;
	
	// AutoPan
	private float autoPanTripMeter = 0;
	private boolean autoPanning = false;
	
	// AutoPan config
	private boolean autoPanEnabled = false;
	private final float autoPanSpeed = 0.9f;//0.05f;
	private	final float pitchDampingFactor = 0.005f; // range from 1 to 0
	private final long autoPanLatency = 2500; // in milis
	private	final float terminateDamping = 0.01f; // when to stop damping
	
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
	
	public void autoPan() {
		float pitch;
		if (autoPanEnabled) {
			// Auto Pan can begin if not disabled
			long currentTime = System.currentTimeMillis();
			if(currentTime >= (InputManager.lastInteractTime + autoPanLatency)) {
				// User is not interacting
				autoPanning = true;
				
				// Bring down camera pitch if it's not leveled
				pitch = getPitch();
				if(pitch != 0) {
					if(Math.abs(pitch) > terminateDamping) {
						pitch *= (1 - pitchDampingFactor);
						setPitch(pitch);
					}
					else
						setPitch(0);
				}
				
				// Pan
				yaw(autoPanSpeed);
				autoPanTripMeter += autoPanSpeed;
			}
			else {
				autoPanning = false;
			}
		}
	}
	
	public void setAutoPan(boolean b) {
		this.autoPanEnabled = b;
	}
	
	public boolean getAutoPan() {
		return this.autoPanEnabled;
	}
	
	public boolean isAutoPanning() {
		return (autoPanEnabled && autoPanning);
	}
	
	public boolean cycleComplete() {
		if(autoPanTripMeter > 360.0f)
			return true;
		else
			return false;
	}
	
	public void resetTripMeter() {
		autoPanTripMeter = 0;
	}
}
