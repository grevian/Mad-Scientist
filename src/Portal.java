import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

public class Portal extends UseableObject {

	private Place endPoint;
	
	public Portal(String configFile, GameCore mCore, Place location) throws SlickException {
		super(configFile, mCore);
		endPoint = location;
	}

	@Override
	public boolean addUser(Minion mUnit)
	{
		super.addUser(mUnit);
		// Minions vanish as soon as they use the portal, even if they still take
		// a few seconds to arrive at their destination
		mUnit.setOnScreen(false);
		mUnit.setStatus("Travelling to " + endPoint.getName());
		return true;
	}
	
	@Override
	public boolean doEffect(Minion mUnit, String mAction) {
		String sendName = "pending-send-to-"+endPoint.getName()+"-for-" + mUnit.getTypeName().toLowerCase();
		getCore().setValue(sendName, getCore().getValue(sendName)-1);
		endPoint.addUser(mUnit);
		return true;
	}

	@Override
	public String getTypeName() {		
		return "Portal to " + endPoint.getName();
	}
	
	public void upgrade() {
		// reduce time to use
	}

}
