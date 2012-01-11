package net.Builder.Core;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class BlockLibrary {

	private FilenameFilter blockFileFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return !name.endsWith(".blk");
		}
	};

	private Map<Short, Block> blockMap = new HashMap<Short, Block>();
	private Map<String, Short> blockNameMap = new HashMap<String, Short>();

	private static BlockLibrary blocklib = null;

	private PreparedStatement newBlockStmt;

	public static BlockLibrary getBlockLibrary() {
		if (blocklib == null) {
			blocklib = new BlockLibrary();
		}
		return blocklib;
	}

	private short counter = 1;

	private BlockLibrary() {
	}

	@SuppressWarnings("unchecked")
	public void init() {
		newBlockStmt = Database.getDb().getPreparedStatement(
				"insert into blockmap(name,id) values(?,?)");
		Statement stmt = Database.getDb().getStatement();
		try {
			ResultSet rs = stmt.executeQuery("select * from blockmap");
			while (rs.next()) {
				blockNameMap.put(rs.getString("name"), rs.getShort("id"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		getBlockFromDirs("data/Blocks");

		System.out.println("BL init");
	}

	private void getBlockFromDirs(String path) {
		File directory = new File(path);
		System.out.println(directory.getAbsolutePath());
		File[] children = directory.listFiles();
		System.out.println(Arrays.toString(children));
		if (children != null) {
			for (File child : children) {
				System.out.println("Making block from file " + child.getName());
				Block newBlock = Block.makeBlockFromFile(child);
				String name = child.getName().split("\\.")[0];
				newBlock.setName(name);
				if (newBlock != null) {
					if (blockNameMap.get(newBlock.getName()) != null) {
						blockMap.put(blockNameMap.get(newBlock.getName()),
								newBlock);
						newBlock.setID(blockNameMap.get(newBlock.getName()));
						System.out.println(newBlock.getName()
								+ " already in mapping");
					} else {
						blockMap.put(counter, newBlock);
						blockNameMap.put(name, counter);
						newBlock.setID(counter);
						try {
							newBlockStmt.setString(1, newBlock.getName());
							newBlockStmt.setShort(2, newBlock.getId());
							newBlockStmt.execute();
						} catch (SQLException e) {
							e.printStackTrace();
						}

					}
					System.out.println("Loaded block " + newBlock.getName());

					counter++;
				}
			}
		}

	}

	public Block getBlock(short id) {
		return blockMap.get(id);
	}

	public Block getBlock(String name) {
		return blockMap.get(blockNameMap.get(name));
	}

	public Short getBlockID(String name) {
		return blockNameMap.get(name);
	}
}
