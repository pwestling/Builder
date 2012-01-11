package net.Builder.Core.WorldGen;

import java.util.Random;

import net.Builder.Core.BlockLibrary;
import net.Builder.Core.Chunk;
import net.Builder.util.Profiler;
import net.Builder.util.SimplexNoiseGenerator;

public class Simplex2DWorldBuilder implements WorldBuilder {

	private SimplexNoiseGenerator sngHeight;
	private SimplexNoiseGenerator sngElev;
	private SimplexNoiseGenerator sngDetail;
	private SimplexNoiseGenerator sngRough;

	private Random rand;
	private double heightScale = 10000;
	private double elevationScale = 400;
	private double detailScale = 30;
	private double roughScale = 70;

	private int heightLimit = 512;

	public Simplex2DWorldBuilder(long seed) {

		rand = new Random(seed);

		sngHeight = new SimplexNoiseGenerator(rand.nextLong());
		sngElev = new SimplexNoiseGenerator(rand.nextLong());
		sngDetail = new SimplexNoiseGenerator(rand.nextLong());
		sngRough = new SimplexNoiseGenerator(rand.nextLong());
	}

	@Override
	public Chunk makeChunk(int x, int y, int z) {
		// long time = System.nanoTime();
		 
		Chunk c = new Chunk(x, y, z);
		c.setBlocks(makeBlocks2D(x, y, z));
		c.init();
		//c.compress();
		//System.out.println("Chunk gen took "+((System.nanoTime()-time)/1000000000));
		return c;
	}

	private short[][][] makeBlocks2D(int x, int y, int z) {
		short[][][] blocks = new short[Chunk.chunkSize][Chunk.chunkSize][Chunk.chunkSize];
		short grassid = BlockLibrary.getBlockLibrary().getBlockID("grass");
		short stoneid = BlockLibrary.getBlockLibrary().getBlockID("stone");
		int[][] heightMap = new int[Chunk.chunkSize][Chunk.chunkSize];
		short airid = BlockLibrary.getBlockLibrary().getBlockID("air");
		int step = 1;
		if (y < -64 || y >= heightLimit + Chunk.chunkSize) {
			for (int i = 0; i < Chunk.chunkSize; i++) {
				for (int j = 0; j < Chunk.chunkSize; j++) {
					for (int k = 0; k < Chunk.chunkSize; k++) {
						blocks[i][j][k] = airid;
					}
				}
			}
			return blocks;
		}
		for (int i = 0; i < Chunk.chunkSize; i++) {
			for (int j = 0; j < Chunk.chunkSize; j++) {
				heightMap[i][j] = 0;
			}
		}
		int chunkLimit = heightLimit;

		for (int i = 0; i < Chunk.chunkSize; i += step) {
			for (int j = 0; j < Chunk.chunkSize; j += step) {
				double px = (i + x + Math.random());
				double pz = (j + z + Math.random());
				int height = (int) (Math.abs(sngElev.noise(px
						/ (elevationScale), pz / (elevationScale))
						* sngHeight.noise(px / heightScale, pz / heightScale)) * chunkLimit);
				int roughLimit = height / 4;
				height += (sngDetail.noise(px / detailScale, pz / detailScale) * sngRough
						.noise(px / roughScale, pz / roughScale)) * roughLimit;
				heightMap[i][j] = height;

			}

		}

		for (int i = 0; i < Chunk.chunkSize; i++) {
			for (int j = 0; j < Chunk.chunkSize; j++) {

				int height = Math.min(Math.max(heightMap[i][j] - y, 0),
						Chunk.chunkSize);
				int dirtHeight = Math.min(rand.nextInt(4) + 3, height);
				if (y < 0) {
					dirtHeight = 0;
				}
				if (rand.nextDouble() < 0.01) {
					dirtHeight = 0;
				}

				for (int k = 0; k < height - dirtHeight; k++) {
					blocks[i][j][k] = stoneid;
				}
				for (int k = height - dirtHeight; k < height && k >= 0; k++) {
					blocks[i][j][k] = grassid;
				}
				for (int k = height; k < Chunk.chunkSize; k++) {
					blocks[i][j][k] = airid;
				}

			}
		}

		return blocks;
	}

}
