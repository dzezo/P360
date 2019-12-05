package panorama;

import org.lwjgl.util.vector.Vector3f;

import models.Body;
import models.Cylinder;
import models.Sphere;

public class Panorama extends Texture {
	/* BodyType */
	public static final int TYPE_CYLINDRICAL = 0;
	public static final int TYPE_SPHERICAL = 1;

	private Body[] body = new Body[2];
	private int type;
	
	/* World transform */
	private Vector3f translation = new Vector3f(0,0,0);
	private Vector3f rotation = new Vector3f(0,0,0);
	private Vector3f scale = new Vector3f(1,1,1);
	
	public Panorama() {
		super();
		
		float imageAspect = (float) width / (float) height;
		if(imageAspect == 2) {
			body[0] = new Sphere(width, 180.0f);
			body[1] = new Sphere(width, 0);
			type = TYPE_SPHERICAL;
		}
		else {
			body[0] = new Cylinder(width, height, 0);
			body[1] = new Cylinder(width, height, 180.0f);
			type = TYPE_CYLINDRICAL;
		}
	}
	
	public Body getBody() {
		return body[0];
	}
	
	// drugi deo
	public Body getBody2() {
		return body[1];
	}
	
	public int getType() {
		return type;
	}
	
	public Vector3f getTranslation() {
		return translation;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Vector3f getScale() {
		return scale;
	}
}
