
package com.github.epd.sprout.scenes;

import android.content.Intent;
import android.net.Uri;

import com.github.epd.sprout.ShatteredPixelDungeon;
import com.github.epd.sprout.effects.Flare;
import com.github.epd.sprout.messages.Languages;
import com.github.epd.sprout.ui.Archs;
import com.github.epd.sprout.ui.ExitButton;
import com.github.epd.sprout.ui.Icons;
import com.github.epd.sprout.ui.NewRedButton;
import com.github.epd.sprout.ui.RenderedTextMultiline;
import com.github.epd.sprout.ui.Window;
import com.github.epd.sprout.windows.WndMessage;
import com.watabou.input.Touchscreen.Touch;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.TouchArea;

//TODO:UPDATE ME BEFORE PUSHING TO REDDIT!
public class AboutScene extends PixelScene {

	private NewRedButton btn;
	private NewRedButton btn2;
	private NewRedButton btn3;
	private NewRedButton btn4;

	private static final String TTL_SHPX = "ESPD";

	private static final String TXT_SHPX =
			"Modded from Sprouted Pixel Dungeon.";

	private static final String LNK_SHPX = "ShatteredPixel.com";

	private static final String TTL_WATA = "Pixel Dungeon";

	private static final String TXT_WATA =
			"Code & Graphics: Watabou\n" +
					"Music: Cube_Code";

	private static final String LNK_WATA = "pixeldungeon.watabou.ru";

