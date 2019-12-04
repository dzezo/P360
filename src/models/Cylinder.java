package models;

import utils.Loader;

public class Cylinder extends Body {
	
	// Must be divisible with 360
	private static final float angleStep = 1.0f;
	
	public Cylinder(int w, int h, float startAngle) {
		vertexCount = (int) (2*(180/angleStep + 1)); //////////////////
		radius = (float) (w/(2*Math.PI));
		
		posCoords = new float[vertexCount*3];
		texCoords = new float[vertexCount*2];
		indices = new int[vertexCount];
		
		float x,y,z; // posCoords
		float s,t;   // texCoords
		float alpha;
		float sStep = angleStep/180;
		
		int vertexIndex = 0;
		for(alpha=startAngle, s = 0; alpha<=startAngle + 180; alpha+=angleStep, s+=sStep) {
			x = (float) (radius * (Math.cos(alpha / 180 * Math.PI)));
			z = (float) (radius * (Math.sin(alpha / 180 * Math.PI)));
			y = (float) h/2;
			t = 0;
			posCoords[3*vertexIndex] = x;
			posCoords[3*vertexIndex + 1] = y;
			posCoords[3*vertexIndex + 2] = z;
			texCoords[2*vertexIndex] = s;
			texCoords[2*vertexIndex + 1] = t;
			indices[vertexIndex] = vertexIndex;
			vertexIndex++;
			
			y = (float) -h/2;
			t = 1;
			posCoords[3*vertexIndex] = x;
			posCoords[3*vertexIndex + 1] = y;
			posCoords[3*vertexIndex + 2] = z;
			texCoords[2*vertexIndex] = s;
			texCoords[2*vertexIndex + 1] = t;
			indices[vertexIndex] = vertexIndex;
			vertexIndex++;
		}
		
		vaoID = Loader.loadToVAO(posCoords, texCoords, indices);
	}
}
