package models;

import utils.Loader;

/*
 * Sphere
 * Iscrtavanje: TRIANGLE_STRIP
 * coordCount: broj jedinstvenih tacaka
 * vertexCount: broj tacaka
 * Ovo postoji zato sto se u svakoj iteraciji prave dve tacke gornja i donja, 
 * sto znaci da je gornja tacka u sledecoj iteraciji vec generisana.
 * Resenje:
 * 		newVertexIndex: pracenje indeksa novih tacaka
 * 		oldVertexIndex: pracenje indeksa tacka iz prethodnog reda
 */

public class Sphere extends Body {
	
	//private static final float angleStep = 1.0f; // Must be divisible with 360
	//private static final int coordCount = (int) ((180/angleStep + 1) * (180/angleStep + 1)); ////////////////////////
	private static final double gradation = 90;
	private static final int coordCount = (int) (2*(gradation + 1)*(gradation + 1));
	
	public Sphere(int width, double endAngle) {
		vertexCount = 2*coordCount;
		radius = (float) (width/(2*Math.PI));
		
		posCoords = new float[coordCount*3];
		texCoords = new float[coordCount*2];
		indices = new int[vertexCount];
		
		int vertexCounter = 0;
		// indices array
		int newVertexIndex = 0;
		int oldVertexIndex = 1;
		/*
		float phi, theta;
		float x, y, z;
		float s,t;
		float tStep = angleStep/180;
		float sStep = angleStep/180; /////////
		
		for (phi = 0, t = 0; phi < 180; phi += angleStep, t += tStep) {
			for (theta = endAngle + 180, s = 0; theta >= endAngle; theta -= angleStep, s += sStep) {
				if(phi == 0) {
					z = (float) (Math.sin(phi / 180 * Math.PI)*Math.cos(theta / 180 * Math.PI));
					x = (float) (Math.sin(phi / 180 * Math.PI)*Math.sin(theta / 180 * Math.PI));
					y = (float) Math.cos(phi / 180 * Math.PI);
					
					posCoords[3*newVertexIndex] = x*radius;
					posCoords[3*newVertexIndex + 1] = y*radius;
					posCoords[3*newVertexIndex + 2] = z*radius;
					texCoords[2*newVertexIndex] = s;
					texCoords[2*newVertexIndex + 1] = t;
					indices[vertexCounter++] = newVertexIndex++;
				}
				else if (phi == angleStep && theta > endAngle) {
					indices[vertexCounter++] = oldVertexIndex;
					oldVertexIndex += 2;
				}
				else
					indices[vertexCounter++] = oldVertexIndex++;
				
				z = (float) (Math.sin((phi+angleStep) / 180 * Math.PI)*Math.cos(theta / 180 * Math.PI));
				x = (float) (Math.sin((phi+angleStep) / 180 * Math.PI)*Math.sin(theta / 180 * Math.PI));
				y = (float) Math.cos((phi+angleStep) / 180 * Math.PI);
				
				posCoords[3*newVertexIndex] = x*radius;
				posCoords[3*newVertexIndex + 1] = y*radius;
				posCoords[3*newVertexIndex + 2] = z*radius;
				texCoords[2*newVertexIndex] = s;
				texCoords[2*newVertexIndex + 1] = t + tStep;		
				indices[vertexCounter++] = newVertexIndex++;
			}
		}
		*/
		
		float x, y, z;
		float s, t;
		double alpha1, alpha2, beta;
		
		for(double j = 0; j < gradation; j++) {
			alpha1 = j/gradation * Math.PI;
			alpha2 = (j+1)/gradation * Math.PI;
			for(double i = gradation; i >= 0; i--) {
				beta = i/gradation * endAngle;
				
				z = (float) (Math.sin(alpha1)*Math.cos(beta));
				x = (float) (Math.sin(alpha1)*Math.sin(beta));
				y = (float) Math.cos(alpha1);
				
				s = (endAngle > 0) ? (float) (1.0 - beta / endAngle) : (float) (beta / endAngle);
				t = (float) (alpha1 / Math.PI);
				
				posCoords[3*newVertexIndex] = x*radius;
				posCoords[3*newVertexIndex + 1] = y*radius;
				posCoords[3*newVertexIndex + 2] = z*radius;
				texCoords[2*newVertexIndex] = s;
				texCoords[2*newVertexIndex + 1] = t;
				indices[vertexCounter++] = newVertexIndex++;
				
				z = (float) (Math.sin(alpha2)*Math.cos(beta));
				x = (float) (Math.sin(alpha2)*Math.sin(beta));
				y = (float) Math.cos(alpha2);
				
				t = (float) (alpha2 / Math.PI);
				
				posCoords[3*newVertexIndex] = x*radius;
				posCoords[3*newVertexIndex + 1] = y*radius;
				posCoords[3*newVertexIndex + 2] = z*radius;
				texCoords[2*newVertexIndex] = s;
				texCoords[2*newVertexIndex + 1] = t;
				indices[vertexCounter++] = newVertexIndex++;
			}
		}
		
		vaoID = Loader.loadToVAO(posCoords, texCoords, indices);
	}
}
