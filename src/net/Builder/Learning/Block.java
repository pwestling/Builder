package net.Builder.Learning;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Random;

import net.Builder.util.FloatVector;

public class Block {

	public boolean visible[] = { false, false, false, false, false, false };
	boolean isVisible = true;

	int list = 0;

	float blue = 0;
	float red = 0;
	float green = 0;

	public Block() {
		red = (float) Math.random();
		blue = (float) Math.random();
		green = (float) Math.random();
	}

	public void draw(int x, int y, int z, HashMap<String, FloatVector> vertices) {
		if (isVisible) {
			float tlx = x;
			float tly = y;
			float tlz = z;
			float brx = x + 1;
			float bry = y + 1;
			float brz = z + 1;

			if (visible[0]) {

				vertices.get("blue").add(tlx).add(bry).add(tlz);
				vertices.get("blue").add(brx).add(bry).add(tlz);
				vertices.get("blue").add(brx).add(tly).add(tlz);
				vertices.get("blue").add(tlx).add(tly).add(tlz);

			}
			if (visible[1]) {
				vertices.get("red").add(brx).add(tly).add(tlz);
				vertices.get("red").add(brx).add(bry).add(tlz);
				vertices.get("red").add(brx).add(bry).add(brz);
				vertices.get("red").add(brx).add(tly).add(brz);
			}
			if (visible[2]) {
				vertices.get("green").add(tlx).add(tly).add(tlz);
				vertices.get("green").add(tlx).add(tly).add(brz);
				vertices.get("green").add(tlx).add(bry).add(brz);
				vertices.get("green").add(tlx).add(bry).add(tlz);
			}
			if (visible[3]) {
				vertices.get("yellow").add(tlx).add(tly).add(brz);
				vertices.get("yellow").add(brx).add(tly).add(brz);
				vertices.get("yellow").add(brx).add(bry).add(brz);
				vertices.get("yellow").add(tlx).add(bry).add(brz);

			}
			if (visible[4]) {
				vertices.get("purple").add(tlx).add(tly + 1).add(tlz);
				vertices.get("purple").add(tlx).add(tly + 1).add(brz);
				vertices.get("purple").add(brx).add(tly + 1).add(brz);
				vertices.get("purple").add(brx).add(tly + 1).add(tlz);
			}
			if (visible[5]) {
				vertices.get("white").add(brx).add(bry - 1).add(tlz);
				vertices.get("white").add(brx).add(bry - 1).add(brz);
				vertices.get("white").add(tlx).add(bry - 1).add(brz);
				vertices.get("white").add(tlx).add(bry - 1).add(tlz);

			}

		}

	}

	/*
	 * public void cuboid(float tlx, float tly, float tlz, float brx, float bry,
	 * float brz) { glColor3f(red, green, blue); glBegin(GL_QUADS); if
	 * (visible[0]) {
	 * 
	 * vertices.put(tlx, bry, tlz); vertices.put(brx, bry, tlz);
	 * vertices.put(brx, tly, tlz); vertices.put(tlx, tly, tlz);
	 * 
	 * } if (visible[1]) { vertices.put(brx, tly, tlz); vertices.put(brx, bry,
	 * tlz); vertices.put(brx, bry, brz); vertices.put(brx, tly, brz); } if
	 * (visible[2]) { vertices.put(tlx, tly, tlz); vertices.put(tlx, tly, brz);
	 * vertices.put(tlx, bry, brz); vertices.put(tlx, bry, tlz); } if
	 * (visible[3]) { vertices.put(tlx, tly, brz); vertices.put(brx, tly, brz);
	 * vertices.put(brx, bry, brz); vertices.put(tlx, bry, brz);
	 * 
	 * } if (visible[4]) { vertices.put(tlx, tly, tlz); vertices.put(tlx, tly,
	 * brz); vertices.put(brx, tly, brz); vertices.put(brx, tly, tlz); } if
	 * (visible[5]) { vertices.put(brx, bry, tlz); vertices.put(brx, bry, brz);
	 * vertices.put(tlx, bry, brz); vertices.put(tlx, bry, tlz);
	 * 
	 * } glEnd();
	 * 
	 * }
	 */

	public boolean visible() {
		return isVisible;
	}

}
