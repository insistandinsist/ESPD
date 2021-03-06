
package com.github.epd.sprout.windows;

import com.github.epd.sprout.Chrome;
import com.github.epd.sprout.Dungeon;
import com.github.epd.sprout.ShatteredPixelDungeon;
import com.github.epd.sprout.messages.Messages;
import com.github.epd.sprout.scenes.PixelScene;
import com.github.epd.sprout.ui.RenderedTextMultiline;
import com.github.epd.sprout.ui.Window;
import com.watabou.input.Touchscreen.Touch;
import com.watabou.noosa.Game;
import com.watabou.noosa.TouchArea;
import com.watabou.utils.SparseArray;

public class WndStory extends Window {

	private static final int WIDTH_P = 120;
	private static final int WIDTH_L = 144;
	private static final int MARGIN = 6;

	private static final float bgR = 0.77f;
	private static final float bgG = 0.73f;
	private static final float bgB = 0.62f;

	public static final int ID_SEWERS = 0;
	public static final int ID_PRISON = 1;
	public static final int ID_CAVES = 2;
	public static final int ID_METROPOLIS = 3;
	public static final int ID_HALLS = 4;
	public static final int ID_SOKOBAN1 = 5;
	public static final int ID_SOKOBAN2 = 6;
	public static final int ID_SOKOBAN3 = 7;
	public static final int ID_SOKOBAN4 = 8;
	public static final int ID_SAFELEVEL = 9;
	public static final int ID_TOWN = 10;
	public static final int ID_ZOT = 11;

	private static final SparseArray<String> CHAPTERS = new SparseArray<String>();

	static {
		CHAPTERS.put(
				ID_SEWERS,
				Messages.get(WndStory.class, "sewers"));

		CHAPTERS.put(
				ID_PRISON,
				Messages.get(WndStory.class, "prison"));

		CHAPTERS.put(
				ID_CAVES,
				Messages.get(WndStory.class, "caves"));

		CHAPTERS.put(
				ID_METROPOLIS,
				Messages.get(WndStory.class, "city"));

		CHAPTERS.put(
				ID_HALLS,
				Messages.get(WndStory.class, "halls"));
		CHAPTERS.put(
				ID_SOKOBAN1,
				Messages.get(WndStory.class, "s1"));
		CHAPTERS.put(
				ID_SOKOBAN2,
				Messages.get(WndStory.class, "s2"));
		CHAPTERS.put(
				ID_SOKOBAN3,
				Messages.get(WndStory.class, "s3"));
		CHAPTERS.put(
				ID_SOKOBAN4,
				Messages.get(WndStory.class, "s4"));
		CHAPTERS.put(
				ID_SAFELEVEL,
				Messages.get(WndStory.class, "s5"));
		CHAPTERS.put(
				ID_TOWN,
				Messages.get(WndStory.class, "s6"));
		CHAPTERS.put(
				ID_ZOT,
				Messages.get(WndStory.class, "s7"));
	}

	private RenderedTextMultiline tf;

	private float delay;

	public WndStory(String text) {
		super(0, 0, Chrome.get(Chrome.Type.SCROLL));

		tf = PixelScene.renderMultiline(text, 7);
		tf.maxWidth(ShatteredPixelDungeon.landscape() ?
				WIDTH_L - MARGIN * 2 :
				WIDTH_P - MARGIN * 2);
		tf.invert();
		tf.setPos(MARGIN, 0);
		add(tf);

		add(new TouchArea(chrome) {
			@Override
			protected void onClick(Touch touch) {
				hide();
			}
		});

		resize((int) (tf.width() + MARGIN * 2),
				(int) Math.min(tf.height(), 180));
	}

	@Override
	public void update() {
		super.update();

		if (delay > 0 && (delay -= Game.elapsed) <= 0) {
			shadow.visible = chrome.visible = tf.visible = true;
		}
	}

	public static void showChapter(int id) {

		if (Dungeon.chapters.contains(id)) {
			return;
		}

		String text = CHAPTERS.get(id);
		if (text != null) {
			WndStory wnd = new WndStory(text);
			if ((wnd.delay = 0.6f) > 0) {
				wnd.shadow.visible = wnd.chrome.visible = wnd.tf.visible = false;
			}

			Game.scene().add(wnd);

			Dungeon.chapters.add(id);
		}
	}
}
