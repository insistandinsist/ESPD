
package com.github.epd.sprout.sprites;

import android.graphics.RectF;

import com.github.epd.sprout.Assets;
import com.github.epd.sprout.Dungeon;
import com.github.epd.sprout.actors.hero.Hero;
import com.github.epd.sprout.actors.hero.HeroClass;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class HeroSprite extends CharSprite {

	private static final int FRAME_WIDTH = 12;
	private static final int FRAME_HEIGHT = 15;

	private static final int RUN_FRAMERATE = 20;

	private static TextureFilm tiers;

	private Animation fly;

	public HeroSprite() {
		super();

		link(Dungeon.hero);

		texture(Dungeon.hero.heroClass.spritesheet());
		updateArmor();

		if (ch.isAlive()) idle();
		else die();
	}

	public void updateArmor() {

		TextureFilm film = new TextureFilm(tiers(), ((Hero) ch).tier(),
				FRAME_WIDTH, FRAME_HEIGHT);

		idle = new Animation(1, true);
		idle.frames(film, 0, 0, 0, 1, 0, 0, 1, 1);

		run = new Animation(RUN_FRAMERATE, true);
		run.frames(film, 2, 3, 4, 5, 6, 7);

		die = new Animation(20, false);
		die.frames(film, 8, 9, 10, 11, 12, 11);

		attack = new Animation(15, false);
		attack.frames(film, 13, 14, 15, 0);

		zap = attack.clone();

		operate = new Animation(8, false);
		operate.frames(film, 16, 17, 16, 17);

		fly = new Animation(1, true);
		fly.frames(film, 18);
	}

	@Override
	public void place(int p) {
		super.place(p);
		Camera.main.target = this;
	}

	@Override
	public void move(int from, int to) {
		super.move(from, to);
		if (ch.flying) {
			play(fly);
		}
		Camera.main.target = this;
	}

	@Override
	public void jump(int from, int to, Callback callback) {
		super.jump(from, to, callback);
		play(fly);
	}

	@Override
	public void bloodBurstA(PointF from, int damage) {
	}

	@Override
	public void update() {
		sleeping = ((Hero) ch).resting;

		super.update();
	}

	public boolean sprint(boolean on) {
		run.delay = on ? 0.667f / RUN_FRAMERATE : 1f / RUN_FRAMERATE;
		return on;
	}

	public static TextureFilm tiers() {
		if (tiers == null) {
			SmartTexture texture = TextureCache.get(Assets.ROGUE);
			tiers = new TextureFilm(texture, texture.width, FRAME_HEIGHT);
		}

		return tiers;
	}

	public static Image avatar(HeroClass cl, int armorTier) {

		RectF patch = tiers().get(armorTier);
		Image avatar = new Image(cl.spritesheet());
		RectF frame = avatar.texture.uvRect(1, 0, FRAME_WIDTH, FRAME_HEIGHT);
		frame.offset(patch.left, patch.top);
		avatar.frame(frame);

		return avatar;
	}
}
