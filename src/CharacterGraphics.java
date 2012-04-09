import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import java.util.HashMap;

public class CharacterGraphics {

	private HashMap<String, Animation> mAnimations = new HashMap<String, Animation>(3);
	private Animation currentAnimation = null;
	private int x = 0, y = 0;

	public void addAnimation(String animationName, Animation nAnimation)
			throws SlickException {
		assert (nAnimation != null);
		mAnimations.put(animationName, nAnimation);
	}

	public void setAnimation(String animationName) throws SlickException {
		Animation temp = mAnimations.get(animationName);
		if (temp == null)
			throw new SlickException("Animation not Found: " + animationName);
		currentAnimation = temp;
	}
	
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		currentAnimation.update(delta);
	}

	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		currentAnimation.draw(x, y);
	}
	
	public int getXPos()
	{
		return x;
	}
	
	public int getYPos()
	{
		return y;	
	}

}
