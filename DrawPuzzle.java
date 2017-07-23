
package com.SlidePuzzle;

import android.opengl.Matrix;
import java.util.Random;
import android.opengl.GLES30;

class DrawPuzzle {

	private final float PuzzleSize = 6.0f;

	private int b_width;//1辺のブロック個数
	private int b_pcs;//ブロック個数
	private int drawCallNum;
	private int drawBlockNum;
	private float b_size;
	private boolean creat;
	private int b_widthTmp;
	private boolean touching;
	private boolean AutoOff;

	private int B_Array[];//ブロック配列インデックス(スペースも含める)
	private Block[] B_obj;//各ブロックobj
	private int spaceI;//スペースインデックス
	private float spaceX, spaceY;

	//VBO ID
	private int[][] mVBOID;
	//VAO ID
	private int[][] mVAOID;

	private float[] mMVPMatrix = new float[16 * Global.mvpArr];

	//タッチ計算用マトリックス
	private Matrix3D ThetaTemp;

	//シャッフル
	private boolean shuffling;
	Random rnd;

	private class shuArray {
		float distX;
		float distY;
		float distZ;
	}

	shuArray[] Sarray;

	//AutoMatic
	private boolean Auto;
	private boolean[] BanMove;

	public class Pos {
		float x;
		float y;
	}

	private Pos PosArr[];

	public DrawPuzzle() {
		creat = false;//初期化フラグ
		touching = false;
		AutoOff = false;
		shuffling = false;
		Auto = false;
		b_pcs = 0;
		drawCallNum = 0;//初回の描画ループ時パズル描画スキップの為
		drawBlockNum = 0;
		b_width = 4;

		ThetaTemp = new Matrix3D();
		ThetaTemp.MatrixIdentity();
		//Randomクラスのインスタンス化
		rnd = new Random();
	}

	public void CreateFlg(int b_w) {
		if (touching || shuffling || Auto || creat)
			return;
		b_widthTmp = b_w;
		creat = true;
	}

	private void Create(int poshandle, int uvhandle) {

		if (!creat)
			return;

		if (b_widthTmp != -1) //-1の場合数値そのまま
			b_width = b_widthTmp;

		int pcs = b_width * b_width;

		B_obj = new Block[pcs];
		B_Array = new int[pcs + 1];//スペース含む
		PosArr = new Pos[pcs + 1];
		BanMove = new boolean[pcs];
		b_size = PuzzleSize / (float) b_width;
		float adjust = (float) (b_width - 1) * 0.5f;

		//全ブロック数÷MVP配列数(全ブロック数 <= MVP配列数の場合1ループ1回のドローコールで済む)
		int pcs1 = pcs / Global.mvpArr;//pcs1==1ループのドローコール数(きりのいい数字のみなのでこれで問題無い,改造する場合計算式注意)
		if (pcs1 <= 0)
			pcs1 = 1;

		int bl = pcs;
		if (bl > Global.mvpArr)
			bl = Global.mvpArr;//bl== 1ドローコールで使うブロック数

		//1ループのドローコール数だけ生成
		mVBOID = new int[pcs1][2];
		mVAOID = new int[pcs1][1];

		int b_id = 0;
		//ブロック生成, 座標入力
		for (int y = 0; y < b_width; y++) {
			for (int x = 0; x < b_width; x++) {
				float yy = ((float) y - adjust) * b_size;
				float xx = ((float) x - adjust) * b_size;
				PosArr[y * b_width + x] = new Pos();
				PosArr[y * b_width + x].x = xx;
				PosArr[y * b_width + x].y = yy;
				B_obj[y * b_width + x] = new Block();
				B_obj[y * b_width + x].Create(b_size, b_width, xx, yy, 0.0f, x, y, b_id % bl);
				b_id++;
				if (x == b_width - 1 && y == b_width - 1) {
					spaceY = yy;
					spaceX = xx + b_size;
					PosArr[y * b_width + x + 1] = new Pos();
					PosArr[y * b_width + x + 1].x = spaceX;
					PosArr[y * b_width + x + 1].y = spaceY;
				}
			}
		}

		//1ループのドローコール数分, 各VBO,IBO生成
		for (int i = 0; i < pcs1; i++) {
			//1 Float(Int)Bufferで使う分の頂点数だけ生成
			float mVertices[] = new float[(12 + 16) * 6 * bl];
			int mIndexBf[] = new int[6 * 6 * bl];

			//1ドローコールで使用する頂点配列生成
			for (int i1 = 0; i1 < bl; i1++) {

				float v1[] = B_obj[bl * i + i1].getVertices();
				for (int k = 0; k < 168; k++)//3 * 4 * 6 + 4 * 4 * 6
					mVertices[168 * i1 + k] = v1[k];

				int ind[] = B_obj[bl * i + i1].getIndex();
				for (int k = 0; k < 6 * 6; k++)
					mIndexBf[6 * 6 * i1 + k] = ind[k];
			}

			//VBO, IBO
			GLES30.glGenBuffers(2, mVBOID[i], 0);
			GLES30.glGenVertexArrays(1, mVAOID[i], 0);

			Graphic.bindBufferObj(mVAOID[i][0], mVBOID[i][0], mVBOID[i][1], poshandle, uvhandle, 3, 4, mVertices,
					mIndexBf);
		}

		for (int i = 0; i < pcs; i++)
			B_Array[i] = i;
		B_Array[pcs] = -1;//スペースは-1
		spaceI = pcs;
		b_pcs = pcs;
		drawBlockNum = bl;
		drawCallNum = pcs1;
		creat = false;
	}

