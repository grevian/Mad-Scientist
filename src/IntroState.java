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
	private Image intro;
	private long startTime;
	private boolean skip = false;
	
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
		intro = new Image("res/intro.PNG");
		startTime = 0;
	}

	@Override
	public void leave(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		
		intro.destroy(); // Make sure it's cleaned up, probably not needed
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		
		arg2.setBackground(Color.black);
		
		// Draw the image centered on the screen
		arg2.drawImage(intro, (arg0.getWidth()/2)-(intro.getWidth()/2),
				(arg0.getHeight()/2)-(intro.getHeight()/2));	
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
		
		startTime += arg2;
		
		if ( startTime > 6000 ) // After six seconds, we automatically move forward
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
