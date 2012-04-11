import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

public class Hunchback extends Minion {

	private int townDelta = 0;
	private int townUpdateTime = 10000;
	
	public Hunchback(GameCore mCore) throws SlickException
	{
		super(mCore, ConfigReader.readConfig("./res/hunchback.cfg"));
		setValue("Money", 0);
		setValue("Parts", 0);
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
	
}
