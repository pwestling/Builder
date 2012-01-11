package net.Builder.Learning;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class PerformanceTest {

	public static final int SCREEN_WIDTH = 640;
	public static final int SCREEN_HEIGHT = 480;

	public static class QuadData {
		float x1, y1, x2, y2; // lower left, upper right
	}

	public interface RenderTest {
		void init(int numQuads);

		void render();
	}

	public static class ImmediateTest implements RenderTest {
		private QuadData[] data;

		public ImmediateTest() {
			init(10000);
		}

		@Override
		public void init(int numQuads) {
			data = generateQuads(numQuads);
		}

		@Override
		public void render() {
			GL11.glBegin(GL11.GL_QUADS);
			for (int i = 0; i < data.length; i++) {
				GL11.glVertex2f(data[i].x1, data[i].y1);
				GL11.glVertex2f(data[i].x1, data[i].y2);
				GL11.glVertex2f(data[i].x2, data[i].y2);
				GL11.glVertex2f(data[i].x2, data[i].y1);
			}
			GL11.glEnd();
		}

		@Override
		public String toString() {
			return "immediate mode";
		}

	}

	public static class VertexArrayTest implements RenderTest {
		private QuadData[] data;
		private FloatBuffer vertices;

		public VertexArrayTest() {
			init(10000);
		}

		@Override
		public void init(int numQuads) {
			data = generateQuads(numQuads);
			vertices = BufferUtils.createFloatBuffer(2 * 4 * numQuads);
		}

		@Override
		public void render() {
			vertices.clear();
			for (int i = 0; i < data.length; i++) {
				vertices.put(data[i].x1).put(data[i].y1);
				vertices.put(data[i].x1).put(data[i].y2);
				vertices.put(data[i].x2).put(data[i].y2);
				vertices.put(data[i].x2).put(data[i].y1);
			}
			vertices.flip();

			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			GL11.glVertexPointer(2, 0, vertices);
			GL11.glDrawArrays(GL11.GL_QUADS, 0, vertices.limit() / 2);
			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		}

		@Override
		public String toString() {
			return "vertex arrays";
		}
	}

	public static class VBOTest implements RenderTest {
		private QuadData[] data;
		private int id = -1;
		private FloatBuffer vertices;
		private boolean dynamic = false;

		public VBOTest(boolean dynamic) {
			this.dynamic = dynamic;
			init(10000);
		}

		@Override
		public void init(int numQuads) {
			data = generateQuads(numQuads);
			if (id != -1)
				ARBVertexBufferObject.glDeleteBuffersARB(id);
			id = ARBVertexBufferObject.glGenBuffersARB();
			vertices = BufferUtils.createFloatBuffer(2 * 4 * numQuads);
			updateVBO();
		}

		private void updateVBO() {
			vertices.clear();
			for (int i = 0; i < data.length; i++) {
				vertices.put(data[i].x1).put(data[i].y1);
				vertices.put(data[i].x1).put(data[i].y2);
				vertices.put(data[i].x2).put(data[i].y2);
				vertices.put(data[i].x2).put(data[i].y1);
			}
			vertices.flip();
			ARBVertexBufferObject.glBindBufferARB(
					ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, id);
			int mode = ARBVertexBufferObject.GL_DYNAMIC_DRAW_ARB;
			if (!dynamic)
				mode = ARBVertexBufferObject.GL_STATIC_DRAW_ARB;
			ARBVertexBufferObject.glBufferDataARB(
					ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vertices, mode);
			ARBVertexBufferObject.glBindBufferARB(
					ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, 0);
		}

		@Override
		public void render() {
			if (dynamic) {
				updateVBO();
			}
			ARBVertexBufferObject.glBindBufferARB(
					ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, id);
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
			GL11.glDrawArrays(GL11.GL_QUADS, 0, vertices.limit() / 2);
			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
			ARBVertexBufferObject.glBindBufferARB(
					ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, 0);
		}

		@Override
		public String toString() {
			return dynamic ? "dynamic VBO" : "static VBO";
		}
	}

	public static QuadData[] generateQuads(int num) {
		QuadData[] data = new QuadData[num];
		for (int i = 0; i < data.length; i++) {
			data[i] = new QuadData();
			data[i].x1 = (float) Math.random() * SCREEN_WIDTH;
			data[i].y1 = (float) Math.random() * SCREEN_WIDTH;
			data[i].x2 = data[i].x1 + 1; // keep quads small
			data[i].y2 = data[i].y1 + 1; // to keep fillrate low
		}
		return data;
	}

	private static RenderTest currTest;
	private static int iterations;
	private static int num;
	private static boolean renderQuad;

	public static void main(String[] args) throws Exception {
		setDisplayMode();
		Display.setTitle("PerformanceTest");
		Display.setFullscreen(false);
		Display.setVSyncEnabled(false);
		Display.create(new PixelFormat(32, 0, 24, 8, 0));
		Mouse.setGrabbed(false);

		String extensions = GL11.glGetString(GL11.GL_EXTENSIONS);
		if (!extensions.contains("GL_ARB_vertex_buffer_object")) {
			System.out.println("GL_ARB_vertex_buffer_object not available");
		}

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glOrtho(0, SCREEN_WIDTH, 0, SCREEN_HEIGHT, -1, 1);

		RenderTest[] tests = new RenderTest[] { new ImmediateTest(),
				new VertexArrayTest(), new VBOTest(true), new VBOTest(false), };
		int testIndex = 0;
		renderQuad = false;
		currTest = tests[testIndex];

		prev = getTime();
		iterations = 1;
		num = 100000;

		for (RenderTest test : tests) {
			test.init(num);
		}
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glColor4ub((byte) 55, (byte) 55, (byte) 55, (byte) 255);
		while (true) {
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			if (renderQuad) {
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(10, 10);
				GL11.glVertex2f(10, 20);
				GL11.glVertex2f(20, 20);
				GL11.glVertex2f(20, 10);
				GL11.glEnd();
			}

			for (int i = 0; i < iterations; i++) {
				currTest.render();
			}

			GL11.glPopMatrix();

			if (Display.isCloseRequested()
					|| Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				break;
			}
			Display.update();

			updateFPS();

			// process input to switch tests etc
			boolean reinit = false;
			Keyboard.poll();
			while (Keyboard.next()) {
				if (Keyboard.getEventKeyState() == true) {
					switch (Keyboard.getEventKey()) {
					case Keyboard.KEY_SPACE:
						testIndex = (testIndex + 1) % tests.length;
						currTest = tests[testIndex];
						resetFPS();
						System.out.println("Switching to " + currTest);
						break;
					case Keyboard.KEY_Q:
						renderQuad = !renderQuad;
						resetFPS();
						break;
					case Keyboard.KEY_1:
						iterations = 1;
						num = 100000;
						reinit = true;
						break;
					case Keyboard.KEY_2:
						iterations = 10;
						num = 10000;
						reinit = true;
						break;
					case Keyboard.KEY_3:
						iterations = 100;
						num = 1000;
						reinit = true;
						break;
					case Keyboard.KEY_4:
						iterations = 1000;
						num = 100;
						reinit = true;
						break;
					case Keyboard.KEY_5:
						iterations = 10000;
						num = 10;
						reinit = true;
						break;
					}
				}
			}
			if (reinit) {
				for (RenderTest test : tests) {
					test.init(num);
				}
				resetFPS();
			}
		}
		GL11.glPopMatrix();
	}

	private static long prev;
	private static long elapsed = 0;
	private static int frames = 0;

	private static void updateFPS() {
		// update FPS, print out every second
		frames++;
		long now = getTime();
		elapsed += (now - prev);
		prev = now;
		if (elapsed > 1000) {
			String extra = renderQuad ? "with quad" : "without quad";
			System.out
					.println("FPS: " + frames + " (" + currTest + ", " + num
							+ " quads x" + iterations + " iteration(s), "
							+ extra + ")");
			elapsed -= 1000;
			frames = 0;
		}
	}

	private static void resetFPS() {
		elapsed = 0;
		frames = 0;
	}

	private static long getTime() {
		return (long) ((double) Sys.getTime()
				/ (double) Sys.getTimerResolution() * 1000.0);
	}

	private static void setDisplayMode() throws Exception {
		DisplayMode[] dm = org.lwjgl.util.Display.getAvailableDisplayModes(
				SCREEN_WIDTH, SCREEN_HEIGHT, -1, -1, -1, -1, 60, 60);
		org.lwjgl.util.Display.setDisplayMode(dm, new String[] {
				"width=" + SCREEN_WIDTH,
				"height=" + SCREEN_HEIGHT,
				"freq=" + 60,
				"bpp="
						+ org.lwjgl.opengl.Display.getDisplayMode()
								.getBitsPerPixel() });
	}
}
