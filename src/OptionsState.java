import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class OptionsState extends BasicGameState {

	public static int ID = 5;
	private boolean backState = false;
	private ArrayList<OptionMenuItem> mList;
	private int chosen = 0;
	private boolean edit = false;
	
	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		mList = new ArrayList<OptionMenuItem>();
		mList.add(new OptionMenuItem("Sound Effects", "false", OptionMenuItem.BOOL));
		mList.add(new OptionMenuItem("Music", "false", OptionMenuItem.BOOL));
		mList.add(new OptionMenuItem("Difficulty", "5", OptionMenuItem.INT));
	}

	@Override
	public void leave(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
			chosen = 0;
			edit = false;
			backState = false;
	}
	
	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		
		for ( int i = 0; i < mList.size(); i++ )
		{
			OptionMenuItem currentChoice = mList.get(i);
			if ( chosen == i )
				arg2.setColor(currentChoice.getSelectedColor());
			else
				arg2.setColor(currentChoice.getUnselectedColor());
			arg2.drawString(currentChoice.getLabel() + "	: " + currentChoice.getValue(), arg0.getWidth()/2, (i*20)+(arg0.getHeight()/2));
		}

	}
	
	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
		if ( backState )
			arg1.enterState(MenuState.ID, new FadeOutTransition(), new FadeInTransition());
		if ( edit )
		{
			System.out.println("Doing Edit");
			OptionMenuItem current = mList.get(chosen);
			// Easy Enough
			if ( current.getType() == OptionMenuItem.BOOL )
			{
				System.out.println("Inverting Boolean");
				current.setValue( String.valueOf(!Boolean.parseBoolean(current.getValue())) );
				edit = false;
			}
		}

	}

	@Override
	public void keyPressed(int key, char arg1) {
		if ( key == Input.KEY_UP )
		{
			chosen = Math.max(0, chosen-1);
		}
		
		if ( key == Input.KEY_DOWN )
		{
			chosen = Math.min(mList.size()-1, chosen+1);
		}
	
		if ( key == Input.KEY_ESCAPE )
		{
			backState = true;
		}
		
		if ( (key == Input.KEY_ENTER) && !edit )
		{
			edit  = true;
		}
		if ( (key == Input.KEY_ENTER) && edit )
		{
			edit = false;
		}
		
		
	}
	
}
