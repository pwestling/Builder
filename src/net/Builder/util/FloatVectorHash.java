package net.Builder.util;

import java.util.HashMap;

public class FloatVectorHash extends HashMap<Integer, FloatVector> {

	public FloatVector get(Integer key) {
		FloatVector fv = super.get(key);
		if (fv != null) {
			return fv;
		}

		fv = new FloatVector();
		super.put(key, fv);
		return fv;
	}

}
