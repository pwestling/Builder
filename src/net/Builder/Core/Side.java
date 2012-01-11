package net.Builder.Core;

public enum Side {
	TOP(0),
	NORTH(1),
	EAST(2),
	SOUTH(3),
	WEST(4),
	BOTTOM(5);
	
	public int index;
	
	private Side(int index){
		this.index=index;
	}
	

}
