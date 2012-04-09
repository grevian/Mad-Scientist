import org.newdawn.slick.Color;

public class OptionMenuItem {
	
	// The Basics
	private String Label;
	private String Value;
	private int Type;

	// Display options
	private Color uColor = Color.gray;
	private Color sColor = Color.red;
	
	// Option Types
	public static int BOOL = 0;
	public static int INT = 1;
	public static int STRING = 2;
	
	public OptionMenuItem(String mLabel, String mValue, int mType)
	{
		assert(Type <= 2 );
		Label = mLabel;
		Value = mValue;
		Type = mType;		
	}
	
	// basic accessors
	public String getLabel() { return Label; }
	public String getValue() { return Value; }
	public Color getUnselectedColor() { return uColor; }
	public Color getSelectedColor() { return sColor; }
	public int getType() { return Type; }
	
	// Kind of a hacky little type checking system here, but eh
	boolean setValue(String mValue)
	{
		if ( Type == STRING )
		{
			Value = mValue;
			return true;
		}
		if ( Type == INT )
		{
			try {
				Integer.parseInt(mValue);
			}
			catch (NumberFormatException e)
			{
				return false;
			}
			Value = mValue;
			return true;
		}
		if ( Type == BOOL )
		{
			if ( mValue.equalsIgnoreCase("true") || mValue.equalsIgnoreCase("false"))
			{
				Value = mValue;
				return true;
			}
			else
				return false;
		}
		return false;
	}
	
	
}
