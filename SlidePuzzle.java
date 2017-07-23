
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

	//�e�{�^��
	private Button[] Button = new Button[10];
	//�e�{�^�����C�A�E�g
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
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//�c�Œ�
		//fullscreenON
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//titlebarOFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// �[����OpenGL ES 3.0���T�|�[�g���Ă��邩�`�F�b�N
		if (configurationInfo.reqGlEsVersion >= 0x30000) {
			mGLView.setEGLContextClientVersion(3); // OpenGL�o�[�W������ݒ�
			mGLView.setRenderer(renderer); // �����_����ݒ�
		} else {
			return;
		}

		setContentView(mGLView);

		//�e�{�^���ݒ�
		//�X�^�[�g�{�^��
		LayoutButton("Start", 0, 0, 0, 0, 0);
		showButton(0);
		//�C�x���g�ǉ�
		this.Button[0].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideButton(0);

				for (int i = 1; i < 10; i++)
					showButton(i);

				renderer.CreatePuzzle(4);
			}
		});

		//���Z�b�g�{�^��
		LayoutButton("Reset", 1, 0, 350, 0, 0);
		//�C�x���g�ǉ�
		this.Button[1].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				renderer.ResetPuzzle();
			}
		});

		//3�{�^��
		LayoutButton("16", 2, -200, 400, 0, 0);
		//�C�x���g�ǉ�
		this.Button[2].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				renderer.CreatePuzzle(4);
			}
		});

		//4�{�^��
		LayoutButton("64", 3, -100, 400, 0, 0);
		//�C�x���g�ǉ�
		this.Button[3].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				renderer.CreatePuzzle(8);
			}
		});

		//5�{�^��
		LayoutButton("256", 4, 0, 400, 0, 0);
		//�C�x���g�ǉ�
		this.Button[4].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				renderer.CreatePuzzle(16);
			}
		});

		//6�{�^��
		LayoutButton("1024", 5, 100, 400, 0, 0);
		//�C�x���g�ǉ�
		this.Button[5].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				renderer.CreatePuzzle(32);
			}
		});

		//�V���b�t���{�^��
		LayoutButton("Sufflie", 6, 100, 350, 0, 0);
		//�C�x���g�ǉ�
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

		//�e�N�X�`���؂�ւ�(�摜)
		LayoutButton("image", 7, -100, 350, 0, 0);
		//�C�x���g�ǉ�
		this.Button[7].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				renderer.AutoOff();
				//ACTION_OPEN_DOCUMENT�̓A�v������h�L�������g �v���o�C�_�����L����h�L�������g�ւ́A�����Ԃ̌Œ�A�N�Z�X���\�ɂ���
				//ACTION_GET_CONTENT�̓A�v���Ńf�[�^�̓ǂݎ��ƃC���|�[�g�݂̂��s��
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

				//CATEGORY_OPENABLE ���C���e���g�ɒǉ�����ƁA���ʂ��t�B���^�����O����A
				//�摜�t�@�C���Ȃǂ̊J�����Ƃ��ł���h�L�������g�݂̂��\������܂�
				intent.addCategory(Intent.CATEGORY_OPENABLE);

				//intent.setType("image/*") �Ō��ʂ�����Ƀt�B���^�����O���A�摜 MIME �f�[�^�^�C�v�̃h�L�������g�݂̂�\�����܂��B
				intent.setType("image/*");

				startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
			}
		});

		//�e�N�X�`���؂�ւ�(����)
		LayoutButton("video", 8, -200, 350, 0, 0);
		//�C�x���g�ǉ�
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

		//����
		LayoutButton("Auto", 9, 200, 350, 0, 0);
		//�C�x���g�ǉ�
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

		//�v���R�[�hREAD_REQUEST_CODE�ƂƂ���ACTION_OPEN_DOCUMENT�C���e���g�����M����܂����B
		//�����ɕ\������Ă��郊�N�G�X�g�R�[�h����v���Ȃ��ꍇ�́A
		//���̈Ӑ}�ɑ΂��鉞���ł��B���̃R�[�h�͂܂��������s���Ȃ��ł��������B
		if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {
			//���[�U�[���I�����������́A�Ӑ}�ŕԂ���܂���B
			//����ɁA���̃h�L�������g�ւ�URI�́A���̃��\�b�h�Ƀp�����[�^�Ƃ��ĕԂ����C���e���g�Ɋ܂܂�܂��B
			//resultData.getData�i�j���g�p���Ă���URI���擾���܂��B
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

	//�{�^�����C�A�E�g
	private void LayoutButton(String str, int no, int left, int top, int right, int bottom) {
		//�{�^�����C�A�E�g
		params[no] = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params[no].gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		params[no].setMargins(left, top, right, bottom);
		//�{�^���̔z�u
		this.Button[no] = new Button(this);
		this.Button[no].setText(str);
		Button[no].setVisibility(View.GONE);//���������l�߂ď���,INVISIBLE���Ƃ��܂������Ȃ������炭�[���̏������x���̉e��
		addContentView(Button[no], params[no]);
	}

	//�X�^�[�g�{�^���\��
	public void showButton(int no) {
		Button[no].setVisibility(View.VISIBLE);//�\��
	}

	//�X�^�[�g�{�^����\��
	public void hideButton(int no) {
		Button[no].setVisibility(View.INVISIBLE);//����
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//���f�B�A�v���[���[���
		renderer.releaseMediaPlayer();
		//�V�F�[�_�[���
		renderer.releaseShader();
		//�e�N�X�`���폜
		TextureManager.deleteAll();
		TextureManager.deleteStorageAll();
	}
}
