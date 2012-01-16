package net.Builder.Core;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import net.Builder.User.Player;
import net.Builder.Render.Camera;
import net.Builder.Render.FPSCamera;
import net.Builder.Render.Renderer;
import net.Builder.util.FloatVector;
import net.Builder.util.Profiler;

import org.lwjgl.opengl.Display;

public class Main {

	public static void main(String[] args) {

		try {
			Database.setDbLocation("test.db");

			Renderer r = Renderer.getRenderer();
			r.screenWidth = 1366;
			r.screenHeight = 768;
			r.init();
			Player p = new Player("thehivemind5");
			Camera cam = new FPSCamera();
			cam.setAnchor(p);
			r.setCamera(cam);

			BlockLibrary.getBlockLibrary().init();
			Profiler.mark();
			World.worldSize = 16;
			World world = World.getWorld();
			world.addPlayer(p);
			world.loadChunks(100);
			System.out.println("World uses " + Profiler.memorySinceMark());
			r.setRoot(world);

			long time = System.nanoTime();
			double dt = 1 / 40.0;
			world.startUpdater();
			System.out.println("Updater running");
			while (!Display.isCloseRequested()) {
				Display.sync(80);
				double frameTime = ((System.nanoTime() - time) / 1000000);
				time = System.nanoTime();
				while (frameTime > 0.0) {
					double deltaTime = Math.min(frameTime, dt);
					p.update(deltaTime);

					frameTime -= dt;
				}
				EntityManager.getManager().doPhysics();

				r.render();

			}
			world.stopUpdater();
			System.out.println("Commiting");
			Database.getDb().commit();
			System.out.println("Commited");
			Display.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			// Database.getDb().rollback();
		}
	}
}
