import java.util.Properties;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public abstract class Minion extends Entity {

	private int beauty, brains, brawn;
	private int Health, MaxHealth;
	
	// Instance counting used for uniqely naming objects
	private int iCounter = getCounter();
	private static int counter = 1;
	private static int getCounter() {
		return counter++;
	}


	public Minion(PathMask level, Properties config) throws SlickException {
		super(level, config);

		this.setBeauty(Integer.parseInt(config.getProperty("beauty")));
		this.setBrains(Integer.parseInt(config.getProperty("brains")));
		this.setBrawn(Integer.parseInt(config.getProperty("brawn")));

	}

	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException
	{
		super.update(container, game, delta);
	}

	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		super.render(arg0, arg1, arg2);
	}
	
	public String getName()
	{
		return getTypeName() + iCounter;
	}

	public int getBeauty() {
		return beauty;
	}

	public void setBeauty(int beauty) {
		this.beauty = beauty;
	}

	public int getBrains() {
		return brains;
	}

	public void setBrains(int brains) {
		this.brains = brains;
	}

	public int getBrawn() {
		return brawn;
	}

	public void setBrawn(int brawn) {
		this.brawn = brawn;
	}

	@Override
	public boolean isFriendly() {
		return true;
	}

	public int getMaxHealth() {
		return MaxHealth;
	}

	public void setHealth(int health) {
		Health = health;
	}

	public int getHealth() {
		return Health;
	}

	public void setStatus(String string) {
		// TODO Auto-generated method stub
		
	}

	public abstract void placeUpdate(Place place, int delta);

}
