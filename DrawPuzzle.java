
package com.SlidePuzzle;

import android.opengl.Matrix;
import java.util.Random;
import android.opengl.GLES30;

class DrawPuzzle {

	private final float PuzzleSize = 6.0f;

	private int b_width;//1�ӂ̃u���b�N��
	private int b_pcs;//�u���b�N��
	private int drawCallNum;
	private int drawBlockNum;
	private float b_size;
	private boolean creat;
	private int b_widthTmp;
	private boolean touching;
	private boolean AutoOff;

	private int B_Array[];//�u���b�N�z��C���f�b�N�X(�X�y�[�X���܂߂�)
	private Block[] B_obj;//�e�u���b�Nobj
	private int spaceI;//�X�y�[�X�C���f�b�N�X
	private float spaceX, spaceY;

	//VBO ID
	private int[][] mVBOID;
	//VAO ID
	private int[][] mVAOID;

	private float[] mMVPMatrix = new float[16 * Global.mvpArr];

	//�^�b�`�v�Z�p�}�g���b�N�X
	private Matrix3D ThetaTemp;

	//�V���b�t��
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
		creat = false;//�������t���O
		touching = false;
		AutoOff = false;
		shuffling = false;
		Auto = false;
		b_pcs = 0;
		drawCallNum = 0;//����̕`�惋�[�v���p�Y���`��X�L�b�v�̈�
		drawBlockNum = 0;
		b_width = 4;

		ThetaTemp = new Matrix3D();
		ThetaTemp.MatrixIdentity();
		//Random�N���X�̃C���X�^���X��
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

		if (b_widthTmp != -1) //-1�̏ꍇ���l���̂܂�
			b_width = b_widthTmp;

		int pcs = b_width * b_width;

		B_obj = new Block[pcs];
		B_Array = new int[pcs + 1];//�X�y�[�X�܂�
		PosArr = new Pos[pcs + 1];
		BanMove = new boolean[pcs];
		b_size = PuzzleSize / (float) b_width;
		float adjust = (float) (b_width - 1) * 0.5f;

		//�S�u���b�N����MVP�z��(�S�u���b�N�� <= MVP�z�񐔂̏ꍇ1���[�v1��̃h���[�R�[���ōς�)
		int pcs1 = pcs / Global.mvpArr;//pcs1==1���[�v�̃h���[�R�[����(����̂��������݂̂Ȃ̂ł���Ŗ�薳��,��������ꍇ�v�Z������)
		if (pcs1 <= 0)
			pcs1 = 1;

		int bl = pcs;
		if (bl > Global.mvpArr)
			bl = Global.mvpArr;//bl== 1�h���[�R�[���Ŏg���u���b�N��

		//1���[�v�̃h���[�R�[������������
		mVBOID = new int[pcs1][2];
		mVAOID = new int[pcs1][1];

		int b_id = 0;
		//�u���b�N����, ���W����
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

		//1���[�v�̃h���[�R�[������, �eVBO,IBO����
		for (int i = 0; i < pcs1; i++) {
			//1 Float(Int)Buffer�Ŏg�����̒��_����������
			float mVertices[] = new float[(12 + 16) * 6 * bl];
			int mIndexBf[] = new int[6 * 6 * bl];

			//1�h���[�R�[���Ŏg�p���钸�_�z�񐶐�
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
		B_Array[pcs] = -1;//�X�y�[�X��-1
		spaceI = pcs;
		b_pcs = pcs;
		drawBlockNum = bl;
		drawCallNum = pcs1;
		creat = false;
	}

