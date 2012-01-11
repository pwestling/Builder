package net.Builder.Render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import net.Builder.util.FloatVector;
import net.Builder.util.FloatVectorHash;

public interface Viewable {

	public FloatVectorHash vertices();

	public boolean isDirty();

	public void setListId(int id);

	public int getListID();

	public boolean isVisible();

	public float[] boundingSphere();

	public Collection<Viewable> getChildren();

	public void cleanup();

}
