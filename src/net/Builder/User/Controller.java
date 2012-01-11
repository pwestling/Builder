package net.Builder.User;

import net.Builder.Core.Entity;
import net.Builder.Core.Updateable;

public abstract class Controller implements Updateable {

	Entity controlled;

	public Controller(Entity controlled) {
		super();
		this.controlled = controlled;
	}

}