	//AutoMaticで使用
	private boolean swapPosWait(int dir) {
		int indSp = getIndexAroundSpace(dir);//getMoveFで使用するB_Arrayで使用
		if (indSp == -1)
			return false;
		int ind = B_Array[indSp];//swapPos内で変化するのでここで値取得
		if (BanMove[ind])
			return false;
		boolean ref = swapPos(dir);
		while (ref && B_obj[ind].getMoveF()) {
		}
		return ref;
	}

	private boolean swapPos(int dir) {
		float distX = 0.0f;
		float distY = 0.0f;
		int ind;

		ind = getIndexAroundSpace(dir);
		if (ind == -1)
			return false;//移動不可の場合false

		switch (dir) {
		case 0://スペースから上方向のブロック
			distY = -b_size;
			break;
		case 1://下方向
			distY = b_size;
			break;
		case 2://左方向
			distX = -b_size;
			break;
		case 3://右方向
			distX = b_size;
			break;
		}

		float x = PosArr[ind].x;
		float y = PosArr[ind].y;
		spaceX = x;
		spaceY = y;
		B_obj[B_Array[ind]].setDistancePos(x + distX, y + distY, 0.0f);
		B_Array[spaceI] = B_Array[ind];
		B_Array[ind] = -1;
		spaceI = ind;

		return true;
	}

	private int getIndexAroundSpace(int dir) {

		//b_width=4の場合の配列状態
		//16 15 14 13 12
		//   11 10  9  8
		//    7  6  5  4
		//    3  2  1  0
		int ind = -1;
		switch (dir) {
		case 0:
			//スペース上ブロック
			ind = spaceI + b_width;
			if (ind > b_pcs - 1)
				return -1;
			break;
		case 1:
			//スペース下ブロック
			ind = spaceI - b_width;
			if (ind < 0 || ind == b_pcs - b_width)
				return -1;
			break;
		case 2:
			//スペース左ブロック
			ind = spaceI + 1;
			if (ind > b_pcs || (ind % b_width) == 0 && ind != b_pcs)
				return -1;
			break;
		case 3:
			//スペース右ブロック
			ind = spaceI - 1;
			if (ind < 0 || (ind % b_width) == b_width - 1 && ind != b_pcs - 1)
				return -1;
			break;
		}

		return ind;
	}

	public void stopAuto() {
		if (Auto)
			AutoOff = true;
	}

