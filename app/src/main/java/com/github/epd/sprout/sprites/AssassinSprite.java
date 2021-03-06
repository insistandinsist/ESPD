
package com.github.epd.sprout.sprites;

import com.github.epd.sprout.Assets;
import com.github.epd.sprout.Dungeon;
import com.github.epd.sprout.actors.Actor;
import com.github.epd.sprout.actors.Char;
import com.github.epd.sprout.actors.buffs.Buff;
import com.github.epd.sprout.actors.buffs.Slow;
import com.github.epd.sprout.items.weapon.missiles.Shuriken;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class AssassinSprite extends MobSprite {

	private Animation cast;

	public AssassinSprite() {
		super();

		texture(Assets.ASSASSIN);

		TextureFilm frames = new TextureFilm(texture, 14, 16);

		idle = new Animation(2, true);
		idle.frames(frames, 0, 0, 0, 1);

		run = new Animation(15, false);
		run.frames(frames, 2, 3, 4, 5, 0);

		attack = new Animation(15, false);
		attack.frames(frames, 6, 7, 7, 0);

		cast = attack.clone();

		die = new Animation(8, false);
		die.frames(frames, 8, 9, 10, 10);

		play(run.clone());
	}


	@Override
	public void attack(int cell) {
		if (!Dungeon.level.adjacent(cell, ch.pos)) {
			Char enemy = Actor.findChar(cell);
			((MissileSprite) parent.recycle(MissileSprite.class)).reset(ch.pos,
					cell, new Shuriken(), new Callback() {
						@Override
						public void call() {
							ch.onAttackComplete();
						}
					});


			if (Random.Int(5) == 0) {
				Buff.affect(enemy, Slow.class, Slow.duration(enemy));
			}


			play(cast);
			turnTo(ch.pos, cell);

		} else {

			super.attack(cell);

		}
	}

	@Override
	public void onComplete(Animation anim) {
		if (anim == run) {
			synchronized (this) {
				isMoving = false;
				idle();
				notifyAll();
			}
		} else {
			super.onComplete(anim);
		}
	}
}
