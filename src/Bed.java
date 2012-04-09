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
		mUnit.setStatus("Doing Nothing");
		return true;
	}
	
	@Override
	public boolean doEffect(Minion mUnit) {
		mUnit.setHealth(mUnit.getMaxHealth());
		return true;
	}

	@Override
	public String getTypeName() {
		return "Bed";
	}

}
