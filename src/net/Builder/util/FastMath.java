package net.Builder.util;

public class FastMath {

	// This method is a *lot* faster than using (int)Math.floor(x)
	public static int fastfloor(double x) {
		return x > 0 ? (int) x : (int) x - 1;
	}
	
}
