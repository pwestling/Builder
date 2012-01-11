package net.Builder.Core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import net.Builder.Render.TextureManager;

public class Block implements Comparable<Block> {

	private int[] textureIDs = new int[6];
	private short id;
	private String name;
	private Map<String, String> stringProps = new TreeMap<String, String>();
	private Map<String, Boolean> boolProps = new TreeMap<String, Boolean>();

	public static Block makeBlockFromFile(File file) {
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(file));
			String[] texPath = null;
			TextureManager manager = TextureManager.getManager();
			Block block = new Block();

			for (int i = 0; i < 6; i++) {

				texPath = fileReader.readLine().split("@");
				if (texPath[0].equals("null")) {
					continue;
				}
				int texid = manager.loadTex(texPath[0], texPath[1]);
				block.setTexture(i, texid);

			}
			String line;
			String propertyType;
			String propertyName;
			String propertyValue;
			line = fileReader.readLine();
			while (line != null) {
				if (line.equals("") || line.trim().startsWith("//")) {
					line = fileReader.readLine();
					continue;
				}
				String[] parts = line.trim().split(":");
				propertyType = parts[0].trim();
				propertyName = parts[1].trim();
				propertyValue = parts[2].trim();
				if (propertyType.equals("bool")) {
					block.boolProps.put(propertyName,
							Boolean.parseBoolean(propertyValue));
				}
				if (propertyType.equals("string")) {
					block.stringProps.put(propertyName, propertyValue);
				}
				line = fileReader.readLine();
			}
			return block;

		} catch (IOException e) {
			System.out.println("Block specification missing or incorrect");
			e.printStackTrace();
			return null;
		}

	}

	private Block() {

	}

	public void setTexture(Side side, int textureID) {
		textureIDs[side.index] = textureID;
	}

	public void setTexture(int side, int textureID) {
		textureIDs[side] = textureID;
	}

	

	public Integer getTexture(Side side) {
		return textureIDs[side.index];
	}

	public void setID(short id) {
		this.id = id;

	}

	public boolean boolProp(String propName) {
		Boolean b = boolProps.get(propName);
		if (b != null)
			return b;
		return false;
	}

	public String stringProp(String propName) {
		String s = stringProps.get(propName);
		if (s != null)
			return s;
		return "";
	}

	public short getId() {
		return id;
	}

	@Override
	public int compareTo(Block b) {
		return this.id - b.getId();

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
