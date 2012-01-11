package net.Builder.Render;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslated;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class FPSCamera extends Camera {

	float[] proj = new float[16];
	float[] modl = new float[16];
	float[] clip = new float[16];
	float[][] frustum = new float[6][4];

	FloatBuffer projBuf = BufferUtils.createFloatBuffer(16);
	FloatBuffer modlBuf = BufferUtils.createFloatBuffer(16);

	@Override
	public void transform() {

		glRotatef(anchor.getPitch(), 1, 0, 0);
		glRotatef(anchor.getHeading(), 0, 1, 0);

		glTranslated(-anchor.getX(), -anchor.getY() + 0.75, -anchor.getZ());

	}

	@Override
	public float[][] getFrustum() {
		projBuf.clear();
		modlBuf.clear();
		glGetFloat(GL_PROJECTION_MATRIX, projBuf);
		glGetFloat(GL_MODELVIEW_MATRIX, modlBuf);

		for (int i = 0; i < 16; i++) {
			proj[i] = projBuf.get(i);
			modl[i] = modlBuf.get(i);
		}

		/* Combine the two matrices (multiply projection by modelview) */
		clip[0] = modl[0] * proj[0] + modl[1] * proj[4] + modl[2] * proj[8]
				+ modl[3] * proj[12];
		clip[1] = modl[0] * proj[1] + modl[1] * proj[5] + modl[2] * proj[9]
				+ modl[3] * proj[13];
		clip[2] = modl[0] * proj[2] + modl[1] * proj[6] + modl[2] * proj[10]
				+ modl[3] * proj[14];
		clip[3] = modl[0] * proj[3] + modl[1] * proj[7] + modl[2] * proj[11]
				+ modl[3] * proj[15];

		clip[4] = modl[4] * proj[0] + modl[5] * proj[4] + modl[6] * proj[8]
				+ modl[7] * proj[12];
		clip[5] = modl[4] * proj[1] + modl[5] * proj[5] + modl[6] * proj[9]
				+ modl[7] * proj[13];
		clip[6] = modl[4] * proj[2] + modl[5] * proj[6] + modl[6] * proj[10]
				+ modl[7] * proj[14];
		clip[7] = modl[4] * proj[3] + modl[5] * proj[7] + modl[6] * proj[11]
				+ modl[7] * proj[15];

		clip[8] = modl[8] * proj[0] + modl[9] * proj[4] + modl[10] * proj[8]
				+ modl[11] * proj[12];
		clip[9] = modl[8] * proj[1] + modl[9] * proj[5] + modl[10] * proj[9]
				+ modl[11] * proj[13];
		clip[10] = modl[8] * proj[2] + modl[9] * proj[6] + modl[10] * proj[10]
				+ modl[11] * proj[14];
		clip[11] = modl[8] * proj[3] + modl[9] * proj[7] + modl[10] * proj[11]
				+ modl[11] * proj[15];

		clip[12] = modl[12] * proj[0] + modl[13] * proj[4] + modl[14] * proj[8]
				+ modl[15] * proj[12];
		clip[13] = modl[12] * proj[1] + modl[13] * proj[5] + modl[14] * proj[9]
				+ modl[15] * proj[13];
		clip[14] = modl[12] * proj[2] + modl[13] * proj[6] + modl[14]
				* proj[10] + modl[15] * proj[14];
		clip[15] = modl[12] * proj[3] + modl[13] * proj[7] + modl[14]
				* proj[11] + modl[15] * proj[15];

		/* Extract the numbers for the RIGHT plane */
		frustum[0][0] = clip[3] - clip[0];
		frustum[0][1] = clip[7] - clip[4];
		frustum[0][2] = clip[11] - clip[8];
		frustum[0][3] = clip[15] - clip[12];

		/* Normalize the result */
		double t = Math.sqrt(frustum[0][0] * frustum[0][0] + frustum[0][1]
				* frustum[0][1] + frustum[0][2] * frustum[0][2]);
		frustum[0][0] = (float) (frustum[0][0] / t);
		frustum[0][1] = (float) (frustum[0][1] / t);
		frustum[0][2] = (float) (frustum[0][2] / t);
		frustum[0][3] = (float) (frustum[0][3] / t);

		/* Extract the numbers for the LEFT plane */
		frustum[1][0] = clip[3] + clip[0];
		frustum[1][1] = clip[7] + clip[4];
		frustum[1][2] = clip[11] + clip[8];
		frustum[1][3] = clip[15] + clip[12];

		/* Normalize the result */
		t = Math.sqrt(frustum[1][0] * frustum[1][0] + frustum[1][1]
				* frustum[1][1] + frustum[1][2] * frustum[1][2]);
		frustum[1][0] = (float) (frustum[1][0] / t);
		frustum[1][1] = (float) (frustum[1][1] / t);
		frustum[1][2] = (float) (frustum[1][2] / t);
		frustum[1][3] = (float) (frustum[1][3] / t);

		/* Extract the BOTTOM plane */
		frustum[2][0] = clip[3] + clip[1];
		frustum[2][1] = clip[7] + clip[5];
		frustum[2][2] = clip[11] + clip[9];
		frustum[2][3] = clip[15] + clip[13];

		/* Normalize the result */
		t = Math.sqrt(frustum[2][0] * frustum[2][0] + frustum[2][1]
				* frustum[2][1] + frustum[2][2] * frustum[2][2]);
		frustum[2][0] = (float) (frustum[2][0] / t);
		frustum[2][1] = (float) (frustum[2][1] / t);
		frustum[2][2] = (float) (frustum[2][2] / t);
		frustum[2][3] = (float) (frustum[2][3] / t);

		/* Extract the TOP plane */
		frustum[3][0] = clip[3] - clip[1];
		frustum[3][1] = clip[7] - clip[5];
		frustum[3][2] = clip[11] - clip[9];
		frustum[3][3] = clip[15] - clip[13];

		/* Normalize the result */
		t = Math.sqrt(frustum[3][0] * frustum[3][0] + frustum[3][1]
				* frustum[3][1] + frustum[3][2] * frustum[3][2]);
		frustum[3][0] = (float) (frustum[3][0] / t);
		frustum[3][1] = (float) (frustum[3][1] / t);
		frustum[3][2] = (float) (frustum[3][2] / t);
		frustum[3][3] = (float) (frustum[3][3] / t);

		/* Extract the FAR plane */
		frustum[4][0] = clip[3] - clip[2];
		frustum[4][1] = clip[7] - clip[6];
		frustum[4][2] = clip[11] - clip[10];
		frustum[4][3] = clip[15] - clip[14];

		/* Normalize the result */
		t = Math.sqrt(frustum[4][0] * frustum[4][0] + frustum[4][1]
				* frustum[4][1] + frustum[4][2] * frustum[4][2]);
		frustum[4][0] = (float) (frustum[4][0] / t);
		frustum[4][1] = (float) (frustum[4][1] / t);
		frustum[4][2] = (float) (frustum[4][2] / t);
		frustum[4][3] = (float) (frustum[4][3] / t);

		/* Extract the NEAR plane */
		frustum[5][0] = clip[3] + clip[2];
		frustum[5][1] = clip[7] + clip[6];
		frustum[5][2] = clip[11] + clip[10];
		frustum[5][3] = clip[15] + clip[14];

		/* Normalize the result */
		t = Math.sqrt(frustum[5][0] * frustum[5][0] + frustum[5][1]
				* frustum[5][1] + frustum[5][2] * frustum[5][2]);
		frustum[5][0] = (float) (frustum[5][0] / t);
		frustum[5][1] = (float) (frustum[5][1] / t);
		frustum[5][2] = (float) (frustum[5][2] / t);
		frustum[5][3] = (float) (frustum[5][3] / t);

		return frustum;

	}

	@Override
	public void rotate() {
		// TODO Auto-generated method stub

	}

}
