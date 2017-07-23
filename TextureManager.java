
package com.SlidePuzzle;

import java.util.Map;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import android.opengl.GLES30;

public class TextureManager {

	//テクスチャ保持
	private static Map<Integer, Integer> mTextures = new Hashtable<Integer, Integer>();
	//ギャラリー画像保持
	private static int[] mStorageTex = new int[10];

	//ロードしたテクスチャ追加
	public static final void addTexture(int resId, int texId) {
		mTextures.put(resId, texId);
	}

	//ロードしたギャラリーテクスチャ
	public static final void addStorageTexture(int stexno, int texId) {
		mStorageTex[stexno] = texId;
	}

	//テクスチャ削除
	public static final void deleteTexture(int resId) {
		if (mTextures.containsKey(resId)) {
			int[] texId = new int[1];
			texId[0] = mTextures.get(resId);
			GLES30.glDeleteTextures(1, texId, 0);
			mTextures.remove(resId);
		}
	}

	//ギャラリーテクスチャ削除
	public static final void deleteStorageTexture(int stexno) {
		if (mStorageTex[stexno] == -1)
			return;
		int[] texid = new int[1];
		texid[0] = mStorageTex[stexno];
		GLES30.glDeleteTextures(1, texid, 0);
		mStorageTex[stexno] = 0;
	}

	//全テクスチャ削除
	public static final void deleteAll() {
		List<Integer> keys = new ArrayList<Integer>(mTextures.keySet());
		for (Integer key : keys) {
			deleteTexture(key);
		}
	}

	//全ギャラリーテクスチャ削除
	public static final void deleteStorageAll() {
		for (int i = 0; i < 10; i++)
			deleteStorageTexture(i);
	}
}
