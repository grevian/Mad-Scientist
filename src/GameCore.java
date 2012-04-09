
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

public class GameCore {
	
	private ArrayList<Entity> Minions = new ArrayList<Entity>();
	private ArrayList<Entity> Enemies = new ArrayList<Entity>();
	private ArrayList<Place> Places = new ArrayList<Place>();
	private ArrayList<UseableObject> Objects = new ArrayList<UseableObject>();
	
	private SlickGame mGame;
	private PathMask mMask;
	private Properties configFile;
	
	private HashMap<String, Integer> mValueStore = new HashMap<String, Integer>();
	
	public GameCore(SlickGame slickGame) throws SlickException
	{
		mGame = slickGame;
		mValueStore.put("GameTime", 0);
		configFile = ConfigReader.readConfig("res/config.cfg");
		if ( configFile == null )
			throw new SlickException("Could not load game configuration.");
	}
	
	public MinionTask unitIdle(Minion mUnit)
	{
		return null;
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {

		// Update the game clock
		mValueStore.put("GameTime", mValueStore.get("GameTime")+delta);
		
		for ( Entity e: Minions )
			e.update(container, game, delta);
		
		for ( Entity e: Enemies )
			e.update(container, game, delta);
		
		for ( UseableObject o: Objects )
			o.update(container, game, delta);
		
	}
	
	public void addObject(UseableObject mObj)
	{
		Objects.add(mObj);
	}	
	
	public void addObject(UseableObject mObj, Coord coord)
	{
		mObj.setLocation(coord);
		this.addObject(mObj);
	}
	
	public int getGameTime() {
		return mValueStore.get("GameTime");
	}

	public void setLevelPath(PathMask pathMask) {
		this.mMask = pathMask;		
	}

	// FIXME: This is an ugly hack required by the image loading being deferred, we call this once the engine is up and running
	public void initFinal() {
		// Generate the pathfinding data from the mask image
		mMask.GenerateGrid();		
	}

	public ArrayList<UseableObject> getObjects() {
		return Objects;
	}

	public ArrayList<Entity> getEnemies() {
		return Enemies;
	}
	
	public ArrayList<Entity> getMinions() {
		return Minions;
	}

	public PathMask getLevel() {
		return mMask;
	}

	public void addMinion(Entity mMinion)
	{
		Minions.add(mMinion);
	}	
	
	public void addMinion(Entity mMinion, Coord coord)
	{
		Log.debug("Placing Minion " + mMinion.getName() + " at coordinates " + coord.toString() ); 
		mMinion.setPosition(coord.getX(), coord.getY());
		this.addMinion(mMinion);
	}
	
	public Properties getConfiguration()
	{
		return configFile;
	}

	public SlickGame getGame() {
		return mGame;
	}

	public ArrayList<Place> getPlaces() {
		return Places;
	}

	public void removeMinion(Minion mUnit) {
		Minions.remove(mUnit);
	}

	public int getValue(String string) {
		return mValueStore.get(string);
	}

	public void setValue(String string, int i) {
		mValueStore.put(string, i);		
	}

	public void sendMinion(String unitName, Place place) {
		// TODO Auto-generated method stub
		Log.info("Request to send " + unitName + " to " + place.getName());
	}

	public void callMinion(String unitName, Place place) {
		// TODO Auto-generated method stub
		Log.info("Request to recall " + unitName + " from " + place.getName());
	}
	
}
