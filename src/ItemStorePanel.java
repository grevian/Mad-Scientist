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

public class ItemStorePanel 
{
	private MouseOverArea buyButton, upgradeButton, detailButton;
	private int MoneyCost, TLCost, UpgradeCost;
	private int x,y;
	private boolean unitEnabled = false;
	private Image itemImage, detailButtonImage, buyButtonImage, upgradeButtonImage;
	private final String itemName;
	private Properties itemConfig;
	private String levels[];
	private int level = 0;
	private GameCore mCore;
	private final Sound buyFail, buySucceed;
	
	private String[] details = { "Gold Cost: ", "Tech Required: ", "Upgrade Cost:" };

	// TODO: Move this into the config file
	private static final String themeDir = "./res/themes/";
	
	private int padding = 4;
	
	public ItemStorePanel(final String itemName, final GameCore mCore, int x, int y) throws SlickException
	{
		this.mCore = mCore;
		
		buyFail = new Sound(mCore.getConfiguration().getProperty("ClickFailSound"));
		buySucceed = new Sound(mCore.getConfiguration().getProperty("ClickSound"));

		this.itemName = itemName;
		this.x = x;
		this.y = y;
		String[] units = mCore.getConfiguration().getProperty("items", "").split(",");
		for ( int i = 0; i < units.length; i++ )
		{
			if ( itemName.equalsIgnoreCase(units[i])) 
			{
				unitEnabled = true;
			}
		}
		
		Properties mConfig = mCore.getConfiguration();
		Properties mTheme = ConfigReader.readConfig(themeDir + mConfig.getProperty("theme"));
		buyButtonImage = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("build-button"));
		detailButtonImage = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("detail-button"));
		upgradeButtonImage = new Image(themeDir + mTheme.getProperty("folder") + "/" + mTheme.getProperty("upgrade-button"));	
		
		itemConfig = ConfigReader.readConfig("./res/items/" + itemName + ".cfg");
		itemImage = new Image(itemConfig.getProperty("icon"));
		
		MoneyCost = Integer.parseInt(itemConfig.getProperty("gold-cost"));
		levels = new String[itemConfig.getProperty("levels").split(",").length];
		
		for ( String s: itemConfig.getProperty("levels").split(",") )
			levels[Integer.parseInt(s)-1] = itemConfig.getProperty("level-" + s);
		
		UpgradeCost = -1;
		TLCost = -1;
		
		if ( levels.length > 1 )
		{
			UpgradeCost = Integer.parseInt(levels[1].split(",")[0]);	
			TLCost = Integer.parseInt(levels[1].split(",")[1]);
		}
		
		buyButton = new MouseOverArea(mCore.getGame().getContainer(), buyButtonImage, x + padding + itemImage.getWidth(), y + 18)
		{
			public void mouseClicked(int button,
                    int x, int y, int clickCount)
			{
				if (x >= this.getX()
						&& x < this.getX() + this.getWidth()
						&& y >= this.getY()
						&& y <= this.getY() + this.getHeight()) 
				{
					OverlayGUI.LogMessage("Sorry, not yet implemented");
					buyFail.play();
				}
			}
		};
		
		upgradeButton = new MouseOverArea(mCore.getGame().getContainer(), upgradeButtonImage, x + padding + itemImage.getWidth(), y + buyButtonImage.getHeight() + 18)
		{
			private boolean maxed = false;
			
			public void mouseClicked(int button,
                    int x, int y, int clickCount)
			{
				if (x >= this.getX()
						&& x < this.getX() + this.getWidth()
						&& y >= this.getY()
						&& y <= this.getY() + this.getHeight()) 
				{
					
					if ( mCore.getValue("Money") >= UpgradeCost)
					{
						if ( mCore.getValue("Tech") >= TLCost )
						{
							if ( !maxed )
							{
								mCore.setValue("Money", mCore.getValue("Money")-UpgradeCost);
								buySucceed.play();
								maxed = !setItemLevel(getItemLevel()+1);
							}
						}
						else
						{
							buyFail.play();
							OverlayGUI.LogMessage("You do not have enough tech to upgrade that!");
						}
					}
					else
					{
						buyFail.play();
						OverlayGUI.LogMessage("You do not have enough Money to upgrade that!");
					}
				}
			}
		};
		
		detailButton = new MouseOverArea(mCore.getGame().getContainer(), detailButtonImage, x + padding + itemImage.getWidth(), y + padding + buyButtonImage.getHeight() + upgradeButtonImage.getHeight() + 18)
		{
			public void mouseClicked(int button,
                    int x, int y, int clickCount)
			{
				if (x >= this.getX()
						&& x < this.getX() + this.getWidth()
						&& y >= this.getY()
						&& y <= this.getY() + this.getHeight()) 
				{
					OverlayGUI.LogMessage(itemConfig.getProperty("desc"));
				}
			}
		};
		
	}
	
	public final boolean setItemLevel(int v)
	{
		level = Math.min(v, levels.length-1);
		if ( level >= levels.length )
		{
			OverlayGUI.LogMessage("The " + itemName + " level is MAXIMUM!");
			return false; // Max level
		}
		
		for (UseableObject o: mCore.getObjects() )
		{
			if ( o.getTypeName().equalsIgnoreCase(itemName) )
				o.upgrade();
		}
		OverlayGUI.LogMessage(itemName + " has been upgraded!");
		
		if ( level >= levels.length-1 )
		{
			OverlayGUI.LogMessage("Maximum level of " + itemName + " reached!");
			UpgradeCost = -1;	
			TLCost = -1;
			return false;
		}
		else
		{
			UpgradeCost = Integer.parseInt(levels[v+1].split(",")[0]);	
			TLCost = Integer.parseInt(levels[v+1].split(",")[1]);
			return true;
		}
	}
	
	public final int getItemLevel()
	{
		return level;
	}
	
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		arg2.drawString(itemName, x, y);
		int h = arg2.getFont().getHeight(itemName);
		itemImage.draw(x, y + h);
		
		if ( MoneyCost > 0 ) // Negative cost means it can't be bought
			buyButton.render(arg0, arg2);
		
		detailButton.render(arg0, arg2);
		
		if ( itemConfig.getProperty("levels").split(",").length > 1 && UpgradeCost > 0 && TLCost > 0)
			upgradeButton.render(arg0, arg2);
		
		int w = 0;
		int lh = 20;
		int yOffset = Math.max(y + itemImage.getHeight(), upgradeButton.getY() + upgradeButton.getHeight() );
		yOffset += lh;
		if (MoneyCost > 0) {
			// Gold Cost
			arg2.drawString(details[0], x, yOffset);
			w = arg2.getFont().getWidth(details[0]);
			arg2.drawString(String.valueOf(MoneyCost), x + w + 2, yOffset);
		}
		
		yOffset += lh;
		
		// Upgrade Cost
		if ( UpgradeCost > 0 && levels.length > getItemLevel() )
		{
			// Upgrade cost
			arg2.drawString(details[2], x, yOffset);
			w = arg2.getFont().getWidth(details[2]);
			arg2.drawString(String.valueOf(UpgradeCost), x + w + 2, yOffset);
			
			yOffset += lh*2;
			// Tech Level Required to upgrade
			arg2.drawString(details[1], x, yOffset);
			w = arg2.getFont().getWidth(details[1]);
			arg2.drawString(String.valueOf(TLCost), x + w + 2, yOffset);
		}
		
		
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
		buyButton.setLocation(x + padding + itemImage.getWidth(), y);
		detailButton.setLocation(x + padding + itemImage.getWidth(), y + padding + buyButtonImage.getHeight());
	}
	
	public int getWidth()
	{
		return itemImage.getWidth() + padding + detailButton.getWidth();
	}
	
	public int getHeight()
	{
		int textLines = 3;
		return itemImage.getHeight() + padding + (textLines+4)*18;
	}

}
