package net.Builder.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.Builder.Core.Actor;
import net.Builder.Core.BlockLibrary;
import net.Builder.Core.BoundingBox;
import net.Builder.Core.Database;
import net.Builder.Core.Entity;
import net.Builder.Core.EntityManager;
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
		EntityManager.getManager().register(this);

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
			this.y = 50;
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
	public BoundingBox getBoundingBox() {

		return new BoundingBox(x, y, z, 0.5, 1, 0.5);
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

		controller.update(delta);

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
