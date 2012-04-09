import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

public class Hunchback extends Minion {

	private int heldResources = 0;
	private int townDelta = 0;
	private int townUpdateTime = 10000;
	
	public void addResources(int value)
	{
		heldResources += value;
	}
	
	public Hunchback(PathMask level) throws SlickException
	{
		super(level, ConfigReader.readConfig("./res/hunchback.cfg"));
	}
	
	public void doExampleTask()
	{
		this.addTask(TaskLibrary.getWalkTo(this, this.getLevel().RandomLocation()));
		this.addTask(TaskLibrary.getWalkTo(this, this.getLevel().RandomLocation()));
		this.addTask(TaskLibrary.getRecoverTask(this));
		this.addTask(TaskLibrary.getWalkTo(this, this.getLevel().RandomLocation()));
		this.addTask(TaskLibrary.getWalkTo(this, this.getLevel().RandomLocation()));		
	}

	public void placeUpdate(Place mPlace, int delta)
	{
		if ( mPlace.getName().compareToIgnoreCase("town") == 0 )
		{
			townDelta += delta;
			if (townDelta >= townUpdateTime )
			{
				townDelta = 0;
				Log.debug("Minion " + getName() + " perked in " + mPlace.getName() );
			}
		}
	}
	
	protected void removeResources(int i) {
		heldResources -= i;
	}

	protected int getResources() {
		return heldResources;
	}
	
}
