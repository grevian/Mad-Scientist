import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

public class OverlayGUI {
	
	private static ArrayList<String> ConsoleMessage = new ArrayList<String>();
	private static ArrayList<Integer> MessageAge = new ArrayList<Integer>();
	private static int MaxMessageAge = 4000;
	private static Color mFontCol = new Color(1.0f, 1.0f, 1.0f);
	
	public static void LogMessage(String Message)
	{
		ConsoleMessage.add(Message);
		MessageAge.add(0);
	}
	
	public static void update(int delta)
	{
		// Age and remove stale console messages
		for ( int i = 0; i < MessageAge.size(); i++ )
		{
			MessageAge.set(i, MessageAge.get(i)+delta);
			if ( MessageAge.get(i) >= MaxMessageAge )
			{
				MessageAge.remove(i);
				ConsoleMessage.remove(i);
			}
		}
	}
	
	public static void renderConsole(Graphics g)
	{
		int consoleHeight = 120;
		int padding = 2;
		int yOffset = 800-(consoleHeight+padding);
		int lineHeight = g.getFont().getLineHeight();
		
		Color oldColor = g.getColor();
		g.setColor(new Color(new Color(0.0f, 0.0f, 0.0f, 0.5f)));
		g.fillRoundRect(50, yOffset, 700, consoleHeight, 10);
		g.setColor(mFontCol);
		for ( int i = 0; i < (consoleHeight/lineHeight) && i < ConsoleMessage.size(); i++ )
		{
			g.drawString(ConsoleMessage.get(i), 55, yOffset);
			yOffset += lineHeight + padding;
		}
		g.setColor(oldColor);
	}
	
}
