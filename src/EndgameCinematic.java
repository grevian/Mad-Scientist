import java.io.File;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;


public class EndgameCinematic extends BasicGameState {

	public static int ID = 12;
	private Animation mWalk, mSmash, mIdle, current;
	private Image mMap, Village, Graveyard, Castle, smashedVillage;
	private ParticleSystem mEffects;
	private Sound Laugh;
	
	private long timeDelta = 0;
	
	private String[] outro = {
			"You've done it!  You've really done it!  Now the whole world will have",
			"to take notice of your brilliance!  It's alive!  Fame, fortune, and",
			"respect await you!  Your creation is beautiful, it's unstoppable,",
			"it, it...",
			"It doesn't listen very well, maybe we should have given it some ears?",
			"It doesn't seem very happy with the world, or us!  It's coming this way,", 
			"RUN!"
	};
	
	int stage = 0;
	private int monster_x, monster_y;
	
	private int village_x, village_y, graveyard_x, graveyard_y, castle_x, castle_y;
	
	ParticleSystem explosion;
	ConfigurableEmitter fire;
	private int displayLines = 0;
	private int displayDelta = 0;
	private int displayDelay;
	
	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		mIdle= new Animation(new SpriteSheet("./res/monster-walk.png", 54, 60, 10), 400);
		mIdle.setLooping(false);
		mWalk = new Animation(new SpriteSheet("./res/monster-walk.png", 54, 60, 10), 400);
		mWalk.setLooping(true);
		mSmash = new Animation(new SpriteSheet("./res/monster-smash.png", 60, 68, 2), 1000);
		mSmash.setLooping(false);
		mMap = new Image("./res/themes/default/map.png");
		Village = new Image("./res/themes/default/village.png");
		smashedVillage = new Image("./res/themes/default/village.png"); // TODO: Get a wrecked village image
		Graveyard = new Image("./res/themes/default/graveyard.png");
		Castle = new Image("./res/themes/default/castle.png");
		mEffects = new ParticleSystem("./res/dot.png", 2000);
		mEffects.setUsePoints(false);
		mEffects.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
		Laugh = new Sound("./res/Sounds/Evil-Laugh.wav");
		
		village_x = 450;
		village_y = 500;
		graveyard_x = 350;
		graveyard_y = 300;
		castle_x = 150;
		castle_y = 400;


		try {
			File xmlFile = new File("./res/explosion.xml");
			explosion = ParticleIO.loadConfiguredSystem(xmlFile);
			explosion.setPosition(village_x+(Village.getWidth()/2),village_y+(Village.getHeight()/2));
			explosion.setUsePoints(false);
			explosion.setRemoveCompletedEmitters(false);
			explosion.setVisible(false);
			
			xmlFile = new File("./res/flame.xml");
			fire = ParticleIO.loadEmitter(xmlFile);
			fire.setPosition(0,0);
			explosion.addEmitter(fire);
			
			ConfigurableEmitter fire2 = fire.duplicate();
			fire2.setPosition(100, 50);
			explosion.addEmitter(fire2);
			
			ConfigurableEmitter fire3 = fire.duplicate();
			fire3.setPosition(-40, 40);
			explosion.addEmitter(fire3);
			
			
		} catch (Exception e) {
			System.out.println("Exception: " +e.getMessage());
			e.printStackTrace();
		}
		
		
		monster_x = castle_x+40;
		monster_y = castle_y+30;
		
		current = mSmash;
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		mMap.draw(0,0);
		Village.draw(village_x, village_y);
		Graveyard.draw(graveyard_x, graveyard_y);
		Castle.draw(castle_x, castle_y);
		current.draw( monster_x, monster_y );
		mEffects.render();
		explosion.render();
		
		arg2.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));
		arg2.fillRoundRect(50, 0, 700, 200, 10);
		arg2.setColor(Color.white);
		int yOffset = 0;
		for ( int i = 0; i < displayLines; i++ )
		{
			arg2.drawString(outro[i], 55, yOffset);
			yOffset += arg2.getFont().getLineHeight() + 3;
		}
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
		
		displayDelay += arg2;
		if ( displayDelay >= 8000 )
		{
			displayDelta += arg2;
			if ( displayDelta >= 2000 )
			{
				displayLines = Math.min(outro.length, displayLines+1);
				displayDelta = 0;
			}
		}
		
		
		current.update(arg2);
		mEffects.update(arg2);
		explosion.update(arg2);
		
		timeDelta += arg2;
		
		// Monster roars around the castle a bit (3 seconds)
		if ( stage == 0 )
		{
			if ( timeDelta >= 3000 )
			{
				stage++;
				Laugh.play();
				current = mSmash;
			}
		}
		// Monster starts walking to town (After a 1 second pause)
		if ( stage == 1 )
		{
			if ( timeDelta >= 4000 )
			{
				current = mWalk;
				mSmash.restart();
				stage++;
				timeDelta = 0;
			}
		}
		// Monster walks (dunno how long it'll take)
		if ( stage == 2 )
		{
			if ( timeDelta >= 100 )
			{
				if ( monster_x <= village_x+30 )
					monster_x += 5;
				if (monster_y <= village_y+30)
					monster_y += 5;
				timeDelta = 0;
			}
			
			if ( monster_x >= village_x+30 && monster_y >= village_y+30 )
				stage++;
		}
		// Monster arrives, reset the timer
		if ( stage == 3 )
		{
			current = mSmash;
			mSmash.setLooping(true);
			timeDelta = 0;
			stage++;
			Laugh.play();
		}
		
		// Monster tears shit up for (4 seconds)
		if ( stage == 4 )
		{
			if ( timeDelta >= 4000 )
				stage++;
		}
		
		// Town is in ruins
		if ( stage == 5 )
		{
			Village = smashedVillage;
			playEffects();
			stage++;
			current = mWalk;
			mSmash.restart();
			timeDelta = 0;
		}
		
		// Wait around for a sec to enjoy it
		if ( stage == 6 )
		{
			if ( timeDelta >= 3000 )
			{
				stage++;
			}
		}
		
		// Wander up to the graveyard
		if ( stage == 7 )
		{
			if ( timeDelta >= 100 )
			{
				if ( monster_x >= graveyard_x+30 )
					monster_x -= 5;
				if (monster_y >= graveyard_y+30)
					monster_y -= 5;
				timeDelta = 0;
			}
			if ( monster_x <= graveyard_x+30 && monster_y <= graveyard_y+30 )
			{
				current.setLooping(false);
				stage++;
				timeDelta = 0;
			}
		}
		
		// Wait around for a sec to enjoy it
		if ( stage == 8 )
		{
			if ( timeDelta >= 2000 )
			{
				current.setLooping(true);
				stage++;
				timeDelta = 0;
			}
		}
		
		// Start walking to the castle...
		if ( stage == 9 )
		{
			if ( timeDelta >= 100 )
			{
				if ( monster_x >= castle_x+30 )
					monster_x -= 5;
				if (monster_y <= castle_y+30)
					monster_y += 5;
				timeDelta = 0;
			}
			if ( monster_x <= castle_x+30 && monster_y >= castle_y+30 )
			{
				current.setLooping(false);
				stage++;
				timeDelta = 0;
				arg1.enterState(QuitState.ID, new FadeOutTransition(), new FadeInTransition() );
			}
		}
		
			
	}

	private void playEffects() {
		explosion.setVisible(true);
		explosion.reset();
	}

}
