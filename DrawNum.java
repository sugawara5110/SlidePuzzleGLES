
package com.SlidePuzzle;

class DrawNum {

	private Draw2D d2 = new Draw2D();

	public DrawNum() {
		
	}

	private void draw_sub(int num, float x, float y, int transformhandle, int uvwhhandle, int poshandle, int uvhandle,
			int texture) {

		float u = (float) (num % 4) * 0.25f;
		float v = (float) (num / 4) * 0.25f;

		d2.draw(x, y, u, v, transformhandle, uvwhhandle, poshandle, uvhandle, texture);
	}

	public void draw(int num, int digit, float x, float y, int transformhandle, int uvwhhandle, int poshandle,
			int uvhandle, int texture) {

		int nu = num;
		int remainder;
		int offset = 0;
		do {
			remainder = nu % 10;
			draw_sub(remainder, x + 40.0f * (digit - offset), y, transformhandle, uvwhhandle, poshandle, uvhandle,
					texture);
			nu /= 10;
			offset++;
			if (nu == 0)
				break;
		} while (true);
	}
}
