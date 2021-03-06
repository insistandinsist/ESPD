
package com.github.epd.sprout.items.weapon.melee.relic;

import com.github.epd.sprout.actors.buffs.Buff;
import com.github.epd.sprout.actors.buffs.MagicImmunity;
import com.github.epd.sprout.actors.hero.Hero;
import com.github.epd.sprout.messages.Messages;
import com.github.epd.sprout.sprites.ItemSpriteSheet;
import com.github.epd.sprout.ui.BuffIndicator;
import com.github.epd.sprout.utils.GLog;

import java.util.ArrayList;

public class CromCruachAxe extends RelicMeleeWeapon {

	public CromCruachAxe() {
		super(6, 1f, 1f);

	}


	{
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.CROMAXE;

		level = 0;
		exp = 0;
		levelCap = 15;

		charge = 0;
		chargeCap = 1000;

		cooldown = 0;
		bones = false;

		defaultAction = AC_DISPEL;

	}

	public static final String AC_DISPEL = Messages.get(CromCruachAxe.class, "ac_dispel");

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && charge >= chargeCap)
			actions.add(AC_DISPEL);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_DISPEL)) {
			GLog.p(Messages.get(this, "ready"));
			charge = 0;
			Buff.prolong(hero, MagicImmunity.class, 2f * (level / 10));
		} else
			super.execute(hero, action);
	}


	public class DispelCounter extends WeaponBuff {

		@Override
		public boolean act() {
			if (charge < chargeCap) {
				charge += level;
				if (charge >= chargeCap) {
					charge = chargeCap;
					GLog.p(Messages.get(CromCruachAxe.class, "buffdesc"));
				}
				updateQuickslot();
			}
			spend(TICK);
			return true;
		}

		@Override
		public String toString() {
			return Messages.get(CromCruachAxe.class, "buffname");
		}

		@Override
		public int icon() {
			if (cooldown == 0)
				return BuffIndicator.NONE;
			else
				return BuffIndicator.NONE;
		}

		@Override
		public void detach() {
			cooldown = 0;
			charge = 0;
			super.detach();
		}

	}


	@Override
	protected WeaponBuff passiveBuff() {
		return new DispelCounter();
	}

}


