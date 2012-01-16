package net.Builder.Core;

import java.util.ArrayList;

public class EntityManager {

	ArrayList<Entity> allEntities = new ArrayList<Entity>();

	static EntityManager manager;

	private EntityManager() {

	}

	public static EntityManager getManager() {
		if (manager == null) {
			manager = new EntityManager();
		}

		return manager;
	}

	public void register(Entity ent) {
		allEntities.add(ent);
	}

	public void doPhysics() {

		for (Entity ent : allEntities) {

			BoundingBox boundingBox = ent.getBoundingBox();

			int rad = boundingBox.getRad();
			float x = ent.getX();
			float y = ent.getY();
			float z = ent.getZ();

			World world = World.getWorld();

			for (int i = -rad; i <= rad; i++) {
				for (int j = -rad; j <= rad; j++) {
					for (int k = -rad; k <= rad; k++) {
						Block b = world.getBlock(x + i, y + j, z + k);
						if (b.boolProp("solid")) {
							System.out.println("Collision check with "
									+ b.getName() + " at x:" + (x + i) + " y:"
									+ (y + j) + " z:" + (z + k));
							boundingBox.collideCube(x, y, z);

						}
					}
				}
			}

			ent.setX(ent.getX() + boundingBox.getXTrans());
			ent.setY(ent.getY() + boundingBox.getYTrans());
			ent.setZ(ent.getZ() + boundingBox.getZTrans());

			System.out.println("Translated " + boundingBox.getXTrans() + " "
					+ boundingBox.getYTrans() + " " + boundingBox.getZTrans());
			System.exit(0);

		}

	}
}
