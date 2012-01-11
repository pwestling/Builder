package net.Builder.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.Builder.Render.Renderer;
import net.Builder.Render.Viewable;
import net.Builder.util.*;

public class Chunk implements Viewable {

	private float x;
	private float y;
	private float z;

	public static final int chunkSize = 32;

	
	private ShortVector[][] zippedBlocks;
	private short[][][] unzippedBlocks;
	private boolean compressed = false;

	private float radius;

	private boolean dirty = true;

	private int listID = 0;

	private boolean savedSinceDirty = false;

	FloatVectorHash mesh = null;

	private static final int[] vxtop = { 1, 0, 0, 1 };
	private static final int[] vytop = { 1, 1, 1, 1 };
	private static final int[] vztop = { 0, 0, 1, 1 };
	private static final int[] vxbot = { 0, 1, 1, 0 };
	private static final int[] vybot = { 0, 0, 0, 0 };
	private static final int[] vzbot = { 0, 0, 1, 1 };
	private static final int[] vxe = { 1, 1, 1, 1 };
	private static final int[] vye = { 0, 1, 1, 0 };
	private static final int[] vze = { 0, 0, 1, 1 };
	private static final int[] vxw = { 0, 0, 0, 0 };
	private static final int[] vyw = { 1, 0, 0, 1 };
	private static final int[] vzw = { 0, 0, 1, 1 };
	private static final int[] vxn = { 0, 0, 1, 1 };
	private static final int[] vyn = { 1, 0, 0, 1 };
	private static final int[] vzn = { 1, 1, 1, 1 };
	private static final int[] vxs = { 0, 0, 1, 1 };
	private static final int[] vys = { 0, 1, 1, 0 };
	private static final int[] vzs = { 0, 0, 0, 0 };

	public Chunk(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;

		radius = (float) Math.hypot(Math.hypot(chunkSize / 2, chunkSize / 2),
				chunkSize / 2);

	}

	public void init() {
		generateMeshStripes();
	}

	private void generateMeshStripes() {
		vertexCount = 0;
		FloatVectorHash[] mesh = new FloatVectorHash[6];
		for (int i = 0; i < 6; i++) {
			mesh[i] = new FloatVectorHash();
		}
		// Block block = lookup(0, 0, 0);
		int[] skipped = { 0, 0, 0, 0, 0, 0 };
		for (int i = 0; i < chunkSize; i++) {
			for (int k = 0; k < chunkSize; k++) {
				resetSkipped(skipped);
				for (int j = 0; j < chunkSize; j++) {
					incrementSkipped(skipped);
					Block block = lookup(i, j, k);
					if (block.boolProp("visible")) {

						stripedMeshForSide(i, j, k, 0, 1, 0, vxtop, vytop,
								vztop, k, skipped, Side.TOP, block, mesh);

						stripedMeshForSide(i, j, k, 0, -1, 0, vxbot, vybot,
								vzbot, k, skipped, Side.BOTTOM, block,
								mesh);

						stripedMeshForSide(i, j, k, 1, 0, 0, vxe, vye, vze, i,
								skipped, Side.EAST, block, mesh);

						stripedMeshForSide(i, j, k, -1, 0, 0, vxw, vyw, vzw, i,
								skipped, Side.WEST, block, mesh);
					} else {
						resetSkipped(skipped);
					}

				}
			}
		}
		for (int j = 0; j < chunkSize; j++) {

			for (int k = 0; k < chunkSize; k++) {

				resetSkipped(skipped);
				for (int i = 0; i < chunkSize; i++) {
					incrementSkipped(skipped);
					Block block = lookup(i, j, k);
					if (block.boolProp("visible")) {

						stripedMeshForJSide(i, j, k, 0, 0, 1, vxn, vyn, vzn, j,
								skipped, Side.NORTH, block, mesh);

						stripedMeshForJSide(i, j, k, 0, 0, -1, vxs, vys, vzs,
								j, skipped, Side.SOUTH, block, mesh);
					} else {
						resetSkipped(skipped);
					}
				}
			}
		}

		for (int i = 1; i < 6; i++) {
			for (Integer key : mesh[i].keySet()) {
				mesh[0].get(key).add(mesh[i].get(key));
			}
		}

		// System.out.println("Generated mesh has " + vertexCount +
		// " vertices");
		this.mesh = mesh[0];

	}

	private void incrementSkipped(int[] skipped) {
		for (int it = 0; it < 6; it++) {
			skipped[it] = skipped[it] + 1;
		}

	}

