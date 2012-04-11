import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Path.Step;

public abstract class Entity implements Mover {

	private static int counter = 1;

	public abstract boolean isFriendly();
	public abstract String getName();
	
	private String TypeName;
	private int speed;
	
	private PathMask levelGrid;
	private AStarPathFinder mPathfinder;
	private CharacterGraphics mGraphics;
	
	private LinkedList<Path> mPaths;
	private Path currentPath = null;
	private int pathStep = 0;
	private float moveDelta = 0.0f;
	
	private long timeDelta = System.currentTimeMillis();
	
	// Grid position
	private int x, y;
	
	private ArrayList<MinionTask> mTasks = new ArrayList<MinionTask>();
	private boolean visible = true;
	private String StatusMessage;
	
	private Properties mProps;
	private HashMap<String, Integer> valueStore = new HashMap<String, Integer>();
	protected int iCounter = getCounter();
	
	private GameCore mCore;
	
	protected GameCore getCore()
	{
		return mCore;
	}
	
	protected Entity(GameCore mCore, Properties mProps) throws SlickException
	{
		// Basic initializations
		this.mCore = mCore;
		mPaths = new LinkedList<Path>();
		this.mProps = mProps;		
		
		// Set up the basic entity components
		this.setLevel(mCore.getLevel()); // Needed for pathfinding
		this.setTypeName(mProps.getProperty("TypeName")); // Used for display purposes mostly
		this.setSpeed(Integer.parseInt(mProps.getProperty("speed"))); // Used for movement animation
	}
	
	public void initGraphics() throws SlickException
	{
		// Load up the graphics and animations for this Entity
		mGraphics = new CharacterGraphics();
		String animList[] = mProps.getProperty("Animations").split(",");
		for ( int i = 0; i < animList.length; i++ )
		{
			Animation mAnim = new Animation(new SpriteSheet(mProps.getProperty(animList[i]), 35, 40, 5), 400);
			mGraphics.addAnimation(animList[i], mAnim);
		}
		mGraphics.setAnimation(animList[0]);
	}
	
