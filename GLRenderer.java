
package com.SlidePuzzle;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.content.Context;
import android.content.res.Resources;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.lang.System;
import android.media.MediaPlayer;
import android.graphics.SurfaceTexture;
import android.view.Surface;

public class GLRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

	private float[] mSTMatrix = new float[16];

	private int mRenderTexNo;
	private int mNekoTexNo;
	private int mStTexNo;
	private int mMovieTexNo;
	private int mBackTexNo;
	private int mNumTexNo;
	private Context mContext;
	private long prevtime;
	private long timefps;
	private int fps;
	private int frame;
	private boolean updateSurface = false;
	private MediaPlayer mMediaPlayer = null;
	private SurfaceTexture mSurface;

	private float fovy;//画角
	private float zNear;
	private float zFar;
	private float eyeX, eyeY, eyeZ;//視点
	private float centerX, centerY, centerZ;//注視点
	private float upX, upY, upZ;//上方向

	//タッチ計算用マトリックス
	private Matrix3D Look;//視点
	private Matrix3D viewPort;
	private Matrix3D Per;
	private Matrix3D LPv;
	private Shader sd;
	private int sdNo = 0;
	private DrawPuzzle pzl;
	private DrawBack db;
	private DrawNum num;

	public GLRenderer(Context context) {
		mContext = context;
		fovy = 45.0f;
		zNear = 1.0f;
		zFar = 70.0f;
		eyeX = 0.0f;
		eyeY = 0.0f;
		eyeZ = 20.0f;
		centerX = 0.0f;
		centerY = 0.0f;
		centerZ = 0.0f;
		upX = upZ = 0.0f;
		upY = 1.0f;
		prevtime = System.currentTimeMillis();
		frame = 0;
		timefps = System.currentTimeMillis();
		fps = 60;
		Look = new Matrix3D();
		Look.MatrixIdentity();
		viewPort = new Matrix3D();
		viewPort.MatrixIdentity();
		Per = new Matrix3D();
		Per.MatrixIdentity();
		LPv = new Matrix3D();
		LPv.MatrixIdentity();
		sd = new Shader();
		db = new DrawBack();
		pzl = new DrawPuzzle();
		num = new DrawNum();
	}

	//サーフェスが初めて作成された際・再作成された際に呼ばれる
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // 描画領域を黒色でクリア

		sd.createShader();

		Resources res = mContext.getResources();
		int resId1 = res.getIdentifier("nekokin", "drawable", mContext.getPackageName());
		int resId2 = res.getIdentifier("wall1", "drawable", mContext.getPackageName());
		int resId3 = res.getIdentifier("number_texture", "drawable", mContext.getPackageName());
		mNekoTexNo = Graphic.loadTexture(res, resId1);
		mBackTexNo = Graphic.loadTexture(res, resId2);
		mNumTexNo = Graphic.loadTexture(res, resId3);
		mRenderTexNo = mNekoTexNo;
	}

	public void CreatePuzzle(int pcs) {
		pzl.CreateFlg(pcs);
	}

	public void ResetPuzzle() {
		pzl.CreateFlg(-1);
	}

	public void Sufflie() {
		pzl.shuffle();
	}

	public void Auto() {
		pzl.AutoMatic();
	}

	public void AutoOff() {
		pzl.stopAuto();
	}

	private void getTime() {
		long currenttime = System.currentTimeMillis();
		Global.looptime = currenttime - prevtime;
		prevtime = currenttime;

		frame++;
		if (currenttime - timefps > 1000) {
			timefps = currenttime;
			fps = frame;
			frame = 0;
		}
		Global.timef = (float) Global.looptime * 0.05f;
	}

	// 画面回転時など、サーフェスが変更された際に呼ばれる
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		//DepthTest有効
		GLES30.glEnable(GLES30.GL_DEPTH_TEST);
		GLES30.glDepthFunc(GLES30.GL_LEQUAL);

		// スクリーンが変わり画角を変更する場合、射影行列を作り直す
		GLES30.glViewport(0, 0, width, height);

		//アルファブレンド有効
		GLES30.glEnable(GLES30.GL_BLEND);
		//合成アルゴリズム, これから描画する画像係数, すでに描画した画像係数
		GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);

		//カリングの有効化
		GLES30.glEnable(GLES30.GL_CULL_FACE);
		// 裏面を描画しない
		GLES30.glFrontFace(GLES30.GL_CCW);
		GLES30.glCullFace(GLES30.GL_BACK);

		Global.width = width;
		Global.height = height;

		Look.MatrixLookAtLH(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		viewPort.MatrixViewPort();
		Per.MatrixPerspectiveFovLH(fovy, Global.width / Global.height, zNear, zFar);
		Matrix3D tmp1 = new Matrix3D();
		tmp1.MatrixIdentity();
		tmp1.MatrixMultiply(Look, Per);
		LPv.MatrixMultiply(tmp1, viewPort);

		//テクスチャ生成
		//Global.TextureModeが0の場合デフォルトテクスチャを使用
		switch (Global.TextureMode) {
		case 0:
			Global.videoInit = false;
			mediaPlayerRelease();

			sdNo = 0;
			mRenderTexNo = mNekoTexNo;
			break;

		case 1:
			Global.videoInit = false;
			mediaPlayerRelease();

			sdNo = 0;
			if (Global.bmp != null) {
				TextureManager.deleteStorageTexture(0);
				mStTexNo = Graphic.loadStorageTexture(0);
				mRenderTexNo = mStTexNo;
			}
			break;

		case 2:
			MediaPlayerInit();
			break;
		}
	}

	synchronized public void onFrameAvailable(SurfaceTexture surface) {
		updateSurface = true;
	}

	private void mediaPlayerRelease() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	private void MediaPlayerInit() {
		if (Global.videoInit == false) {
			mediaPlayerRelease();
			mMediaPlayer = new MediaPlayer();
			try {
				mMediaPlayer.setDataSource(mContext, Global.videoUri);
				//再生準備、再生可能状態になるまでブロック
				mMediaPlayer.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			TextureManager.deleteStorageTexture(1);
			mMovieTexNo = Graphic.loadMovieTexture(1);
			mSurface = new SurfaceTexture(mMovieTexNo);
			mRenderTexNo = mMovieTexNo;
			mSurface.setOnFrameAvailableListener(this);

			Surface surface = new Surface(mSurface);
			mMediaPlayer.setSurface(surface);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			surface.release();

			try {
				mMediaPlayer.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}

			sdNo = 1;

			mMediaPlayer.start();

			Global.videoInit = true;
		}
	}

	public void releaseShader() {
		sd.stopProgram();
		sd.releaseProgram();
	}

	public void releaseMediaPlayer() {
		mediaPlayerRelease();
	}

	public void touched(float dx, float dy, float mx, float my, float ux, float uy, long touchTime, boolean mov) {
		pzl.TouchInside(LPv, dx, dy, mx, my, ux, uy, touchTime, mov);//openglの座標系に合わせる
	}

	// 新しいフレームを描画する度に呼ばれる
	@Override
	public void onDrawFrame(GL10 gl) {

		if (Global.TextureMode == 2 && Global.videoInit && updateSurface) {
			synchronized (this) {
				mSurface.updateTexImage();
				mSurface.getTransformMatrix(mSTMatrix);
				updateSurface = false;
			}
		}

		GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT); // バッファのクリア
		GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

		sd.startProgram(2);
		db.draw(sd.getMVPMatrixHandle(), sd.getSTMatrixHandle(), mSTMatrix, Look.m, Per.m, sd.getPositionHandle(),
				sd.getUvHandle(), mBackTexNo, 2);

		sd.startProgram(sdNo);
		pzl.draw(sd.getMVPMatrixHandle(), sd.getSTMatrixHandle(), mSTMatrix, Look.m, Per.m, sd.getPositionHandle(),
				sd.getUvHandle(), mRenderTexNo, sdNo);

		sd.startProgram(3);
		num.draw(fps, 2, 400.0f, 100.0f, sd.getTransformHandle(), sd.getUvwhHandle(), sd.getPositionHandle(),
				sd.getUvHandle(), mNumTexNo);

		getTime();
	}
}