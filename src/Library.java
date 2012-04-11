import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


public class Library extends UseableObject {

	private int PerkDelay = 15000, counter = 0;
	
	public Library(GameCore mCore) throws SlickException {
		super("./res/items/library.cfg", mCore);
		getCore().setValue("research-level", 1);
		getCore().setValue("queued-research-tasks", 0);
	}

	@Override
	public boolean doEffect(Minion mUnit, String effect) {
		if ( effect.equalsIgnoreCase("research") )
		{
			getCore().setValue("Tech", getCore().getValue("Tech")+ 1);
			OverlayGUI.LogMessage("A Researching Minion has upgraded your knowledge level to " + getCore().getValue("Tech"));
			return true;
		}
		return false;
	}

	@Override
	public String getTypeName() {
		return "Library";
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		super.update(container, game, delta);
		
		counter += delta; 

		
		int ResearchLevel = getCore().getValue("research-level");

		if (counter >= (PerkDelay / ResearchLevel)) {
			// Reset the timer no matter what
			counter = 0;
			// But only queue a task if we haven't hit the max-researchers level yet
			if (getCore().getValue("queued-research-tasks") < getCore()
					.getValue("research-level")
					&& this.getUsers().size() < this.getMaxUsers()
					&& this.getUsers().size() < getCore().getValue("research-level"))
			{
				getCore().setValue("queued-research-tasks",getCore().getValue("queued-research-tasks")+1);

				final Library mLib = this;
				final GameCore mCore = getCore();

				getCore().queueTask(new MinionTask() {
					protected void execute() throws SlickException {
						mCore.setValue("queued-research-tasks", mCore.getValue("queued-research-tasks")-1);
						getMinion().addTask(TaskLibrary.getWalkTo(mLib.getLocation()));
						getMinion().addTask(TaskLibrary.useObjectTask(mLib, "research"));	
					}

					@Override
					public String getDescription() {
						return "Walk to the library and start using it";
					}

					@Override
					public String getStatus() {
						return "Researching new techniques";
					}

					@Override
					public boolean isComplete() {
						return true;
					}

					@Override
					public void update(int delta) throws SlickException {
					}
					
					@Override
					public boolean requireType(String type) {
						// Only hunchbacks can do research
						return type.equalsIgnoreCase("Hunchback");
					}

				});
			}
		}
	}

	public void upgrade()
	{
		// Increase the max tech level
	}
	
}
