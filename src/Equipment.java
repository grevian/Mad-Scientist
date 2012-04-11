import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;


public class Equipment extends UseableObject {
	
	private int level = 1;
	private int StoredParts = 0;
	
	String[] units;

	public Equipment(GameCore mCore) throws SlickException {
		super("./res/items/equipment.cfg", mCore);
		
		units = mCore.getConfiguration().getProperty("units").split(",");
		
		for ( String s: units )
		{
			getCore().setValue("queue-build-minion-" + s, 0);
			getCore().setValue("queue-build-minion-" + s + "-emitted", 0);
			
		}
		
	}

	@Override
	public boolean doEffect(Minion mUnit, String effect) {
		StoredParts += mUnit.getValue("Parts");
		mUnit.setValue("Parts", 0);
		String creatureName = effect.split("-")[1];
		
		if ( effect.startsWith("create-") )
		{
			Minion m = MinionFactory.constructMinion(creatureName, getCore());
			if (StoredParts >= m.getValue("parts-cost")
					&& getCore().getValue("Money") >= m.getValue("gold-cost")
					&& getCore().getValue("Tech") >= m.getValue("tech-cost"))
			{
				StoredParts -= m.getValue("parts-cost");
				getCore().setValue("Money", getCore().getValue("Money") - m.getValue("gold-cost"));
				getCore().setValue("queue-build-minion-" + creatureName, getCore().getValue("queue-build-minion-" + creatureName)-1);
				getCore().setValue("queue-build-minion-" + creatureName + "-emitted", getCore().getValue("queue-build-minion-" + creatureName + "-emitted")-1);
				getCore().addMinion(m, getLocation());
				return true;
			}
			else
			{
				if ( StoredParts <= m.getValue("parts-cost") )
				{
					// Get the event re-emitted to keep the task in progress
					getCore().setValue("queue-build-minion-" + creatureName + "-emitted", getCore().getValue("queue-build-minion-" + creatureName + "-emitted")-1);
				}
				else if ( getCore().getValue("Money") < m.getValue("gold-cost") )
				{
					OverlayGUI.LogMessage("Could not afford to build " + creatureName);
					getCore().setValue("queue-build-minion-" + creatureName, 0);
					Log.info("Not enough gold to build " + creatureName + ", Task Cancelled");
				}
				else
				{
					OverlayGUI.LogMessage("Tech level not high enough to build " + creatureName);
					OverlayGUI.LogMessage("(How did you even try?!)");
					getCore().setValue("queue-build-minion-" + creatureName, 0);
					Log.warn("Attempted to build a creature outside the users Techlevel?!");
				}
			}
		}
		return false;
	}

	@Override
	public String getTypeName() {
		return "Equipment";
	}
	
	public int getLevel()
	{
		return level;
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
	throws SlickException {
		
		for ( String s: units )
		{
			int e = getCore().getValue("queue-build-minion-" + s + "-emitted");
			if ( e < getCore().getValue("queue-build-minion-" + s))
			{
				getCore().setValue("queue-build-minion-" + s + "-emitted", getCore().getValue("queue-build-minion-" + s + "-emitted")+1);
				getCore().queueTask(this.buildMinionTask(s, getCore()));
			}
		}
		
		
	}

	// TODO: This should almost definitely be refactored into the TaskLibrary
	public MinionTask buildMinionTask(final String unitName, GameCore mCore) {
				final Storage mStorage = (Storage) mCore.getLevel().findObjectType("Storage");
				final Equipment mEquipment = (Equipment) mCore.getLevel().findObjectType("Equipment");
				
				if ( mStorage == null )
				{
					Log.error("Could not find a storage location, could not build minion!");
					return null;
				}
				if ( mEquipment == null )
				{
					Log.error("No Lab Equipment available to build new minion!");
					return  null;
				}
				
				return new MinionTask(){

					@Override
					protected void execute() throws SlickException {
						// Some minions may already be carrying parts
						if ( getMinion().getValue("Parts") <= 0 )
						{
							getMinion().addTask(TaskLibrary.getWalkTo(mStorage.getLocation()));
							getMinion().addTask(TaskLibrary.useObjectTask(mStorage, "get-parts"));
						}
						getMinion().addTask(TaskLibrary.getWalkTo(mEquipment.getLocation()));
						getMinion().addTask(TaskLibrary.useObjectTask(mEquipment, "create-" + unitName));
					}

					@Override
					public String getDescription() {
						return "Building a new " + unitName;
					}

					@Override
					public String getStatus() {
						return "Creating Monster";
					}

					@Override
					public boolean isComplete() {
						return true;
					}

					@Override
					public void update(int delta) throws SlickException {}
					
					@Override
					public boolean requireType(String type) {
						// Only hunchbacks can build new monsters
						return type.equalsIgnoreCase("Hunchback");
					}
				};
			
	}
	
	public void upgrade()
	{
		level++;
	}

	
}
