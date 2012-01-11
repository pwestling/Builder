package net.Builder.Test;

import java.util.Arrays;

import net.Builder.util.FloatVector;

public class FloatVectorTest implements Testable {

	@Override
	public void test() {
		boolean failed = false;
		FloatVector vec = new FloatVector();
		boolean check = false;
		check = vec.numItems() == 0;
		if (!check)
			System.err.println("Failed numItems = 0 test");
		vec.add(3.0f);
		vec.add(4.0f);
		vec.add(1.0f);
		float[] test = { 3.0f, 4.0f, 1.0f };
		check = Arrays.equals(vec.toArray(), test);
		if (!check)
			System.err.println("Failed add test");
		vec = new FloatVector();
		test = new float[2000];

		for (int i = 0; i < 2000; i++) {
			float r = (float) Math.random();
			test[i] = r;
			vec.add(r);
		}

		check = Arrays.equals(vec.toArray(), test);
		if (!check)
			System.err.println("Failed large add test");

		vec = new FloatVector();
		FloatVector vec2 = new FloatVector();

		vec.add(3.0f).add(2.0f).add(1.0f);
		vec2.add(3.0f).add(2.0f).add(1.0f);
		vec.add(vec2);

		float[] test2 = { 3.0f, 2.0f, 1.0f, 3.0f, 2.0f, 1.0f };

		check = Arrays.equals(vec.toArray(), test2);
		if (!check) {
			System.err.println("failed vec add vec test");
			System.out.println(Arrays.toString(vec.toArray()));
		}

	}
}
