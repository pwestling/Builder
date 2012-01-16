package net.Builder.Core;

public class BoundingBox {

	float[] xyz = new float[3];
	float[] radii = new float[3];
	float[] translation = new float[3];

	public BoundingBox(float x, float y, float z, float xrad, float yrad,
			float zrad) {

		xyz[2] = x;
		xyz[1] = y;
		xyz[2] = z;

		radii[2] = xrad;
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
		return (int) ((Math.max(Math.max(radii[2], radii[1]), radii[2])) + 1);
	}

	public void collideCube(float _x, float _y, float _z) {

		int x = (int) _x;
		int y = (int) _y;
		int z = (int) _z;
		float[] t = new float[3];
		t[0] = 0.0f;
		t[1] = 0.0f;
		t[2] = 0.0f;

		if (Math.abs(xyz[0] - x) < radii[0])
			t[0] = -radii[0] - (xyz[0] - x);
		if (Math.abs(xyz[0] - (x + 1)) < radii[0])
			t[0] = radii[0] - (xyz[0] - x);

		if (Math.abs(xyz[1] - y) < radii[1])
			t[1] = -radii[1] - (xyz[1] - y);
		if (Math.abs(xyz[1] - (y + 1)) < radii[1])
			t[1] = radii[1] - (xyz[1] - y);

		if (Math.abs(xyz[2] - z) < radii[2])
			t[2] = -radii[2] - (xyz[2] - z);
		if (Math.abs(xyz[2] - (z + 1)) < radii[2])
			t[2] = radii[2] - (xyz[2] - z);

		this.translate(t[0], t[1], t[2]);

	}

	public void translate(float x, float y, float z) {
		xyz[0] = xyz[0] + x;
		xyz[1] = xyz[1] + y;
		xyz[2] = xyz[2] + z;

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