	public void TouchInside(Matrix3D lpv, float dx, float dy, float mx, float my, float ux, float uy, long touchTime,
			boolean mov) {

		if (Auto)
			AutoOff = true;

		if (creat || shuffling || Auto) {
			return;
		}

		for (int i = 0; i < b_pcs; i++) {
			if (B_obj[i].getMoveF())
				return;
		}
		touching = true;

		//タッチ領域検査, スペース位置から上下左右の領域をタッチしているか調べる
		int ind[] = new int[4];

		for (int i = 0; i < 4; i++)
			ind[i] = getIndexAroundSpace(i);

		for (int i = 0; i < 4; i++) {
			if (ind[i] == -1)
				continue;
			if (B_obj[B_Array[ind[i]]].TouchInside(lpv, dx, dy)) {
				swapPos(i);
				break;
			}
		}

		touching = false;
	}

	private void setGap() {
		if (drawCallNum == 0)
			return;
		if (B_Array[b_pcs] == -1) {
			for (int i = 0; i < b_pcs; i++)
				B_obj[i].gapSwitch(false);
		} else {
			for (int i = 0; i < b_pcs; i++)
				B_obj[i].gapSwitch(true);
		}
	}

	public void draw(int mvphandle, int sthandle, float[] st, float[] viewMat, float[] projMat, int poshandle,
			int uvhandle, int texture, int sdNo) {

		Create(poshandle, uvhandle);
		setGap();

		for (int i = 0; i < drawCallNum; i++) {

			for (int i1 = 0; i1 < drawBlockNum; i1++) {

				Matrix.multiplyMM(mMVPMatrix, 16 * i1, viewMat, 0, B_obj[drawBlockNum * i + i1].getWorldMat().m, 0);
				Matrix.multiplyMM(mMVPMatrix, 16 * i1, projMat, 0, mMVPMatrix, 16 * i1);
				B_obj[drawBlockNum * i + i1].upDate();
			}

			Graphic.draw(mVAOID[i][0], texture, mvphandle, mMVPMatrix, sthandle, st, drawBlockNum * 4 * 6, drawBlockNum,
					sdNo);
		}
	}

	private void shuffleInit() {

		Sarray = new shuArray[b_pcs - 1];

		//現在の座標値,インデックス読み込み
		for (int i = 0; i < b_pcs + 1; i++) {
			if (i == spaceI || B_Array[i] == b_pcs - 1)
				continue;
			float x = PosArr[i].x;
			float y = PosArr[i].y;
			Sarray[B_Array[i]] = new shuArray();
			Sarray[B_Array[i]].distX = x;
			Sarray[B_Array[i]].distY = y;
			float ranz = rnd.nextInt(5);
			Sarray[B_Array[i]].distZ = ranz - 2.0f;
		}
		//シャッフル,座標移動先の座標値,インデックスシャッフル
		//偶数回ブロック座標の交換を行う,奇数回だと完成不可能となってしまう
		for (int i = 0; i < b_pcs * 2; i++) {//b_pcs * 2回シャッフルする

			int ran1 = rnd.nextInt(b_pcs + 1);
			int ran2 = rnd.nextInt(b_pcs + 1);
			if (ran1 == spaceI || ran2 == spaceI || B_Array[ran1] == b_pcs - 1 || B_Array[ran2] == b_pcs - 1
					|| ran1 == ran2) //スペースと,左上ブロックは動かさない
			{
				i--;//continueの場合デクリメントしておかないと奇数回シャッフルになる場合有
				continue;
			}
			float tx = Sarray[B_Array[ran1]].distX;
			Sarray[B_Array[ran1]].distX = Sarray[B_Array[ran2]].distX;
			Sarray[B_Array[ran2]].distX = tx;

			float ty = Sarray[B_Array[ran1]].distY;
			Sarray[B_Array[ran1]].distY = Sarray[B_Array[ran2]].distY;
			Sarray[B_Array[ran2]].distY = ty;

			int ind = B_Array[ran1];
			B_Array[ran1] = B_Array[ran2];
			B_Array[ran2] = ind;
		}
	}

