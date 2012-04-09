
public class Tile {

	public static int IMPASSABLE = 1;
	public static int RESOURCES = 2;
	public static int BUILDABLE = 3;
	public static int NORMAL = 4;
	
	private int type;
	
	public int getType()
	{
		return type;
	}
	
	public void setType(int value)
	{
		assert(value <= 4);
		type = value;
	}
	
}