	public void addPath(int gx, int gy) throws SlickException
	{
		// By default, start paths from our current position
		int sx = x;
		int sy = y;
		
		// But if there's a path in progress, or any in the queue, start the path from where they end
		if ( currentPath != null )
		{
			sx = currentPath.getStep(currentPath.getLength()-1).getX();
			sy = currentPath.getStep(currentPath.getLength()-1).getY();
		}
		if ( mPaths.size() > 0 )
		{
			sx = mPaths.getLast().getStep(mPaths.getLast().getLength()-1).getX();
			sy = mPaths.getLast().getStep(mPaths.getLast().getLength()-1).getY();	
		}
		
		Path tPath = mPathfinder.findPath(this, sx, sy, gx, gy);
		if ( tPath == null )
			throw new SlickException("Path not Possible");
		mPaths.push(tPath);
	}
	
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2) throws SlickException
	{
		if ( visible )
			mGraphics.render(arg0, arg1, arg2);
	}
	
	protected boolean addTask(MinionTask mTask)
	{
		mTasks.add(mTask);
		return true;
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException
	{
		updateTasks(container, game, delta);
		updatePathing(container, game, delta);		
		mGraphics.update(container, game, delta);
	}
	
	private void updatePathing(GameContainer container, StateBasedGame game, int delta) 
	{
		// Shortcut out if the unit is not currently visible/on the main map
		if ( !visible )
			return;
		
		if ( currentPath != null && pathStep >= currentPath.getLength() )
		{
			pathStep = 0;
			currentPath = null; // We reached the end of the current path, discard it.
		}
		
		if (currentPath == null && mPaths.size() > 0 )
		{
			pathStep = 0;
			currentPath = mPaths.removeLast();
		}
		
		if ( currentPath != null && pathStep < currentPath.getLength() )
		{
			Step mStep = currentPath.getStep(pathStep);
	
			// Convert from Grid coordinates, to Screen Pixel Coordinates
			int dx = mStep.getX();
			int dy = mStep.getY();
			int sx = mGraphics.getXPos()/10;
			int sy = mGraphics.getYPos()/10;
			
			// We have reached our next grid square 
			if ( dx == sx && dy == sy )
			{
				moveDelta = 0;
				x = sx;
				y = sy;
				pathStep++;
			}
			else
			{
				// TODO: The final argument to lerp should be a 0-1 value that uses the
				// delta as a reference, to keep movement speed tied to time and not
				// framerate
				long nTimeDelta = System.currentTimeMillis();
				long tDelta = nTimeDelta - timeDelta;
				timeDelta = nTimeDelta;
				moveDelta += Math.min(((float)((float)tDelta)/(this.getSpeed()*10)), 1);
				
				int new_x = (int) Entity.lerp(mGraphics.getXPos(), dx*10, moveDelta);
				int new_y = (int) Entity.lerp(mGraphics.getYPos(), dy*10, moveDelta);
				mGraphics.setPosition(new_x, new_y);
			}	
		}
	}

	private void updateTasks(GameContainer container, StateBasedGame game,	int delta) throws SlickException
	{
		if ( mTasks.size() == 0 )
			setStatus("IDLE");
		
		while (mTasks.size() > 0) 
		{
			MinionTask cTask = mTasks.get(0);
			if ( !cTask.isStarted() )
			{
				cTask.start();
				this.setStatus(cTask.getStatus());
				Log.debug(this.getName() + " started task: " + cTask.getDescription());
			}
			
			if ( cTask.isComplete() ) 
			{
				mTasks.remove(cTask);
				Log.debug(this.getName() + " completed task: " + cTask.getDescription());
				if ( mTasks.size() == 0 )
					this.setStatus("IDLE");
			}
			else
			{
				try
				{
					cTask.update(delta);
					return;
				}
				catch (SlickException e)
				{
					Log.warn("Task (" + cTask.getDescription() + ") failed an update! " + e.getMessage());
					cTask.interrupt(false);
				}
			}
		}
	}

	public Coord getPosition()
	{
		return new Coord(this.x, this.y);
	}
	
	public void setPosition(int x, int y)
	{
		// Set an Entities position on the grid
		this.x = x;
		this.y = y;
		mGraphics.setPosition(x*10, y*10);
	}
	
	public void setPositionFromCoord(int x, int y)
	{
		this.x = levelGrid.getGridXAtPoint(x);
		this.x = levelGrid.getGridYAtPoint(y);
	}
	
	public String getTypeName() {
		return TypeName;
	}

	public void setTypeName(String value) {
		TypeName = value;
	}
	
	public void setLevel( PathMask level )
	{
		levelGrid = level;
		// TODO: Find a more appropriate way to set the max search length
		mPathfinder = new AStarPathFinder(levelGrid, levelGrid.getHeightInTiles()*3, false);
	}
	
	public PathMask getLevel()
	{
		return levelGrid;
	}
	
	public Path findPath(int sx, int sy, int dx, int dy)
	{
		return mPathfinder.findPath(this, sx, sy, dx, dy);
	}
	
	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public void setOnScreen(boolean value)
	{
		visible  = value;
	}

	public void setStatus(String string) {
		StatusMessage = string;
	}
	public String getStatus() {
		return StatusMessage;
	}
	public void setValue(String key, int value) {
		valueStore.put(key, value);
	}
	private static int getCounter() {
		return counter++;
	}
	public int getValue(String key) {
		return valueStore.get(key);
	}
	// courtesy of http://slick.javaunlimited.net/viewtopic.php?t=2199
	public static float lerp(int a, int b, float t) 
	{
		if (t < 0)
			return a;
		return a + t * (b - a);
	}
	public void addPath(Coord position) throws SlickException {
		addPath(position.getX(), position.getY());		
	}
	
	public void interrupt() {
		for ( MinionTask m: mTasks )
			m.interrupt(false);
		this.currentPath = null;
		this.mPaths.clear();
		this.pathStep = 0;
	}

}