	public void shuffle() {

		if (shuffling || Auto || creat || touching)
			return;
		shuffling = true;
		int shuNo = 0;
		int i = 0;
		while (true) {
			switch (shuNo) {
			case 0:
				shuffleInit();
				shuNo = 1;
				break;
			case 1:
				for (i = 0; i < b_pcs - 1; i++) {
					B_obj[i].setDistancePos(B_obj[i].getPosition().x, B_obj[i].getPosition().y, Sarray[i].distZ);
					B_obj[i].thetaOn();
				}
				B_obj[b_pcs - 1].thetaOn();
				shuNo = 2;
				break;
			case 2:
				for (i = 0; i < b_pcs - 1; i++) {
					if (B_obj[i].getMoveF())
						break;
				}
				if (i == b_pcs - 1)
					shuNo = 3;
				break;
			case 3:
				for (i = 0; i < b_pcs - 1; i++) {
					B_obj[i].setDistancePos(Sarray[i].distX, Sarray[i].distY, 0.0f);
				}
				shuNo = 4;
				break;
			case 4:
				for (i = 0; i < b_pcs - 1; i++) {
					if (B_obj[i].getMoveF())
						break;
				}
				if (i == b_pcs - 1) {
					shuffling = false;
					return;
				}
				break;
			}
		}
	}

	private void MoveSpace(int distInd) {

		//終了条件
		while (true) {
			if (AutoOff)
				return;
			//到達位置確認
			float x = B_obj[distInd].getPosition().x;
			float y = B_obj[distInd].getPosition().y;

			if ((spaceX == x && spaceY == y - b_size) || (spaceX == x + b_size && spaceY == y - b_size) || //移動先に隣接
					(spaceX == x + b_size && spaceY == y) || (spaceX == x + b_size && spaceY == y + b_size)
					|| (spaceX == x && spaceY == y + b_size) || (spaceX == x - b_size && spaceY == y + b_size)
					|| (spaceX == x - b_size && spaceY == y) || (spaceX == x - b_size && spaceY == y - b_size))
				break;

			boolean f = false;

			//スペースの右側にスペース移動先有り
			if (spaceX - x > 0) {
				f = swapPosWait(3);//スペース右移動(ブロック左移動)
				if (!f) {
					swapPosWait(0);//スペース上移動(ブロック下移動)
				}
				continue;
			}
			//スペースの左側にスペース移動先有り
			if (spaceX - x < 0) {
				swapPosWait(2);//スペース左移動(ブロック右移動)
				continue;
			}
			//スペースの下側にスペース移動先有り
			if (spaceY - y > 0) {
				f = swapPosWait(1);//スペース下移動(ブロック上移動)
				if (!f) {
					swapPosWait(2);//スペース左移動(ブロック右移動)
				}
				continue;
			}
			//スペースの上側にスペース移動先有り
			if (spaceY - y < 0) {
				swapPosWait(0);//スペース上移動(ブロック下移動)
				continue;
			}
		}
	}

