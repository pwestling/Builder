package net.Builder.User;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.Builder.Core.Entity;
import net.Builder.Render.Renderer;

public class LocalController extends Controller {

	double speed = 0.008;
	double rotspeed = 720;

	int w = Renderer.screenWidth;
	int h = Renderer.screenHeight;

	int mouseX = w / 2;
	int mouseY = h / 2;

	boolean fixMouse = true;

	public LocalController(Entity controlled) {
		super(controlled);
		// TODO Auto-generated constructor stub
	}

	boolean fly = false;
	private boolean fast = false;

	@Override
	public void update(double delta) {
		double headingrad = Math.toRadians(controlled.getHeading());
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			controlled.setZ(controlled.getZ() - Math.cos(headingrad) * speed
					* delta);
			controlled.setX(controlled.getX() + Math.sin(headingrad) * speed
					* delta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			controlled.setZ(controlled.getZ() + Math.cos(headingrad) * speed
					* delta);
			controlled.setX(controlled.getX() - Math.sin(headingrad) * speed
					* delta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			controlled.setZ(controlled.getZ() - Math.sin(headingrad) * speed
					* delta);
			controlled.setX(controlled.getX() - Math.cos(headingrad) * speed
					* delta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			controlled.setZ(controlled.getZ() + Math.sin(headingrad) * speed
					* delta);
			controlled.setX(controlled.getX() + Math.cos(headingrad) * speed
					* delta);

		}

		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			fixMouse = false;

		}
		if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
			fixMouse = true;

		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && fly) {
			controlled.setY(controlled.getY() + speed * delta);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)
				&& controlled.getyVel() == 0) {
			controlled.setyVel(0.08);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && fly) {
			controlled.setY(controlled.getY() - speed * delta);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_P) && !fly) {
			fly = true;
			controlled.setNoClip(true);
			controlled.setyVel(0);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_L) && fly) {
			fly = false;
			controlled.setNoClip(false);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_M) && fast) {
			fast = false;
			speed = speed / 5;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_N) && !fast) {
			fast = true;
			speed = speed * 5;
		}

		int mouseDx = Mouse.getX() - mouseX;
		int mouseDy = Mouse.getY() - mouseY;
		double heading = controlled.getHeading();
		double pitch = controlled.getPitch();
		if (fixMouse) {
			controlled.setHeading(heading + ((mouseDx) / ((float) w / 2))
					* delta * rotspeed);
			pitch -= ((mouseDy) / ((float) h / 2)) * delta * rotspeed;
			controlled.setPitch(Math.min(Math.max(pitch, -90), 90));
		}

		mouseX = Mouse.getX();
		mouseY = Mouse.getY();
		// System.out.println(mouseX + " " + mouseY);
		if (fixMouse
				&& (mouseX <= w / 5 || mouseX >= w * 4 / 5 || mouseY <= h / 5 || mouseY >= h * 4 / 5)) {
			mouseX = w / 2;
			mouseY = h / 2;
			Mouse.setCursorPosition(w / 2, h / 2);

		}

	}

}
