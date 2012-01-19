package net.Builder.Core.WorldGen;

import net.Builder.Core.Chunk;
import net.Builder.util.Point;

public interface WorldBuilder {

	public Chunk makeChunk(int x, int y, int z);
	
	public Chunk makeChunk(Point p);

}
