package net.Builder.util;

public class Vector3D {
	
	
	private double x1;
	private double y1;
	private double z1;
	
	private double x2;
	private double y2;
	private double z2;
	
	
	public Vector3D(Point p1, Point p2){
		this(p1.x(),p1.y(),p1.z(),p2.x(),p2.y(),p2.z());
	}
	
	
	public Vector3D(double x1, double y1, double z1, double x2, double y2,
			double z2) {
		super();
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}
	
	public double dx(){
		return x2-x1;
	}
	
	public double dy(){
		return y2-y1;
	}
	
	public double dz(){
		return z2-z1;
	}
	
	public float dxf(){
		return (float)(x2-x1);
	}
	
	public float dyf(){
		return (float)(y2-y1);
	}
	
	public float dzf(){
		return (float)(z2-z1);
	}
	
	
	
	
	
	
	
	
	

}
