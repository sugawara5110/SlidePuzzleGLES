
package com.SlidePuzzle;

import java.lang.Math;

public class Matrix3D {

	public float[] m = new float[16];
	private float[] mat = new float[4];

	//正規化用
	private float[] normalize = new float[4];

	public float[] Normalize(float x, float y, float z, float w) {
		float nor = (float) Math.sqrt(x * x + y * y + z * z + w * w);
		if (nor != 0.0f) {
			normalize[0] = x / nor;
			normalize[1] = y / nor;
			normalize[2] = z / nor;
			normalize[3] = w / nor;
		} else {
			normalize[0] = 0.0f;
			normalize[1] = 0.0f;
			normalize[2] = 0.0f;
			normalize[3] = 0.0f;
		}
		return normalize;
	}

	public void MatrixIdentity() {
		m[0] = 1.0f;
		m[1] = 0.0f;
		m[2] = 0.0f;
		m[3] = 0.0f;

		m[4] = 0.0f;
		m[5] = 1.0f;
		m[6] = 0.0f;
		m[7] = 0.0f;

		m[8] = 0.0f;
		m[9] = 0.0f;
		m[10] = 1.0f;
		m[11] = 0.0f;

		m[12] = 0.0f;
		m[13] = 0.0f;
		m[14] = 0.0f;
		m[15] = 1.0f;
	}

	public void MatrixNormalize() {
		mat = Normalize(m[0], m[4], m[8], 0.0f);
		m[0] = mat[0];
		m[4] = mat[1];
		m[8] = mat[2];
		mat = Normalize(m[1], m[5], m[9], 0.0f);
		m[1] = mat[0];
		m[5] = mat[1];
		m[9] = mat[2];
		mat = Normalize(m[2], m[6], m[10], 0.0f);
		m[2] = mat[0];
		m[6] = mat[1];
		m[10] = mat[2];
	}

	public void MatrixRotationX(float theta) {
		float the = theta * (float) Math.PI / 180.0f;
		m[0] = 1.0f;
		m[1] = 0.0f;
		m[2] = 0.0f;
		m[3] = 0.0f;

		m[4] = 0.0f;
		m[5] = (float) Math.cos(the);
		m[6] = (float) Math.sin(the);
		m[7] = 0.0f;

		m[8] = 0.0f;
		m[9] = (float) -Math.sin(the);
		m[10] = (float) Math.cos(the);
		m[11] = 0.0f;

		m[12] = 0.0f;
		m[13] = 0.0f;
		m[14] = 0.0f;
		m[15] = 1.0f;
	}

	public void MatrixRotationY(float theta) {
		float the = theta * (float) Math.PI / 180.0f;
		m[0] = (float) Math.cos(the);
		m[1] = 0.0f;
		m[2] = (float) -Math.sin(the);
		m[3] = 0.0f;

		m[4] = 0.0f;
		m[5] = 1.0f;
		m[6] = 0.0f;
		m[7] = 0.0f;

		m[8] = (float) Math.sin(the);
		m[9] = 0.0f;
		m[10] = (float) Math.cos(the);
		m[11] = 0.0f;

		m[12] = 0.0f;
		m[13] = 0.0f;
		m[14] = 0.0f;
		m[15] = 1.0f;
	}

	public void MatrixRotationZ(float theta) {
		float the = theta * (float) Math.PI / 180.0f;
		m[0] = (float) Math.cos(the);
		m[1] = (float) Math.sin(the);
		m[2] = 0.0f;
		m[3] = 0.0f;

		m[4] = (float) -Math.sin(the);
		m[5] = (float) Math.cos(the);
		m[6] = 0.0f;
		m[7] = 0.0f;

		m[8] = 0.0f;
		m[9] = 0.0f;
		m[10] = 1.0f;
		m[11] = 0.0f;

		m[12] = 0.0f;
		m[13] = 0.0f;
		m[14] = 0.0f;
		m[15] = 1.0f;
	}

