package net.Builder.Core;

public interface Entity {

	public float getX();

	public float getY();

	public float getZ();

	public void setX(double x);

	public void setY(double y);

	public void setZ(double z);

	public float getHeading();

	public float getPitch();

	public void setHeading(double heading);

	public void setPitch(double pitch);

	public BoundingBox getBoundingBox();

	public void save();

	public double getxVel();

	public void setxVel(double xVel);

	public double getyVel();

	public void setyVel(double yVel);

	public double getzVel();

	public void setzVel(double zVel);

}
