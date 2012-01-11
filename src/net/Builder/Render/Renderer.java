package net.Builder.Render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.awt.Font;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.Builder.Core.BlockLibrary;
import net.Builder.Core.Chunk;
import net.Builder.Core.World;
import net.Builder.User.Player;
import net.Builder.Test.TestChunk;
import net.Builder.util.FloatVector;
import net.Builder.util.FloatVectorHash;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

public class Renderer {

	private Camera camera;
	private TextureManager texManager;
	private Viewable root;

	public static int screenWidth = 400;
	public static int screenHeight = 300;

	private float fovy = 69;
	private float clipNear = 0.1f;
	private float clipFar = 5000;

	private float[][] frustum;

	private TrueTypeFont font;

	private static Renderer renderer;

	public void setCamera(Camera cam) {
		this.camera = cam;
	}

	public void init() {
		texManager = TextureManager.getManager();

		try {

			Renderer.setDisplayMode(screenWidth, screenHeight, false);
			Display.create();
		} catch (LWJGLException e) {
			System.out.println("Display Creation Error");
			e.printStackTrace();
		}
		Font awtFont = new Font("Times New Roman", Font.PLAIN, 18);
		font = new TrueTypeFont(awtFont, true);
		glClearColor(0, 0, 0, 0);
		glViewport(0, 0, screenWidth, screenHeight);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(fovy, (float) screenWidth / (float) screenHeight,
				clipNear, clipFar);

		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		FloatVector mat_specular = new FloatVector(4);
		mat_specular.add(0.1f).add(0.1f).add(0.1f).add(1.0f);
		FloatVector mat_shininess = new FloatVector(4);
		mat_shininess.add(10.0f).add(0.0f).add(0.0f).add(0.0f);
		light_position = new FloatVector(4);
		light_position.add(-10.0f).add(50.0f).add(-10.0f).add(1.0f);
		FloatVector light_ambient = new FloatVector(4);
		light_ambient.add(1.0f).add(1.0f).add(1.0f).add(0.0f);
		glShadeModel(GL_SMOOTH);

		glMaterial(GL_FRONT, GL_SPECULAR, mat_specular.toBuffer());
		glMaterial(GL_FRONT, GL_SHININESS, mat_shininess.toBuffer());

		glLight(GL_LIGHT0, GL_AMBIENT, light_ambient.toBuffer());

		FloatVector localViewer = new FloatVector();
		localViewer.add(1.0f).add(0.0f).add(0.0f).add(0.0f);

		glEnable(GL_LIGHTING);
		glLightModel(GL_LIGHT_MODEL_LOCAL_VIEWER, localViewer.toBuffer());
		glEnable(GL_LIGHT0);

		// glEnable(GL_FOG);
		glFogi(GL_FOG_MODE, GL_EXP2);
		FloatVector fog_color = new FloatVector(4);
		fog_color.add(0.62f).add(0.82f).add(0.88f).add(1.0f);
		// FloatVector fog_color = new FloatVector(4);
		// fog_color.add(1.00f).add(1.00f).add(1.00f).add(1.0f);

		glFog(GL_FOG_COLOR, fog_color.toBuffer());
		glFogf(GL_FOG_DENSITY, 0.008f);

	}

	FloatVector light_position;

	public void render() {

		updateFPS();
		glLoadIdentity();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		renderSkybox();
		camera.transform();

		frustum = camera.getFrustum();
		glLight(GL_LIGHT0, GL_POSITION, light_position.toBuffer());
		renderHelper(root);
		// System.out.println("Lists: " + numLists);
		// System.out.println("Lists size: " + listSizeInBytes);
		glFlush();
		Display.update();

	}

	int skyboxList = 0;

	private void renderHelper(Viewable node) {
		if (inFrustum(node.boundingSphere())) {
			if (node.isDirty()) {
				FloatVectorHash mesh = node.vertices();
				initList(mesh, node);
			}

			if (node.isVisible()) {
				glCallList(node.getListID());
			}

			Collection<Viewable> children = node.getChildren();
			if (children != null) {

				for (Viewable child : children) {
					renderHelper(child);
				}
			}

		} else {
			// System.out.println("Culled");
		}
	}