	public void MatrixMultiply(Matrix3D mat1, Matrix3D mat2) {
		m[0] = mat1.m[0] * mat2.m[0] + mat1.m[1] * mat2.m[4] + mat1.m[2] * mat2.m[8] + mat1.m[3] * mat2.m[12];
		m[1] = mat1.m[0] * mat2.m[1] + mat1.m[1] * mat2.m[5] + mat1.m[2] * mat2.m[9] + mat1.m[3] * mat2.m[13];
		m[2] = mat1.m[0] * mat2.m[2] + mat1.m[1] * mat2.m[6] + mat1.m[2] * mat2.m[10] + mat1.m[3] * mat2.m[14];
		m[3] = mat1.m[0] * mat2.m[3] + mat1.m[1] * mat2.m[7] + mat1.m[2] * mat2.m[11] + mat1.m[3] * mat2.m[15];

		m[4] = mat1.m[4] * mat2.m[0] + mat1.m[5] * mat2.m[4] + mat1.m[6] * mat2.m[8] + mat1.m[7] * mat2.m[12];
		m[5] = mat1.m[4] * mat2.m[1] + mat1.m[5] * mat2.m[5] + mat1.m[6] * mat2.m[9] + mat1.m[7] * mat2.m[13];
		m[6] = mat1.m[4] * mat2.m[2] + mat1.m[5] * mat2.m[6] + mat1.m[6] * mat2.m[10] + mat1.m[7] * mat2.m[14];
		m[7] = mat1.m[4] * mat2.m[3] + mat1.m[5] * mat2.m[7] + mat1.m[6] * mat2.m[11] + mat1.m[7] * mat2.m[15];

		m[8] = mat1.m[8] * mat2.m[0] + mat1.m[9] * mat2.m[4] + mat1.m[10] * mat2.m[8] + mat1.m[11] * mat2.m[12];
		m[9] = mat1.m[8] * mat2.m[1] + mat1.m[9] * mat2.m[5] + mat1.m[10] * mat2.m[9] + mat1.m[11] * mat2.m[13];
		m[10] = mat1.m[8] * mat2.m[2] + mat1.m[9] * mat2.m[6] + mat1.m[10] * mat2.m[10] + mat1.m[11] * mat2.m[14];
		m[11] = mat1.m[8] * mat2.m[3] + mat1.m[9] * mat2.m[7] + mat1.m[10] * mat2.m[11] + mat1.m[11] * mat2.m[15];

		m[12] = mat1.m[12] * mat2.m[0] + mat1.m[13] * mat2.m[4] + mat1.m[14] * mat2.m[8] + mat1.m[15] * mat2.m[12];
		m[13] = mat1.m[12] * mat2.m[1] + mat1.m[13] * mat2.m[5] + mat1.m[14] * mat2.m[9] + mat1.m[15] * mat2.m[13];
		m[14] = mat1.m[12] * mat2.m[2] + mat1.m[13] * mat2.m[6] + mat1.m[14] * mat2.m[10] + mat1.m[15] * mat2.m[14];
		m[15] = mat1.m[12] * mat2.m[3] + mat1.m[13] * mat2.m[7] + mat1.m[14] * mat2.m[11] + mat1.m[15] * mat2.m[15];
	}

	public void MatrixTranslation(float movx, float movy, float movz) {
		m[0] = 1.0f;
		m[1] = 0.0f;
		m[2] = 0.0f;
		m[3] = 0.0f;
		m[4] = 0.0f;
		m[5] = 1.0f;
		m[6] = 0.0f;
		m[7] = 0.0f;
		m[8] = 0.0f;
		m[9] = 0.0f;
		m[10] = 1.0f;
		m[11] = 0.0f;
		m[12] = movx;
		m[13] = movy;
		m[14] = movz;
		m[15] = 1.0f;
	}

	public void MatrixLookAtLH(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3,
			float z3) {
		//z軸
		float zx = x2 - x1;
		float zy = y2 - y1;
		float zz = z2 - z1;
		MatrixLookAt(x1, y1, z1, x2, y2, z2, x3, y3, z3, zx, zy, zz);
	}

	public void MatrixLookAtRH(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3,
			float z3) {
		//z軸
		float zx = x1 - x2;
		float zy = y1 - y2;
		float zz = z1 - z2;
		MatrixLookAt(x1, y1, z1, x2, y2, z2, x3, y3, z3, zx, zy, zz);
	}

	private void MatrixLookAt(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3,
			float zx, float zy, float zz) {
		//正規化
		float[] nor = Normalize(zx, zy, zz, 0.0f);
		zx = nor[0];
		zy = nor[1];
		zz = nor[2];

		//x軸(外積)
		float xx = y3 * zz - z3 * zy;
		float xy = z3 * zx - x3 * zz;
		float xz = x3 * zy - y3 * zx;
		//正規化
		float[] nor1 = Normalize(xx, xy, xz, 0.0f);
		xx = nor1[0];
		xy = nor1[1];
		xz = nor1[2];

		//y軸(外積)
		float yx = zy * xz - zz * xy;
		float yy = zz * xx - zx * xz;
		float yz = zx * xy - zy * xx;

		//平行移動(内積)
		float mx = -(x1 * xx + y1 * xy + z1 * xz);
		float my = -(x1 * yx + y1 * yy + z1 * yz);
		float mz = -(x1 * zx + y1 * zy + z1 * zz);

		m[0] = xx;
		m[1] = yx;
		m[2] = zx;
		m[3] = 0.0f;

		m[4] = xy;
		m[5] = yy;
		m[6] = zy;
		m[7] = 0.0f;

		m[8] = xz;
		m[9] = yz;
		m[10] = zz;
		m[11] = 0.0f;

		m[12] = mx;
		m[13] = my;
		m[14] = mz;
		m[15] = 1.0f;
	}

