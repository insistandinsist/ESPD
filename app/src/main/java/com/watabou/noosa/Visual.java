
package com.watabou.noosa;

import com.watabou.glwrap.Matrix;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;

public class Visual extends Gizmo {

	public float x;
	public float y;
	public float width;
	public float height;

	public PointF scale;
	public PointF origin;

	protected float[] matrix;

	public float rm;
	public float gm;
	public float bm;
	public float am;
	public float ra;
	public float ga;
	public float ba;
	public float aa;

	public PointF speed;
	public PointF acc;

	public float angle;
	public float angularSpeed;

	private float lastX, lastY, lastW, lastH, lastA;
	private PointF lastScale = new PointF(), lastOrigin = new PointF();

	public Visual(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		scale = new PointF(1, 1);
		origin = new PointF();

		matrix = new float[16];

		resetColor();

		speed = new PointF();
		acc = new PointF();
	}

	@Override
	public void update() {
		updateMotion();
	}

	@Override
	public void draw() {
		if (lastX != x ||
				lastY != y ||
				lastW != width ||
				lastH != height ||
				lastA != angle ||
				lastScale.x != scale.x ||
				lastScale.y != scale.y ||
				lastOrigin.x != origin.x ||
				lastOrigin.y != origin.y) {

			lastX = x;
			lastY = y;
			lastW = width;
			lastH = height;
			lastA = angle;
			lastScale.x = scale.x;
			lastScale.y = scale.y;
			lastOrigin.x = origin.x;
			lastOrigin.y = origin.y;

			updateMatrix();
		}
	}

	protected void updateMatrix() {
		Matrix.setIdentity(matrix);
		Matrix.translate(matrix, x, y);
		if (origin.x != 0 || origin.y != 0)
			Matrix.translate(matrix, origin.x, origin.y);
		if (angle != 0) {
			Matrix.rotate(matrix, angle);
		}
		if (scale.x != 1 || scale.y != 1) {
			Matrix.scale(matrix, scale.x, scale.y);
		}
		if (origin.x != 0 || origin.y != 0)
			Matrix.translate(matrix, -origin.x, -origin.y);
	}

	public PointF point() {
		return new PointF(x, y);
	}

	public PointF point(PointF p) {
		x = p.x;
		y = p.y;
		return p;
	}

	public Point point(Point p) {
		x = p.x;
		y = p.y;
		return p;
	}

	public PointF center() {
		return new PointF(x + width / 2, y + height / 2);
	}

	public PointF center(PointF p) {
		x = p.x - width / 2;
		y = p.y - height / 2;
		return p;
	}

	public float width() {
		return width * scale.x;
	}

	public float height() {
		return height * scale.y;
	}

	protected void updateMotion() {

		if (acc.x != 0)
			speed.x += acc.x * Game.elapsed;
		if (speed.x != 0)
			x += speed.x * Game.elapsed;

		if (acc.y != 0)
			speed.y += acc.y * Game.elapsed;
		if (speed.y != 0)
			y += speed.y * Game.elapsed;

		if (angularSpeed != 0)
			angle += angularSpeed * Game.elapsed;
	}

	public void alpha(float value) {
		am = value;
		aa = 0;
	}

	public float alpha() {
		return am + aa;
	}

	public void invert() {
		rm = gm = bm = -1f;
		ra = ga = ba = +1f;
	}

	public void lightness(float value) {
		if (value < 0.5f) {
			rm = gm = bm = value * 2f;
			ra = ga = ba = 0;
		} else {
			rm = gm = bm = 2f - value * 2f;
			ra = ga = ba = value * 2f - 1f;
		}
	}

	public void brightness(float value) {
		rm = gm = bm = value;
	}

	public void tint(float r, float g, float b, float strength) {
		rm = gm = bm = 1f - strength;
		ra = r * strength;
		ga = g * strength;
		ba = b * strength;
	}

	public void tint(int color, float strength) {
		rm = gm = bm = 1f - strength;
		ra = ((color >> 16) & 0xFF) / 255f * strength;
		ga = ((color >> 8) & 0xFF) / 255f * strength;
		ba = (color & 0xFF) / 255f * strength;
	}

	public void color(float r, float g, float b) {
		rm = gm = bm = 0;
		ra = r;
		ga = g;
		ba = b;
	}

	public void color(int color) {
		color(((color >> 16) & 0xFF) / 255f, ((color >> 8) & 0xFF) / 255f, (color & 0xFF) / 255f);
	}

	public void hardlight(float r, float g, float b) {
		ra = ga = ba = 0;
		rm = r;
		gm = g;
		bm = b;
	}

	public void hardlight(int color) {
		hardlight((color >> 16) / 255f, ((color >> 8) & 0xFF) / 255f, (color & 0xFF) / 255f);
	}

	public void resetColor() {
		rm = gm = bm = am = 1;
		ra = ga = ba = aa = 0;
	}

	public boolean overlapsPoint(float x, float y) {
		return x >= this.x && x < this.x + width * scale.x && y >= this.y && y < this.y + height * scale.y;
	}

	public boolean overlapsScreenPoint(int x, int y) {
		Camera c = camera();
		if (c != null) {
			PointF p = c.screenToCamera(x, y);
			return overlapsPoint(p.x, p.y);
		} else {
			return false;
		}
	}

	// true if its bounding box intersects its camera's bounds
	public boolean isVisible() {
		Camera c = camera();

		if (c == null || !visible) return false;

		//x coord
		if (x > c.scroll.x + c.width)
			return false;
		else if (!(x >= c.scroll.x || x + width() >= c.scroll.x))
			return false;

		//y coord
		if (y > c.scroll.y + c.height)
			return false;
		else if (!(y >= c.scroll.y || y + height() >= c.scroll.y))
			return false;

		return true;
	}
}
