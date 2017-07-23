
package com.SlidePuzzle;

import android.opengl.GLSurfaceView;
import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.util.Log;

public class GLView extends GLSurfaceView {

	private float dX, dY, mX, mY, uX, uY;
	private long touchTime;

	//レンダラーインスタンスを保持しレンダラーメソッドを使用できるようにする
	private GLRenderer renderer;

	public GLView(Context context) {
		super(context);
		//タッチイベント取得可能にする
		setFocusable(true);
		dX = dY = mX = mY = uX = uY = 0.0f;
	}

	@Override
	public void setRenderer(Renderer rend) {
		super.setRenderer(rend);
		this.renderer = (GLRenderer) rend;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		super.surfaceChanged(holder, format, w, h);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		boolean mov = false;
		Log.i(getClass().toString(), "Touch!");
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			dX = event.getX();
			dY = event.getY();
			Log.i(getClass().toString(), "ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			mX = event.getX();
			mY = event.getY();
			Log.i(getClass().toString(), "ACTION_MOVE");
			Log.i(getClass().toString(), String.format("TX = %f TY = %f", mX, mY));
			break;
		case MotionEvent.ACTION_UP:
			touchTime = event.getEventTime() - event.getDownTime();//EventTimeイベント発生時間 - DownTimeダウンイベント発生時間
			uX = event.getX();
			uY = event.getY();
			mov = true;
			Log.i(getClass().toString(), "ACTION_UP");
			break;
		}

		renderer.touched(dX, dY, mX, mY, uX, uY, touchTime, mov);

		return true;//falseだとACTION_DOWN以降は実行されない
	}
}
