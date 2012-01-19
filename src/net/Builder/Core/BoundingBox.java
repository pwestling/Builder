package net.Builder.Core;

import net.Builder.util.Point;

public class BoundingBox {

	Point center = new Point();
	Point radii = new Point();
	Point translation = new Point();

	public BoundingBox(double x, double y, double z, double xrad, double yrad,
			double zrad) {

		center.set(x,y,z);

		radii.set(xrad, yrad, zrad);


	}
	
	public BoundingBox(Point center, Point radii){
		this.center = center;
		this.radii = radii;
	}

	

	public int getRad() {
		return (int)Math.ceil( ((Math.max(Math.max(radii.x(), radii.y()), radii.z()))));
	}

	public boolean collideCube(Point p) {

	

		if (center.x() + radii.x() > p.xi() && center.x() - radii.x() < p.xi() + 1
				&& center.y() + radii.y() > p.yi() && center.y() - radii.y() < p.yi() + 1
				&& center.z() + radii.z() > p.zi() && center.z() - radii.z() < p.zi() + 1) {

			
			return true;
		}
		
		return false;

	}

	public void translate(float x, float y, float z) {
		center.translate(x, y, z);

		translation.translate(x,y,z);
	}

	
}
