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
	private boolean autoPanEnabled = true;
	private final float autoPanSpeed = 0.05f;
	private	final float pitchDampingFactor = 0.005f; // range from 1 to 0
	private final long autoPanLatency = 2500; // in milis
	private	final float terminateDamping = 0.01f; // when to stop damping
	
	/**
	 * Get camera wold positon
	 */
	public Vector3f getPosition() {
		return position;
	}

	/* PITCH CONTROL METHODS */
	
	public float getPitch() {
		return pitch;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	/**
	 * Rotate vertically
	 * @param angle - angle of rotation
	 */
	public void pitch(float angle) {
		if(pitch+angle < pitchMin)
			pitch = pitchMin;
		else if (pitch+angle > pitchMax)
			pitch = pitchMax;
		else
			pitch += angle;
	}
	
	public void setPitchLimit(float pitchLimit) {
		this.pitchMax = pitch + pitchLimit;
		this.pitchMin = pitch - pitchLimit;
	}

	/* YAW CONTROL METHODS */
	
	public float getYaw() {
		return yaw;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	/**
	 * Rotate horizontally
	 * @param angle - angle of rotation
	 */
	public void yaw(float angle) {
		yaw += angle;
	}
	
	/* AUTO PAN METHODS */
	
	public void autoPan() {
		float pitch;
		// Auto Pan can begin if not disabled
		if (autoPanEnabled) {
			long currentTime = System.currentTimeMillis();
			
			// Check if user has recently interacted
			if(currentTime >= (InputManager.lastInteractTime + autoPanLatency)) {
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
				
				// Pan around 
				yaw(autoPanSpeed);
				
				// Update trip meter
				autoPanTripMeter += autoPanSpeed;
			}
			else {
				autoPanning = false;
			}
		}
	}
	
	/**
	 * Function that toggles auto pan on and off.
	 * @returns current auto pan state
	 */
	public boolean setAutoPan() {
		InputManager.lastInteractTime = 0;
		
		autoPanEnabled = !autoPanEnabled;

		return autoPanEnabled;
	}
	
	public boolean getAutoPan() {
		return this.autoPanEnabled;
	}
	
	/**
	 * @returns true if camera is rotating by itself
	 */
	public boolean isAutoPanning() {
		return (autoPanEnabled && autoPanning);
	}
	
	/**
	 * @returns true if auto pan made a full circle
	 */
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
