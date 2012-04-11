import java.util.Random;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;


public class TaskLibrary {
	
	private static Random mRand = new Random();
	
	public static MinionTask getWalkTo(final Coord mDest )
	{
		return TaskLibrary.getWalkTo(mDest.getX(), mDest.getY());
	}
	
	public static MinionTask getIdleTask()
	{
		final int waitTime = mRand.nextInt(3)*1000;
		
		return new MinionTask()
		{
			
			@Override
			protected void execute() throws SlickException {
				getMinion().addTask(TaskLibrary.getWalkTo(getMinion().getLevel().RandomLocation()));
				getMinion().addTask(TaskLibrary.getWaitTask(waitTime));	
			}

			@Override
			public String getDescription() {
				return "Wasting Time";
			}

			@Override
			public String getStatus() {
				return "Wandering";
			}

			@Override
			public boolean isComplete() {
				return true;
			}

			@Override
			public void update(int delta) throws SlickException {
			}
			
		};
	}
	
	public static MinionTask getWalkTo(final int x, final int y )
	{
		return new MinionTask()
		{
			private int finalXPos, finalYPos;
			@Override
			protected void execute() throws SlickException {
				finalXPos = x;
				finalYPos = y;
				try {
				getMinion().addPath(x, y);
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
				Coord mCoord = getMinion().getPosition();
			
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

			@Override
			public String getStatus() {
				return "Walking";
			}
		};
		
	}
	
	public static MinionTask getRecoverTask()
	{
		return new MinionTask()
		{
			@Override
			protected void execute() throws SlickException {
				UseableObject mRecovery = getMinion().getLevel().findObjectType("Bed");
				// Walk to the recovery object
				if ( mRecovery == null )
				{
					Log.warn("Unit needs to recover, but no recovery locations are available!");
					getMinion().addTask( TaskLibrary.getWaitTask(2000) );
					// getMinion().addTask(TaskLibrary.getRecoverTask());
				}
				else
				{
					Coord dest = mRecovery.getLocation();
					// Walk to the object, then use it
					getMinion().addTask( TaskLibrary.getWalkTo(dest.getX(), dest.getY()));
					getMinion().addTask(TaskLibrary.useObjectTask(mRecovery));
				}

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

			@Override
			public String getStatus() {
				return "Finding somewhere to rest";
			}
			
		};
	}

	public static MinionTask getWaitTask(final int i) {
		return new MinionTask()
		{
			int counter = 0;
			int waitTime = i;
			
			protected void execute() throws SlickException {
			}

			@Override
			public String getDescription() {
				return "Wait for " + Math.round(i/1000) + " seconds...";
			}

			@Override
			public String getStatus() {
				return "Waiting for something";
			}

			@Override
			public boolean isComplete() {
				if ( counter >= waitTime )
					return true;
				return false;
			}

			@Override
			public void update(int delta) throws SlickException {
				counter += delta;
			}
		};
	}

	public static MinionTask useObjectTask(final UseableObject mObj) {
		return TaskLibrary.useObjectTask(mObj, "");
	}

	public static MinionTask useObjectTask(final UseableObject mObj,
			final String string) {
		return new MinionTask() {

		private boolean completed = false;
			private int timeUsed;
			private UseableObject mObject = mObj;
			private String effect = string;
			
			@Override
			protected void execute() throws SlickException {
				Log.debug("Minion " + getMinion().getName() + " is attempting to use object " + mObject.getName() );
				int range = mObject.getRange();
				Coord oLoc = mObject.getLocation();
				Coord mLoc = getMinion().getPosition();
				
				if ( Math.abs(oLoc.getX() - mLoc.getX()) > range || Math.abs(oLoc.getY() - mLoc.getY()) > range)
				{
					Log.warn("Minion attempted to use an object they were not within range of! " + getMinion().getName() + " attempted to use " + mObject.getName());
					completed = true;
					return;
				}
				
				if ( mObject.getCurrentUsers() < mObject.getMaxUsers() )
				{
					mObject.addUser((Minion) getMinion());
				}
				else
				{
					// Wander around a bit then try again
					getMinion().addTask(TaskLibrary.getWalkTo(getMinion().getLevel().RandomLocation(getMinion().getPosition(), 5)));
					getMinion().addTask(TaskLibrary.getWaitTask(3000));
					getMinion().addTask(TaskLibrary.getWalkTo(mObject.getLocation()));
					getMinion().addTask(TaskLibrary.useObjectTask(mObject, effect));
					completed = true; // This attempt is completed
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
					mObject.doEffect((Minion) getMinion(), effect);
					mObject.removeUser((Minion) getMinion());
					completed = true;
				}				
			}

			@Override
			public String getDescription() {
				return "Use the object " + mObject.getName();
			}

			@Override
			public String getStatus() {
				return "Making use of a(n) " + mObject.getName();
			}
			
		};
	}
	
	public static MinionTask groupTasks( final String filter, MinionTask ... nt )
	{
		final MinionTask[] f = nt;
		
		return new MinionTask()
		{
			MinionTask t[] = f; // Store the tasks for later idea on grouping tasks...
			
			@Override
			protected void execute() throws SlickException {
				for ( MinionTask mt: t )
				{
					getMinion().addTask(mt);
				}
			}

			@Override
			public String getDescription() {
				return "A Utility Task for wrapping multiple tasks together";
			}

			@Override
			public String getStatus() {
				return "Doing a number of things";
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
				if ( filter == null )
					return true;
				else
					return type.equalsIgnoreCase(filter);
			}

			
		};
	}
	
}
