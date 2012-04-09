import org.newdawn.slick.Color;
import org.newdawn.slick.gui.MouseOverArea;

public class MenuChoice {
	private int enterState;
	private MouseOverArea DisplayImage;
	private Color uColor = Color.gray;
	private Color sColor = Color.red;
	
	public MenuChoice(MouseOverArea mImage, int mState)
	{
		enterState = mState;
		DisplayImage = mImage;
	}
	
	public int getState() { return enterState; }
	public Color getUnselectedColor() { return uColor; }
	public Color getSelectedColor() { return sColor; }
	public MouseOverArea getDisplayImage() { return DisplayImage; }
	
}
