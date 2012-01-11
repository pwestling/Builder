package net.Builder.Learning;

import java.util.ArrayList;

import net.Builder.Test.TestChunk;

public class World {

	ArrayList<TestChunk> chunks = new ArrayList<TestChunk>();

	public World(int chunklength) {

		for (int i = 0; i < chunklength; i++) {

			for (int j = 0; j < chunklength; j++) {
				int chunkSize = 16;
				chunks.add(new TestChunk(i * chunkSize, j * chunkSize, 0,
						chunkSize));

			}

		}

	}

	public void draw(float[][] frustum) {
		for (TestChunk c : chunks) {
			// c.draw(frustum);
		}
	}

}
