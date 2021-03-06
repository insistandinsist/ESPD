
package com.github.epd.sprout.items.misc;

import com.github.epd.sprout.Assets;
import com.github.epd.sprout.Dungeon;
import com.github.epd.sprout.Statistics;
import com.github.epd.sprout.actors.hero.Hero;
import com.github.epd.sprout.actors.mobs.Mob;
import com.github.epd.sprout.effects.CellEmitter;
import com.github.epd.sprout.effects.Speck;
import com.github.epd.sprout.items.Item;
import com.github.epd.sprout.items.bombs.DumplingBomb;
import com.github.epd.sprout.items.weapon.missiles.RiceBall;
import com.github.epd.sprout.messages.Messages;
import com.github.epd.sprout.sprites.ItemSpriteSheet;
import com.github.epd.sprout.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class Rice extends Item {

	public static final String AC_COOK = Messages.get(Rice.class, "ac1");
	public static final String AC_COOKBOMB = Messages.get(Rice.class, "ac2");

	public static final float TIME_TO_COOK = 1;
	public static final float TIME_TO_COOK_BOMB = 4;

	{
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.SEED_RICE;

		stackable = false;

		defaultAction = AC_COOK;
	}

	private int bombcost = 5;

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_COOK);
		actions.add(AC_COOKBOMB);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		if (action.equals(AC_COOK)) {

			hero.spend(TIME_TO_COOK);
			hero.busy();

			hero.sprite.operate(hero.pos);

			RiceBall riceball = new RiceBall();
			if (riceball.doPickUp(Dungeon.hero)) {
				GLog.i(Messages.get(Hero.class, "have"), riceball.name());
				Statistics.ballsCooked++;
			} else {
				Dungeon.level.drop(riceball, Dungeon.hero.pos).sprite.drop();
				Statistics.ballsCooked++;
			}


			if (Statistics.ballsCooked > 200) {
				detach(Dungeon.hero.belongings.backpack);
				GLog.n(Messages.get(Rice.class, "b1"));
			} else if (Statistics.ballsCooked > 175) {
				GLog.n(Messages.get(Rice.class, "b2"));
			} else if (Statistics.ballsCooked > 150) {
				GLog.n(Messages.get(Rice.class, "b3"));
			}

		} else if (action.equals(AC_COOKBOMB)) {

			hero.spend(TIME_TO_COOK_BOMB);
			hero.busy();

			hero.sprite.operate(hero.pos);

			DumplingBomb bomb = new DumplingBomb();
			if (bomb.doPickUp(Dungeon.hero)) {
				GLog.i(Messages.get(Hero.class, "have"), bomb.name());
				Statistics.ballsCooked += bombcost;
			} else {
				Dungeon.level.drop(bomb, Dungeon.hero.pos).sprite.drop();
				Statistics.ballsCooked += bombcost;
			}


			if (Statistics.ballsCooked > 200) {
				detach(Dungeon.hero.belongings.backpack);
				GLog.n(Messages.get(Rice.class, "b1"));
			} else if (Statistics.ballsCooked > 175) {
				GLog.n(Messages.get(Rice.class, "b2"));
			} else if (Statistics.ballsCooked > 150) {
				GLog.n(Messages.get(Rice.class, "b3"));
			}


		} else {
			super.execute(hero, action);

		}
	}

	@Override
	public boolean doPickUp(Hero hero) {
		if (super.doPickUp(hero)) {

			if (Dungeon.level != null && Dungeon.depth == 32) {
				for (Mob mob : Dungeon.level.mobs) {
					mob.beckon(Dungeon.hero.pos);
				}

				GLog.w(Messages.get(Rice.class, "enrage"));
				CellEmitter.center(Dungeon.hero.pos).start(
						Speck.factory(Speck.SCREAM), 0.3f, 3);
				Sample.INSTANCE.play(Assets.SND_CHALLENGE);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int price() {
		return 10 * quantity;
	}

	@Override
	public String info() {
		return Messages.get(this, "desc");
	}
}
