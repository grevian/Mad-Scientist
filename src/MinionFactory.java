import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

public class MinionFactory {

	public static Minion constructMinion(String unitName, GameCore mCore )
	{
		Minion mMinion = null;
		try {
			if (unitName.compareToIgnoreCase("Hunchback") == 0)
			{
				Log.warn("Factory created a Hunchback");
				mMinion = new Hunchback(mCore);
			}
			if (unitName.compareToIgnoreCase("Monster") == 0)
			{
				Log.warn("Factory created a Monster");
				mMinion = new Monster(mCore);
			}
			if (unitName.compareToIgnoreCase("Skeleton") == 0)
			{
				Log.warn("Factory created a Skeleton");
				mMinion = new Skeleton(mCore);
			}
		} catch (SlickException e) {
			e.printStackTrace();
			Log.warn("Creation failure for unit type " + unitName + ", Returning Null");
			return null;
		}
		
		if ( mMinion == null )
			Log.warn("Minion Factory Creation Failed, Unknown Type Maybe? " + unitName );
		else
			try {
				mMinion.initGraphics();
			} catch (SlickException e) {
				// Probably the graphics have not yet been initialized, the
				// method
				// will get called again by the init functions
			}
		return mMinion;

	}
	
	public static boolean hireMinion(String unitName, GameCore mCore) {

		Minion mMinion = constructMinion(unitName, mCore);
		Coord mCoord = mCore.getLevel().findObjectType("Portal to Town").getLocation();

		if ( mMinion == null )
			return false;		
		if ( mCoord == null )
			return false;
		
		mCore.addMinion(mMinion, mCoord);
		return true;
	}

}
