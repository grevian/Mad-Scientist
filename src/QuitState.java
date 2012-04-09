import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


public class QuitState extends BasicGameState {

	private int exitDelay = 4000;
	public static int ID = 4;
	
	private String[] credits = { "Thanks for playing!", "", "Team:", "   Josh Hayes-Sheen", "   Kenneth Briggs", "   Leon Fiddler" };

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		System.out.println("Entered Quit State");
	}
	
	@Override
	public void leave(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		arg2.setColor(Color.gray);
		for ( int i = 0; i < credits.length; i++ )
			arg2.drawString(credits[i], (arg0.getWidth()/2)-100, (i*20)+200);

	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
		
		// Reduce remaining time by the update delta
		exitDelay -= arg2;
		
		// if we've displayed our full time period, exit.
		if ( exitDelay <= 0 )
		{
			arg0.exit();
		}
		

	}

}