	private void AutoRound(int sourceInd, int distInd) {

		boolean fr = false;//true:時計, false:反時計
		boolean movp = false;//回転方向切換フラグ
		float sx = B_obj[sourceInd].getPosition().x;
		float sy = B_obj[sourceInd].getPosition().y;
		float dx = PosArr[distInd].x;
		float dy = PosArr[distInd].y;

		if (sx == dx && sy == dy)
			return;

		MoveSpace(sourceInd);
		if (AutoOff)
			return;

		while (true) {
			sx = B_obj[sourceInd].getPosition().x;
			sy = B_obj[sourceInd].getPosition().y;

			if (sx == dx && sy == dy) //移動完
				break;

			//移動対象ブロックの下にスペース有り
			if (spaceX == sx && spaceY == sy - b_size) {
				if (sy - dy > 0) {//移動先が下の場合
					swapPosWait(0);//移動対象ブロック下移動
					continue;
				}
				if (!movp)
					fr = !fr;//移動未実施時,回転方向切換
				if (!fr)
					movp = swapPosWait(2);//回転方向分岐右
				else
					movp = swapPosWait(3);//回転方向分岐左
			}
			if (AutoOff)
				return;
			//移動対象ブロックの左下にスペース有り
			if (spaceX == sx + b_size && spaceY == sy - b_size) {
				if (!movp)
					fr = !fr;
				if (!fr)
					movp = swapPosWait(0);//回転方向分岐下
				else
					movp = swapPosWait(3);//回転方向分岐左
			}
			if (AutoOff)
				return;
			//移動対象ブロックの左にスペース有り
			if (spaceX == sx + b_size && spaceY == sy) {
				if (sx - dx < 0) {//移動先が左の場合
					swapPosWait(3);//移動対象ブロック左移動
					continue;
				}
				if (!movp)
					fr = !fr;
				if (!fr)
					movp = swapPosWait(0);//回転方向分岐下
				else
					movp = swapPosWait(1);//回転方向分岐上
			}
			if (AutoOff)
				return;
			//移動対象ブロックの左上にスペース有り
			if (spaceX == sx + b_size && spaceY == sy + b_size) {
				if (!movp)
					fr = !fr;
				if (!fr)
					movp = swapPosWait(3);//回転方向分岐左
				else
					movp = swapPosWait(1);//回転方向分岐上
			}
			if (AutoOff)
				return;
			//移動対象ブロックの上にスペース有り
			if (spaceX == sx && spaceY == sy + b_size) {
				if (sy - dy < 0) {//移動先が上の場合
					swapPosWait(1);//移動対象ブロック上移動
					continue;
				}
				if (!movp)
					fr = !fr;
				if (!fr)
					movp = swapPosWait(3);//回転方向分岐左
				else
					movp = swapPosWait(2);//回転方向分岐右
			}
			if (AutoOff)
				return;
			//移動対象ブロックの右上にスペース有り
			if (spaceX == sx - b_size && spaceY == sy + b_size) {
				if (!movp)
					fr = !fr;
				if (!fr)
					movp = swapPosWait(1);//回転方向分岐上
				else
					movp = swapPosWait(2);//回転方向分岐右
			}
			if (AutoOff)
				return;
			//移動対象ブロックの右にスペース有り
			if (spaceX == sx - b_size && spaceY == sy) {
				if (sx - dx > 0) {//移動先が右の場合
					swapPosWait(2);//移動対象ブロック右移動
					continue;
				}
				if (!movp)
					fr = !fr;
				if (!fr)
					movp = swapPosWait(1);//回転方向分岐上
				else
					movp = swapPosWait(0);//回転方向分岐下
			}
			if (AutoOff)
				return;
			//移動対象ブロックの右下にスペース有り
			if (spaceX == sx - b_size && spaceY == sy - b_size) {
				if (!movp)
					fr = !fr;
				if (!fr)
					movp = swapPosWait(2);//回転方向分岐右
				else
					movp = swapPosWait(0);//回転方向分岐下
			}
			if (AutoOff)
				return;
		}
	}

