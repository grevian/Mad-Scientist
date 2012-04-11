
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.Log;

public class GameCore {
	
	private ArrayList<Entity> Minions = new ArrayList<Entity>();
	private ArrayList<Entity> Enemies = new ArrayList<Entity>();
	
	private ArrayList<Entity> minionKillQueue = new ArrayList<Entity>();
	private ArrayList<Entity> enemyKillQueue = new ArrayList<Entity>();
	
	private ArrayList<Place> Places = new ArrayList<Place>();
	private ArrayList<UseableObject> Objects = new ArrayList<UseableObject>();
	private ArrayList<MinionTask> availableTasks = new ArrayList<MinionTask>();
	
	private SlickGame mGame;
	private PathMask mMask;
	private Properties configFile;
	
	private HashMap<String, Integer> mValueStore = new HashMap<String, Integer>();
	
	// Stuff used in the victory condition
	private boolean GAME_WON = false;
	private int victory_timer = -1;
	private boolean finale_flags[] = {false, false, false, false, false};
	private Sound Laugh;
	private Music BGTune, EndingTune; 
	
	public GameCore(SlickGame slickGame) throws SlickException
	{
		mGame = slickGame;
		mValueStore.put("GameTime", 0);
		configFile = ConfigReader.readConfig("res/config.cfg");
		if ( configFile == null )
			throw new SlickException("Could not load game configuration.");
		gameSetup();
		setValue("Parts", 0);
		setValue("Money", 0);
		setValue("Tech", 0);
		setValue("Special Parts", 0);
		
		// Load the effects systems and resources
		Laugh = new Sound("./res/Sounds/Evil-Laugh.wav");
		BGTune = new Music("./res/music/Dungeon6.xm");
		EndingTune = new Music("./res/music/Ending2.xm");
		
		BGTune.loop();
		
		if ( configFile.getProperty("debug").equalsIgnoreCase("true") )
			initDebug();
	}
	
