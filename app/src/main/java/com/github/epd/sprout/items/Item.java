
package com.github.epd.sprout.items;

import com.github.epd.sprout.Assets;
import com.github.epd.sprout.Dungeon;
import com.github.epd.sprout.ShatteredPixelDungeon;
import com.github.epd.sprout.actors.Actor;
import com.github.epd.sprout.actors.Char;
import com.github.epd.sprout.actors.buffs.SnipersMark;
import com.github.epd.sprout.actors.hero.Hero;
import com.github.epd.sprout.effects.Speck;
import com.github.epd.sprout.items.bags.Bag;
import com.github.epd.sprout.items.weapon.missiles.Boomerang;
import com.github.epd.sprout.items.weapon.missiles.JupitersWraith;
import com.github.epd.sprout.items.weapon.missiles.MissileWeapon;
import com.github.epd.sprout.mechanics.Ballistica;
import com.github.epd.sprout.messages.Messages;
import com.github.epd.sprout.scenes.CellSelector;
import com.github.epd.sprout.scenes.GameScene;
import com.github.epd.sprout.sprites.ItemSprite;
import com.github.epd.sprout.sprites.MissileSprite;
import com.github.epd.sprout.ui.QuickSlotButton;
import com.github.epd.sprout.utils.GLog;
import com.github.epd.sprout.utils.Utils;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Item implements Bundlable {

	private static final String TXT_PACK_FULL = Messages.get(Item.class, "pack_full");

	private static final String TXT_TO_STRING = "%s";
	private static final String TXT_TO_STRING_X = "%s x%d";
	private static final String TXT_TO_STRING_LVL = "%s%+d";
	private static final String TXT_TO_STRING_LVL_X = "%s%+d x%d";

	protected static final float TIME_TO_THROW = 1.0f;
	protected static final float TIME_TO_PICK_UP = 1.0f;
	protected static final float TIME_TO_DROP = 0.5f;

	public static final String AC_DROP = Messages.get(Item.class, "ac_drop");
	public static final String AC_THROW = Messages.get(Item.class, "ac_throw");

	public String defaultAction;
	public boolean usesTargeting;

	protected String name = "smth";
	public int image = 0;

	public boolean stackable = false;
	public int quantity = 1;

	public int level = 0;
	public int consumedValue = 0;
	public boolean levelKnown = false;

	public boolean cursed;
	public boolean cursedKnown;
	public boolean reinforced;

	// Unique items persist through revival
	public boolean unique = false;

	// whether an item can be included in heroes remains
	public boolean bones = false;

	private static Comparator<Item> itemComparator = new Comparator<Item>() {
		@Override
		public int compare(Item lhs, Item rhs) {
			return Generator.Category.order(lhs)
					- Generator.Category.order(rhs);
		}
	};

	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = new ArrayList<String>();
		actions.add(AC_DROP);
		actions.add(AC_THROW);
		return actions;
	}

	public boolean doPickUp(Hero hero) {
		if (collect(hero.belongings.backpack)) {

			GameScene.pickUp(this);
			Sample.INSTANCE.play(Assets.SND_ITEM);
			if (!ShatteredPixelDungeon.autocollect()) {
				hero.spendAndNext(TIME_TO_PICK_UP);
			} else {
				hero.spendAndNext(0);
			}
			return true;

		} else {
			return false;
		}
	}

	public void doDrop(Hero hero) {
		hero.spendAndNext(TIME_TO_DROP);
		Dungeon.level.drop(detachAll(hero.belongings.backpack), hero.pos).sprite
				.drop(hero.pos);
	}

	public void syncVisuals() {
		// do nothing by default, as most items need no visual syncing.
	}

	public void doThrow(Hero hero) {
		GameScene.selectCell(thrower);
	}

	public void execute(Hero hero, String action) {

		curUser = hero;
		curItem = this;

		if (action.equals(AC_DROP)) {

			doDrop(hero);

		} else if (action.equals(AC_THROW)) {

			doThrow(hero);

		}
	}

	public void execute(Hero hero) {
		execute(hero, defaultAction);
	}

	protected void onThrow(int cell) {
		Heap heap = Dungeon.level.drop(this, cell);
		if (!heap.isEmpty()) {
			heap.sprite.drop(cell);
		}
	}

	public boolean collect(Bag container) {

		ArrayList<Item> items = container.items;

		if (items.contains(this)) {
			return true;
		}

		for (Item item : items) {
			if (item instanceof Bag && ((Bag) item).grab(this)) {
				return collect((Bag) item);
			}
		}

		if (stackable) {
			for (Item item : items) {
				if (isSimilar(item)) {
					item.quantity += quantity;
					item.updateQuickslot();
					return true;
				}
			}
		}

		if (items.size() < container.size) {

			items.add(this);
			if (stackable || this instanceof Boomerang || this instanceof JupitersWraith)
				Dungeon.quickslot.replaceSimilar(this);
			updateQuickslot();
			Collections.sort(items, itemComparator);
			return true;

		} else {

			GLog.n(TXT_PACK_FULL, name());
			return false;

		}
	}

	public boolean collect() {
		return collect(Dungeon.hero.belongings.backpack);
	}

	public static void autocollect(Item item, int pos){
		if (ShatteredPixelDungeon.autocollect()) {
			if (item.doPickUp(Dungeon.hero)) {
				GLog.i("\n" + Messages.get(Hero.class, "have", item.name()));
			} else {
				Dungeon.level.drop(item, Dungeon.hero.pos).sprite.drop();
			}
		} else Dungeon.level.drop(item, pos).sprite.drop();
	}

	public final Item detach(Bag container) {

		if (quantity <= 0) {

			return null;

		} else if (quantity == 1) {

			if (stackable || this instanceof Boomerang || this instanceof JupitersWraith) {
				Dungeon.quickslot.convertToPlaceholder(this);
			}

			return detachAll(container);

		} else {

			quantity--;
			updateQuickslot();

			try {

				// pssh, who needs copy constructors?
				Item detached = getClass().newInstance();
				Bundle copy = new Bundle();
				this.storeInBundle(copy);
				detached.restoreFromBundle(copy);
				detached.quantity(1);

				detached.onDetach();
				return detached;
			} catch (Exception e) {
				return null;
			}
		}
	}

	public final Item detach(Bag container, Integer quant) {

		// TODO: Maybe optimize this method to prevent 0 quantity items

		if (quantity <= 0) {

			return null;

		} else if (quantity == 1) {

			if (stackable || this instanceof Boomerang || this instanceof JupitersWraith) {
				Dungeon.quickslot.convertToPlaceholder(this);
			}

			return detachAll(container);

		} else {

			quantity -= quant;
			updateQuickslot();

			try {

				// pssh, who needs copy constructors?
				Item detached = getClass().newInstance();
				Bundle copy = new Bundle();
				this.storeInBundle(copy);
				detached.restoreFromBundle(copy);
				detached.quantity(quant);

				detached.onDetach();
				return detached;
			} catch (Exception e) {
				return null;
			}
		}
	}

	public final Item detachAll(Bag container) {
		Dungeon.quickslot.clearItem(this);
		updateQuickslot();

		for (Item item : container.items) {
			if (item == this) {
				container.items.remove(this);
				item.onDetach();
				return this;
			} else if (item instanceof Bag) {
				Bag bag = (Bag) item;
				if (bag.contains(this)) {
					return detachAll(bag);
				}
			}
		}

		return this;
	}

	public boolean isSimilar(Item item) {
		return getClass() == item.getClass();
	}

	protected void onDetach() {
	}

	public Item uncurse() {
		cursed = false;
		return this;
	}

	public Item reinforce() {
		reinforced = true;
		return this;
	}


	public Item upgrade() {

		uncurse();
		cursedKnown = true;
		this.level++;


		updateQuickslot();

		return this;
	}

	final public Item upgrade(int n) {
		for (int i = 0; i < n; i++) {
			upgrade();
		}

		return this;
	}

	public Item degrade() {

		this.level--;

		return this;
	}

	final public Item degrade(int n) {
		for (int i = 0; i < n; i++) {
			degrade();
		}

		return this;
	}

	public int visiblyUpgraded() {
		return levelKnown ? level : 0;
	}

	public boolean visiblyCursed() {
		return cursed && cursedKnown;
	}

	public boolean isUpgradable() {
		return true;
	}

	public boolean isReinforced() {
		return reinforced;
	}

	public boolean isIdentified() {
		return levelKnown && cursedKnown;
	}

	public boolean isEquipped(Hero hero) {
		return false;
	}

	public Item identify() {

		levelKnown = true;
		cursedKnown = true;

		return this;
	}

	public static void evoke(Hero hero) {
		hero.sprite.emitter().burst(Speck.factory(Speck.EVOKE), 5);
	}

	@Override
	public String toString() {

		if (levelKnown && level != 0) {
			if (quantity > 1) {
				return Utils.format(TXT_TO_STRING_LVL_X, name(), level,
						quantity);
			} else {
				return Utils.format(TXT_TO_STRING_LVL, name(), level);
			}
		} else {
			if (quantity > 1) {
				return Utils.format(TXT_TO_STRING_X, name(), quantity);
			} else {
				return Utils.format(TXT_TO_STRING, name());
			}
		}
	}

	public String name() {
		return name;
	}

	public final String trueName() {
		return name;
	}

	public int image() {
		return image;
	}

	public ItemSprite.Glowing glowing() {
		return null;
	}

	public String info() {
		return desc();
	}

	public String desc() {
		return "";
	}

	public int quantity() {
		return quantity;
	}

	public Item quantity(int value) {
		quantity = value;
		return this;
	}

	public int price() {
		return 0;
	}

	public static Item virtual(Class<? extends Item> cl) {
		try {

			Item item = cl.newInstance();
			item.quantity = 0;
			return item;

		} catch (Exception e) {
			return null;
		}
	}

	public Item random() {
		return this;
	}

	public String status() {
		return quantity != 1 ? Integer.toString(quantity) : null;
	}

	public void updateQuickslot() {
		QuickSlotButton.refresh();
	}

	private static final String QUANTITY = "quantity";
	private static final String LEVEL = "level";
	private static final String LEVEL_KNOWN = "levelKnown";
	private static final String CURSED = "cursed";
	private static final String REINFORCED = "reinforced";
	private static final String CURSED_KNOWN = "cursedKnown";
	private static final String OLDSLOT = "quickslot";
	private static final String QUICKSLOT = "quickslotpos";

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(QUANTITY, quantity);
		bundle.put(LEVEL, level);
		bundle.put(LEVEL_KNOWN, levelKnown);
		bundle.put(CURSED, cursed);
		bundle.put(REINFORCED, reinforced);
		bundle.put(CURSED_KNOWN, cursedKnown);
		if (Dungeon.quickslot.contains(this)) {
			bundle.put(QUICKSLOT, Dungeon.quickslot.getSlot(this));
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		quantity = bundle.getInt(QUANTITY);
		levelKnown = bundle.getBoolean(LEVEL_KNOWN);
		cursedKnown = bundle.getBoolean(CURSED_KNOWN);

		int level = bundle.getInt(LEVEL);
		if (level > 0) {
			upgrade(level);
		} else if (level < 0) {
			degrade(-level);
		}

		cursed = bundle.getBoolean(CURSED);
		reinforced = bundle.getBoolean(REINFORCED);

		// only want to populate slot on first load.
		if (Dungeon.hero == null) {
			// support for pre-0.2.3 saves and rankings
			if (bundle.contains(OLDSLOT)) {
				Dungeon.quickslot.setSlot(0, this);
			} else if (bundle.contains(QUICKSLOT)) {
				Dungeon.quickslot.setSlot(bundle.getInt(QUICKSLOT), this);
			}
		}
	}

	public void cast(final Hero user, int dst) {

		final int cell = new Ballistica(user.pos, dst, Ballistica.PROJECTILE).collisionPos;
		user.sprite.zap(cell);
		user.busy();

		Sample.INSTANCE.play(Assets.SND_MISS, 0.6f, 0.6f, 1.5f);

		Char enemy = Actor.findChar(cell);
		QuickSlotButton.target(enemy);


		float delay = TIME_TO_THROW;
		if (this instanceof MissileWeapon) {
			delay *= ((MissileWeapon) this).speedFactor(user);
			if (enemy != null) {
				SnipersMark mark = user.buff(SnipersMark.class);
				if (mark != null) {
					if (mark.object == enemy.id()) {
						delay *= 0.5f;
					}
					user.remove(mark);
				}
			}
		}
		final float finalDelay = delay;

		((MissileSprite) user.sprite.parent.recycle(MissileSprite.class))
				.reset(user.pos, cell, this, new Callback() {
					@Override
					public void call() {
						Item.this.detach(user.belongings.backpack)
								.onThrow(cell);
						user.spendAndNext(finalDelay);
					}
				});
	}

	protected static Hero curUser = null;
	protected static Item curItem = null;
	protected static CellSelector.Listener thrower = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target != null) {
				curItem.cast(curUser, target);
			}
		}

		@Override
		public String prompt() {
			return Messages.get(Item.class, "prompt");
		}
	};
}