	//AutoMatic�Ŏg�p
	private boolean swapPosWait(int dir) {
		int indSp = getIndexAroundSpace(dir);//getMoveF�Ŏg�p����B_Array�Ŏg�p
		if (indSp == -1)
			return false;
		int ind = B_Array[indSp];//swapPos���ŕω�����̂ł����Œl�擾
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
			return false;//�ړ��s�̏ꍇfalse

		switch (dir) {
		case 0://�X�y�[�X���������̃u���b�N
			distY = -b_size;
			break;
		case 1://������
			distY = b_size;
			break;
		case 2://������
			distX = -b_size;
			break;
		case 3://�E����
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

		//b_width=4�̏ꍇ�̔z����
		//16 15 14 13 12
		//   11 10  9  8
		//    7  6  5  4
		//    3  2  1  0
		int ind = -1;
		switch (dir) {
		case 0:
			//�X�y�[�X��u���b�N
			ind = spaceI + b_width;
			if (ind > b_pcs - 1)
				return -1;
			break;
		case 1:
			//�X�y�[�X���u���b�N
			ind = spaceI - b_width;
			if (ind < 0 || ind == b_pcs - b_width)
				return -1;
			break;
		case 2:
			//�X�y�[�X���u���b�N
			ind = spaceI + 1;
			if (ind > b_pcs || (ind % b_width) == 0 && ind != b_pcs)
				return -1;
			break;
		case 3:
			//�X�y�[�X�E�u���b�N
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

		//�^�b�`�̈挟��, �X�y�[�X�ʒu����㉺���E�̗̈���^�b�`���Ă��邩���ׂ�
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

		//���݂̍��W�l,�C���f�b�N�X�ǂݍ���
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
		//�V���b�t��,���W�ړ���̍��W�l,�C���f�b�N�X�V���b�t��
		//������u���b�N���W�̌������s��,��񂾂Ɗ����s�\�ƂȂ��Ă��܂�
		for (int i = 0; i < b_pcs * 2; i++) {//b_pcs * 2��V���b�t������

			int ran1 = rnd.nextInt(b_pcs + 1);
			int ran2 = rnd.nextInt(b_pcs + 1);
			if (ran1 == spaceI || ran2 == spaceI || B_Array[ran1] == b_pcs - 1 || B_Array[ran2] == b_pcs - 1
					|| ran1 == ran2) //�X�y�[�X��,����u���b�N�͓������Ȃ�
			{
				i--;//continue�̏ꍇ�f�N�������g���Ă����Ȃ��Ɗ��V���b�t���ɂȂ�ꍇ�L
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

		//�I������
		while (true) {
			if (AutoOff)
				return;
			//���B�ʒu�m�F
			float x = B_obj[distInd].getPosition().x;
			float y = B_obj[distInd].getPosition().y;

			if ((spaceX == x && spaceY == y - b_size) || (spaceX == x + b_size && spaceY == y - b_size) || //�ړ���ɗא�
					(spaceX == x + b_size && spaceY == y) || (spaceX == x + b_size && spaceY == y + b_size)
					|| (spaceX == x && spaceY == y + b_size) || (spaceX == x - b_size && spaceY == y + b_size)
					|| (spaceX == x - b_size && spaceY == y) || (spaceX == x - b_size && spaceY == y - b_size))
				break;

			boolean f = false;

			//�X�y�[�X�̉E���ɃX�y�[�X�ړ���L��
			if (spaceX - x > 0) {
				f = swapPosWait(3);//�X�y�[�X�E�ړ�(�u���b�N���ړ�)
				if (!f) {
					swapPosWait(0);//�X�y�[�X��ړ�(�u���b�N���ړ�)
				}
				continue;
			}
			//�X�y�[�X�̍����ɃX�y�[�X�ړ���L��
			if (spaceX - x < 0) {
				swapPosWait(2);//�X�y�[�X���ړ�(�u���b�N�E�ړ�)
				continue;
			}
			//�X�y�[�X�̉����ɃX�y�[�X�ړ���L��
			if (spaceY - y > 0) {
				f = swapPosWait(1);//�X�y�[�X���ړ�(�u���b�N��ړ�)
				if (!f) {
					swapPosWait(2);//�X�y�[�X���ړ�(�u���b�N�E�ړ�)
				}
				continue;
			}
			//�X�y�[�X�̏㑤�ɃX�y�[�X�ړ���L��
			if (spaceY - y < 0) {
				swapPosWait(0);//�X�y�[�X��ړ�(�u���b�N���ړ�)
				continue;
			}
		}
	}

	private void AutoRound(int sourceInd, int distInd) {

		boolean fr = false;//true:���v, false:�����v
		boolean movp = false;//��]�����؊��t���O
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

			if (sx == dx && sy == dy) //�ړ���
				break;

			//�ړ��Ώۃu���b�N�̉��ɃX�y�[�X�L��
			if (spaceX == sx && spaceY == sy - b_size) {
				if (sy - dy > 0) {//�ړ��悪���̏ꍇ
					swapPosWait(0);//�ړ��Ώۃu���b�N���ړ�
					continue;
				}
				if (!movp)
					fr = !fr;//�ړ������{��,��]�����؊�
				if (!fr)
					movp = swapPosWait(2);//��]��������E
				else
					movp = swapPosWait(3);//��]��������
			}
			if (AutoOff)
				return;
			//�ړ��Ώۃu���b�N�̍����ɃX�y�[�X�L��
			if (spaceX == sx + b_size && spaceY == sy - b_size) {
				if (!movp)
					fr = !fr;
				if (!fr)
					movp = swapPosWait(0);//��]��������
				else
					movp = swapPosWait(3);//��]��������
			}
			if (AutoOff)
				return;
			//�ړ��Ώۃu���b�N�̍��ɃX�y�[�X�L��
			if (spaceX == sx + b_size && spaceY == sy) {
				if (sx - dx < 0) {//�ړ��悪���̏ꍇ
					swapPosWait(3);//�ړ��Ώۃu���b�N���ړ�
					continue;
				}
				if (!movp)
					fr = !fr;
				if (!fr)
					movp = swapPosWait(0);//��]��������
				else
					movp = swapPosWait(1);//��]���������
			}
			if (AutoOff)
				return;
			//�ړ��Ώۃu���b�N�̍���ɃX�y�[�X�L��
			if (spaceX == sx + b_size && spaceY == sy + b_size) {
				if (!movp)
					fr = !fr;
				if (!fr)
					movp = swapPosWait(3);//��]��������
				else
					movp = swapPosWait(1);//��]���������
			}
			if (AutoOff)
				return;
			//�ړ��Ώۃu���b�N�̏�ɃX�y�[�X�L��
			if (spaceX == sx && spaceY == sy + b_size) {
				if (sy - dy < 0) {//�ړ��悪��̏ꍇ
					swapPosWait(1);//�ړ��Ώۃu���b�N��ړ�
					continue;
				}
				if (!movp)
					fr = !fr;
				if (!fr)
					movp = swapPosWait(3);//��]��������
				else
					movp = swapPosWait(2);//��]��������E
			}
			if (AutoOff)
				return;
			//�ړ��Ώۃu���b�N�̉E��ɃX�y�[�X�L��
			if (spaceX == sx - b_size && spaceY == sy + b_size) {
				if (!movp)
					fr = !fr;
				if (!fr)
					movp = swapPosWait(1);//��]���������
				else
					movp = swapPosWait(2);//��]��������E
			}
			if (AutoOff)
				return;
			//�ړ��Ώۃu���b�N�̉E�ɃX�y�[�X�L��
			if (spaceX == sx - b_size && spaceY == sy) {
				if (sx - dx > 0) {//�ړ��悪�E�̏ꍇ
					swapPosWait(2);//�ړ��Ώۃu���b�N�E�ړ�
					continue;
				}
				if (!movp)
					fr = !fr;
				if (!fr)
					movp = swapPosWait(1);//��]���������
				else
					movp = swapPosWait(0);//��]��������
			}
			if (AutoOff)
				return;
			//�ړ��Ώۃu���b�N�̉E���ɃX�y�[�X�L��
			if (spaceX == sx - b_size && spaceY == sy - b_size) {
				if (!movp)
					fr = !fr;
				if (!fr)
					movp = swapPosWait(2);//��]��������E
				else
					movp = swapPosWait(0);//��]��������
			}
			if (AutoOff)
				return;
		}
	}

	private void AutoMaticSub(int range, int j) {

		int i;
		boolean ret = false;
		//��1�񏈗�
		while (true) {

			//��1�񊮐�����
			for (i = j; i < range + j; i++) {//��1��`�F�b�N
				if (B_obj[i].getPosition().x != PosArr[i].x || B_obj[i].getPosition().y != PosArr[i].y)
					break;//�l����v�̊ԌJ��Ԃ�
			}
			if (i == range + j) {//�S����v������ړ��֎~����,break
				for (i = j; i < range + j; i++) {
					BanMove[i] = true;
				}
				if (range == 2) {
					swapPosWait(2);
					swapPosWait(2);
					ret = true;
					break;
				} //��v�̏ꍇ���̏����ŏI��
				break; //��v�����ꍇwhile����
			}

			//���͈͓���1��1��ڏ��� 
			for (i = j + 1; i < range + j; i++) {
				AutoRound(i, i - 1);
				if (AutoOff)
					return;
				BanMove[i] = true; //�ړ��֎~ON
			} //for�I���
			if (ret == true)
				break;
			//���ړ��s��ԏ���
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
				continue;//�ړ��s��ԏ�����ŏ��ɖ߂�

			}

			//���͈͓���1��E1����
			AutoRound(j, j + b_width);
			if (AutoOff)
				return;
			BanMove[j] = true; //�ړ��֎~ON
			//���͈͓���1��2��ڏ���
			for (i = range + j - 1; i >= j + 1; i--) {
				BanMove[i] = false; //�ړ��O�ړ��֎~OFF
				AutoRound(i, i);
				if (AutoOff)
					return;
				BanMove[i] = true; //�ړ���ړ��֎~ON

			} //for�I���

			if (range == 2) {
				BanMove[j] = false;
				swapPosWait(0);
				swapPosWait(2);
				swapPosWait(2);
				ret = true;
				break;
			} //����

			//���͈͓���1��E1����
			BanMove[j] = false; //�ړ��O�ړ��֎~OFF
			AutoRound(j, j);
			if (AutoOff)
				return;
			BanMove[j] = true; //�ړ���ړ��֎~ON
			break;
			//while����
		} //��1�񏈗����[�v�I���
		if (ret == true)
			return;

		j += b_width; //for�������l�X�V
		range--; //�͈͍X�V

		while (true) {//�E1�񏈗����[�v

			//�E1�񊮐�����
			for (i = j; i <= j + b_width * (range - 1); i += b_width) {
				if (B_obj[i].getPosition().x != PosArr[i].x || B_obj[i].getPosition().y != PosArr[i].y)
					break;//�l����v�̊ԌJ��Ԃ�
			}
			if (i > j + b_width * (range - 1)) { //�S����v������ړ��֎~����,break
				for (i = j; i <= j + b_width * (range - 1); i += b_width) {
					BanMove[i] = true;
				}
				break;//��v�����ꍇwhile����
			}

			//���͈͓��E1��1��ڏ���
			for (i = j + b_width; i <= j + b_width * (range - 1); i += b_width) {
				AutoRound(i, i - b_width);
				if (AutoOff)
					return;
				BanMove[i] = true; //�ړ��֎~ON

			} //for�I���

			//���ړ��s��ԏ���
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
				continue;//�ړ��s��ԏ�����ŏ��ɖ߂�
			}

			//���͈͓��E1���1����
			AutoRound(j, j + 1);//���~�̏ꍇwhile����
			if (AutoOff)
				return;
			BanMove[j] = true; //�ړ��֎~ON

			//���͈͓��E1��2��ڏ���
			for (i = j + b_width * (range - 1); i >= j + b_width; i -= b_width) {
				BanMove[i] = false; //�ړ��֎~OFF
				AutoRound(i, i);
				if (AutoOff)
					return;
				BanMove[i] = true; //�ړ��֎~ON

			} //for�I���

			//���͈͓��E1���1����
			BanMove[j] = false; //�ړ��֎~OFF
			AutoRound(j, j);
			if (AutoOff)
				return;
			BanMove[j] = true; //�ړ��֎~ON

			break;//while����

		}
		//�E1�񏈗����[�v�I���

		j++; //for�������l�ύX
		AutoMaticSub(range, j);
	}

	public void AutoMatic() {

		if (shuffling || Auto || creat || touching)
			return;
		Auto = true;
		//�u���b�N�ړ��֎~���菉����
		for (int i = 0; i < b_pcs; i++)
			BanMove[i] = false;

		AutoMaticSub(b_width, 0);

		//�u���b�N�ړ��֎~����S����
		for (int i = 0; i < b_pcs; i++)
			BanMove[i] = false;
		Auto = false;
		AutoOff = false;
	}
}
