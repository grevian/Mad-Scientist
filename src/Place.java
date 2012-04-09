import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public abstract class Place {
	private String name;
	private GameCore mCore;
	private ArrayList<Minion> mUsers;
	
	protected Place(String name, GameCore mCore)
	{
		this.name = name;
		this.mCore = mCore;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta)
	throws SlickException
	{
		for ( Minion m: mUsers )
		{
			m.update(container, game, delta);
			m.placeUpdate(this, delta);
		}
	}
	
	/* Render the place on the map */
	public abstract void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
	throws SlickException;
	
	public ArrayList<Minion> getUsers() {
		return mUsers;
	}
	
	protected GameCore getCore()
	{
		return mCore;
	}

	public void addUser(Minion mUnit) {
		mUsers.add(mUnit);
	}

	public abstract Image getImage();

	public abstract Coord getPosition();
	
}
