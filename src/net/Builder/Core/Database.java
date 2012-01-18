package net.Builder.Core;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;

import javax.print.attribute.ResolutionSyntax;

public class Database {

	private static Database db;

	private Connection conn;

	private static String url;

	private Map<String, PreparedStatement> routines = new TreeMap<String, PreparedStatement>();

	int pendingCalls = 0;

	public Statement getStatement() {
		try {
			return conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public PreparedStatement getPreparedStatement(String statement) {
		try {
			return conn.prepareStatement(statement);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Malformed SQL");
			return null;
		}
	}

	public static void setDbLocation(String url) {
		Database.url = url;
	}

	public static Database getDb() {
		if (db == null) {
			db = new Database();
		}
		return db;

	}

	public void makeRoutine(String name, String statement) {
		PreparedStatement stmt = this.getPreparedStatement(statement);
		routines.put(name, stmt);
	}

	public void setRoutineParam(String name, int place, double p) {
		try {
			routines.get(name).setDouble(place, p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setRoutineParam(String name, int place, byte[] p) {
		try {
			routines.get(name).setBytes(place, p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setRoutineParam(String name, int place, int p) {
		try {
			routines.get(name).setInt(place, p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setRoutineParam(String name, int place, String p) {
		try {
			routines.get(name).setString(place, p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void submitRoutine(String name) {
		try {
			routines.get(name).execute();
			addToPendingCalls();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public ResultSet submitQueryRoutine(String name) {
		try {
			ResultSet rs = routines.get(name).executeQuery();
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
			return null;
		}
	}

	private void addToPendingCalls() {
		pendingCalls++;
		if (pendingCalls > 2000) {
			System.out.println("Commiting due to count");
			this.commit();
			pendingCalls = 0;
			System.out.println("Commited");
		}

	}

	public ResultSet executeQuery(String statement) {

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(statement);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		return rs;
	}

	public void execute(String statement) {

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.execute(statement);
			addToPendingCalls();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}

	}

	private Database() {
		try {
			Class.forName("org.sqlite.JDBC");
			File dbFile = new File(Database.url);
			boolean newDb = !(dbFile).exists();
			conn = DriverManager.getConnection("jdbc:sqlite:" + Database.url);
			conn.createStatement().execute("PRAGMA synchronous = off");
			conn.setAutoCommit(false);

			if (newDb) {
				Statement stmt = conn.createStatement();

				stmt.execute("create table chunks(id text, blocks blob, primary key(id))");
				stmt.execute("create table seed(seedValue bigint)");
				stmt.execute("create table blockmap(name varchar(255), id integer)");
				stmt.execute("create table players(name varchar(255), x real, y real, z real, heading real, pitch real)");

			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to create database");
			System.exit(0);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("Driver not found");
			System.exit(0);
		}
	}

	public void commit() {
		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void rollback() {
		try {
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}

	}
}
