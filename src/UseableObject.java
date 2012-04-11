import java.util.ArrayList;
import java.util.Properties;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;


public abstract class UseableObject {

	private Properties config;
	private ArrayList<Minion> users;
	private int maxUsers, timeToUse, range;
	private Coord Location;
	private Image icon;

	// Instance counting used for uniqely naming objects
	private int iCounter = getCounter();
	private GameCore mCore;
	private static int counter = 1;
	private static int getCounter() {
		return counter++;
	}
	
	protected GameCore getCore() {
		return mCore;
	}
	
	public String toString() {
		String userList = "";
		for ( Minion m: users )
		{
			userList += m.getName();
			userList += ", ";
		}
		if ( userList.length() > 2 )
			userList.substring(0, userList.length()-2);
		return getName() + " - Users: " + userList;
	}
	
	public void initGraphics() throws SlickException
	{
		icon = new Image(this.getConfig().getProperty("icon"));
	}

	public UseableObject(String configFile, GameCore mCore) throws SlickException
	{
		this.mCore = mCore;
		config = ConfigReader.readConfig(configFile);
		maxUsers = Integer.parseInt(config.getProperty("maxusers", "1"));
		timeToUse = Integer.parseInt(config.getProperty("time-to-use", "200"));
		range = Integer.parseInt(config.getProperty("range", "1"));
		users = new ArrayList<Minion>(maxUsers);
	}
	
	public boolean addUser(Minion mUnit) 
	{
		users.add(mUnit);
		return true;
	}
	
	public boolean doEffect(Minion mUnit)
	{
		return doEffect(mUnit, "");
	}
	
	public abstract boolean doEffect(Minion minion, String string);
	
	protected Properties getConfig() {
		return config;
	}

	public int getCurrentUsers() {
		return users.size();
	}

	public Coord getLocation() {
		return Location;
	};
	
	public int getMaxUsers() {
		return maxUsers;
	}
	
	public String getName()
	{
		return getTypeName() + iCounter;
	}
	public int getRange() {
		return range;
	}
	
	public int getTimeToUse() {
		return timeToUse;
	}

	public abstract String getTypeName();

	public ArrayList<Minion> getUsers() {
		return users;
	}

	public boolean removeUser(Minion mUnit) {
		return users.remove(mUnit);
	}

	public void setLocation(Coord location) {
		Location = location;
	}
	
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		// TODO: Change icons depending on wether the item is "in use" 		
		Coord mLoc = this.getLocation();
		arg2.drawImage(icon, mLoc.getX()*10, mLoc.getY()*10); // FIXME: Another place TileSize should be used
	}

	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// TODO: Some objects may do things, for example scan around themselves and do things to nearby entities
		// Optionally, instead of relying on "doEffect", they could make changes to minions here, if their effect
		// is incremental
	}
	
	public abstract void upgrade();
	
}