	private void resetSkipped(int[] skipped) {
		for (int it = 0; it < 6; it++) {
			skipped[it] = 0;
		}

	}

	private int vertexCount = 0;

	private void stripedMeshForSide(int i, int j, int k, int dx, int dy,
			int dz, int[] vx, int[] vy, int[] vz, int limiter, int[] skipped,
			Side side, Block block, FloatVectorHash[] mesh) {
		int limit = chunkSize - 1;
		if (dx == -1 || dy == -1 || dz == -1) {
			limit = 0;
		}
		if (limiter == limit
				|| !lookup(i + dx, j + dz, k + dy).boolProp("visible")) {

			int texid = block.getTexture(side);
			FloatVector fv = mesh[side.index].get(texid);

			if (j == 0
					|| getBlockFromGrid(i,j - 1,k) != getBlockFromGrid(i,j,k)
					|| (limiter != limit && lookup(i + dx, j - 1 + dz, k + dy)
							.boolProp("visible"))) {

				fv.add(0).add(1);
				fv.add(dx).add(dy).add(dz);
				fv.add(x + i + vx[0]).add(y + k + vy[0]).add(j + z + vz[0]);

				fv.add(0).add(0);
				fv.add(dx).add(dy).add(dz);
				fv.add(x + i + vx[1]).add(y + k + vy[1]).add(j + z + vz[1]);
				vertexCount += 2;

			}
			if (j == chunkSize - 1
					|| getBlockFromGrid(i,j,k) != getBlockFromGrid(i,j+1,k) 
					|| (limiter != limit && lookup(i + dx, j + 1 + dz, k + dy)
							.boolProp("visible"))) {

				fv.add(skipped[side.index]).add(0);
				fv.add(dx).add(dy).add(dz);
				fv.add(x + i + vx[2]).add(y + k + vy[2]).add(j + z + vz[2]);

				fv.add(skipped[side.index]).add(1);
				fv.add(dx).add(dy).add(dz);
				fv.add(x + i + vx[3]).add(y + k + vy[3]).add(j + z + vz[3]);
				skipped[side.index] = 0;

				vertexCount += 2;

			}
		} else {
			skipped[side.index] = 0;
		}
	}

	private short getBlockFromGrid(int i, int j, int k) {
		if(compressed){
		short[] unzip = unzipCol(zippedBlocks[i][j]);
		return unzip[k];
		}else{
			return unzippedBlocks[i][j][k];
		}
	}
	
	private void setBlockInGrid(int i, int j, int k, short id) {
		if(compressed){
		short[] unzip = unzipCol(zippedBlocks[i][j]);
		
		unzip[k] = id;
		
		zippedBlocks[i][j] = zipCol(unzip);
		}else{
			unzippedBlocks[i][j][k] = id;
		}
		
		
		
		
		
	}
	
	private ShortVector zipCol(short[] unzip) {
		ShortVector zip = new ShortVector();
		short counter = 0;
		short lastid = unzip[0];
		for(int k = 0; k < chunkSize; k++){
			counter++;
			if(unzip[k] != lastid || k == chunkSize-1){
				zip.add(counter).add(lastid);
				lastid = unzip[k];
				counter = 0;
			}
		}
		zip.compact();
		return zip;
	}

	short[] unzipCol(ShortVector vec){
		short[] unzip = new short[chunkSize];
		int pointer = 0;
		for(int index = 0; index < vec.numItems(); index+=2){
			short count = vec.get(index);
			short curid = vec.get(index+1);
			for(int c = 0; c < count; c++){
				unzip[pointer] = curid;
				pointer++;
			}
		}
		
		return unzip;
	}

