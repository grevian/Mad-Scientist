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


public class GameLost extends BasicGameState {

	public static int ID = 13;
	private Image mMap, Village, Graveyard, Castle;
	private ParticleSystem mEffects;
	
	private boolean exploded = false;
	
	private String[] outro = {
			"They just couldn't understand...",
			"Everything we worked so hard for, everything we built, everything we",
			"learned, all up in flames...",
			"If only we had defended our lab better, maybe we could have had",
			"enough time to finish the Masterpiece.  Come on now, let's get going",
			"to the next town before somebody sees through our disguises, because",
			"you really don't make a pretty girl."
	};
	
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
		mMap = new Image("./res/themes/default/map.png");
		Village = new Image("./res/themes/default/village.png");
		Graveyard = new Image("./res/themes/default/graveyard.png");
		Castle = new Image("./res/themes/default/castle.png");
		mEffects = new ParticleSystem("./res/dot.png", 2000);
		mEffects.setUsePoints(false);
		mEffects.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
		
		village_x = 450;
		village_y = 500;
		graveyard_x = 350;
		graveyard_y = 300;
		castle_x = 150;
		castle_y = 400;


		try {
			File xmlFile = new File("./res/explosion.xml");
			explosion = ParticleIO.loadConfiguredSystem(xmlFile);
			explosion.setPosition(castle_x+(Castle.getWidth()/2),castle_y+(Castle.getHeight()/2));
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

	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		mMap.draw(0,0);
		Village.draw(village_x, village_y);
		Graveyard.draw(graveyard_x, graveyard_y);
		Castle.draw(castle_x, castle_y);
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
		
		if ( !exploded )
		{
			exploded = true;
			playEffects();
		}
		displayDelay += arg2;
		if ( displayDelay >= 2000 )
		{
			displayDelta += arg2;
			if ( displayDelta >= 2000 )
			{
				displayLines = Math.min(outro.length, displayLines+1);
				displayDelta = 0;
			}
		}

		mEffects.update(arg2);
		explosion.update(arg2);
					
	}

	private void playEffects() {
		explosion.setVisible(true);
		explosion.reset();
	}

}
