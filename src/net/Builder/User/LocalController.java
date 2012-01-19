package net.Builder.User;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.Builder.Core.Entity;
import net.Builder.Render.Renderer;
import net.Builder.util.Point;

public class LocalController extends Controller {

	double speed = 4.0;
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
		Point pos = controlled.getPos().clone();
		Point vel = controlled.getVel();
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			pos.translate(Math.sin(headingrad) * speed
					* delta, 0, - Math.cos(headingrad) * speed
					* delta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			pos.translate(-Math.sin(headingrad) * speed
					* delta, 0,  Math.cos(headingrad) * speed
					* delta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			pos.translate(-Math.cos(headingrad) * speed
					* delta, 0, - Math.sin(headingrad) * speed
					* delta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			pos.translate(Math.cos(headingrad) * speed
					* delta, 0,  Math.sin(headingrad) * speed
					* delta);

		}

		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			fixMouse = false;

		}
		if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
			fixMouse = true;

		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && fly) {
			pos.translate(0,speed * delta,0);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)
				&& controlled.getVel().y() == 0) {
			vel.translate(0, 0.08, 0);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && fly) {
			pos.translate(0,-speed * delta,0);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_P) && !fly) {
			fly = true;
			controlled.setNoClip(true);
			vel.setY(0);
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
			double dh = ((mouseDx) / ((float) w / 2))* delta * rotspeed;
			controlled.setHeading(heading + dh);
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
		
		controlled.setIntendedMove(pos);
		controlled.setVel(vel);

	}

}
