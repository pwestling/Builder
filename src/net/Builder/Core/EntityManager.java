package net.Builder.Core;

import java.util.ArrayList;

import net.Builder.util.Point;

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
		//System.out.println("phys");
		for (Entity ent : allEntities) {
//			if (!ent.getNoClip()) {
//				BoundingBox boundingBox = ent.getBoundingBox();
//
//				int rad = boundingBox.getRad();
//
//				Point pos = ent.getPos();
//				Point iterPos = new Point();
//				World world = World.getWorld();
//
//				for (int i = -rad; i <= rad; i++) {
//					for (int j = -rad; j <= rad; j++) {
//						for (int k = -rad; k <= rad; k++) {
//							iterPos.set(i, j, k);
//							Point p = pos.plus(iterPos);
//							Block b = world.getBlock(p);
//							if (b.boolProp("solid")) {
//								if (boundingBox.collideCube(p)) return;
//
//							}
//						}
//					}
//				}
//
//			
//
//			}
			//System.out.println("Set move");
			ent.setPos(ent.getIntendedMove());
		}

	}
}
