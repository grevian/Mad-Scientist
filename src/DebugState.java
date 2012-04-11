import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class DebugState extends BasicGameState {
	
	public static int ID = 11;
	private GameCore mCore;
	private GameCore.DebugHelper mHelper;
	

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		mCore = ((SlickGame)arg1).getGameCore();

		// Weird syntax...
		mHelper = mCore.new DebugHelper(mCore);
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		arg2.setBackground(Color.black);
		arg2.clear();
		
		int yOffset = 20;
		int xOffset = 10;
		
		
		// List Minions and their states
		if ( yOffset + ((mCore.getMinions().size() * 14)+28) >= 800 )
		{
			xOffset = 500;
			yOffset = 20;
		}
		arg2.drawString("Minions", xOffset, yOffset);
		yOffset += 14;
		arg2.drawLine(xOffset, yOffset, xOffset+110, yOffset);
		for ( Entity e: mCore.getMinions() )
		{
			Minion m = (Minion)e;
			arg2.drawString(m.toString(), xOffset, yOffset);
			yOffset += 14;
		}
		yOffset += 25;
		
		// List Objects and their stats
		if ( yOffset + ((mCore.getObjects().size() * 14)+28) >= 800 )
		{
			xOffset = 500;
			yOffset = 20;
		}
		arg2.drawString("Objects", xOffset, yOffset);
		yOffset += 14;
		arg2.drawLine(xOffset, yOffset, xOffset+110, yOffset);
		for ( UseableObject o: mCore.getObjects() )
		{
			arg2.drawString(o.toString(), xOffset, yOffset);
			yOffset += 14;
		}
		yOffset += 25;
		
		// List the contents of the valueStore
		if ( yOffset + ((mHelper.getValueStore().size() * 14)+28) >= 800 )
		{
			xOffset = 500;
			yOffset = 20;
		}
		arg2.drawString("Global Values", xOffset, yOffset);
		arg2.drawLine(xOffset, yOffset+14, xOffset+120, yOffset+14);
		yOffset += 20;
		for ( String s: mHelper.getValueStore().keySet() )
		{
			int v = mHelper.getValueStore().get(s);
			arg2.drawString(s + " = " + v, xOffset, yOffset);
			yOffset += 14;
		}
		yOffset += 25;
		
		// List the currently queued tasks
		if ( yOffset + ((mHelper.getTaskQueue().size() * 14)+28) >= 800 )
		{
			xOffset = 500;
			yOffset = 20;
		}
		arg2.drawString("Queued Tasks", xOffset, yOffset);
		arg2.drawLine(xOffset, yOffset+14, xOffset+120, yOffset+14);
		yOffset += 20;
		for ( MinionTask t: mHelper.getTaskQueue() )
		{
			arg2.drawString(t.getDescription(), xOffset, yOffset);
			yOffset += 14;
		}
		
		// List the current enemy states
		if ( yOffset + ((mCore.getEnemies().size() * 14)+28) >= 800 )
		{
			xOffset = 500;
			yOffset = 20;
		}
		arg2.drawString("Enemies", xOffset, yOffset);
		arg2.drawLine(xOffset, yOffset+14, xOffset+120, yOffset+14);
		yOffset += 20;
		for ( Entity e: mCore.getEnemies() )
		{
			arg2.drawString(e.toString(), xOffset, yOffset);
			yOffset += 14;
		}
		
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
	}
	public void keyPressed(int key, char arg1) {
		if ( key == Input.KEY_ESCAPE )
		{
			mCore.getGame().enterState(CastleScreen.ID);
		}
	}

}
