import org.newdawn.slick.SlickException;

public class Skeleton extends Minion {
	
	public Skeleton(GameCore mCore) throws SlickException
	{
		super(mCore, ConfigReader.readConfig("./res/skeleton.cfg"));
	}
	
	public void placeUpdate(Place mPlace, int delta)
	{
		
	}

}