	@Override
	public void create() {

		if (ShatteredPixelDungeon.language() != Languages.CHINESE) {

			super.create();

			final float colWidth = Camera.main.width / (ShatteredPixelDungeon.landscape() ? 2 : 1);
			final float colTop = (Camera.main.height / 2) - (ShatteredPixelDungeon.landscape() ? 30 : 90);
			final float wataOffset = ShatteredPixelDungeon.landscape() ? colWidth : 0;

			Image shpx = Icons.SHPX.get();
			shpx.x = (colWidth - shpx.width()) / 2;
			shpx.y = colTop;
			align(shpx);
			add(shpx);

			new Flare(7, 64).color(0x225511, true).show(shpx, 0).angularSpeed = +20;

			RenderedText shpxtitle = renderText(TTL_SHPX, 8);
			shpxtitle.hardlight(Window.SHPX_COLOR);
			add(shpxtitle);

			shpxtitle.x = (colWidth - shpxtitle.width()) / 2;
			shpxtitle.y = shpx.y + shpx.height + 5;
			align(shpxtitle);

			RenderedTextMultiline shpxtext = renderMultiline(TXT_SHPX, 8);
			shpxtext.maxWidth((int) Math.min(colWidth, 120));
			add(shpxtext);

			shpxtext.setPos((colWidth - shpxtext.width()) / 2, shpxtitle.y + shpxtitle.height() + 12);
			align(shpxtext);

			RenderedTextMultiline shpxlink = renderMultiline(LNK_SHPX, 8);
			shpxlink.maxWidth(shpxtext.maxWidth());
			shpxlink.hardlight(Window.SHPX_COLOR);
			add(shpxlink);

			shpxlink.setPos((colWidth - shpxlink.width()) / 2, shpxtext.bottom() + 6);
			align(shpxlink);

			TouchArea shpxhotArea = new TouchArea(shpxlink.left(), shpxlink.top(), shpxlink.width(), shpxlink.height()) {
				@Override
				protected void onClick(Touch touch) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + LNK_SHPX));
					Game.instance.startActivity(intent);
				}
			};
			add(shpxhotArea);

			Image wata = Icons.WATA.get();
			wata.x = wataOffset + (colWidth - wata.width()) / 2;
			wata.y = ShatteredPixelDungeon.landscape() ?
					colTop :
					shpxlink.top() + wata.height + 20;
			align(wata);
			add(wata);

			new Flare(7, 64).color(0x112233, true).show(wata, 0).angularSpeed = +20;

			RenderedText wataTitle = renderText(TTL_WATA, 8);
			wataTitle.hardlight(Window.TITLE_COLOR);
			add(wataTitle);

			wataTitle.x = wataOffset + (colWidth - wataTitle.width()) / 2;
			wataTitle.y = wata.y + wata.height + 11;
			align(wataTitle);

			RenderedTextMultiline wataText = renderMultiline(TXT_WATA, 8);
			wataText.maxWidth((int) Math.min(colWidth, 120));
			add(wataText);

			wataText.setPos(wataOffset + (colWidth - wataText.width()) / 2, wataTitle.y + wataTitle.height() + 12);
			align(wataText);

			RenderedTextMultiline wataLink = renderMultiline(LNK_WATA, 8);
			wataLink.maxWidth((int) Math.min(colWidth, 120));
			wataLink.hardlight(Window.TITLE_COLOR);
			add(wataLink);

			wataLink.setPos(wataOffset + (colWidth - wataLink.width()) / 2, wataText.bottom() + 6);
			align(wataLink);

			TouchArea hotArea = new TouchArea(wataLink.left(), wataLink.top(), wataLink.width(), wataLink.height()) {
				@Override
				protected void onClick(Touch touch) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + LNK_WATA));
					Game.instance.startActivity(intent);
				}
			};
			add(hotArea);


			Archs archs = new Archs();
			archs.setSize(Camera.main.width, Camera.main.height);
			addToBack(archs);

			ExitButton btnExit = new ExitButton();
			btnExit.setPos(Camera.main.width - btnExit.width(), 0);
			add(btnExit);

			fadeIn();
		} else {

			String TXT1 = "简单的发芽像素地牢";
			String TXT2 = "";


			super.create();

			final float colWidth = Camera.main.width / (ShatteredPixelDungeon.landscape() ? 2 : 1);
			final float colTop = (Camera.main.height / 2) - (ShatteredPixelDungeon.landscape() ? 30 : 90);


			RenderedText title = renderText(TXT1, 8);
			title.hardlight(0xFFFF00);
			add(title);

			title.x = (colWidth - title.width()) / 2;
			title.y = colTop;
			align(title);

			btn2 = new NewRedButton("制作者") {
				@Override
				protected void onClick() {
					parent.add(new WndMessage("制作者联系方式：\nQQ：_3529858533_\n电子邮件：_3529858533@qq.com_\n\n" +
							"错误报告/反馈请发送至_3529858533@qq.com_，并提示我查收。\n" +
							"与此mod相关的QQ群：\n" +
							"ESPD内部开发群。如果想加入，请向我发送邮件以获得相关信息。\n" +
							"本应用遵守GPLv3并开源，源代码地址：" +
							"\n\nhttps://github.com/G2159687/ESPD" +
							"\n\n但是，尽管该应用是开源的，我仍然推荐从可信的渠道（如酷安以及由我组建的QQ群）下载" +
							"以避免出现安全问题。"));
				}
			};
			btn2.setRect((colWidth - 80) / 2, title.y + title.height() + 12, 80, 18);
			add(btn2);


			RenderedTextMultiline text = renderMultiline(TXT2, 7);
			text.maxWidth((int) Math.min(colWidth, 120));
			//	add( text );

			text.setPos((colWidth - text.width()) / 2, title.y + title.height() + 12);
			//	align(text);

			btn = new NewRedButton("鸣谢") {
				@Override
				protected void onClick() {
					parent.add(new WndMessage("\n\n用于翻译本mod的代码由_00-Evan_制作，并由我移植到此版本，同时此版本也使用了很多来自破碎的像素地牢的代码，在此一并表示感谢。" +
							"\n_发芽_的像素地牢 作者：_dachhack_\n联系方式：_reddit.com/u/dachhack_" +
							"\n_破碎_的像素地牢 作者：_00-Evan_\n联系方式：_Evan@ShatteredPixel.com_" +
							"\n_像素地牢_作者：_watabou_\n联系方式：_pixeldungeon@watabou.ru_" +
							"\n\n感谢_@笑看LZ撸管_制作的新法杖（如缓慢、狂乱等）贴图。" +
							"\n\n同时感谢以下百度贴吧吧友的支持（排名不分先后）：\n" +
							"@youxia5325，@雷暴jj怪，@as1169344561，@圣人川川摩羯，@zpjsunny，@远逝之光，@zhbom，@zhongzhengze，@屠城管，@fyf672，@如日飞仙"));
				}
			};
			btn.setRect((colWidth - 80) / 2, title.y + title.height() + 32, 80, 18);
			add(btn);

			btn3 = new NewRedButton("游戏须知") {
				@Override
				protected void onClick() {
					parent.add(new WndMessage("1、如果你在游戏中遇到了闪退问题，请尽量按故障排查中写的方法来收集错误报告！" +
							"只要是在“已知bug总览”列表之外的问题，基本都是未经我确认的潜在型问题。也就是说，只要你不发报告，" +
							"我就根本无法定位错误，更谈不上修复了。所以，如果你希望你遇到的问题被尽快修复，请尽量在出错时发送报告！\n\n" +
							"2、如果你在游戏中遇到了游戏流程方面的问题（如卡关、没有游戏目标），或物品类问题（如精金物品），" +
							"请在提问前先完整地查看一遍帮助！如果某人提问了帮助中已经写过的问题，我是一定不会回答的，至于是否" +
							"有其他人回答就看他的运气了。"));
				}
			};
			btn3.setRect((colWidth - 80) / 2, title.y + title.height() + 52, 80, 18);
			add(btn3);

			btn4 = new NewRedButton("已知bug总览") {
				@Override
				protected void onClick() {
					parent.add(new WndMessage("1、请_不要切换语言_！否则出现任何后果概不负责！！" +
							"\n\n2、有一些_推羊_的关卡似乎还有闪退的情况。我没有时间来通关推羊关卡，所以只靠我自己的力量来修复比较困难。" +
							"如果你愿意对此mod做出一些贡献，请将推羊关卡里会导致闪退的_详细步骤_告诉我（有_错误报告_更好），我会尝试修复。" +
							"\n\n3、现有一小部分在游戏中突然无法进行任何操作的情况。遇到此类情况的，请退出游戏并重新进入，并向我反馈" +
							"出现错误之前的操作以及出错的层数（基本上一个截图就能说明一切），我需要大量的信息来判断此错误的原因。" +
							"\n\n4、现已知有一些与电击类攻击有关的闪退问题，包括电法杖、电附魔武器以及怪物放电。不过，由于没有人能提供" +
							"任何有效的错误报告，我暂时无法修复。希望有好心人（我也不想这么说，真的很无奈）在出错时能提供一份有效的错误报告" +
							"来帮助我修复此问题。"));
				}
			};
			btn4.setRect((colWidth - 80) / 2, title.y + title.height() + 72, 80, 18);
			add(btn4);

			Archs archs = new Archs();
			archs.setSize(Camera.main.width, Camera.main.height);
			addToBack(archs);

			ExitButton btnExit = new ExitButton();
			btnExit.setPos(Camera.main.width - btnExit.width(), 0);
			add(btnExit);

			fadeIn();
		}
	}

	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchNoFade(TitleScene.class);
	}
}
