import java.util.ArrayList;
import java.util.Properties;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;


public class Enemy extends Entity {

	private Properties config;
	private final Sound hitSound;
	
	public Enemy(GameCore mCore, Properties mProps) throws SlickException {
		super(mCore, mProps);
		config = mProps;
		
		hitSound = new Sound("./res/Sounds/Blow1.wav");
		
		setValue("strength", Integer.parseInt(config.getProperty("strength")));
		setValue("health", Integer.parseInt(config.getProperty("health")));
		setStatus("IDLE");
		
	}
	
	protected boolean addTask(MinionTask mTask)
	{
		try
		{
			mTask.setMinion(this);
			return super.addTask(mTask);
		}
		catch (SlickException e)
		{
			return false;
		}
	}	

	@Override
	public boolean isFriendly() {
		return false;
	}

	@Override
	public String getName()
	{
		return getTypeName() + iCounter;
	}

	public void attack(Minion m)
	{
		Log.info(getName() + " is attacking " + m.getName() + "!");
		setStatus("Attacking!");
		addTask(getAttackTask(m));
	}
	
	@SuppressWarnings("unchecked") // We know it's Minions because it's coming from the gameCore
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException
	{
		super.update(container, game, delta);
		GameCore mGame = ((SlickGame)game).getGameCore();
		
		if ( getValue("health") <= 0 )
		{
			mGame.kill(this);
			return;
		}
		
		if ( getStatus().equalsIgnoreCase("IDLE") )
		{
			// Find and attack a target at random
			ArrayList<Entity> mTargets = (ArrayList<Entity>) mGame.getMinions().clone();
			java.util.Collections.shuffle(mTargets);
			if ( mTargets.size() > 0 )
				attack((Minion) mTargets.get(0));
		}
	}

	public MinionTask getAttackTask(final Minion Target)
	{
		final Enemy e = this;
		
		return new MinionTask() {
			int reposTimer = 0;
			int attackTimer = 0;
			
			@Override
			protected void execute() throws SlickException {
				Target.combatEngage(e);
				try {
				getMinion().addPath(Target.getPosition());
				}
				catch (SlickException e)
				{
					// eh
				}
			}

			@Override
			public String getDescription() {
				return "Try to destroy you!";
			}

			@Override
			public String getStatus() {
				return "Attacking!";
			}

			@Override
			public boolean isComplete() {
				// We keep going until the target is dead
				if ( Target.getHealth() < 0) return true;
				if ( isInterrupted() ) return true;
				return false;
			}

			@Override
			public void update(int delta) throws SlickException {
				// Check if we're within attack range of our target,
				if (getMinion().getPosition().rangeOf(Target.getPosition()) > 5) {
					reposTimer += delta;
					// If not, re-plot a path every few seconds (We assume they're moving too)
					if (reposTimer >= 1500) {
						Log.debug("Enemy plotting new route to target");
						getMinion().interrupt();
						// getMinion().addTask(); // re-add an attack
						reposTimer = 0;
					}
				}
				else
				{
					Log.debug("Enemy Attacking!");
					attackTimer += delta;
					if ( attackTimer >= 1500 )
					{
						// We are within range of our target, Attack them!
						Target.setHealth(Target.getHealth() - getValue("strength"));
						hitSound.play();
					}
				}
				
			}
			
		};
	}
	
	public String toString()
	{
		return getName() + "HP: " + getValue("health") + " - " + getStatus();
	}
	
}
