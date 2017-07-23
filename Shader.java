
package com.SlidePuzzle;

import android.opengl.GLES30;

public class Shader {

	private int mProgramHandle[] = new int[4];
	private int mMVPMatrixHandle[] = new int[4];// u_MVPMatrixのハンドル
	private int mSTMatrixHandle[] = new int[4];
	private int mPositionHandle[] = new int[4];// a_Positionのハンドル
	private int mUvHandle[] = new int[4];
	private int mTransformHandle;
	private int mUvwhHandle;

	private int ind = 0;

	public void createShader() {
		createNormalShader();
		createMovieShader();
		createBackShader();
		create2DShader();
	}

	private void createNormalShader() {
		ind = 0;
		createShader(ShaderSource.vertexShader, ShaderSource.fragmentShader);
		mMVPMatrixHandle[0] = GLES30.glGetUniformLocation(mProgramHandle[0], "u_MVPMatrix");
		mSTMatrixHandle[0] = 0;
	}

	private void createMovieShader() {
		ind = 1;
		createShader(ShaderSource.vertexShaderMov, ShaderSource.fragmentShaderMov);
		mMVPMatrixHandle[1] = GLES30.glGetUniformLocation(mProgramHandle[1], "u_MVPMatrix");
		mSTMatrixHandle[1] = GLES30.glGetUniformLocation(mProgramHandle[1], "u_STMatrix");
	}

	private void createBackShader() {
		ind = 2;
		createShader(ShaderSource.vertexShaderBk, ShaderSource.fragmentShaderBk);
		mMVPMatrixHandle[2] = GLES30.glGetUniformLocation(mProgramHandle[2], "u_MVPMatrix");
		mSTMatrixHandle[2] = 0;
	}

	private void create2DShader() {
		ind = 3;
		createShader(ShaderSource.vertexShader2D, ShaderSource.fragmentShader2D);
		mTransformHandle = GLES30.glGetUniformLocation(mProgramHandle[3], "u_Transform");
		mUvwhHandle = GLES30.glGetUniformLocation(mProgramHandle[3], "u_UvWH");
	}

	public void startProgram(int Ind) {
		//シェーダプログラム適用
		ind = Ind;
		GLES30.glUseProgram(mProgramHandle[ind]);
	}

	public int getTransformHandle() {
		return mTransformHandle;
	}

	public int getUvwhHandle() {
		return mUvwhHandle;
	}

	public int getProgramHandle() {
		return mProgramHandle[ind];
	}

	public int getMVPMatrixHandle() {
		return mMVPMatrixHandle[ind];
	}

	public int getSTMatrixHandle() {
		return mSTMatrixHandle[ind];
	}

	public int getPositionHandle() {
		return mPositionHandle[ind];
	}

	public int getUvHandle() {
		return mUvHandle[ind];
	}

	public void stopProgram() {
		//シェーダー停止
		GLES30.glUseProgram(0);
	}

	public void releaseProgram() {
		//プログラム解放
		GLES30.glDeleteProgram(mProgramHandle[0]);
		GLES30.glDeleteProgram(mProgramHandle[1]);
		GLES30.glDeleteProgram(mProgramHandle[2]);
		GLES30.glDeleteProgram(mProgramHandle[3]);
	}

	private void createShader(String vs, String fs) {
		//バーテックスシェーダをコンパイル
		int vertexShaderHandle = compileShader(GLES30.GL_VERTEX_SHADER, vs);
		//フラグメントシェーダをコンパイル
		int fragmentShaderHandle = compileShader(GLES30.GL_FRAGMENT_SHADER, fs);
		//シェーダプログラムをリンク
		mProgramHandle[ind] = AttachShader(vertexShaderHandle, fragmentShaderHandle);

		//シェーダーリンク完了したらシェーダーオブジェクト解放
		GLES30.glDeleteShader(vertexShaderHandle);
		GLES30.glDeleteShader(fragmentShaderHandle);

		// ハンドル(ポインタ)の取得
		mPositionHandle[ind] = GLES30.glGetAttribLocation(mProgramHandle[ind], "a_Position");
		mUvHandle[ind] = GLES30.glGetAttribLocation(mProgramHandle[ind], "a_Uv");
	}

	private int AttachShader(int vsHandle, int fsHandle) {
		//シェーダプログラムをリンク
		int progHandle = GLES30.glCreateProgram();
		if (progHandle != 0) {
			GLES30.glAttachShader(progHandle, vsHandle); // バーテックスシェーダをアタッチ
			GLES30.glAttachShader(progHandle, fsHandle); // フラグメントシェーダをアタッチ
			GLES30.glBindAttribLocation(progHandle, 0, "a_Position"); // attributeのindexを設定
			GLES30.glBindAttribLocation(progHandle, 1, "a_Uv"); // attributeのindexを設定
			GLES30.glLinkProgram(progHandle); // バーテックスシェーダとフラグメントシェーダをプログラムへリンク

			// リンク結果のチェック
			final int[] linkStatus = new int[1];
			GLES30.glGetProgramiv(progHandle, GLES30.GL_LINK_STATUS, linkStatus, 0);

			if (linkStatus[0] == 0) {
				// リンク失敗
				GLES30.glDeleteProgram(progHandle);
				progHandle = 0;
			}
		}
		if (progHandle == 0) {
			throw new RuntimeException("Error creating program.");
		}
		return progHandle;
	}

	private int compileShader(int shaderType, String src) {
		int shaderHandle = GLES30.glCreateShader(shaderType);
		if (shaderHandle != 0) {
			GLES30.glShaderSource(shaderHandle, src); // シェーダソースを送信し
			GLES30.glCompileShader(shaderHandle); // コンパイル

			// コンパイル結果のチェック
			final int[] compileStatus = new int[1];
			GLES30.glGetShaderiv(shaderHandle, GLES30.GL_COMPILE_STATUS, compileStatus, 0);

			if (compileStatus[0] == 0) {
				// コンパイル失敗
				GLES30.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}
		if (shaderHandle == 0) {
			throw new RuntimeException("Error creating vertex shader.");
		}
		return shaderHandle;
	}
}