	int numLists = 0;
	int listSizeInBytes = 0;

	public void initList(FloatVectorHash mesh, Viewable node) {
		if (mesh != null) {
			if (node.getListID() != 0) {
				glDeleteLists(node.getListID(), 1);
				numLists--;
			}
			int listID = glGenLists(1);
			numLists++;
			glNewList(listID, GL_COMPILE);
			glEnableClientState(GL_VERTEX_ARRAY);
			for (Integer key : mesh.keySet()) {
				FloatBuffer buf = mesh.get(key).toBuffer();
				texManager.bindTex(key);
				glInterleavedArrays(GL_T2F_N3F_V3F, 0, buf);
				glDrawArrays(GL_QUADS, 0, buf.limit() / 8);
				listSizeInBytes += buf.limit() / 8 * 4;
			}
			glDisableClientState(GL_VERTEX_ARRAY);
			glEndList();
			node.setListId(listID);
			node.cleanup();
		}
	}

	private void renderSkybox() {
		if (skyboxList == 0) {

			skyboxList = glGenLists(1);
			int texid = 0;
			try {
				texid = TextureManager.getManager().loadTex("sky",
						"data/texture/sky.png");
			} catch (IOException e) {
				e.printStackTrace();
			}
			float size = 2.0f;
			FloatVector boxVertices = new FloatVector(24);
			float tlx = size;
			float tly = size;
			float tlz = size;
			float brx = -size;
			float bry = -size;
			float brz = -size;

			boxVertices.add(0.0f).add(0.0f);
			boxVertices.add(tlx).add(tly).add(tlz);
			boxVertices.add(0.0f).add(1.0f);
			boxVertices.add(brx).add(tly).add(tlz);
			boxVertices.add(1.0f).add(1.0f);
			boxVertices.add(brx).add(bry).add(tlz);
			boxVertices.add(1.0f).add(0.0f);
			boxVertices.add(tlx).add(bry).add(tlz);

			boxVertices.add(0.0f).add(0.0f);
			boxVertices.add(brx).add(tly).add(brz);
			boxVertices.add(0.0f).add(1.0f);
			boxVertices.add(brx).add(bry).add(brz);
			boxVertices.add(1.0f).add(1.0f);
			boxVertices.add(brx).add(bry).add(tlz);
			boxVertices.add(1.0f).add(0.0f);
			boxVertices.add(brx).add(tly).add(tlz);

			boxVertices.add(0.0f).add(0.0f);
			boxVertices.add(tlx).add(bry).add(tlz);
			boxVertices.add(0.0f).add(1.0f);
			boxVertices.add(tlx).add(bry).add(brz);
			boxVertices.add(1.0f).add(1.0f);
			boxVertices.add(tlx).add(tly).add(brz);
			boxVertices.add(1.0f).add(0.0f);
			boxVertices.add(tlx).add(tly).add(tlz);

			boxVertices.add(0.0f).add(0.0f);
			boxVertices.add(tlx).add(bry).add(brz);
			boxVertices.add(0.0f).add(1.0f);
			boxVertices.add(brx).add(bry).add(brz);
			boxVertices.add(1.0f).add(1.0f);
			boxVertices.add(brx).add(tly).add(brz);
			boxVertices.add(1.0f).add(0.0f);
			boxVertices.add(tlx).add(tly).add(brz);

			boxVertices.add(0.0f).add(0.0f);
			boxVertices.add(brx).add(tly).add(tlz);
			boxVertices.add(0.0f).add(1.0f);
			boxVertices.add(brx).add(tly).add(brz);
			boxVertices.add(1.0f).add(1.0f);
			boxVertices.add(tlx).add(tly).add(brz);
			boxVertices.add(1.0f).add(0.0f);
			boxVertices.add(tlx).add(tly).add(tlz);

			boxVertices.add(0.0f).add(0.0f);
			boxVertices.add(tlx).add(bry).add(tlz);
			boxVertices.add(0.0f).add(1.0f);
			boxVertices.add(tlx).add(bry).add(brz);
			boxVertices.add(1.0f).add(1.0f);
			boxVertices.add(brx).add(bry).add(brz);
			boxVertices.add(1.0f).add(0.0f);
			boxVertices.add(brx).add(bry).add(tlz);

			glNewList(skyboxList, GL_COMPILE);
			glDisable(GL_LIGHTING);
			glDisable(GL_CULL_FACE);
			glDepthMask(false);
			glEnableClientState(GL_VERTEX_ARRAY);

			TextureManager.getManager().bindTex(texid);
			glDisable(GL_TEXTURE_2D);
			glColor3f(0.62f, 0.82f, 0.88f);
			glInterleavedArrays(GL_T2F_V3F, 0, boxVertices.toBuffer());
			glDrawArrays(GL_QUADS, 0, boxVertices.toBuffer().limit() / 5);
			glDisableClientState(GL_VERTEX_ARRAY);
			glDepthMask(true);
			glEnable(GL_LIGHTING);
			glEnable(GL_CULL_FACE);
			glEnable(GL_TEXTURE_2D);
			glEndList();

		}

		glCallList(skyboxList);

	}

