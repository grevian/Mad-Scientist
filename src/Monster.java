import org.newdawn.slick.SlickException;

public class Monster extends Minion {
	
	public Monster(GameCore mCore) throws SlickException
	{
		super(mCore, ConfigReader.readConfig("./res/monster.cfg"));
	}
	
	public void placeUpdate(Place mPlace, int delta)
	{
		
	}

}
