import java.util.Properties;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

public class BuyUnitPanel 
{
	private MouseOverArea buyButton, detailButton;
	private int MoneyCost, PartsCost, TLCost;
	private int x,y;
	private boolean unitEnabled = false;
	private Image unitImage, detailButtonImage, buyButtonImage, buildButtonImage;
	private final String unitName;
	private final Sound buyFail, buySucceed;

	private String[] details = { "Gold Cost: ", "Parts Cost: ", "Tech Level Required: " };
	
	// TODO: Move this into the config file
	private static final String themeDir = "./res/themes/";
	
	private int padding = 4;
	
	public BuyUnitPanel(final String unitName, final GameCore mCore, int x, int y) throws SlickException
	{
		buyFail = new Sound(mCore.getConfiguration().getProperty("ClickFailSound"));
		buySucceed = new Sound(mCore.getConfiguration().getProperty("ClickSound"));
		
		this.unitName = unitName;
		this.x = x;
		this.y = y;
		String[] units = mCore.getConfiguration().getProperty("units").split(",");
		for ( int i = 0; i < units.length; i++ )
		{
			if ( unitName.equalsIgnoreCase(units[i])) 
			{
				unitEnabled = true;
			}
		}
		
		Properties mConfig = mCore.getConfiguration();
		Properties mTheme = ConfigReader.readConfig(themeDir + mConfig.getProperty("theme"));
		buyButtonImage = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("hire-button"));
		detailButtonImage = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("detail-button"));
		buildButtonImage = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("build-button"));	
		
		final Properties unitConfig = ConfigReader.readConfig("./res/" + unitName + ".cfg");
		unitImage = new Image(unitConfig.getProperty("icon"));
		
		MoneyCost = Integer.parseInt(unitConfig.getProperty("gold-cost"));
		PartsCost = Integer.parseInt(unitConfig.getProperty("parts-cost"));
		TLCost = Integer.parseInt(unitConfig.getProperty("tech-cost"));
		
		Image buttonImage = (PartsCost > 0) ? buildButtonImage : buyButtonImage;
		
		buyButton = new MouseOverArea(mCore.getGame().getContainer(), buttonImage, x + padding + unitImage.getWidth(), y + 18)
		{
			public void mouseClicked(int button,
                    int x, int y, int clickCount)
			{
				if (x >= this.getX()
						&& x < this.getX() + this.getWidth()
						&& y >= this.getY()
						&& y <= this.getY() + this.getHeight()) 
				{
					if ( mCore.getValue("Money") >= MoneyCost && mCore.getValue("Parts") >= PartsCost && mCore.getValue("Tech") >= TLCost )
					{
						Log.info("Attempting to buy unit " + unitName);
						
						// If the unit has a parts cost, it means it must be made in our lab
						if ( PartsCost > 0 )
						{
							mCore.setValue("queue-build-minion-" + unitName, mCore.getValue("queue-build-minion-" + unitName)+1);
							OverlayGUI.LogMessage("Minions will now build a new " + unitName);
							buySucceed.play();
						} // Otherwise it can just be hired and will arrive from town
						else
						{
							mCore.setValue("Money", mCore.getValue("Money")-MoneyCost);
							MinionFactory.hireMinion(unitName, mCore);
							OverlayGUI.LogMessage("A new " + unitName + " Has been hired");
							buySucceed.play();
						}
						
					}
					else
					{
						// Should play a bzzt sound or something here
						OverlayGUI.LogMessage("Could not afford minion!");
						buyFail.play();
					}
				}
			}
		};
		
		detailButton = new MouseOverArea(mCore.getGame().getContainer(), detailButtonImage, x + padding + unitImage.getWidth(), y + padding + buyButtonImage.getHeight() + 20)
		{
			public void mouseClicked(int button,
                    int x, int y, int clickCount)
			{
				if (x >= this.getX()
						&& x < this.getX() + this.getWidth()
						&& y >= this.getY()
						&& y <= this.getY() + this.getHeight()) 
				{
					OverlayGUI.LogMessage(unitConfig.getProperty("desc"));
				}
			}
		};
		
	}
	
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		arg2.drawString(unitName, x, y);
		int h = arg2.getFont().getHeight(unitName);
		unitImage.draw(x, y + h);
		buyButton.render(arg0, arg2);
		detailButton.render(arg0, arg2);
		
		int w = 0;
		int lh = 20;
		// Gold Cost
		arg2.drawString(details[0], x, y + unitImage.getHeight() + lh + (padding*4));
		w = arg2.getFont().getWidth(details[0]);
		arg2.drawString(String.valueOf(MoneyCost), x + w + 2, y + unitImage.getHeight() + lh + (padding*4));
		
		// Parts Cost
		arg2.drawString(details[1], x, y + unitImage.getHeight() + (lh*2) + (padding*4));
		w = arg2.getFont().getWidth(details[1]);
		arg2.drawString(String.valueOf(PartsCost), x + w + 2, y + unitImage.getHeight() + (lh*2) + (padding*4));
		
		// Tech Level Required
		arg2.drawString(details[2], x, y + unitImage.getHeight() + (lh*3) + (padding*4));
		w = arg2.getFont().getWidth(details[2]);
		arg2.drawString(String.valueOf(TLCost), x + w + 2, y + unitImage.getHeight() + (lh*3) + (padding*4));
		
		
		if ( !unitEnabled )
		{
			// If the unit is disabled, Draw a gray rectangle over the panel
			Color oldCol = arg2.getColor();
			Color mCol = new Color(0.3f, 0.3f, 0.3f, 0.5f);
			arg2.setColor(mCol);
			arg2.drawRect(x, y, getWidth(), getHeight());
			arg2.setColor(oldCol);
		}
	}
	
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
		resetButtons();
	}
	
	private void resetButtons()
	{
		buyButton.setLocation(x + padding + unitImage.getWidth(), y);
		detailButton.setLocation(x + padding + unitImage.getWidth(), y + padding + buyButtonImage.getHeight());
	}
	
	public int getWidth()
	{
		return unitImage.getWidth() + padding + detailButton.getWidth();
	}
	
	public int getHeight()
	{
		int textLines = 3;
		return unitImage.getHeight() + padding + (textLines+4)*18;
	}

}
