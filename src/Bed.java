import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

public class Bed extends UseableObject {
	
	public Bed(GameCore mCore) throws SlickException {
		super("./res/items/bed.cfg", mCore);
	}

	@Override
	public boolean addUser(Minion mUnit)
	{
		super.addUser(mUnit);
		Log.info("Minion " + mUnit.getName() + " started using " + getName());
		mUnit.setOnScreen(false);
		mUnit.setStatus("In Bed");
		return true;
	}
	
	@Override
	public boolean removeUser(Minion mUnit)
	{
		super.removeUser(mUnit);
		Log.info("Minion " + mUnit.getName() + " stopped using " + getName());
		mUnit.setOnScreen(true);
		return true;
	}
	
	@Override
	public boolean doEffect(Minion mUnit, String effect) {
		if ( effect.equalsIgnoreCase("sleep") )
		{
			mUnit.setHealth(mUnit.getMaxHealth());
			return true;
		}
		return false;
	}

	@Override
	public String getTypeName() {
		return "Bed";
	}

	public void upgrade()
	{
		// Reduce time to use
	}
	
}
