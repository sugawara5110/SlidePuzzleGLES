
package com.SlidePuzzle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.graphics.Bitmap.Config;
import android.opengl.GLES30;

public class Graphic {

	public static final float[] createBlockVer(float siz) {

		float size = siz * 0.5f;
		float[] vertices = {
				// �O 
				-size, -size, size * 0.5f, size, -size, size * 0.5f, -size, size, size * 0.5f, size, size, size * 0.5f,
				// �� 
				-size, -size, -size * 0.5f, size, -size, -size * 0.5f, -size, size, -size * 0.5f, size, size,
				-size * 0.5f,
				// �� 
				-size, -size, size * 0.5f, -size, -size, -size * 0.5f, -size, size, size * 0.5f, -size, size,
				-size * 0.5f,
				// �E
				size, -size, size * 0.5f, size, -size, -size * 0.5f, size, size, size * 0.5f, size, size, -size * 0.5f,
				// �� 
				-size, size, size * 0.5f, size, size, size * 0.5f, -size, size, -size * 0.5f, size, size, -size * 0.5f,
				// �� 
				-size, -size, size * 0.5f, size, -size, size * 0.5f, -size, -size, -size * 0.5f, size, -size,
				-size * 0.5f, };

		return vertices;
	}

	public static final int[] createBlockInd(int b_id) {

		int id = b_id * 24;

		int[] index = { 0 + id, 2 + id, 1 + id, 1 + id, 2 + id, 3 + id, 4 + id, 5 + id, 6 + id, 5 + id, 7 + id, 6 + id,
				8 + id, 9 + id, 10 + id, 9 + id, 11 + id, 10 + id, 12 + id, 14 + id, 13 + id, 13 + id, 14 + id, 15 + id,
				16 + id, 18 + id, 17 + id, 17 + id, 18 + id, 19 + id, 20 + id, 21 + id, 22 + id, 21 + id, 23 + id,
				22 + id, };

		return index;
	}

	public static final float[] createBlockCoord(float tx, float ty, float b_id, float b_width) {

		float pitch = 1 / b_width;
		float xs = tx * pitch;
		float ys = ty * pitch;
		float xe = xs + pitch;
		float ye = ys + pitch;

		float ysr = 1.0f - ye;
		float yer = 1.0f - ys;

		float[] coords = {

				//0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, //����x,yr
				xs, yer, b_id, b_id, xe, yer, b_id, b_id, xs, ysr, b_id, b_id, xe, ysr, b_id, b_id,

				//0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, //����x,yr
				xs, yer, b_id, b_id, xe, yer, b_id, b_id, xs, ysr, b_id, b_id, xe, ysr, b_id, b_id,

				//0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, //����zr,yr
				0.0f, 1.0f, b_id, b_id, 1.0f, 1.0f, b_id, b_id, 0.0f, 0.0f, b_id, b_id, 1.0f, 0.0f, b_id, b_id,

				//0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, //�E��zr,yr
				0.0f, 1.0f, b_id, b_id, 1.0f, 1.0f, b_id, b_id, 0.0f, 0.0f, b_id, b_id, 1.0f, 0.0f, b_id, b_id,

				//0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, //���x,z
				0.0f, 1.0f, b_id, b_id, 1.0f, 1.0f, b_id, b_id, 0.0f, 0.0f, b_id, b_id, 1.0f, 0.0f, b_id, b_id,

				//0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, };//���x,z
				0.0f, 1.0f, b_id, b_id, 1.0f, 1.0f, b_id, b_id, 0.0f, 0.0f, b_id, b_id, 1.0f, 0.0f, b_id, b_id, };

		return coords;
	}

	public static final void bindBufferObj(int vao, int vbo, int ibo, int poshandle, int uvhandle, int verNum,
			int uvNum, float[] ver, int[] ind) {

		FloatBuffer polygonVertices = Graphic.makeFloatBuffer(ver);
		IntBuffer indexBf = Graphic.makeIntBuffer(ind);

		//VBO, IBO
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo);

		GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, ver.length * 4, polygonVertices, GLES30.GL_STATIC_DRAW);

		GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ibo);

		GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, ind.length * 4, indexBf, GLES30.GL_STATIC_DRAW);

		//VAO
		GLES30.glBindVertexArray(vao);

		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo);
		GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ibo);

		GLES30.glEnableVertexAttribArray(poshandle);
		GLES30.glEnableVertexAttribArray(uvhandle);

		GLES30.glVertexAttribPointer(poshandle, verNum, GLES30.GL_FLOAT, false, (verNum + uvNum) * 4, 0);

		GLES30.glVertexAttribPointer(uvhandle, uvNum, GLES30.GL_FLOAT, false, (verNum + uvNum) * 4, verNum * 4);

		GLES30.glBindVertexArray(0);
	}

	public static final void draw2D(int vaoID, int texture, int TransformHandle, float[] trans, int UvwhHandle,
			float[] uvwh) {

		GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);

		GLES30.glBindVertexArray(vaoID);

		GLES30.glUniform2f(TransformHandle, trans[0], trans[1]);
		GLES30.glUniform4f(UvwhHandle, uvwh[0], uvwh[1], uvwh[2], uvwh[3]);

		GLES30.glDrawElements(GLES30.GL_TRIANGLES, (int) (4 * 1.5), GLES30.GL_UNSIGNED_INT, 0);

		GLES30.glBindVertexArray(0);

		GLES30.glFinish();
	}

	public static final void draw(int vaoID, int texture, int MVPMatrixHandle, float[] mvp, int STMatrixHandle,
			float[] st, int ver, int unMatNum, int shaderNo) {

		int GL_TEXTURE_EXTERNAL_OES = 36197;

		//�e�N�X�`���L����
		GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
		if (shaderNo == 1) {
			//�e�N�X�`���I�u�W�F�N�g�̎w��
			GLES30.glBindTexture(GL_TEXTURE_EXTERNAL_OES, texture);
		} else {
			//�e�N�X�`���I�u�W�F�N�g�̎w��
			GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
		}

		GLES30.glBindVertexArray(vaoID);

		GLES30.glUniformMatrix4fv(MVPMatrixHandle, unMatNum, false, mvp, 0);
		if (shaderNo == 1)
			GLES30.glUniformMatrix4fv(STMatrixHandle, 1, false, st, 0);

		//�C���f�b�N�X�L��`��
		GLES30.glDrawElements(GLES30.GL_TRIANGLES, (int) (ver * 1.5), GLES30.GL_UNSIGNED_INT, 0);

		GLES30.glBindVertexArray(0);

		GLES30.glFinish();
	}

	public static final int loadTexture(Resources resources, int resId) {

		int[] textures = new int[1];

		//Bitmap�쐬
		Bitmap bmp = BitmapFactory.decodeResource(resources, resId, options);
		if (bmp == null)
			return 0;

		//openGL�p�e�N�X�`������
		GLES30.glGenTextures(1, textures, 0);//��������e�N�X�`����,�e�N�X�`��Id�z��,
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);
		GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bmp, 0);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

		//openGL�֓]������, VM��������ɍ쐬����bitmap�j��
		bmp.recycle();

		//TextureManager�ɓo�^
		TextureManager.addTexture(resId, textures[0]);

		return textures[0];
	}

	public static final int loadStorageTexture(int stexno) {

		int[] textures = new int[1];

		//openGL�p�e�N�X�`������
		GLES30.glGenTextures(1, textures, 0);//��������e�N�X�`����,�e�N�X�`��Id�z��,
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);
		GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, Global.bmp, 0);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

		//openGL�֓]������, VM��������ɍ쐬����bitmap�j��
		Global.bmp.recycle();
		Global.bmp = null;

		//TextureManager�ɓo�^
		TextureManager.addStorageTexture(stexno, textures[0]);

		return textures[0];
	}

	public static final int loadMovieTexture(int stexno) {

		int[] textures = new int[1];
		int GL_TEXTURE_EXTERNAL_OES = 36197;

		//openGL�p�e�N�X�`������
		GLES30.glGenTextures(1, textures, 0);//��������e�N�X�`����,�e�N�X�`��Id�z��,
		GLES30.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textures[0]);
		GLES30.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
		GLES30.glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
		//TextureManager�ɓo�^
		TextureManager.addStorageTexture(stexno, textures[0]);

		return textures[0];
	}

	private static final BitmapFactory.Options options = new BitmapFactory.Options();

	static {
		//���\�[�X�������T�C�Y����
		options.inScaled = false;
		//32bit�摜�Ƃ��ēǂݍ���
		options.inPreferredConfig = Config.ARGB_8888;
	}

	public static final IntBuffer makeIntBuffer(int[] arr) {
		//�V�X�e���������̈�m��
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		IntBuffer ib = bb.asIntBuffer();
		ib.put(arr);//�z��]��
		ib.position(0);
		return ib;
	}

	public static final FloatBuffer makeFloatBuffer(float[] arr) {
		//�V�X�e���������̈�m��
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);//�z��]��
		fb.position(0);
		return fb;
	}

	public static ByteBuffer makeByteBuffer(byte[] array) {
		ByteBuffer bb = ByteBuffer.allocateDirect(array.length).order(ByteOrder.nativeOrder());
		bb.put(array).position(0);
		return bb;
	}
}
