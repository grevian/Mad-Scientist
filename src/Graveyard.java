import java.util.Properties;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;


public class Graveyard extends Place {
	int x, y;
	Image mapImage;
	private int GatherStepTime_c = 0;
	private int GatherStepTime = 10000;
	private int PARTS_PER_UNIT = 3;
	private static final int UI_OFFSET = 200;	
	
	private static final String themeDir = "./res/themes/";
	private static int IMAGE = 0, X = 1, Y = 2;
	
	public Graveyard(GameCore mCore) throws SlickException
	{
		super("Graveyard", mCore);
		Properties mConfig = mCore.getConfiguration();
		Properties mTheme = ConfigReader.readConfig(themeDir + mConfig.getProperty("theme"));
		String[] townAttributes = mTheme.getProperty("graveyard").split(",");
		x = Integer.parseInt(townAttributes[X]);
		y = Integer.parseInt(townAttributes[Y]);
		mapImage = new Image(themeDir + mTheme.getProperty("folder") + "/" + townAttributes[IMAGE]);
		getCore().setValue("Parts", 0);
	}
	
	@Override
	public Image getImage() {
		return mapImage;
	}

	@Override
	public Coord getPosition() {
		return new Coord(x, y);
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		arg2.drawImage(mapImage, x, y+UI_OFFSET);
	}

	@Override
	public void addUser(Minion mUnit)
	{
		// Hide the Minion from the main screen, and remove them from the gameCore list
		super.addUser(mUnit);
		getCore().removeMinion(mUnit);
		mUnit.setOnScreen(false);
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta)
	throws SlickException {
		super.update(container, game, delta);
		
		GatherStepTime_c += delta;
		if ( GatherStepTime_c >= GatherStepTime )
		{
			GatherStepTime_c = 0;
			// TODO: Add resources for each minion that's present
			getCore().setValue("Parts", getCore().getValue("Parts") + (PARTS_PER_UNIT * getUsers().size()));
			if ( getUsers().size() > 0 )
				Log.debug("Recieved Parts from rummaging through the graveyard, Current Parts Available: " + getCore().getValue("Parts"));
		}
		
	}

}