	private boolean inFrustum(float[] boundingSphere) {
		for (int p = 0; p < 6; p++) {
			double dist = frustum[p][0] * (boundingSphere[0]) + frustum[p][1]
					* (boundingSphere[1]) + frustum[p][2] * (boundingSphere[2])
					+ frustum[p][3];
			if (dist <= -boundingSphere[3]) {
				// System.out.println("Not In Frustum");
				return false;
			}

		}
		// System.out.println("In Frustum");
		return true;
	}

	private int fps = 0;
	private int oldfps = 0;
	private long lastFPS = System.currentTimeMillis();

	private void updateFPS() {
		if (System.currentTimeMillis() - lastFPS > 1000) {
			oldfps = fps;
			Display.setTitle("FPS: " + oldfps + " Y: "
					+ camera.getAnchor().getY());
			fps = 0; // reset the FPS counter
			lastFPS += 1000; // add one second
		}
		// GL11.glMatrixMode(GL11.GL_PROJECTION);
		// glPushMatrix();
		// GL11.glLoadIdentity();
		// GL11.glOrtho(0, screenWidth, screenHeight, 0, 1, -1);
		//
		// Color.white.bind();
		// font.drawString(10, 10, "FPS: " + oldfps);
		// glPopMatrix();
		// GL11.glMatrixMode(GL11.GL_MODELVIEW);
		fps++;
	}

	/**
	 * Set the display mode to be used
	 * 
	 * @param width
	 *            The width of the display required
	 * @param height
	 *            The height of the display required
	 * @param fullscreen
	 *            True if we want fullscreen mode
	 */
	public static void setDisplayMode(int width, int height, boolean fullscreen) {

		// return if requested DisplayMode is already set
		if ((Display.getDisplayMode().getWidth() == width)
				&& (Display.getDisplayMode().getHeight() == height)
				&& (Display.isFullscreen() == fullscreen)) {
			return;
		}

		try {
			DisplayMode targetDisplayMode = null;

			if (fullscreen) {
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				for (int i = 0; i < modes.length; i++) {
					DisplayMode current = modes[i];

					if ((current.getWidth() == width)
							&& (current.getHeight() == height)) {
						if ((targetDisplayMode == null)
								|| (current.getFrequency() >= freq)) {
							if ((targetDisplayMode == null)
									|| (current.getBitsPerPixel() > targetDisplayMode
											.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequence against
						// the
						// original display mode then it's probably best to go
						// for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display
								.getDesktopDisplayMode().getBitsPerPixel())
								&& (current.getFrequency() == Display
										.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else {
				targetDisplayMode = new DisplayMode(width, height);
			}

			if (targetDisplayMode == null) {
				System.out.println("Failed to find value mode: " + width + "x"
						+ height + " fs=" + fullscreen);
				return;
			}

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode " + width + "x" + height
					+ " fullscreen=" + fullscreen + e);
		}
	}

	public static Renderer getRenderer() {
		if (renderer == null) {
			renderer = new Renderer();
		}
		return renderer;
	}

	public void setRoot(Viewable root) {
		this.root = root;
	}

}
