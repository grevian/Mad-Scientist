import java.util.Properties;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


public class BuyState extends BasicGameState {

	public static int ID = 10;
	private GameCore mCore;
	private BuyUnitPanel[] unitPanels;
	private ItemStorePanel[] itemPanels;
		
	// TODO: Move this into the config file
	private static final String themeDir = "./res/themes/";
	
	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		mCore = ((SlickGame)arg1).getGameCore();
		
		String[] units = mCore.getConfiguration().getProperty("units").split(",");
		String[] items = mCore.getConfiguration().getProperty("items").split(",");
		
		unitPanels = new BuyUnitPanel[units.length];
		itemPanels = new ItemStorePanel[items.length];
		
		int xOffset = 50;
		int yOffset = 20;
		for ( int i = 0; i < units.length; i++ )
		{
			unitPanels[i] = new BuyUnitPanel(units[i], mCore, xOffset, yOffset);
			yOffset += unitPanels[i].getHeight();
			if ( yOffset + unitPanels[i].getHeight() > arg0.getHeight() )
			{
				// my crappy layout attempt
				yOffset = 20;
				xOffset += unitPanels[0].getWidth();
			}
		}
		
		for ( int i = 0; i < items.length; i++ )
		{
			itemPanels[i] = new ItemStorePanel(items[i], mCore, xOffset, yOffset);
			yOffset += itemPanels[i].getHeight();
			if ( yOffset + itemPanels[i].getHeight() > arg0.getHeight() )
			{
				// my crappy layout attempt
				yOffset = 20;
				xOffset += 300;
			}
		}

	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		
		for ( BuyUnitPanel b: unitPanels )
			b.render(arg0, arg1, arg2);

		for ( ItemStorePanel b: itemPanels )
			b.render(arg0, arg1, arg2);

		OverlayGUI.renderConsole(arg2);
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
		// TODO Auto-generated method stub
		OverlayGUI.update(arg2);
	}
	
	@Override
	public void keyPressed(int key, char arg1) {
		if ( key == Input.KEY_ESCAPE )
		{
			mCore.getGame().enterState(CastleScreen.ID);
		}
	}
	
	
}
