import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


public class ResourceState extends BasicGameState {
	
	public static int ID = 9;
	private GameCore mCore;
	private String gold = "Gold: ";
	private String bodyParts = "Body Parts: ";
	private String tech = "Tech Level: ";
	private Image bgImage;

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		mCore = ((SlickGame)arg1).getGameCore();
		bgImage = new Image("./res/themes/default/Resourcescreen.png");

	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		arg2.setBackground(Color.black);
		bgImage.draw(0,0);
		arg2.drawString(gold + (mCore.getValue("Money")), 40, 70);
		arg2.drawString(bodyParts + (mCore.getValue("Parts")), 40, 110);
		arg2.drawString(tech + (mCore.getValue("Tech")), 40, 150);
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
	}
	public void keyPressed(int key, char arg1) {
		if ( key == Input.KEY_ESCAPE )
		{
			mCore.getGame().enterState(CastleScreen.ID);
		}
	}

}
