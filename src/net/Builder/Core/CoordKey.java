package net.Builder.Core;

public class CoordKey implements Comparable<CoordKey> {

	public final int x;
	public final int y;
	public final int z;

	public CoordKey(double x, double y, double z) {
		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;
	}

	public CoordKey(float x, float y, float z) {
		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;
	}

	public CoordKey(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof CoordKey) {
			CoordKey ck = (CoordKey) that;
			return x - ck.x == 0 && y - ck.y == 0 && z - ck.z == 0;
		}
		return false;

	}

	@Override
	public int hashCode() {
		return (x + " " + y + " " + z).hashCode();
	}

	@Override
	public int compareTo(CoordKey o) {
		if (z - o.z > 0) {
			return 1;
		}
		if (z - o.z < 0) {
			return -1;
		}
		if (y - o.y > 0) {
			return 1;
		}
		if (y - o.y < 0) {
			return -1;
		}
		if (x - o.x > 0) {
			return 1;
		}
		if (x - o.x < 0) {
			return -1;
		}
		if (z - o.z > 0) {
			return 1;
		}
		if (z - o.z < 0) {
			return -1;
		}
		return 0;

	}
}
