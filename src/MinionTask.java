import org.newdawn.slick.SlickException;


public abstract class MinionTask {
	
	private boolean isStarted = false, interrupted = false;
	private boolean canResume = true;
	public boolean isStarted() { return isStarted; }
	
	public void start() throws SlickException { isStarted = true; execute(); }
	
	// Setup initial task parameters as needed
	protected abstract void execute() throws SlickException;
	
	// Test if the task has been completed
	public abstract boolean isComplete();
	
	// Interrupt the task, indicate wether it will be resumed later or not
	public boolean interrupt(boolean canResume) {
		interrupted = true;
		this.canResume  = canResume;
		return true;
	}
	
	protected boolean isInterrupted() { return interrupted; }
	protected boolean canResume() { return canResume; }

	// Update the tasks progress
	public abstract void update(int delta) throws SlickException;
	
	public abstract String getDescription();
	
}
