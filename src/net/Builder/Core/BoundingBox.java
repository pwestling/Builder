package net.Builder.Core;

public class BoundingBox {

	float[] center = new float[3];
	float[] radii = new float[3];
	float[] translation = new float[3];

	public BoundingBox(float x, float y, float z, float xrad, float yrad,
			float zrad) {

		center[0] = x;
		center[1] = y;
		center[2] = z;

		radii[0] = xrad;
		radii[1] = yrad;
		radii[2] = zrad;

		translation[0] = 0.0f;
		translation[1] = 0.0f;
		translation[2] = 0.0f;

	}

	public BoundingBox(double d, double e, double f, double g, double h,
			double i) {
		this((float) d, (float) e, (float) f, (float) g, (float) h, (float) i);
	}

	public int getRad() {
		return (int) ((Math.max(Math.max(radii[0], radii[1]), radii[2])) + 3);
	}

	public void collideCube(float _x, float _y, float _z) {

		int x = (int) _x;
		int y = (int) _y;
		int z = (int) _z;
		float[] t = new float[3];
		t[0] = 0.0f;
		t[1] = 0.0f;
		t[2] = 0.0f;

		if (center[0] + radii[0] > x && center[0] - radii[0] < x + 1
				&& center[1] + radii[1] > y && center[1] - radii[1] < y + 1
				&& center[2] + radii[2] > z && center[2] - radii[2] < z + 1) {

			if (center[0] + radii[0] - x < (x + 1) - (center[0] - radii[0])) {
				t[0] = -(center[0] + radii[0] - x);
			} else {
				t[0] = (x + 1) - (center[0] - radii[0]);
			}
			if (center[1] + radii[1] - y < (y + 1) - (center[1] - radii[1])) {
				t[1] = -(center[1] + radii[1] - y);
			} else {
				t[1] = (y + 1) - (center[1] - radii[1]);
			}
			if (center[2] + radii[2] - z < (z + 1) - (center[2] - radii[2])) {
				t[2] = -(center[2] + radii[2] - z);
			} else {
				t[2] = (z + 1) - (center[2] - radii[2]);
			}
			System.out.println(t[0] + " " + t[1] + " " + t[2]);

		}

		this.translate(t[0], t[1], t[2]);

	}

	public void translate(float x, float y, float z) {
		center[0] = center[0] + x;
		center[1] = center[1] + y;
		center[2] = center[2] + z;

		translation[0] = translation[0] + x;
		translation[1] = translation[1] + y;
		translation[2] = translation[2] + z;
	}

	public float getXTrans() {
		return translation[0];
	}

	public float getYTrans() {
		return translation[1];
	}

	public float getZTrans() {
		return translation[2];
	}
}
