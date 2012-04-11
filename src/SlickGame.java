import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class SlickGame extends StateBasedGame {

	private GameCore mCore;
	
	public SlickGame(String name) throws SlickException {
		super(name);
		mCore = new GameCore(this);
	}

	@Override
	public void initStatesList(GameContainer arg0) throws SlickException {
		addState(new IntroState());
		addState(new IdleState());
		addState(new MenuState());
		addState(new CastleScreen());
		addState(new MapState());
		addState(new QuitState());
		addState(new OptionsState());
		addState(new ObjectiveState());
		addState(new ResourceState());
		addState(new BuyState());
		addState(new DebugState());
		addState(new EndgameCinematic());
		addState(new GameLost());
		mCore.initGraphics();
	}

	public GameCore getGameCore() {
		return mCore;
	}
	
}
