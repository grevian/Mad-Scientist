import org.newdawn.slick.SlickException;

public class Monster extends Minion {
	
	public Monster(PathMask level) throws SlickException
	{
		super(level, ConfigReader.readConfig("./res/monster.cfg"));
	}
	
	public void placeUpdate(Place mPlace, int delta)
	{
		
	}

}
