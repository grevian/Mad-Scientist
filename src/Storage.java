import org.newdawn.slick.SlickException;


public class Storage extends UseableObject {

	public Storage(GameCore mCore) throws SlickException {
		super("./res/items/storage.cfg", mCore);
	}

	@Override
	public boolean doEffect(Minion mUnit, String effect) 
	{
		if ( effect.equalsIgnoreCase("get-parts") )
		{
			if(getCore().getValue("Parts")>1)
			{
				getCore().setValue("Parts", getCore().getValue("Parts")-2);
				mUnit.setValue("Parts", 2);
				return true;
			}
		}
		else if ( effect.equalsIgnoreCase("put-parts"))
		{
			getCore().setValue("Parts", getCore().getValue("Parts")+mUnit.getValue("Parts"));
			mUnit.setValue("Parts", 0);
			return true;
		}
		return false;
	}

	@Override
	public String getTypeName() {
		return "Storage";
	}
	
	public void upgrade()
	{
		// Maybe reduce the time to use...
	}

}
