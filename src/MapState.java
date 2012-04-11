import java.util.ArrayList;
import java.util.Properties;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;


public class MapState extends BasicGameState {

	public static int ID = 7;
	
	// TODO: Move this into the config file
	private static final String themeDir = "./res/themes/";
	
	private static int UIOffset = 200; 
	private ArrayList<MapElement> mapElementList = new ArrayList<MapElement>();
	private ArrayList<UnitController> mUnitControllers = new ArrayList<UnitController>();
	private Image backgroundMap, plus, minus;
	private GameCore mCore;

	private MapElement currentSelection;
	
	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		mCore = ((SlickGame)arg1).getGameCore();
		Properties mConfig = mCore.getConfiguration();
		Properties mTheme = ConfigReader.readConfig(themeDir + mConfig.getProperty("theme"));
		backgroundMap = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("main-map"));
		plus = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("plus"));
		minus = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("minus"));
		
		for ( int i = 0; i < mCore.getPlaces().size(); i++ )
		{
			Place mTemp = mCore.getPlaces().get(i);
			
			for ( String unit : mConfig.getProperty("units").split(",") )
			{
				mCore.setValue("pending-call-from-"+mTemp.getName()+"-for-" + unit, 0);
				mCore.setValue("pending-send-to-"+mTemp.getName()+"-for-" + unit, 0);	
			}
			
			if ( mTemp.getName() == "Town" )
				mapElementList.add(new MapElement((Town)mTemp, this));
			else if ( mTemp.getName() == "Graveyard" )
				mapElementList.add(new MapElement((Graveyard)mTemp, this));
		}
		
		this.setSelected(mapElementList.get(0).getPlace());
		
		String[] units = mConfig.getProperty("units").split(",");
		
		for ( int i = 0; i < units.length; i++ )
		{
			mUnitControllers.add(new UnitController(mCore, units[i], ((i+1)*160), 20, this));
		}
		
		for (MapElement m : mapElementList) {
			m.disable();
		}
		for (UnitController uc : mUnitControllers) {
			uc.disable();
		}
	}
	
	public void setSelected(Place mPlace)
	{
		Log.info(mPlace.getName() + " selected on map screen");
		
		for (MapElement m : mapElementList) {
			if ( m.getPlace() == mPlace )
			{
				currentSelection = m;
				m.setSelected(true);
			}
			else
				m.setSelected(false);
		}
	}
	
	public MapElement getSelected()
	{
		return currentSelection;
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		arg2.drawImage(backgroundMap, 0, 0);
		for ( int i = 0; i < mapElementList.size(); i++ )
		{
			mapElementList.get(i).render(arg0, arg1, arg2);
		}
		for ( int i = 0; i < mUnitControllers.size(); i++ )
		{
			mUnitControllers.get(i).render(arg0, arg1, arg2);
		}
		
		OverlayGUI.renderConsole(arg2);
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
		OverlayGUI.update(arg2);
	}
	
	@Override
	public void enter(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		Log.debug("Map State Entered");
		for (MapElement m : mapElementList) {
			m.enable();
		}
		for (UnitController uc : mUnitControllers) {
			uc.enable();
		}

	}
	
	@Override
	public void leave(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		for (MapElement m : mapElementList) {
			m.disable();
		}
		for (UnitController uc : mUnitControllers) {
			uc.disable();
		}
		Log.debug("Map State Left");		
	}
	
	@Override
	public void keyPressed(int key, char arg1) {
		if ( key == Input.KEY_ESCAPE )
		{
			mCore.getGame().enterState(CastleScreen.ID);
		}
	}
	
	private class MapElement
	{
		private final Place mPlace;
		private MouseOverArea mElement;
		private boolean selected = false;
		
		public MapElement(final Place mPlace, final MapState mMap) throws SlickException
		{
			this.mPlace = mPlace;
			Coord mPos = mPlace.getPosition();
			mElement = new MouseOverArea(mCore.getGame().getContainer(), mPlace.getImage(), mPos.getX(), mPos.getY()+UIOffset)
			{
				public void mouseClicked(int button,
                        int x, int y, int clickCount)
				{
					if (x >= this.getX()
							&& x < this.getX() + this.getWidth()
							&& y >= this.getY()
							&& y <= this.getY() + this.getHeight()) 
					{
						mMap.setSelected(mPlace);
					}
				}
			};
		}
		
		public Place getPlace() {
			return mPlace;
		}

		public void disable() {
			mElement.setAcceptingInput(false);
		}

		public void enable() {
			mElement.setAcceptingInput(true);
		}
		
		public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
		throws SlickException {
			mElement.render(arg0, arg2);
			mPlace.render(arg0, arg1, arg2);
			if ( selected )
			{
				// FIXME: There are actually push/pop methods on arg2, but they don't work as expected?
				Color popColor = arg2.getColor();
				arg2.setColor(Color.red);
				arg2.setLineWidth(3);
				arg2.drawRoundRect(mElement.getX()-5, mElement.getY()-5, mElement.getWidth()+5, mElement.getHeight()+5, 20);
				arg2.setColor(popColor);
			}
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public boolean isSelected() {
			return selected;
		}
		
	}
	
	private class UnitController
	{
		private Image unitImage;
		private MouseOverArea sendUnit;
		private MouseOverArea callUnit;
		private String unitName;
		private int x, y;
		private GameCore mCore;
		private MapState mMap;
	
		private final static int padding = 2;
		
		public UnitController(final GameCore mCore, final String unitName, int xpos, int ypos, final MapState mMap ) throws SlickException
		{
			Log.info("Constructing Map Control for unit " + unitName);
			this.mMap = mMap;
			this.x = xpos;
			this.y = ypos;
			this.unitName = unitName;
			this.mCore = mCore;
			
			Properties mConfig = mCore.getConfiguration();
			Properties mTheme = ConfigReader.readConfig(themeDir + mConfig.getProperty("theme"));
			Properties unitConfig = ConfigReader.readConfig("./res/" + unitName + ".cfg");
			
			unitImage = new Image(unitConfig.getProperty("icon"));
			final int unitWidth = unitImage.getWidth();
			
			plus = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("plus"));
			minus = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("minus"));
			
			sendUnit = (new MouseOverArea(mCore.getGame().getContainer(), plus, xpos + unitWidth + padding, ypos)
			{
				public void mouseClicked(int button, int x, int y,
						int clickCount) 
				{
					if (x >= this.getX()
							&& x < this.getX() + this.getWidth()
							&& y >= this.getY()
							&& y <= this.getY() + this.getHeight()) 
					{
						mCore.sendMinion(unitName, mMap.getSelected().getPlace());
						String sendName = "pending-send-to-"+mMap.getSelected().getPlace().getName()+"-for-" + unitName;
						mCore.setValue(sendName, mCore.getValue(sendName)+1);
					}
				}
			});
			
			callUnit = (new MouseOverArea(mCore.getGame().getContainer(), minus, xpos + unitWidth + padding, ypos + 40)
			{
				public void mouseClicked(int button, int x, int y,
						int clickCount) 
				{
					if (x >= this.getX()
							&& x < this.getX() + this.getWidth()
							&& y >= this.getY()
							&& y <= this.getY() + this.getHeight()) 
					{
						mCore.callMinion(unitName, mMap.getSelected().getPlace());
					}
				}
			});
		}
		
		public void enable() {
			sendUnit.setAcceptingInput(true);
			callUnit.setAcceptingInput(true);
		}
		
		public void disable() {
			sendUnit.setAcceptingInput(false);
			callUnit.setAcceptingInput(false);
		}

		public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
		throws SlickException {
			int unitCount = 0;
			int unitPresentCount = 0;
			
			for (Entity m: 	mCore.getMinions() )
			{
				if ( m.getTypeName().toLowerCase().startsWith(unitName) )
					unitCount++;
			}
			
			for ( Entity m: mMap.getSelected().getPlace().getUsers() )
			{
				if ( m.getTypeName().toLowerCase().startsWith(unitName) )
					unitPresentCount++;
			}
			
			
			arg2.drawImage(unitImage, x, y);
			
			String sendName = "pending-send-to-"+mMap.getSelected().getPlace().getName()+"-for-" + unitName;
			
			arg2.drawString(Integer.toString(unitPresentCount) + "/" + Integer.toString(mCore.getValue(sendName)), x+unitImage.getWidth() + plus.getWidth(), y+3);
			arg2.drawString(Integer.toString(unitCount), x+unitImage.getWidth() + plus.getWidth(), y+45);
			arg2.drawString(unitName, x, y+unitImage.getHeight()+40);
			
			callUnit.render(arg0, arg2);
			sendUnit.render(arg0, arg2);
		}
	}

}
