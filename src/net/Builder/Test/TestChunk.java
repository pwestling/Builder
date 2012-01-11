package net.Builder.Test;

import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslated;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.Builder.Learning.Block;
import net.Builder.Render.Viewable;
import net.Builder.util.FloatVector;

public class TestChunk {

	double x;
	double y;
	double z;
	double radius;
	boolean dirty = true;
	HashMap<String, FloatVector> vertices = new HashMap<String, FloatVector>();
	static int chunkSize = 16;

	Block[][][] blocks;

	public TestChunk(int x, int y, int z, int chunkSize) {
		this.chunkSize = chunkSize;
		blocks = new Block[chunkSize + 2][chunkSize + 2][chunkSize + 2];
		this.x = x;
		this.y = y;
		this.z = z;
		radius = Math.sqrt(chunkSize * chunkSize + chunkSize * chunkSize);
		Random r = new Random();
		for (int i = 1; i < chunkSize; i++) {
			for (int j = 1; j < chunkSize; j++) {
				// int zmax = r.nextInt((chunkSize - 1)) + 1;
				// int zmax = chunkSize;
				int zmax = r.nextInt(3) + chunkSize / 2;
				for (int k = 1; k < zmax; k++) {
					blocks[i][j][k] = new Block();
				}
			}
		}
		int visibleFace = 0;
		for (int i = 1; i < chunkSize; i++) {
			for (int j = 1; j < chunkSize; j++) {
				for (int k = 1; k < chunkSize; k++) {
					if (blocks[i][j][k] != null) {
						if (blocks[i][j][k + 1] == null) {
							blocks[i][j][k].visible[4] = true;
							visibleFace++;
						}
						if (blocks[i][j + 1][k] == null) {
							blocks[i][j][k].visible[3] = true;
							visibleFace++;
						}
						if (blocks[i + 1][j][k] == null) {
							blocks[i][j][k].visible[1] = true;
							visibleFace++;
						}
						if (blocks[i][j][k - 1] == null) {
							blocks[i][j][k].visible[5] = true;
							visibleFace++;
						}
						if (blocks[i][j - 1][k] == null) {
							blocks[i][j][k].visible[0] = true;
							visibleFace++;
						}
						if (blocks[i - 1][j][k] == null) {
							blocks[i][j][k].visible[2] = true;
							visibleFace++;
						}
					}
				}
			}
		}
		System.out.println(visibleFace);
		String[] colors = { "blue", "green", "red", "yellow", "purple", "white" };
		for (String c : colors) {
			vertices.put(c, new FloatVector());
		}

	}

	public HashMap<String, FloatVector> vertices() {
		if (dirty) {
			for (String key : vertices.keySet()) {
				vertices.get(key).clear();
			}
			for (int i = 0; i < chunkSize; i++) {
				for (int j = 0; j < chunkSize; j++) {
					for (int k = 0; k < chunkSize; k++) {
						if (blocks[i][j][k] != null) {
							blocks[i][j][k].draw((int) (x + i), (int) (y + k),
									(int) (z + j), vertices);
						}
					}
				}
			}
			dirty = false;
		}

		return vertices;

	}

	public float[] boundingSphere() {
		float[] boundSphere = { (float) (x + chunkSize / 2),
				(float) (y + chunkSize / 2), (float) (z + chunkSize / 2),
				(float) radius };
		for (float f : boundSphere) {
			// System.out.println(f);
		}
		return boundSphere;
	}

	public ArrayList<Viewable> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

}
