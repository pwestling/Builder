package net.Builder.util;

import net.Builder.Core.Chunk;

public class Point implements Comparable<Point> {
	
	private double x;
	private double y;
	private double z;
	
	
	public Point(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point(){
		this(0,0,0);
	}
	
	public void translate(double x, double y, double z){
		this.x += x;
		this.y += y;
		this.z += z;
	}
	
	public Point translated(double x, double y, double z){
		return new Point(this.x+x, this.y+y, this.z+z);
	}
	
	public void translate(double t){
		this.x += t;
		this.y += t;
		this.z += t;
	}
	
	public Point translated(double t){
		return new Point(this.x+t, this.y+t, this.z+t);
	}
	
	public void scale(double sx, double sy, double sz){
		this.x *= sx;
		this.y *= sy;
		this.z*= sz;
	}
	
	public Point scaled(double sx, double sy, double sz){
		return new Point(this.x*sx, this.y*sy, this.z*sz);
	}
	
	public void scale(double s){
		this.x *= s;
		this.y *= s;
		this.z*= s;
	}
	
	public Point scaled(double s){
		return new Point(this.x*s, this.y*s, this.z*s);
	}
	
	public Point plus(Point p){
		return new Point(this.x+p.x,this.y+p.y, this.z+p.z );
	}
	
	public Point minus(Point p){
		return new Point(this.x-p.x,this.y-p.y, this.z-p.z );
	}
	
	public Point toChunkCoords(){
		
		double sx = x / Chunk.chunkSize;
		double sy = y / Chunk.chunkSize;
		double sz = z / Chunk.chunkSize;

		return new Point(Math.floor(sx), Math.floor(sy),  Math.floor(sz));
		
		
	}
	
	public double x(){
		return x;
	}
	
	public double y(){
		return y;
	}
	
	public double z(){
		return z;
	}
	
	public float xf(){
		return (float) x;
	}
	
	public float yf(){
		return (float) y;
	}
	
	public float zf(){
		return (float) z;
	}
	
	public int xi(){
		return (int) x;
	}
	
	public int yi(){
		return (int) y;
	}
	
	public int zi(){
		return (int) z;
	}
	
	public void setX(double x){
		this.x = x;
	}
	


	public void setY(double y) {
		this.y = y;
	}


	public void setZ(double z) {
		this.z = z;
	}

	public String toString(){
		return this.x()+" "+this.y()+" "+this.z();
	}

	public void set(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toDatabaseKey(){
		return this.xi()+" "+this.yi()+" "+this.zi();
	}
	
	public Point clone(){
		return new Point(x,y,z);
	}
	
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof Point) {
			Point p = (Point) that;
			return x - p.x() == 0 && y - p.y() == 0 && z - p.z() == 0;
		}
		return false;

	}

	@Override
	public int hashCode() {
		return (x + " " + y + " " + z).hashCode();
	}

	@Override
	public int compareTo(Point o) {
		if (z - o.z() > 0) {
			return 1;
		}
		if (z - o.z() < 0) {
			return -1;
		}
		if (y - o.y() > 0) {
			return 1;
		}
		if (y - o.y() < 0) {
			return -1;
		}
		if (x - o.x() > 0) {
			return 1;
		}
		if (x - o.x() < 0) {
			return -1;
		}
		if (z - o.z() > 0) {
			return 1;
		}
		if (z - o.z() < 0) {
			return -1;
		}
		return 0;
	
	
	
	}
	
	

}
