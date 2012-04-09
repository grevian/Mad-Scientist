import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;


public class TaskLibrary {
	
	public static MinionTask getWalkTo(final Minion aUnit, final Coord mDest )
	{
		return TaskLibrary.getWalkTo(aUnit, mDest.getX(), mDest.getY());
	}
	
	public static MinionTask getWalkTo(final Minion aUnit, final int x, final int y )
	{
		return new MinionTask()
		{
			private int finalXPos, finalYPos;
			@Override
			protected void execute() throws SlickException {
				finalXPos = x;
				finalYPos = y;
				try {
				aUnit.addPath(x, y);
				}
				catch (SlickException e)
				{
					Log.warn("Could not add pathing task!: " + e.getMessage());
					this.interrupt(false);
				}
			}

			@Override
			public boolean isComplete() {
				// Not the most efficient thing ever because it instances a new class each time, but eh.
				Coord mCoord = aUnit.getPosition();
			
				if ( isInterrupted() && !canResume() ) return true;
				if ( mCoord.getX() == finalXPos && mCoord.getY() == finalYPos ) return true;
				
				return false;
			}

			@Override
			public void update(int delta) throws SlickException {
			}
			
			public String getDescription()
			{
				return "Walk to coordinates [" + x + "|" + y + "]";
			}
		};
		
	}
	
	public static MinionTask getRecoverTask(final Minion mUnit)
	{
		return new MinionTask()
		{
			@Override
			protected void execute() throws SlickException {
				UseableObject mRecovery = mUnit.getLevel().findObjectType("Bed");
				// Walk to the recovery object
				if ( mRecovery == null )
					Log.warn("Unit needs to recover, but no recovery locations are available!");
				else
				{
					Coord dest = mRecovery.getLocation();
					mUnit.addTask( TaskLibrary.getWalkTo(mUnit, dest.getX(), dest.getY()));
				}

				// Use the object once the unit is there
				mUnit.addTask(TaskLibrary.useObjectTask(mUnit, mRecovery));
			}

			@Override
			public boolean isComplete() {
				return true; // This task just adds other tasks, it's complete as soon as it executes
			}

			@Override
			public void update(int delta) throws SlickException {	
			}

			@Override
			public String getDescription() {
				return "Walk to a bed and use it";
			}
			
		};
	}

	public static MinionTask useObjectTask(final Minion mUnit,
			final UseableObject mObject) {
		return new MinionTask() {
			
			private boolean completed = false;
			private int timeUsed;
			
			@Override
			protected void execute() throws SlickException {
				Log.debug("Minion " + mUnit.getName() + " is attempting to use object " + mObject.getName() );
				int range = mObject.getRange();
				Coord oLoc = mObject.getLocation();
				Coord mLoc = mUnit.getPosition();
				
				if ( Math.abs(oLoc.getX() - mLoc.getX()) > range || Math.abs(oLoc.getY() - mLoc.getY()) > range)
				{
					Log.warn("Minion attempted to use an object they were not within range of! " + mUnit.getName() + " attempted to use " + mObject.getName());
					completed = true;
					return;
				}
				
				if ( mObject.getCurrentUsers() < mObject.getMaxUsers() )
				{
					mObject.addUser(mUnit);
				}
				else
				{
					Log.warn("Minion attempted to use an object that was already in use! " + mUnit.getName() + " attempted to use " + mObject.getName());
					completed = true;
					return;
				}
			}

			@Override
			public boolean isComplete() {
				return completed;
			}

			@Override
			public void update(int delta) throws SlickException {
				timeUsed += delta;
				if ( timeUsed >= mObject.getTimeToUse() )
				{
					mObject.doEffect(mUnit);
					mObject.removeUser(mUnit);
					completed = true;
				}				
			}

			@Override
			public String getDescription() {
				return "Use the object " + mObject.getName();
			}
			
		};
	}
	
}
