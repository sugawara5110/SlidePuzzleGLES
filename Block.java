
package com.SlidePuzzle;

import android.util.Log;

class Block {

	//ブロックの位置情報はブロックごとに持たせる
	public class Pos {
		float x;
		float y;
		float z;
	}

	private Pos currentPos;
	private Pos distancePos;
	private Pos directionPos;
	private float gap;
	private boolean gapOn;

	private boolean setDirection;
	private boolean moveOn;

	//回転
	private boolean thetaf;
	private float theta;
	private Matrix3D thetaMat;

	//頂点
	private float[] mVertices;
	private int[] mIndexBf;

	private Matrix3D moveMat;
	private Matrix3D world;

	//タッチ計算用全面座標
	private Vector3D[] ver = new Vector3D[4];

	public Block() {
		setDirection = false;
		moveOn = false;
		thetaf = false;
		theta = 0.0f;
	}

	public float[] getVertices() {
		return mVertices;
	}

	public int[] getIndex() {
		return mIndexBf;
	}

	public void Create(float blocksize, float b_width, float curx, float cury, float curz, float tx, float ty,
			int b_id) {
		moveMat = new Matrix3D();
		world = new Matrix3D();
		thetaMat = new Matrix3D();
		for (int i = 0; i < 4; i++)
			ver[i] = new Vector3D();
		CreateVertices(blocksize, b_width, tx, ty, b_id);

		distancePos = new Pos();
		currentPos = new Pos();
		directionPos = new Pos();
		distancePos.x = currentPos.x = curx;
		distancePos.y = currentPos.y = cury;
		distancePos.z = currentPos.z = curz;

		directionPos.x = 0.0f;
		directionPos.y = 0.0f;
		directionPos.z = 0.0f;
		gap = 1.0f;
		gapOn = false;
	}

	//2D座標計算,タッチ位置判別
	public boolean TouchInside(Matrix3D lpv, float x, float y) {

		for (int i = 0; i < 4; i++) {
			ver[i].as(mVertices[i * 7], mVertices[i * 7 + 1], mVertices[i * 7 + 2]);
			ver[i].VectorMatrixMultiply(world);
			ver[i].VectorMatrixMultiply(lpv);
			ver[i].VectorViewAfter();
		}

		float MaxX = 0.0f;
		float MaxY = 0.0f;
		float MinX = Global.width;
		float MinY = Global.height;
		for (int i = 0; i < 4; i++) {
			if (ver[i].x > MaxX)
				MaxX = ver[i].x;
			if (ver[i].x < MinX)
				MinX = ver[i].x;
			if (ver[i].y > MaxY)
				MaxY = ver[i].y;
			if (ver[i].y < MinY)
				MinY = ver[i].y;
		}

		if (MinX < x && x < MaxX && MinY < y && y < MaxY) {
			Log.i(getClass().toString(), String.format("TX = %f  TY = %f", x, y));
			return true;
		}
		return false;
	}

	public boolean getMoveF() {
		return setDirection;
	}

	public void upDate() {
		setGap();
		move();
	}

	private void CreateVertices(float size, float b_width, float tx, float ty, int b_id) {

		float ver[] = Graphic.createBlockVer(size);
		float coords[] = Graphic.createBlockCoord(tx, ty, (float) b_id, b_width);//3個目はブロックID

		mVertices = new float[168];//3 * 4 * 6 + 4 * 4 * 6
		int vInd = 0;
		int cInd = 0;
		for (int i = 0; i < 168; i += 7) {
			mVertices[i] = ver[vInd];
			mVertices[i + 1] = ver[vInd + 1];
			mVertices[i + 2] = ver[vInd + 2];
			mVertices[i + 3] = coords[cInd];
			mVertices[i + 4] = coords[cInd + 1];
			mVertices[i + 5] = coords[cInd + 2];
			mVertices[i + 6] = coords[cInd + 3];
			vInd += 3;
			cInd += 4;
		}

		mIndexBf = Graphic.createBlockInd(b_id);
	}

	public void setDistancePos(float distx, float disty, float distz) {
		distancePos.x = distx;
		distancePos.y = disty;
		distancePos.z = distz;
		setDirection = true;
	}

	public Pos getPosition() {
		return currentPos;
	}

	public Matrix3D getWorldMat() {
		//ワールド変換行列生成
		moveMat.MatrixTranslation(currentPos.x * gap, currentPos.y * gap, currentPos.z * gap);
		thetaMat.MatrixRotationY(theta);
		world.MatrixMultiply(moveMat, thetaMat);

		return world;
	}

	public void thetaOn() {
		thetaf = true;
	}

	public void gapSwitch(boolean f) {
		gapOn = f;
	}

	private void setGap() {
		if (gapOn) {
			gap += 0.005 * Global.timef;
			if (gap >= 1.1f)
				gap = 1.1f;
		} else {
			gap -= 0.005 * Global.timef;
			if (gap <= 1.0f)
				gap = 1.0f;
		}
	}

	private void move() {

		float ti = Global.timef;

		//回転
		if (thetaf) {
			theta += (6.0f * ti);
			if (theta >= 360.0f) {
				theta = 0.0f;
				thetaf = false;
			}
		}

		if (setDirection) {
			//移動先POSを元に移動方向設定
			if (!moveOn) {
				moveOn = true;
				if (distancePos.x < currentPos.x)
					directionPos.x = -1.0f;
				if (distancePos.x > currentPos.x)
					directionPos.x = 1.0f;
				if (distancePos.y < currentPos.y)
					directionPos.y = -1.0f;
				if (distancePos.y > currentPos.y)
					directionPos.y = 1.0f;
				if (distancePos.z < currentPos.z)
					directionPos.z = -1.0f;
				if (distancePos.z > currentPos.z)
					directionPos.z = 1.0f;
			}
			float step = 0.20f * ti;
			boolean xf, yf, zf;
			xf = yf = zf = false;
			//移動処理
			if (moveOn) {
				//座標処理
				currentPos.x += (step * directionPos.x);
				currentPos.y += (step * directionPos.y);
				currentPos.z += (step * directionPos.z);
				//移動先到達判定directionPos==0.0fは移動無し状態
				if (directionPos.x == 0.0f || directionPos.x == 1.0f && distancePos.x <= currentPos.x
						|| directionPos.x == -1.0f && distancePos.x >= currentPos.x) {
					currentPos.x = distancePos.x;
					xf = true;
				}
				if (directionPos.y == 0.0f || directionPos.y == 1.0f && distancePos.y <= currentPos.y
						|| directionPos.y == -1.0f && distancePos.y >= currentPos.y) {
					currentPos.y = distancePos.y;
					yf = true;
				}
				if (directionPos.z == 0.0f || directionPos.z == 1.0f && distancePos.z <= currentPos.z
						|| directionPos.z == -1.0f && distancePos.z >= currentPos.z) {
					currentPos.z = distancePos.z;
					zf = true;
				}
				//移動処理完了
				if (xf && yf && zf) {
					xf = yf = zf = false;
					directionPos.x = directionPos.y = directionPos.z = 0.0f;
					moveOn = false;
					setDirection = false;
				}
			}
		}
	}
}
