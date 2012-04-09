import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class SlickTest {

	public static void main(String[] args) throws SlickException {
			
			// Create our game object, and stick it inside the container
			SlickGame mSlickGame = new SlickGame("");
			AppGameContainer container = new AppGameContainer(mSlickGame);
			
			// Set the display to the size indicated in the config file
			container.setDisplayMode(Integer.parseInt(mSlickGame.getGameCore().getConfiguration().getProperty("width")),
		    						 Integer.parseInt(mSlickGame.getGameCore().getConfiguration().getProperty("height")),
		    						 false);
			
			container.setVSync(true);
		    
			// Start things rolling
		    container.setVerbose(true);
		    container.start(); 		
	}

}
