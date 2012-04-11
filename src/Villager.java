import org.newdawn.slick.SlickException;

public class Villager extends Enemy {

	public Villager(GameCore mCore) throws SlickException {
		super(mCore, ConfigReader.readConfig("./res/villager.cfg"));
	}
	
}