	private void initDebug()
	{
		setValue("Parts", 100);
		setValue("Money", 20000);
		setValue("Tech", 15);
		setValue("Special Parts", 6);
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {

		// Update the game clock
		mValueStore.put("GameTime", mValueStore.get("GameTime")+delta);
		
		// The task queue SHOULD be a priority queue, since it's not we
		// shuffle it in the update step to ensure some variety between task
		// execution order
		java.util.Collections.shuffle(availableTasks);
		
		// Remove dead minion entities before trying to update them
		for ( Entity e: minionKillQueue) 
		{
			Minions.remove(e);
			e.setOnScreen(false);
		}
		minionKillQueue.clear();
		
		int startSize = Minions.size();
		for ( int i = 0; i < Minions.size(); i++ )
		{
			if ( Minions.size() != startSize )
			{
				Log.info("Minion Update List size changed (probably a minion was removed by one of it's tasks), skipping the rest of this update iteration for safety");
				break;
			}
			Entity e = Minions.get(i);
			// If a minion is idle, give them a task that's floating in the queue
			if ( e.getStatus() == "IDLE" )
			{
				boolean foundTask = false;
				// Match the minion up to a task it is capable of doing
				for ( int p = 0; p < availableTasks.size(); p++ )
				{
					if ( availableTasks.get(p).requireType(e.getTypeName()) )
					{
						MinionTask cTask = availableTasks.remove(p);
						if ( e.addTask(cTask) )
						{
							Log.info("Added task " + cTask.getDescription() + " To idle minion " + e.getTypeName());
							foundTask = true;
							break;
						}
						else
						{
							Log.warn("Could not add task to minion for unknown reason? Minion: " + e.getTypeName() + " Task: " + cTask.getDescription());
							availableTasks.add(cTask);
						}
					}
				}
				// If there are no appropriate tasks, give the minion an "Idle" task
				if ( !foundTask )
					e.addTask(TaskLibrary.getIdleTask());
			}
			
			e.update(container, game, delta);
		}
		
		// Remove dead entities before trying to update them
		for ( Entity e: enemyKillQueue) 
		{
			Enemies.remove(e);
			e.setOnScreen(false);
		}
		enemyKillQueue.clear();
		
		for ( Entity e: Enemies )
			e.update(container, game, delta);
		
		for ( UseableObject o: Objects )
			o.update(container, game, delta);
		
		for ( Place p: Places )
			p.update(container, game, delta);
		
		// Check if we've met all the required objects to win
		GAME_WON  = ObjectiveCheck();
		
		// Check if we've hit any of the game failure conditions
		if ( FailureCheck() )
		{
			BGTune.stop();
			getGame().enterState(GameLost.ID);
		}
		
		if ( GAME_WON )
		{
			// one time victory setup and messages
			if ( victory_timer < 0 )
			{
				victory_timer = 1;
				OverlayGUI.LogMessage("You have all you need to create your masterpiece!");
				OverlayGUI.LogMessage("You rush to the lab to finish your creation");
				OverlayGUI.LogMessage("Your Minions arrive from all corners of the map to bear witness");
				doVictoryTask();
			}
			else // Victory events happening here
			{
				victory_timer += delta;
				if ( victory_timer >= 1000 && !finale_flags[0] )
				{
					// fire off evil laugh, and change the music
					BGTune.stop();
					Laugh.play();
					finale_flags[0] = true;
				}
				if ( victory_timer >= 3000 && !finale_flags[1] )
				{
					// Fire up the ending tune, and some spark effects
					EndingTune.loop();
					finale_flags[1] = true;
					
					
				}
				if ( victory_timer >= 8000 && !finale_flags[2] )
				{
					finale_flags[2] = true;
					// maybe a blackout?
				}
				if ( victory_timer  >= 12000 && !finale_flags[3] )
				{
					finale_flags[3] = true;
					mGame.enterState(EndgameCinematic.ID, new FadeOutTransition(), new FadeInTransition());
				}
			}	
		}		
	}
	
	private boolean FailureCheck() {
		
		// If the enemy is tearing up your base and no one is
		// defending, than you lose
		if ( Enemies.size() > 0 && Minions.size() < 1 )
			return true;
		
		// If you have no minions, and not enough money to buy any
		// more, then you lose.
		if ( getValue("Money") < 300 )
		{
			boolean haveMinion = false;
			for ( Place p: Places )
			{
				haveMinion = p.getUsers().size() > 0 ? true : haveMinion;
			}
			haveMinion = Minions.size() > 0 ? true : haveMinion;  
			if ( haveMinion == false )
				return true;
		}
		
		return false;
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

	public void removeMinion(Entity mUnit) {
		Minions.remove(mUnit);
	}

	public int getValue(String string) {
		return mValueStore.get(string);
	}

	public void setValue(String string, int i) {
		mValueStore.put(string, i);		
	}

	public void sendMinion(final String unitName, final Place place) {
		
		Portal mPortal = (Portal) mMask.findObjectType("Portal to " + place.getName());
		if ( mPortal == null )
		{
			Log.warn("Attempt to send minion to " + place.getName() + " Failed because no portal could be found");
			return;
		}
		
		queueTask(TaskLibrary.groupTasks("Hunchback", TaskLibrary.getWalkTo(mPortal.getLocation()), TaskLibrary.useObjectTask(mPortal)));
		
		Log.info("Request to send " + unitName + " to " + place.getName());
	}

	public void callMinion(String unitName, Place place) {
		for ( int i = 0; i < place.getUsers().size(); i++ )
		{
			Minion m = place.getUsers().get(i);
			if ( m.getTypeName().equalsIgnoreCase(unitName) )
			{
				if ( place.removeUser(m) )
				{
					addMinion(m);
					m.setOnScreen(true);
					break;
				}					
				else
				{
					Log.warn("Attempted to call minion back from " + place.getName() + ", but could not find that minion there?");
				}
			}
		}
	}
	
	public void queueTask(MinionTask nTask)
	{
		availableTasks.add(nTask);
	}
	
	private void gameSetup() throws SlickException
	{
		Town mTown = new Town(this);
		Places.add(mTown);
		addObject(new Portal("./res/items/portal.cfg", this, mTown ), new Coord(37 , 54));
		
		Graveyard mGrave = new Graveyard(this);
		Places.add(mGrave);
		addObject(new Portal("./res/items/portal.cfg", this, mGrave), new Coord(40, 54));
		
		addObject(new Bed(this), new Coord(6, 35));
		addObject(new Bed(this), new Coord(11, 35));
		addObject(new Bed(this), new Coord(16, 35));
	
		addObject(new Equipment(this), new Coord(31, 10));
		addObject(new Library(this), new Coord(6, 1));
		addObject(new Storage(this), new Coord(61, 1));
		
	}

	public void initGraphics() throws SlickException {
		
		setLevelPath(new PathMask("res/MapMask.png", 10, this));
		
		for ( int i = 0; i < 4; i++ )
		{
			Minion m = new Hunchback(this);
			m.initGraphics();
			addMinion(m, new Coord(60, 7));
		}
		
		for ( Entity e: Minions )
			e.initGraphics();
		
		for ( Entity e: Enemies )
			e.initGraphics();		
		
		for ( UseableObject o: Objects )
			o.initGraphics();
		
		for ( Place p: Places )
			p.initGraphics();	
	}
	
	public final class DebugHelper
	{
		private GameCore mCore;
		public DebugHelper(GameCore mCore)
		{
			this.mCore = mCore;
		}
		
		public HashMap<String, Integer> getValueStore()
		{
			return mCore.mValueStore;
		}
		
		public ArrayList<MinionTask> getTaskQueue()
		{
			return mCore.availableTasks;
		}
	}	
	
	private boolean ObjectiveCheck()
	{
		boolean equipmentCheck = false;
		boolean monsterParts = false;
		
		Equipment e = (Equipment)mMask.findObjectType("Equipment");
		
		equipmentCheck = e.getLevel() >= 4 ? true : false;  
		monsterParts = getValue("Special Parts") >= 6 ? true : false;
		
		return equipmentCheck && monsterParts;
	}
	
	private void doVictoryTask()
	{
		// Recall all minions
		for ( Place p: getPlaces() )
		{
			for ( Minion m: p.getUsers() )
			{
				addMinion(m);
				m.setOnScreen(true);
			}
			p.getUsers().clear();
		}
		
		// Send all our minions to crowd around the lab
		Equipment equipment = (Equipment)mMask.findObjectType("Equipment");
		Random mRand = new Random();
		
		for ( Entity e: getMinions() )
		{
			Minion m = (Minion)e;
			m.addTask(TaskLibrary.getWalkTo(equipment.getLocation()));
			for (int i = 0; i < 10; i++ )
			{
				m.addTask(TaskLibrary.getWalkTo(mMask.RandomLocation(equipment.getLocation(), Math.max(1, mRand.nextInt(10)))));
				m.addTask(TaskLibrary.getWaitTask(mRand.nextInt(4)*1000));
				m.addTask(TaskLibrary.getWalkTo(equipment.getLocation()));
			}
		}
	}

	public void kill(Enemy enemy) {
		enemyKillQueue.add(enemy);
		OverlayGUI.LogMessage("An Enemy " + enemy.getTypeName() + " has been defeated in combat!");
	}
	
	public void kill(Minion minion) {
		minionKillQueue.add(minion);
		OverlayGUI.LogMessage("A " + minion.getTypeName() + " has died in combat!");
	}

	public void addEnemy(Enemy e) {
		Enemies.add(e);
	}
	
	public void addEnemy(Enemy e, Coord pos) {
		e.setPosition(pos.getX(), pos.getY());
		Enemies.add(e);
	}
}
