
package com.SlidePuzzle;

import java.util.Map;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import android.opengl.GLES30;

public class TextureManager {

	//�e�N�X�`���ێ�
	private static Map<Integer, Integer> mTextures = new Hashtable<Integer, Integer>();
	//�M�������[�摜�ێ�
	private static int[] mStorageTex = new int[10];

	//���[�h�����e�N�X�`���ǉ�
	public static final void addTexture(int resId, int texId) {
		mTextures.put(resId, texId);
	}

	//���[�h�����M�������[�e�N�X�`��
	public static final void addStorageTexture(int stexno, int texId) {
		mStorageTex[stexno] = texId;
	}

	//�e�N�X�`���폜
	public static final void deleteTexture(int resId) {
		if (mTextures.containsKey(resId)) {
			int[] texId = new int[1];
			texId[0] = mTextures.get(resId);
			GLES30.glDeleteTextures(1, texId, 0);
			mTextures.remove(resId);
		}
	}

	//�M�������[�e�N�X�`���폜
	public static final void deleteStorageTexture(int stexno) {
		if (mStorageTex[stexno] == -1)
			return;
		int[] texid = new int[1];
		texid[0] = mStorageTex[stexno];
		GLES30.glDeleteTextures(1, texid, 0);
		mStorageTex[stexno] = 0;
	}

	//�S�e�N�X�`���폜
	public static final void deleteAll() {
		List<Integer> keys = new ArrayList<Integer>(mTextures.keySet());
		for (Integer key : keys) {
			deleteTexture(key);
		}
	}

	//�S�M�������[�e�N�X�`���폜
	public static final void deleteStorageAll() {
		for (int i = 0; i < 10; i++)
			deleteStorageTexture(i);
	}
}
