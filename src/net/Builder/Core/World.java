package net.Builder.Core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.Builder.Core.WorldGen.Simplex2DWorldBuilder;
import net.Builder.Core.WorldGen.WorldBuilder;
import net.Builder.User.Player;
import net.Builder.Render.Viewable;
import net.Builder.util.FloatVectorHash;

public class World implements Viewable {

	private Set<CoordKey> chunkKeys = new TreeSet<CoordKey>();
	private Map<CoordKey, Chunk> chunkMap = new TreeMap<CoordKey, Chunk>();
	private int size;
	private float radius;

	private WorldBuilder wb;

	private ArrayList<Entity> players = new ArrayList<Entity>();

	private Thread updateThread;
	private ChunkUpdater updater;

	private static World world = null;
	public static int worldSize;

	public static World getWorld() {
		if (world == null) {
			world = new World(worldSize);
		}
		return world;
	}

	private World(int size) {

		this.size = size;

		long seed = System.nanoTime();
		try {
			ResultSet rs = Database.getDb().executeQuery(
					"select seedValue from seed");
			if (rs.next()) {
				seed = rs.getLong(1);
				System.out.println("Seed " + seed + " loaded from table");
			} else {
				Database.getDb().execute(
						"insert into seed(seedValue) values(" + seed + ")");
				System.out.println("Using new seed " + seed);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		ResultSet rs;
		try {
			rs = Database.getDb().executeQuery("select * from chunks");

			while (rs.next()) {
				String id = rs.getString("id");
				String[] coords = id.split(" ");
				CoordKey key = new CoordKey(Integer.parseInt(coords[0]),
						Integer.parseInt(coords[1]),
						Integer.parseInt(coords[2]));
				chunkKeys.add(key);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		Database.getDb().makeRoutine("saveChunk",
				"update chunks set blocks=? where id=?");
		Database.getDb().makeRoutine("makeChunk",
				"insert into chunks(id) values(?)");
		Database.getDb().makeRoutine("loadChunk",
				"select * from chunks where id=?");

		Database.getDb()
				.makeRoutine("savePlayers",
						"update players set x=?,y=?,z=?,heading=?,pitch=? where name='?'");

		wb = new Simplex2DWorldBuilder(seed);

		radius = (float) Math.hypot(Math.hypot((size / 2 * Chunk.chunkSize),
				(size / 2 * Chunk.chunkSize)), (size / 2 * Chunk.chunkSize));

	}

	public void startUpdater() {
		updater = new ChunkUpdater(this);
		updateThread = new Thread(updater);
		updateThread.setDaemon(true);
		updateThread.start();
		System.out.println("Updater started");
	}

	@Override
	public FloatVectorHash vertices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[] boundingSphere() {
		// this is wrong
		float[] boundSphere = { 0, 0, 0, 100000 };
		return boundSphere;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Collection<Viewable> getChildren() {
		synchronized (chunkMap) {
			return new ArrayList<Viewable>(chunkMap.values());
		}

	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void setListId(int id) {

	}

	@Override
	public int getListID() {
		return 0;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public void cleanup() {

	}

	public void addPlayer(Entity p) {
		players.add(p);

	}

	class ChunkUpdater implements Runnable {

		private World w;
		private boolean stop = false;

		public ChunkUpdater(World w) {
			this.w = w;
		}

		public void stop() {
			stop = true;
		}

		private long timeSinceSave = System.nanoTime();

		@Override
		public void run() {
			while (true && !stop) {
				update();
			}
		}
	}

	private long timeSinceSave = System.nanoTime();

	public void update() {
		loadChunks(1);
		unloadChunks(1);
		 saveLoadedChunks();
		 saveEntities();
	}

	public void stopUpdater() {
		updater.stop();
		try {
			updateThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	int batchCount = 0;

	public void loadChunks(double delta) {
		int loaded = 0;
		long updateTime = System.nanoTime();

		boolean keepGoing = true;
		for (Entity p : players) {
			int cs = Chunk.chunkSize;
			int px = ((int) p.getX()) / cs;
			int py = ((int) p.getY()) / cs;
			int pz = ((int) p.getZ()) / cs;
			for (int i = -size / 2; i < size / 2 && keepGoing; i++) {
				for (int j = -size / 2; j < size / 2 && keepGoing; j++) {
					for (int k = -size / 2; k < size / 2 + 2 && keepGoing; k++) {
						int x = (px + i);
						int y = (py + k);
						int z = (pz + j);
						CoordKey key = new CoordKey(x, y, z);
						if (chunkMap.containsKey(key)) {
							// Loaded, do nothing
						} else {
							makeOrLoadChunk(key);
						}

					}

				}

			}
		}

	}

	public void saveLoadedChunks() {
		for (CoordKey key : chunkMap.keySet()) {
			if (!chunkMap.get(key).isSavedSinceDirty()) {
				byte[] bytes = blocksToByte(chunkMap.get(key).getBlocks());

				Database db = Database.getDb();
				db.setRoutineParam("saveChunk", 1, bytes);
				db.setRoutineParam("saveChunk", 2, key.x + " " + key.y + " "
						+ key.z);
				db.submitRoutine("saveChunk");
				chunkMap.get(key).setSavedSinceDirty(true);
			}
		}
	}

	public void unloadChunks(double delta) {
		ArrayList<CoordKey> keysToRemove = new ArrayList<CoordKey>();
		int writing = 0;
		int batchSize = 0;
		for (CoordKey key : chunkMap.keySet()) {
			boolean visible = false;
			for (int i = 0; i < players.size() && !visible; i++) {
				Entity player = players.get(i);
				int cs = Chunk.chunkSize;
				int px = ((int) player.getX()) / cs;
				int py = ((int) player.getY()) / cs;
				int pz = ((int) player.getZ()) / cs;
				if (Math.abs(px - key.x) <= size / 2
						&& Math.abs(py - key.y) <= size / 2 + 2
						&& Math.abs(pz - key.z) <= size / 2) {
					visible = true;
				}
			}
			if (!visible) {

				short[][][] blocks = chunkMap.get(key).getBlocks();
				byte[] bytes = blocksToByte(blocks);

				writing += bytes.length;
				Database db = Database.getDb();
				db.setRoutineParam("saveChunk", 1, bytes);
				db.setRoutineParam("saveChunk", 2, key.x + " " + key.y + " "
						+ key.z);
				db.submitRoutine("saveChunk");
				keysToRemove.add(key);

			}
		}
		synchronized (chunkMap) {
			for (CoordKey key : keysToRemove) {
				chunkMap.remove(key);
			}
		}

	}

	public void saveEntities() {
		for (Entity p : players) {
			p.save();
		}
	}

	private void loadChunkFromDisk(CoordKey key) {
		Chunk c = new Chunk(key.x * Chunk.chunkSize, key.y * Chunk.chunkSize,
				key.z * Chunk.chunkSize);
		try {
			Database db = Database.getDb();
			db.setRoutineParam("loadChunk", 1, key.x + " " + key.y + " "
					+ key.z);
			ResultSet rs = db.submitQueryRoutine("loadChunk");
			if (rs.next()) {
				byte[] buf = rs.getBytes("blocks");
				if (buf == null) {
					System.out.println("NULL");
					System.exit(0);
				}
				short[][][] blocks = bytesToBlocks(buf);
				c.setBlocks(blocks);
				synchronized (chunkMap) {
					chunkMap.put(key, c);
				}

			} else {
				throw new SQLException("Chunk not present in db");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			c.setBlocks(new short[Chunk.chunkSize][Chunk.chunkSize][Chunk.chunkSize]);
		}

	}

	private byte[] blocksToByte(short[][][] blocks) {
		byte[] bytes = new byte[(int) (Math.pow(Chunk.chunkSize, 3) * 2)];
		int index = 0;
		for (int i = 0; i < Chunk.chunkSize; i++) {
			for (int j = 0; j < Chunk.chunkSize; j++) {
				for (int k = 0; k < Chunk.chunkSize; k++) {
					bytes[index] = (byte) ((blocks[i][j][k] >> 8) & 0xff);
					bytes[index + 1] = (byte) (blocks[i][j][k] & 0xff);
					index += 2;

				}
			}
		}
		// ByteOutputStream bos = new ByteOutputStream();
		// GZIPOutputStream zipper;
		// try {
		// zipper = new GZIPOutputStream(bos);
		// zipper.write(bytes);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// return bos.getBytes();
		return bytes;
	}

	private short[][][] bytesToBlocks(byte[] bytes) {

		// ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		// GZIPInputStream unzipper;
		// byte[] decompressedBytes = null;
		// try {
		// unzipper = new GZIPInputStream(bis);
		//
		// decompressedBytes = new byte[(int) (Math.pow(Chunk.chunkSize, 3) *
		// 2)];
		// int ind = 0;
		// while (unzipper.available() > 0) {
		// bytes[ind] = (byte) unzipper.read();
		// ind++;
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// System.exit(0);
		// }
		// bytes = decompressedBytes;
		short[][][] blocks = new short[Chunk.chunkSize][Chunk.chunkSize][Chunk.chunkSize];
		int index = 0;
		for (int i = 0; i < Chunk.chunkSize; i++) {
			for (int j = 0; j < Chunk.chunkSize; j++) {
				for (int k = 0; k < Chunk.chunkSize; k++) {

					blocks[i][j][k] = (short) ((bytes[index] << 8) + bytes[index + 1]);
					index += 2;

				}
			}
		}
		return blocks;
	}

	public void makeOrLoadChunk(CoordKey key) {
		int cs = Chunk.chunkSize;
		if (chunkKeys.contains(key)) {
			loadChunkFromDisk(key);
		} else {
			Chunk c = wb.makeChunk(key.x * cs, key.y * cs, key.z * cs);
			chunkKeys.add(key);
			synchronized (chunkMap) {
				chunkMap.put(key, c);
			}
			Database db = Database.getDb();
			db.setRoutineParam("makeChunk", 1, key.x + " " + key.y + " "
					+ key.z);
			db.submitRoutine("makeChunk");
		}
	}

	public static CoordKey findChunk(double x, double y, double z) {
		double sx = x / Chunk.chunkSize;
		double sy = y / Chunk.chunkSize;
		double sz = z / Chunk.chunkSize;
		int cx = (int) Math.floor(sx);
		int cy = (int) Math.floor(sy);
		int cz = (int) Math.floor(sz);

		CoordKey key = new CoordKey(cx, cy, cz);

		return key;
	}

	public Block getBlock(double x, double y, double z) {
		CoordKey key = findChunk(x, y, z);
		Chunk c = chunkMap.get(key);
		if (c != null) {
			return c.getBlock(x, y, z);
		} else {
			makeOrLoadChunk(key);
			c = chunkMap.get(key);
			return c.getBlock(x, y, z);
		}
	}

	public void setBlock(double x, double y, double z, short id) {
		CoordKey key = findChunk(x, y, z);
		Chunk c = chunkMap.get(key);
		if (c != null) {
			c.setBlock(x, y, z, id);
		} else {
			makeOrLoadChunk(key);
			c = chunkMap.get(key);
			c.setBlock(x, y, z, id);
		}

	}
}
