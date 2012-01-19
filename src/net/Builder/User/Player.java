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
import net.Builder.util.Point;

public class Player implements Entity, Actor, Updateable {

	Point pos;
	Point vel;
	Point size;
	Point intendedPos;

	double heading;
	double pitch;

	PreparedStatement saveStmt = null;
	String name;

	boolean noClip = false;

	Controller controller;

	public Player(String name) {
		this.name = name;
		this.size = new Point(0.5, 1, 0.5);
		this.vel = new Point();
		this.intendedPos = new Point();
		
		
		EntityManager.getManager().register(this);

		controller = new LocalController(this);
		Statement stmt = Database.getDb().getStatement();
		try {
			ResultSet rs = stmt
					.executeQuery("Select * from players where name='" + name
							+ "'");
			if (rs.next()) {
				System.out.println("Loading player from file");
				pos = new Point(rs.getDouble("x"),rs.getDouble("y"),rs.getDouble("z"));
				this.heading = rs.getDouble("heading");
				this.pitch = rs.getDouble("pitch");
			} else {
				stmt.execute("Insert into players(name) values('" + name + "')");
				pos = new Point(0,50,0);
				this.heading = 0;
				this.pitch = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			pos = new Point(0,50,0);
			this.heading = 0;
			this.pitch = 0;
		}

		saveStmt = Database.getDb().getPreparedStatement(
				"update players set x=?,y=?,z=?,heading=?,pitch=? where name='"
						+ name + "'");
	}


	@Override
	public BoundingBox getBoundingBox() {

		return new BoundingBox(pos, size);
	}

	@Override
	public void save() {
		try {
			saveStmt.setDouble(1, pos.x());

			saveStmt.setDouble(2, pos.y());
			saveStmt.setDouble(3, pos.z());
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
	public boolean getNoClip() {
		return noClip;

	}

	@Override
	public void setNoClip(boolean b) {
		noClip = b;

	}

	@Override
	public Point getPos() {
		return pos;
	}

	@Override
	public void setPos(Point p) {
		pos = p;
		//System.out.println("Pos set to "+pos);
		
	}

	@Override
	public Point getVel() {
		return vel;
	}

	@Override
	public void setVel(Point v) {
		vel = v;
		
	}

	@Override
	public Point getIntendedMove() {
		return intendedPos;
	}


	@Override
	public void setIntendedMove(Point im) {
		intendedPos = im;
		
	}

}
