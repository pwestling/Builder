package net.Builder.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class FloatVector {

	float[] arr;
	int size;
	int pointer;

	public FloatVector() {
		this(30000);
	}

	public FloatVector(int i) {
		size = i;
		arr = new float[size];
		pointer = 0;
	}

	public FloatVector set(int index, float f) {
		arr[index] = f;
		return this;
	}

	public FloatVector add(float f) {
		if (pointer > size - 1) {
			int tempsize = size * 2;
			float[] temp = new float[tempsize];
			System.arraycopy(arr, 0, temp, 0, size);
			arr = temp;
			size = tempsize;
		}
		arr[pointer] = f;
		pointer++;
		return this;
	}

	public FloatVector add(float[] f) {
		if (pointer + f.length > size - 1) {
			int tempsize = (size + f.length) * 2;
			float[] temp = new float[tempsize];
			System.arraycopy(arr, 0, temp, 0, size);
			arr = temp;
			size = tempsize;
		}
		System.arraycopy(f, 0, arr, pointer, f.length);
		pointer += f.length;
		return this;
	}

	public FloatVector add(FloatVector f) {
		this.add(f.toArray());
		return this;
	}

	public float[] toArray() {
		float[] temp = new float[pointer];
		System.arraycopy(arr, 0, temp, 0, pointer);
		return temp;
	}

	public FloatBuffer toBuffer() {
		FloatBuffer buf = BufferUtils.createFloatBuffer(pointer);
		buf.put(arr, 0, pointer);
		buf.flip();
		return buf;
	}

	public void clear() {
		pointer = 0;
	}

	public int numItems() {
		return pointer;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[" + arr[0]);
		for (int i = 1; i < pointer; i++) {
			sb.append("," + arr[i]);
		}
		sb.append("]");
		return sb.toString();
	}
}
