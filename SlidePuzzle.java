
package com.SlidePuzzle;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.view.ViewGroup.LayoutParams;
import android.view.Gravity;
import android.view.View;
import java.lang.String;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.FileDescriptor;
import java.io.IOException;
import android.os.ParcelFileDescriptor;
import android.content.pm.ActivityInfo;

public class SlidePuzzle extends Activity {

	private GLView mGLView;
	private GLRenderer renderer;

	//各ボタン
	private Button[] Button = new Button[10];
	//各ボタンレイアウト
	private FrameLayout.LayoutParams[] params = new FrameLayout.LayoutParams[10];

	private static final int RESULT_PICK_IMAGEFILE = 1001;
	private static final int RESULT_PICK_VIDEOFILE = 1002;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGLView = new GLView(this);
		renderer = new GLRenderer(this);

		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//縦固定
		//fullscreenON
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//titlebarOFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 端末がOpenGL ES 3.0をサポートしているかチェック
		if (configurationInfo.reqGlEsVersion >= 0x30000) {
			mGLView.setEGLContextClientVersion(3); // OpenGLバージョンを設定
			mGLView.setRenderer(renderer); // レンダラを設定
		} else {
			return;
		}

		setContentView(mGLView);

		//各ボタン設定
		//スタートボタン
		LayoutButton("Start", 0, 0, 0, 0, 0);
		showButton(0);
		//イベント追加
		this.Button[0].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideButton(0);

				for (int i = 1; i < 10; i++)
					showButton(i);

				renderer.CreatePuzzle(4);
			}
		});

		//リセットボタン
		LayoutButton("Reset", 1, 0, 350, 0, 0);
		//イベント追加
		this.Button[1].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				renderer.ResetPuzzle();
			}
		});

		//3個ボタン
		LayoutButton("16", 2, -200, 400, 0, 0);
		//イベント追加
		this.Button[2].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				renderer.CreatePuzzle(4);
			}
		});

		//4個ボタン
		LayoutButton("64", 3, -100, 400, 0, 0);
		//イベント追加
		this.Button[3].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				renderer.CreatePuzzle(8);
			}
		});

		//5個ボタン
		LayoutButton("256", 4, 0, 400, 0, 0);
		//イベント追加
		this.Button[4].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				renderer.CreatePuzzle(16);
			}
		});

		//6個ボタン
		LayoutButton("1024", 5, 100, 400, 0, 0);
		//イベント追加
		this.Button[5].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				renderer.CreatePuzzle(32);
			}
		});

		//シャッフルボタン
		LayoutButton("Sufflie", 6, 100, 350, 0, 0);
		//イベント追加
		this.Button[6].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				(new Thread(new Runnable() {
					@Override
					public void run() {
						renderer.Sufflie();
					}
				})).start();
			}
		});

		//テクスチャ切り替え(画像)
		LayoutButton("image", 7, -100, 350, 0, 0);
		//イベント追加
		this.Button[7].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				renderer.AutoOff();
				//ACTION_OPEN_DOCUMENTはアプリからドキュメント プロバイダが所有するドキュメントへの、長期間の固定アクセスを可能にする
				//ACTION_GET_CONTENTはアプリでデータの読み取りとインポートのみを行う
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

				//CATEGORY_OPENABLE をインテントに追加すると、結果がフィルタリングされ、
				//画像ファイルなどの開くことができるドキュメントのみが表示されます
				intent.addCategory(Intent.CATEGORY_OPENABLE);

				//intent.setType("image/*") で結果をさらにフィルタリングし、画像 MIME データタイプのドキュメントのみを表示します。
				intent.setType("image/*");

				startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
			}
		});

		//テクスチャ切り替え(動画)
		LayoutButton("video", 8, -200, 350, 0, 0);
		//イベント追加
		this.Button[8].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				renderer.AutoOff();
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);

				intent.setType("video/*");

				startActivityForResult(intent, RESULT_PICK_VIDEOFILE);
			}
		});

		//自動
		LayoutButton("Auto", 9, 200, 350, 0, 0);
		//イベント追加
		this.Button[9].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				(new Thread(new Runnable() {
					@Override
					public void run() {
						renderer.Auto();
					}
				})).start();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

		//要求コードREAD_REQUEST_CODEとともにACTION_OPEN_DOCUMENTインテントが送信されました。
		//ここに表示されているリクエストコードが一致しない場合は、
		//他の意図に対する応答です。下のコードはまったく実行しないでください。
		if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {
			//ユーザーが選択した文書は、意図で返されません。
			//代わりに、そのドキュメントへのURIは、このメソッドにパラメータとして返されるインテントに含まれます。
			//resultData.getData（）を使用してそのURIを取得します。
			Uri uri = null;
			if (resultData != null) {
				uri = resultData.getData();
				Log.i("", "Uri: " + uri.toString());

				try {
					Global.bmp = getBitmapFromUri(uri);
					Global.TextureMode = 1;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		if (requestCode == RESULT_PICK_VIDEOFILE && resultCode == Activity.RESULT_OK) {
			if (resultData != null) {
				Global.videoUri = resultData.getData();
				Global.TextureMode = 2;
				Global.videoInit = false;
			}
		}
	}

	private Bitmap getBitmapFromUri(Uri uri) throws IOException {
		ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
		FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
		Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
		parcelFileDescriptor.close();
		Bitmap image2 = Bitmap.createScaledBitmap(image, 512, 512, false);
		image.recycle();
		return image2;
	}

	//ボタンレイアウト
	private void LayoutButton(String str, int no, int left, int top, int right, int bottom) {
		//ボタンレイアウト
		params[no] = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params[no].gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		params[no].setMargins(left, top, right, bottom);
		//ボタンの配置
		this.Button[no] = new Button(this);
		this.Button[no].setText(str);
		Button[no].setVisibility(View.GONE);//メモリを詰めて消す,INVISIBLEだとうまくいかないおそらく端末の処理速度差の影響
		addContentView(Button[no], params[no]);
	}

	//スタートボタン表示
	public void showButton(int no) {
		Button[no].setVisibility(View.VISIBLE);//表示
	}

	//スタートボタン非表示
	public void hideButton(int no) {
		Button[no].setVisibility(View.INVISIBLE);//消す
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//メディアプレーヤー解放
		renderer.releaseMediaPlayer();
		//シェーダー解放
		renderer.releaseShader();
		//テクスチャ削除
		TextureManager.deleteAll();
		TextureManager.deleteStorageAll();
	}
}