	private void AutoMaticSub(int range, int j) {

		int i;
		boolean ret = false;
		//下1列処理
		while (true) {

			//下1列完成判別
			for (i = j; i < range + j; i++) {//下1列チェック
				if (B_obj[i].getPosition().x != PosArr[i].x || B_obj[i].getPosition().y != PosArr[i].y)
					break;//値が一致の間繰り返し
			}
			if (i == range + j) {//全部一致したら移動禁止処理,break
				for (i = j; i < range + j; i++) {
					BanMove[i] = true;
				}
				if (range == 2) {
					swapPosWait(2);
					swapPosWait(2);
					ret = true;
					break;
				} //一致の場合この処理で終了
				break; //一致した場合while抜け
			}

			//↓範囲内下1列1回目処理 
			for (i = j + 1; i < range + j; i++) {
				AutoRound(i, i - 1);
				if (AutoOff)
					return;
				BanMove[i] = true; //移動禁止ON
			} //for終わり
			if (ret == true)
				break;
			//↓移動不可状態処理
			if (((B_obj[j].getPosition().x == PosArr[range + j - 1].x
					&& B_obj[j].getPosition().y == PosArr[range + j - 1].y)
					|| (B_obj[j].getPosition().x == PosArr[range + j - 1 + b_width].x
							&& B_obj[j].getPosition().y == PosArr[range + j - 1 + b_width].y
							&& spaceX == PosArr[range + j - 1].x && spaceY == PosArr[range + j - 1].y))) {
				for (i = j + 1; i < range + j; i++) {
					BanMove[i] = false;
				}
				AutoRound(j, range + j - 1 + b_width * 2);
				if (AutoOff)
					return;
				continue;//移動不可状態処理後最初に戻る

			}

			//↓範囲内下1列右1個処理
			AutoRound(j, j + b_width);
			if (AutoOff)
				return;
			BanMove[j] = true; //移動禁止ON
			//↓範囲内下1列2回目処理
			for (i = range + j - 1; i >= j + 1; i--) {
				BanMove[i] = false; //移動前移動禁止OFF
				AutoRound(i, i);
				if (AutoOff)
					return;
				BanMove[i] = true; //移動後移動禁止ON

			} //for終わり

			if (range == 2) {
				BanMove[j] = false;
				swapPosWait(0);
				swapPosWait(2);
				swapPosWait(2);
				ret = true;
				break;
			} //完了

			//↓範囲内下1列右1個処理
			BanMove[j] = false; //移動前移動禁止OFF
			AutoRound(j, j);
			if (AutoOff)
				return;
			BanMove[j] = true; //移動後移動禁止ON
			break;
			//while抜け
		} //下1列処理ループ終わり
		if (ret == true)
			return;

		j += b_width; //for文初期値更新
		range--; //範囲更新

		while (true) {//右1列処理ループ

			//右1列完成判別
			for (i = j; i <= j + b_width * (range - 1); i += b_width) {
				if (B_obj[i].getPosition().x != PosArr[i].x || B_obj[i].getPosition().y != PosArr[i].y)
					break;//値が一致の間繰り返し
			}
			if (i > j + b_width * (range - 1)) { //全部一致したら移動禁止処理,break
				for (i = j; i <= j + b_width * (range - 1); i += b_width) {
					BanMove[i] = true;
				}
				break;//一致した場合while抜け
			}

			//↓範囲内右1列1回目処理
			for (i = j + b_width; i <= j + b_width * (range - 1); i += b_width) {
				AutoRound(i, i - b_width);
				if (AutoOff)
					return;
				BanMove[i] = true; //移動禁止ON

			} //for終わり

			//↓移動不可状態処理
			if ((B_obj[j].getPosition().x == PosArr[j + b_width * (range - 1)].x
					&& B_obj[j].getPosition().y == PosArr[j + b_width * (range - 1)].y)
					|| (B_obj[j].getPosition().x == PosArr[j + b_width * (range - 1) + 1].x
							&& B_obj[j].getPosition().y == PosArr[j + b_width * (range - 1) + 1].y
							&& spaceX == PosArr[j + b_width * (range - 1)].x
							&& spaceY == PosArr[j + b_width * (range - 1)].y)) {
				for (i = j + b_width; i <= j + b_width * (range - 1); i += b_width) {
					BanMove[i] = false;
				}
				AutoRound(j, j + b_width * (range - 1) + 2);
				if (AutoOff)
					return;
				continue;//移動不可状態処理後最初に戻る
			}

			//↓範囲内右1列上1個処理
			AutoRound(j, j + 1);//中止の場合while抜け
			if (AutoOff)
				return;
			BanMove[j] = true; //移動禁止ON

			//↓範囲内右1列2回目処理
			for (i = j + b_width * (range - 1); i >= j + b_width; i -= b_width) {
				BanMove[i] = false; //移動禁止OFF
				AutoRound(i, i);
				if (AutoOff)
					return;
				BanMove[i] = true; //移動禁止ON

			} //for終わり

			//↓範囲内右1列上1個処理
			BanMove[j] = false; //移動禁止OFF
			AutoRound(j, j);
			if (AutoOff)
				return;
			BanMove[j] = true; //移動禁止ON

			break;//while抜け

		}
		//右1列処理ループ終わり

		j++; //for文初期値変更
		AutoMaticSub(range, j);
	}

	public void AutoMatic() {

		if (shuffling || Auto || creat || touching)
			return;
		Auto = true;
		//ブロック移動禁止判定初期化
		for (int i = 0; i < b_pcs; i++)
			BanMove[i] = false;

		AutoMaticSub(b_width, 0);

		//ブロック移動禁止判定全解除
		for (int i = 0; i < b_pcs; i++)
			BanMove[i] = false;
		Auto = false;
		AutoOff = false;
	}
}
