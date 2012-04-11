import java.util.Properties;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

public class CastleGUI {
	
	private Image GUIBackground, MapButtonImage, ResourceButtonImage, ObjectiveButtonImage, StoreButtonImage;
	private MouseOverArea ResourceButton, MapButton, ObjectiveButton, StoreButton;
	private Properties mTheme;
	
	private static String themeDir = "./res/themes/";
	private Properties mConfig;
	private static int offset = 600;
	private static int padding = 30;
	private Sound okClick;
	
	public void enable()
	{
		ResourceButton.setAcceptingInput(true);
		MapButton.setAcceptingInput(true);
		ObjectiveButton.setAcceptingInput(true);
		StoreButton.setAcceptingInput(true);
	}
	
	public void disable()
	{
		ResourceButton.setAcceptingInput(false);
		MapButton.setAcceptingInput(false);	
		ObjectiveButton.setAcceptingInput(false);
		StoreButton.setAcceptingInput(false);
	}
	
	public CastleGUI(GameCore mCore, CastleScreen mScreen) throws SlickException
	{
		mConfig = mCore.getConfiguration();
		mTheme = ConfigReader.readConfig(themeDir + mConfig.getProperty("theme"));
		
		okClick = new Sound(mConfig.getProperty("ClickSound"));
		
		try {
			GUIBackground = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("CastleGuiBackground"));
			MapButtonImage = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("MapButton"));
			ResourceButtonImage = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("ResourceButton"));
			ObjectiveButtonImage = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("ObjectiveButton"));
			StoreButtonImage = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("StoreButton"));
			final GameCore mGC = mCore;
			
			ResourceButton = new MouseOverArea(mCore.getGame().getContainer(), ResourceButtonImage, padding, offset)
			{
				public void mouseClicked(int button,
                        int x, int y, int clickCount)
				{
					if ( x >= this.getX() && x < this.getX()+this.getWidth() && y >= this.getY() && y <= this.getY() + this.getHeight())
					{
						okClick.play();
						mGC.getGame().enterState(ResourceState.ID);
					}
				}
			};
			
			MapButton = new MouseOverArea(mCore.getGame().getContainer(), MapButtonImage, ResourceButton.getWidth()+(padding*2), offset)
			{
				public void mouseClicked(int button,
                        int x, int y, int clickCount)
				{
					if ( x >= this.getX() && x < this.getX()+this.getWidth() && y >= this.getY() && y <= this.getY() + this.getHeight())
					{
						okClick.play();
						mGC.getGame().enterState(MapState.ID);
					}
				}
			};
			
			ObjectiveButton = new MouseOverArea(mCore.getGame().getContainer(), ObjectiveButtonImage, ResourceButton.getWidth()+MapButton.getWidth()+(padding*3), offset)
			{
				public void mouseClicked(int button,
                        int x, int y, int clickCount)
				{
					if ( x >= this.getX() && x < this.getX()+this.getWidth() && y >= this.getY() && y <= this.getY() + this.getHeight())
					{
						okClick.play();
						mGC.getGame().enterState(ObjectiveState.ID);
					}
				}
			};
			
			StoreButton = new MouseOverArea(mCore.getGame().getContainer(), StoreButtonImage, ObjectiveButton.getWidth()+ResourceButton.getWidth()+MapButton.getWidth()+(padding*3), offset)
			{
				public void mouseClicked(int button,
                        int x, int y, int clickCount)
				{
					if ( x >= this.getX() && x < this.getX()+this.getWidth() && y >= this.getY() && y <= this.getY() + this.getHeight())
					{
						okClick.play();
						mGC.getGame().enterState(BuyState.ID);
					}
				}
			};
			
			this.disable();
		} catch (SlickException e) {
			Log.error("Error loading castle gui! " + e.getMessage() );
			throw e;
		}
	}

	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		arg2.drawImage(GUIBackground, 0, offset);
		MapButton.render(arg0, arg2);
		ResourceButton.render(arg0, arg2);
		ObjectiveButton.render(arg0, arg2);
		StoreButton.render(arg0, arg2);
		OverlayGUI.renderConsole(arg2);
	}
	
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
		OverlayGUI.update(arg2);
	}
	
	
}
