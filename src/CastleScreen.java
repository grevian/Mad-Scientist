import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

public class CastleScreen extends BasicGameState {

	public static int ID = 6;
	private GameCore mCore;
	private Image mainLevel;
	private CastleGUI mGui;
	
	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {

		mainLevel = new Image("res/VisMap.png");
		mCore = ((SlickGame)arg1).getGameCore();
		mGui = new CastleGUI(mCore, this);
	}
	
	@Override
	public void enter(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		Log.debug("Main Game State Entered");
		
		mGui.enable();
	}

	@Override
	public void leave(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		Log.debug("Main Game State Left");
		
		mGui.disable();
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		
		arg2.drawImage(mainLevel, 0, 0);
		
		for ( UseableObject o: mCore.getObjects() )
			o.render(arg0, arg1, arg2);
		
		for ( Entity e: mCore.getMinions() )
			e.render(arg0, arg1, arg2);
		
		for ( Entity e: mCore.getEnemies() )
			e.render(arg0, arg1, arg2);		
		
		mGui.render(arg0, arg1, arg2);
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
		
		mCore.update(arg0, arg1, arg2);
		mGui.update(arg0, arg1, arg2);
		
	}
	
	public void mouseClicked(int button,
            int x,
            int y,
            int clickCount)
	{
		// Shortcut out if it's a click on the GUI
		if ( y > 600 )
			return;
		
		// TODO: Display information for whatever was clicked on here
	}
	
	public void keyPressed(int key, char arg1) {
		if ( key == Input.KEY_D )
		{
			if ( Boolean.parseBoolean(mCore.getConfiguration().getProperty("debug")) )
				mCore.getGame().enterState(DebugState.ID);
		}
		if ( key == Input.KEY_A )
		{
			if ( Boolean.parseBoolean(mCore.getConfiguration().getProperty("debug")) )
				for ( Place p: mCore.getPlaces() )
				{
					if ( p.getName().equalsIgnoreCase("town") )
					{
						((Town)p).spawnMob();
						break;
					}
				}
		}
	}
}
