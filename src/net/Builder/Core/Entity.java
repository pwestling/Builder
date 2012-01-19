package net.Builder.Core;

import net.Builder.util.Point;
import net.Builder.util.Vector3D;

public interface Entity {

	public Point getPos();
	
	public void setPos(Point p);
	
	public Point getVel();
	
	public void setVel(Point v);

	public float getHeading();

	public float getPitch();

	public void setHeading(double heading);

	public void setPitch(double pitch);

	public BoundingBox getBoundingBox();

	public boolean getNoClip();

	public void setNoClip(boolean b);

	public void save();

	public Point getIntendedMove();
	
	public void setIntendedMove(Point im);

}
