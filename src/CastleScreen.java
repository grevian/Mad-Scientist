import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
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
		mCore = ((SlickGame)arg1).getGameCore();
		mainLevel = new Image("res/VisMap.png");
		mCore.setLevelPath(new PathMask("res/MapMask.png", 10, mCore));
		mGui = new CastleGUI(mCore, this);
	}
	
	@Override
	public void enter(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		Log.debug("Main Game State Entered");
		
		mCore.addObject(new Bed(mCore), new Coord(30, 7));
		mCore.addObject(new Bed(mCore), new Coord(35, 7));
		mCore.addObject(new Bed(mCore), new Coord(40, 7));
		mCore.addObject(new PortalToTown(mCore), new Coord(60, 7));
		
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
		
		try {
			Log.info("Placing Hunchbacks around point ["+x+"|"+y+"]");
			
			Hunchback tHunch = new Hunchback(mCore.getLevel());
			mCore.addMinion(tHunch);
			tHunch.setPosition(x/10, y/10);
			tHunch.doExampleTask();
			
			
		} catch (SlickException e) {
			e.printStackTrace();
			Log.error("Something fucked up!");
		}
	}
	
}
