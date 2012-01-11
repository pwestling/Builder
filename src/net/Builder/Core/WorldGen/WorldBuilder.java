package net.Builder.Core.WorldGen;

import net.Builder.Core.Chunk;

public interface WorldBuilder {

	public Chunk makeChunk(int x, int y, int z);

}
