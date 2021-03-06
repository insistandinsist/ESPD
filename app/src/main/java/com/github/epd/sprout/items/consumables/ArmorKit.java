
package com.github.epd.sprout.items.consumables;

import com.github.epd.sprout.Assets;
import com.github.epd.sprout.actors.hero.Hero;
import com.github.epd.sprout.effects.Speck;
import com.github.epd.sprout.items.Item;
import com.github.epd.sprout.items.armor.Armor;
import com.github.epd.sprout.items.armor.ClassArmor;
import com.github.epd.sprout.messages.Messages;
import com.github.epd.sprout.scenes.GameScene;
import com.github.epd.sprout.sprites.HeroSprite;
import com.github.epd.sprout.sprites.ItemSpriteSheet;
import com.github.epd.sprout.utils.GLog;
import com.github.epd.sprout.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class ArmorKit extends Item {

	private static final String TXT_SELECT_ARMOR = Messages.get(ArmorKit.class, "prompt");
	private static final String TXT_UPGRADED = Messages.get(ArmorKit.class, "upgraded");

	private static final float TIME_TO_UPGRADE = 2;

	private static final String AC_APPLY = Messages.get(ArmorKit.class, "ac_apply");

	{
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.KIT;

		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_APPLY);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action == AC_APPLY) {

			curUser = hero;
			GameScene.selectItem(itemSelector, WndBag.Mode.ARMOR,
					TXT_SELECT_ARMOR);

		} else {

			super.execute(hero, action);

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

	private void upgrade(Armor armor) {

		detach(curUser.belongings.backpack);

		curUser.sprite.centerEmitter().start(Speck.factory(Speck.KIT), 0.05f,
				10);
		curUser.spend(TIME_TO_UPGRADE);
		curUser.busy();

		GLog.w(TXT_UPGRADED, armor.name());

		ClassArmor classArmor = ClassArmor.upgrade(curUser, armor);
		if (curUser.belongings.armor == armor) {

			curUser.belongings.armor = classArmor;
			((HeroSprite) curUser.sprite).updateArmor();

		} else {

			armor.detach(curUser.belongings.backpack);
			classArmor.collect(curUser.belongings.backpack);

		}

		curUser.sprite.operate(curUser.pos);
		Sample.INSTANCE.play(Assets.SND_EVOKE);
	}

	@Override
	public String info() {
		return Messages.get(this, "desc");
	}

	private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect(Item item) {
			if (item != null) {
				ArmorKit.this.upgrade((Armor) item);
			}
		}
	};
}
