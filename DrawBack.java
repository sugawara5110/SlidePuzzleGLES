
package com.SlidePuzzle;

import android.opengl.Matrix;
import android.opengl.GLES30;

class DrawBack {

	//VBO ID
	private int[] mVBOID;
	//VAO ID
	private int[] mVAOID;
	private Matrix3D thetaMatX = new Matrix3D();
	private Matrix3D thetaMatY = new Matrix3D();
	private float thetaX = 0.0f;
	private float thetaY = 0.0f;
	private float[] mMVPMatrix = new float[16];
	private boolean created = false;

	private void create(int poshandle, int uvhandle) {

		float size = 20.0f;
		float[] vertices = {
				// ‘O 
				-size, -size, size, 0.0f, 1.0f, size, -size, size, 1.0f, 1.0f, -size, size, size, 0.0f, 0.0f, size,
				size, size, 1.0f, 0.0f,
				// Œã 
				-size, -size, -size, 0.0f, 1.0f, size, -size, -size, 1.0f, 1.0f, -size, size, -size, 0.0f, 0.0f, size,
				size, -size, 1.0f, 0.0f,
				// ¶ 
				-size, -size, size, 0.0f, 1.0f, -size, -size, -size, 1.0f, 1.0f, -size, size, size, 0.0f, 0.0f, -size,
				size, -size, 1.0f, 0.0f,
				// ‰E
				size, -size, size, 0.0f, 1.0f, size, -size, -size, 1.0f, 1.0f, size, size, size, 0.0f, 0.0f, size, size,
				-size, 1.0f, 0.0f,
				// ã 
				-size, size, size, 0.0f, 1.0f, size, size, size, 1.0f, 1.0f, -size, size, -size, 0.0f, 0.0f, size, size,
				-size, 1.0f, 0.0f,
				// ’ê 
				-size, -size, size, 0.0f, 1.0f, size, -size, size, 1.0f, 1.0f, -size, -size, -size, 0.0f, 0.0f, size,
				-size, -size, 1.0f, 0.0f, };

		int[] index = { 0, 1, 2, 1, 3, 2, 4, 6, 5, 5, 6, 7, 8, 10, 9, 9, 10, 11, 12, 13, 14, 13, 15, 14, 16, 17, 18, 17,
				19, 18, 20, 22, 21, 21, 22, 23, };

		mVBOID = new int[2];
		mVAOID = new int[1];

		GLES30.glGenBuffers(2, mVBOID, 0);
		GLES30.glGenVertexArrays(1, mVAOID, 0);

		Graphic.bindBufferObj(mVAOID[0], mVBOID[0], mVBOID[1], poshandle, uvhandle, 3, 2, vertices, index);
		created = true;
	}

	public void draw(int mvphandle, int sthandle, float[] st, float[] viewMat, float[] projMat, int poshandle,
			int uvhandle, int texture, int sdNo) {

		if (!created)
			create(poshandle, uvhandle);

		thetaX += (0.02 * Global.timef);
		thetaY += (0.1 * Global.timef);
		if (thetaX > 360.0f)
			thetaX = 0.0f;
		if (thetaY > 360.0f)
			thetaY = 0.0f;
		thetaMatX.MatrixRotationX(thetaX);
		thetaMatY.MatrixRotationY(thetaY);
		Matrix.multiplyMM(thetaMatY.m, 0, thetaMatY.m, 0, thetaMatX.m, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, viewMat, 0, thetaMatY.m, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, projMat, 0, mMVPMatrix, 0);

		Graphic.draw(mVAOID[0], texture, mvphandle, mMVPMatrix, sthandle, st, 24, 1, sdNo);
	}
}
