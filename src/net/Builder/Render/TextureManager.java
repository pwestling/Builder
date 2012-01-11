package net.Builder.Render;

import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.util.glu.GLU.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class TextureManager {

	private HashMap<String, Integer> texNameMap = new HashMap<String, Integer>();
	private ArrayList<Texture> textures = new ArrayList<Texture>();
	private int pointer = 0;
	private static TextureManager manager = null;

	private TextureManager() {

	}

	public static TextureManager getManager() {
		if (manager == null) {
			manager = new TextureManager();
		}
		return manager;
	}

	public void bindTex(String texName) {
		if (texNameMap.get(texName) != null) {
			textures.get(texNameMap.get(texName)).bind();
		}
	}

	public void bindTex(int index) {
		textures.get(index).bind();
	}

	public int loadTex(String name, String path) throws IOException {
		if (texNameMap.get(name) != null)
			return texNameMap.get(name);

		Texture tex = TextureLoader.getTexture("PNG",
				ResourceLoader.getResourceAsStream(path));
		texNameMap.put(name, pointer);
		textures.add(pointer, tex);
		pointer++;
		System.out.println("Loaded texture " + name);
		return pointer - 1;
	}

	public int numTextures() {
		return pointer;
	}
}
