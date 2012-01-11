package net.Builder.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.Builder.Core.Actor;
import net.Builder.Core.BlockLibrary;
import net.Builder.Core.Database;
import net.Builder.Core.Entity;
import net.Builder.Core.Updateable;
import net.Builder.Core.World;

public class Player implements Entity, Actor, Updateable {

	double x;
	double y;
	double z;

	double xVel;
	double yVel;
	double zVel;

	double heading;
	double pitch;

	PreparedStatement saveStmt = null;
	String name;

	Controller controller;

	public Player(String name) {
		this.name = name;
		controller = new LocalController(this);
		Statement stmt = Database.getDb().getStatement();
		try {
			ResultSet rs = stmt
					.executeQuery("Select * from players where name='" + name
							+ "'");
			if (rs.next()) {
				System.out.println("Loading player from file");
				this.x = rs.getDouble("x");
				this.y = rs.getDouble("y");
				this.z = rs.getDouble("z");
				this.heading = rs.getDouble("heading");
				this.pitch = rs.getDouble("pitch");
			} else {
				stmt.execute("Insert into players(name) values('" + name + "')");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			this.x = 0;
			this.y = 30;
			this.z = 0;
			this.heading = 0;
			this.pitch = 0;
		}

		saveStmt = Database.getDb().getPreparedStatement(
				"update players set x=?,y=?,z=?,heading=?,pitch=? where name='"
						+ name + "'");
	}

	@Override
	public float getX() {
		return (float) x;
	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public float getY() {
		return (float) y;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}

	@Override
	public float getZ() {
		return (float) z;
	}

	@Override
	public void setZ(double z) {
		this.z = z;
	}

	@Override
	public float[] getBoundingBox() {
		float[] bound = { (float) (x - 0.5), (float) (y - 1),
				(float) (z - 0.5), (float) (x + 0.5), (float) (y + 1),
				(float) (z + 0.5) };
		return bound;
	}

	@Override
	public void save() {
		try {
			saveStmt.setDouble(1, x);

			saveStmt.setDouble(2, y);
			saveStmt.setDouble(3, z);
			saveStmt.setDouble(4, heading);
			saveStmt.setDouble(5, pitch);
			saveStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Player save unsuccessful");
		}
	}

	@Override
	public void activate(Entity actor) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getHeading() {
		return (float) heading;
	}

	@Override
	public void setHeading(double heading) {
		this.heading = heading;
	}

	@Override
	public float getPitch() {
		return (float) pitch;
	}

	@Override
	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	@Override
	public void update(double delta) {
		double oldx = x;
		double oldy = y;
		double oldz = z;
		controller.update(delta);
//		x += xVel * delta;
//		y += yVel * delta;
//		z += zVel * delta;
//
//		// System.out.println(delta);
//
//		// gravity
//		// System.out.println("Player at " + x + " " + y + " " + z);
//
//		if (World.getWorld().getBlock(x, y, z).boolProp("solid")) {
//
//			x = oldx;
//			y = oldy;
//			z = oldz;
//
//		}
//		if (World.getWorld().getBlock(x, (y - 1), z).boolProp("solid")) {
//
//			x = oldx;
//			y = oldy;
//			z = oldz;
//
//		}
//
//		if (!World.getWorld().getBlock((int) x, (int) (y - 2), (int) z)
//				.boolProp("solid")) {
//			yVel = Math.max(-0.003, yVel - 0.0003 * delta);
//		} else {
//			yVel = 0;
//		}
//
//		y = Math.max(-80, Math.min(80, y));

	}

	@Override
	public double getxVel() {
		return xVel;
	}

	@Override
	public void setxVel(double xVel) {
		this.xVel = xVel;
	}

	@Override
	public double getyVel() {
		return yVel;
	}

	@Override
	public void setyVel(double yVel) {
		this.yVel = yVel;
	}

	@Override
	public double getzVel() {
		return zVel;
	}

	@Override
	public void setzVel(double zVel) {
		this.zVel = zVel;
	}

}
