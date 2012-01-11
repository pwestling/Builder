package net.Builder.util;



public class ShortVector {
	
	short[] arr;
	int size;
	int pointer;

	public ShortVector() {
		this(10);
	}

	public ShortVector(int i) {
		size = i;
		arr = new short[size];
		pointer = 0;
	}

	public ShortVector set(int index, short f) {
		arr[index] = f;
		return this;
	}

	public ShortVector add(short f) {
		if (pointer > size - 1) {
			int tempsize = size * 2;
			short[] temp = new short[tempsize];
			System.arraycopy(arr, 0, temp, 0, size);
			arr = temp;
			size = tempsize;
		}
		arr[pointer] = f;
		pointer++;
		return this;
	}

	public ShortVector add(short[] f) {
		if (pointer + f.length > size - 1) {
			int tempsize = (size + f.length) * 2;
			short[] temp = new short[tempsize];
			System.arraycopy(arr, 0, temp, 0, size);
			arr = temp;
			size = tempsize;
		}
		System.arraycopy(f, 0, arr, pointer, f.length);
		pointer += f.length;
		return this;
	}

	public ShortVector add(ShortVector f) {
		this.add(f.toArray());
		return this;
	}
	
	public short get(int index){
		return arr[index];
	}
	
	public void compact(){
		arr = toArray();
		size = pointer;
	}

	public short[] toArray() {
		short[] temp = new short[pointer];
		System.arraycopy(arr, 0, temp, 0, pointer);
		return temp;
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
