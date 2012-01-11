package net.Builder.Render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;
import net.Builder.Core.Entity;

public abstract class Camera {

	Renderer renderer;
	Entity anchor;

	public abstract void transform();

	public abstract void rotate();

	public abstract float[][] getFrustum();

	public Renderer getRenderer() {
		return renderer;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	public Entity getAnchor() {
		return anchor;
	}

	public void setAnchor(Entity anchor) {
		this.anchor = anchor;
	}

}
