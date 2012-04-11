import java.util.Properties;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.StateBasedGame;

public abstract class Minion extends Entity {

	private int beauty, brains, brawn;
	private int Health, MaxHealth;
	private final Sound hitSound;
	
	public Minion(GameCore mCore, Properties config) throws SlickException {
		super(mCore, config);
		setStatus("IDLE");
		
		hitSound = new Sound("./res/Sounds/Blow1.wav");
		this.setBeauty(Integer.parseInt(config.getProperty("beauty")));
		this.setBrains(Integer.parseInt(config.getProperty("brains")));
		this.setBrawn(Integer.parseInt(config.getProperty("brawn")));
		this.setHealth(Integer.parseInt(config.getProperty("health")));
		this.MaxHealth = Integer.parseInt(config.getProperty("health"));
		
		String[] loadValues = {"gold-cost", "parts-cost", "tech-cost" };
		for ( String s: loadValues )
		{
			this.setValue(s, Integer.parseInt(config.getProperty(s)));
		}
		
	}

	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException
	{
		super.update(container, game, delta);
	}

	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		super.render(arg0, arg1, arg2);
	}
	
	public String getName()
	{
		return getTypeName() + iCounter;
	}
	
	public String toString()
	{
		return getName() + " ["+beauty+"/"+brains+"/"+brawn+"] " + getStatus();
	}

	public int getBeauty() {
		return beauty;
	}

	public void setBeauty(int beauty) {
		this.beauty = beauty;
	}

	public int getBrains() {
		return brains;
	}

	public void setBrains(int brains) {
		this.brains = brains;
	}

	public int getBrawn() {
		return brawn;
	}

	public void setBrawn(int brawn) {
		this.brawn = brawn;
	}

	@Override
	public boolean isFriendly() {
		return true;
	}

	public int getMaxHealth() {
		return MaxHealth;
	}

	public void setHealth(int health) {
		Health = health;
		if ( health < 0 )
			getCore().kill(this);
	}

	public int getHealth() {
		return Health;
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

	public abstract void placeUpdate(Place place, int delta);

	public void combatEngage(Enemy e) {
		this.interrupt(); // Clear all current paths and 
		addTask( getAttackTask(e) );
	}
	
	public MinionTask getAttackTask(final Enemy Target)
	{
		final Minion me = this;
		
		return new MinionTask() {
			int reposTimer = 0;
			int attackTimer = 0;
			
			@Override
			protected void execute() throws SlickException {
				try {
				getMinion().addPath(Target.getPosition());
				}
				catch (SlickException e)
				{
					// do nothing, this task gets repeated by itself anyways
				}
			}

			@Override
			public String getDescription() {
				return "Defending you from attack!";
			}

			@Override
			public String getStatus() {
				return "In Combat!";
			}

			@Override
			public boolean isComplete() {
				// We keep going until the target is dead
				if ( Target.getValue("health") <= 0) return true;
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
						getMinion().interrupt();
						getMinion().addTask(me.getAttackTask(Target)); // re-add ourselves
						reposTimer = 0;
					}
				}
				else
				{
					attackTimer += delta;
					if ( attackTimer >= 1500 )
					{
						// We are within range of our target, Attack them!
						hitSound.play();
						Target.setValue("health", Target.getValue("health") - me.getBrawn());
					}
				}
				
			}
			
		};
	}


}
