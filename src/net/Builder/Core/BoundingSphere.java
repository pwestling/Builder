package net.Builder.Core;
import net.Builder.util.Point;


public class BoundingSphere {

	
	private Point center;
	private double radius;
	
	
	public BoundingSphere(Point center, double radius) {
		super();
		this.center = center;
		this.radius = radius;
	}
	
	public double x(){
		return center.x();
	}
	
	public double y(){
		return center.y();
	}
	
	public double z(){
		return center.z();
	}
	
	public double radius(){
		return radius;
	}
	
	
	
	
	
	
	
}
