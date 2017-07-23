
package com.SlidePuzzle;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import android.opengl.GLES30;

class Draw2D {

	//VBO ID
	private int[] mVBOID;
	//VAO ID
	private int[] mVAOID;
	private boolean created = false;
	private float[] mTransform = new float[2];
	private float[] mUvwh = new float[4];

	private void create(int poshandle, int uvhandle, float siz, float sep) {

		float size = siz * 0.5f;
		float[] vertices = { -size, -size, 0.0f, sep, size, -size, sep, sep, -size, size, 0.0f, 0.0f, size, size, sep,
				0.0f };

		int[] index = { 0, 1, 2, 1, 3, 2 };

		mVBOID = new int[2];
		mVAOID = new int[1];

		GLES30.glGenBuffers(2, mVBOID, 0);
		GLES30.glGenVertexArrays(1, mVAOID, 0);

		Graphic.bindBufferObj(mVAOID[0], mVBOID[0], mVBOID[1], poshandle, uvhandle, 2, 2, vertices, index);
		created = true;
	}

	public void draw(float x, float y, float u, float v, int transformhandle, int uvwhhandle, int poshandle,
			int uvhandle, int texture) {

		if (!created)
			create(poshandle, uvhandle, 40.0f, 0.25f);

		mTransform[0] = x;
		mTransform[1] = y;
		mUvwh[0] = u;
		mUvwh[1] = v;
		mUvwh[2] = Global.width;
		mUvwh[3] = Global.height;
		Graphic.draw2D(mVAOID[0], texture, transformhandle, mTransform, uvwhhandle, mUvwh);
	}
}