	public void MatrixPerspectiveFovLH(float theta, float aspect, float Near, float Far) {
		MatrixPerspectiveFov(theta, aspect, Near, Far, 1.0f, Far - Near);
	}

	public void MatrixPerspectiveFovRH(float theta, float aspect, float Near, float Far) {
		MatrixPerspectiveFov(theta, aspect, Near, Far, -1.0f, Near - Far);
	}

	private void MatrixPerspectiveFov(float theta, float aspect, float Near, float Far, float m11, float fn) {
		float the = theta * (float) Math.PI / 180.0f;
		//透視変換後y方向スケーリング
		float sy = 1.0f / ((float) Math.tan(the / 2.0f));
		//x方向スケーリング
		float sx = sy / aspect;
		//z方向スケーリング
		float sz = Far / fn;

		m[0] = sx;
		m[1] = 0.0f;
		m[2] = 0.0f;
		m[3] = 0.0f;

		m[4] = 0.0f;
		m[5] = sy;
		m[6] = 0.0f;
		m[7] = 0.0f;

		m[8] = 0.0f;
		m[9] = 0.0f;
		m[10] = sz;
		m[11] = m11;

		m[12] = 0.0f;
		m[13] = 0.0f;
		m[14] = -(sz * Near);
		m[15] = 0.0f;
	}

	public void MatrixViewPort() {
		m[0] = Global.width / 2;
		m[1] = 0.0f;
		m[2] = 0.0f;
		m[3] = 0.0f;

		m[4] = 0.0f;
		m[5] = -Global.height / 2;
		m[6] = 0.0f;
		m[7] = 0.0f;

		m[8] = 0.0f;
		m[9] = 0.0f;
		m[10] = 1.0f;
		m[11] = 0.0f;

		m[12] = Global.width / 2;
		m[13] = Global.height / 2;
		m[14] = 0.0f;
		m[15] = 1.0f;
	}

	public void MatrixTranspose() {
		float temp;
		temp = m[1];
		m[1] = m[4];
		m[4] = temp;
		temp = m[2];
		m[2] = m[8];
		m[8] = temp;
		temp = m[3];
		m[3] = m[12];
		m[12] = temp;
		temp = m[6];
		m[6] = m[9];
		m[9] = temp;
		temp = m[7];
		m[7] = m[13];
		m[13] = temp;
		temp = m[11];
		m[11] = m[14];
		m[14] = temp;
	}

	public void D3DtoGLProjection() {
		m[2] = 2.0f * m[2] - m[3];
		m[6] = 2.0f * m[6] - m[7];
		m[10] = 2.0f * m[10] - m[11];
		m[11] = 2.0f * m[14] - m[15];
	}

	//任意軸回転
	public void ArbitraryRotateMatrix(float theta, float x, float y, float z) {
		float the = theta * (float) Math.PI / 180.0f;
		m[0] = x * x * (1.0f - (float) Math.cos(the)) + (float) Math.cos(the);
		m[1] = x * y * (1.0f - (float) Math.cos(the)) + z * (float) Math.sin(the);
		m[2] = x * z * (1.0f - (float) Math.cos(the)) - y * (float) Math.sin(the);
		m[3] = 0.0f;

		m[4] = x * y * (1.0f - (float) Math.cos(the)) - z * (float) Math.sin(the);
		m[5] = y * y * (1.0f - (float) Math.cos(the)) + (float) Math.cos(the);
		m[6] = y * z * (1.0f - (float) Math.cos(the)) + x * (float) Math.sin(the);
		m[7] = 0.0f;

		m[8] = x * z * (1.0f - (float) Math.cos(the)) + y * (float) Math.sin(the);
		m[9] = y * z * (1.0f - (float) Math.cos(the)) - x * (float) Math.sin(the);
		m[10] = z * z * (1.0f - (float) Math.cos(the)) + (float) Math.cos(the);
		m[11] = 0.0f;

		m[12] = 0.0f;
		m[13] = 0.0f;
		m[14] = 0.0f;
		m[15] = 1.0f;
	}
}
