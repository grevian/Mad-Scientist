import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.Log;


public class IntroState extends BasicGameState {

	public static int ID = 0;
	private long startTime;
	private boolean skip = false;
	String[] intro = {
			"They doubted you...",
			"They mocked you...",
			"They called you insane...",
			"But today, you will prove them all wrong!  Your servants stand at the ready,",
			"send them out into the town to raid the village and the graveyard, put them",
			"to work around your lab, and raise an army of re-animated soldiers, with the",
			"biggest and baddest of them all as your champion!"	
	};
	private int perkTimer = 0;
	private int displayLines = 0;
	
	@Override
	public void enter(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		Log.info("Entered Intro State");	
	}

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		startTime = 0;
	}

	@Override
	public void leave(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		
		arg2.setBackground(Color.black);
		
		// Draw the image centered on the screen
		int yOffset = 300;
		for (int i = 0; i < displayLines; i++ )
		{
			arg2.drawString(intro[i], 50, yOffset);
			yOffset += arg2.getFont().getLineHeight()+3;
		}
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
	
		perkTimer += arg2;
		if ( perkTimer >= 1600 )
		{
			displayLines  = Math.min(intro.length, displayLines + 1);
			perkTimer = 0;
		}
		
		startTime += arg2;
		
		if ( startTime > 18000 ) // After six seconds, we automatically move forward
			skip = true;
		
		if (skip)
		{
			arg1.enterState(MenuState.ID, new FadeOutTransition(), new FadeInTransition());
		}

	}

	@Override
	public void mouseClicked(int arg0, int arg1, int arg2, int arg3) {
		skip = true;
	}

	@Override
	public void keyPressed(int arg0, char arg1) {
		skip = true;
	}

}
