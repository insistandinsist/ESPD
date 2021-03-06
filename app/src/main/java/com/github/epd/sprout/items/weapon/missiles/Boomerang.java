
package com.github.epd.sprout.items.weapon.missiles;

import com.github.epd.sprout.Dungeon;
import com.github.epd.sprout.actors.Char;
import com.github.epd.sprout.actors.hero.Hero;
import com.github.epd.sprout.items.Item;
import com.github.epd.sprout.messages.Messages;
import com.github.epd.sprout.sprites.ItemSpriteSheet;
import com.github.epd.sprout.sprites.MissileSprite;

public class Boomerang extends MissileWeapon {

	{
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.BOOMERANG;

		STR = 10;

		MIN = 1;
		MAX = 4;

		stackable = false;
		unique = true;

		bones = false;
	}

	@Override
	public boolean isUpgradable() {
		return true;
	}

	@Override
	public Item upgrade() {
		return upgrade(false);
	}

	@Override
	public Item upgrade(boolean enchant) {
		MIN += 1;
		MAX += 2;
		super.upgrade(enchant);

		updateQuickslot();

		return this;
	}

	@Override
	public Item degrade() {
		MIN -= 1;
		MAX -= 2;
		return super.degrade();
	}

	@Override
	public void proc(Char attacker, Char defender, int damage) {
		super.proc(attacker, defender, damage);
		if (attacker instanceof Hero && ((Hero) attacker).rangedWeapon == this) {
			circleBack(defender.pos, (Hero) attacker);
		}
	}

	@Override
	protected void miss(int cell) {
		circleBack(cell, curUser);
	}

	private void circleBack(int from, Hero owner) {

		((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class))
				.reset(from, curUser.pos, curItem, null);

		if (throwEquiped) {
			owner.belongings.weapon = this;
			owner.spend(-TIME_TO_EQUIP);
			Dungeon.quickslot.replaceSimilar(this);
			updateQuickslot();
		} else if (!collect(curUser.belongings.backpack)) {
			Dungeon.level.drop(this, owner.pos).sprite.drop();
		}
	}

	private boolean throwEquiped;

	@Override
	public void cast(Hero user, int dst) {
		throwEquiped = isEquipped(user);
		if (throwEquiped) Dungeon.quickslot.convertToPlaceholder(this);
		super.cast(user, dst);
	}

	@Override
	public String desc() {
		String info = Messages.get(this, "desc");
		switch (imbue) {
			case LIGHT:
				info += Messages.get(this, "lighter");
				break;
			case HEAVY:
				info += Messages.get(this, "heavier");
				break;
			case NONE:
		}
		if (reinforced) {
			info += Messages.get(this, "reinforced");
		}
		return info;
	}
}
