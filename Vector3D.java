
package com.SlidePuzzle;;

public class Vector3D {
	public float x;
	public float y;
	public float z;
	public float w;

	public void as(float x1, float y1, float z1) {
		x = x1;
		y = y1;
		z = z1;
		w = 1.0f;
	}

	public void VectorMatrixMultiply(Matrix3D mat) {
		float x = this.x;
		float y = this.y;
		float z = this.z;
		float w = this.w;

		this.x = x * mat.m[0] + y * mat.m[4] + z * mat.m[8] + w * mat.m[12];
		this.y = x * mat.m[1] + y * mat.m[5] + z * mat.m[9] + w * mat.m[13];
		this.z = x * mat.m[2] + y * mat.m[6] + z * mat.m[10] + w * mat.m[14];
		this.w = x * mat.m[3] + y * mat.m[7] + z * mat.m[11] + w * mat.m[15];
	}

	public void VectorViewAfter() {
		this.x /= this.w;
		this.y /= this.w;
		this.z /= this.w;
		this.w = 1.0f;
	}

	//‘å‚«‚¢•ûÅ‰‚Ì—v‘f‚É‚È‚é
	public static Vector3D[] bsort(Vector3D[] a, int ln) {
		float tempX, tempY, tempZ;
		for (int i = 0; i < ln - 1; i++) {
			for (int j = ln - 2; j >= i; j--) {

				if (a[j].z < a[j + 1].z) {
					tempX = a[j].x;
					tempY = a[j].y;
					tempZ = a[j].z;
					a[j].x = a[j + 1].x;
					a[j].y = a[j + 1].y;
					a[j].z = a[j + 1].z;
					a[j + 1].x = tempX;
					a[j + 1].y = tempY;
					a[j + 1].z = tempZ;
				}
			}
		}
		return a;
	}

	public static float[] bsort(float[] a, int ln) {
		float temp;
		for (int i = 0; i < ln - 1; i++) {
			for (int j = ln - 2; j >= i; j--) {

				if (a[j] < a[j + 1]) {
					temp = a[j];
					a[j] = a[j + 1];
					a[j + 1] = temp;
				}
			}
		}
		return a;
	}
}