	private void stripedMeshForJSide(int i, int j, int k, int dx, int dy,
			int dz, int[] vx, int[] vy, int[] vz, int limiter, int[] skipped,
			Side side, Block block, FloatVectorHash[] mesh) {
		int limit = chunkSize - 1;
		if (dx == -1 || dy == -1 || dz == -1) {
			limit = 0;
		}
		if (limiter == limit
				|| !lookup(i + dx, j + dz, k + dy).boolProp("visible")) {

			int texid = block.getTexture(side);
			FloatVector fv = mesh[side.index].get(texid);

			if (i == 0
					|| getBlockFromGrid(i-1,j,k)  != getBlockFromGrid(i,j,k) 
					|| (limiter != limit && lookup(i - 1 + dx, j + dz, k + dy)
							.boolProp("visible"))) {

				fv.add(0).add(1);
				fv.add(dx).add(dy).add(dz);
				fv.add(x + i + vx[0]).add(y + k + vy[0]).add(j + z + vz[0]);

				fv.add(0).add(0);
				fv.add(dx).add(dy).add(dz);
				fv.add(x + i + vx[1]).add(y + k + vy[1]).add(j + z + vz[1]);
				vertexCount += 2;

			}
			if (i == chunkSize - 1
					|| getBlockFromGrid(i,j,k)  != getBlockFromGrid(i+1,j,k) 
					|| (limiter != limit && lookup(i + 1 + dx, j + dz, k + dy)
							.boolProp("visible"))) {

				fv.add(skipped[side.index]).add(0);
				fv.add(dx).add(dy).add(dz);
				fv.add(x + i + vx[2]).add(y + k + vy[2]).add(j + z + vz[2]);

				fv.add(skipped[side.index]).add(1);
				fv.add(dx).add(dy).add(dz);
				fv.add(x + i + vx[3]).add(y + k + vy[3]).add(j + z + vz[3]);
				skipped[side.index] = 0;

				vertexCount += 2;

			}
		} else {
			skipped[side.index] = 0;
		}
	}

	@Override
	public FloatVectorHash vertices() {
		if (mesh == null) {
			generateMeshStripes();
		}
		dirty = false;
		return mesh;

	}

	@Override
	public float[] boundingSphere() {
		float[] boundSphere = { (x + chunkSize / 2), (y + chunkSize / 2),
				(z + chunkSize / 2), radius };
		return boundSphere;
	}

	@Override
	public ArrayList<Viewable> getChildren() {
		return null;
	}

	private Block lookup(int i, int j, int k) {
		return BlockLibrary.getBlockLibrary().getBlock(getBlockFromGrid(i,j,k) );
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setListId(int id) {
		listID = id;

	}

	@Override
	public int getListID() {

		return listID;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	public void setBlocks(short[][][] blocks) {
		if(compressed){
		zippedBlocks = new ShortVector[chunkSize][chunkSize];
		for(int i =0; i < chunkSize; i++){
			for(int j = 0; j < chunkSize; j++){
				
				zippedBlocks[i][j] = zipCol(blocks[i][j]);
				
			}
		}
		}else{
			unzippedBlocks = blocks;
		}
	}

	@Override
	public void cleanup() {
		mesh = null;

	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public short[][][] getBlocks() {
		if(compressed){
		short[][][] blocks = new short[chunkSize][chunkSize][chunkSize];
		for(int i =0; i < chunkSize; i++){
			for(int j = 0; j < chunkSize; j++){
				
				blocks[i][j] = unzipCol(zippedBlocks[i][j]);
				
			}
		}
		
		return blocks;
		}else{
			return unzippedBlocks;
		}
			
	}
	
	

	public boolean isCompressed() {
		return compressed;
	}

	
	
	public void compress(){
		if(!compressed){
		short[][][] blocks = getBlocks();
		compressed = true;
		setBlocks(blocks);
		unzippedBlocks = null;
		
		}
	}
	
	public void decompress(){
		if(compressed){
			short[][][] blocks = getBlocks();
			compressed = false;
			setBlocks(blocks);
			zippedBlocks = null;
		}
	}

	public boolean isSavedSinceDirty() {
		return savedSinceDirty;
	}

	public void setSavedSinceDirty(boolean savedSinceDirty) {
		this.savedSinceDirty = savedSinceDirty;
	}


	public Block getBlock(double x, double y, double z) {
		int i = (int) Math.floor(x - this.x);
		int k = (int) Math.floor(y - this.y);
		int j = (int) Math.floor(z - this.z);

		if (i < 0) {
			i += Chunk.chunkSize;
		}
		if (j < 0) {
			j += Chunk.chunkSize;
		}
		if (k < 0) {
			k += Chunk.chunkSize;
		}
		try {
			return lookup(i, j, k);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(this.x + " " + this.y + " " + this.z + " " + x
					+ " " + y + " " + z + " " + i + " " + k + " " + j);
			throw e;
		}

	}

	public void setBlock(double x, double y, double z, short id) {
		int i = (int) Math.floor(x - this.x);
		int k = (int) Math.floor(y - this.y);
		int j = (int) Math.floor(z - this.z);
		if (i < 0) {
			i += Chunk.chunkSize;
		}
		if (j < 0) {
			j += Chunk.chunkSize;
		}
		if (k < 0) {
			k += Chunk.chunkSize;
		}

		setBlockInGrid(i,j,k,id);
		dirty = true;
		savedSinceDirty = false;

	}

	


}
