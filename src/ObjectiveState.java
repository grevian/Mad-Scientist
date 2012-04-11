import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


public class ObjectiveState extends BasicGameState {

	public static int ID = 8;
	private GameCore mCore;
	private String equipmentObjective = "You must have at least Level 4 Lab Equipment to build " +
			"your Masterpiece Monster.";
	private String equipmentLevel = "Your current equipment level is ";
	private String monsterPartsNeeded = "You must have all 6 special parts to build your Masterpiece.";
	private String monsterPartsHave = "You currently have: ";
	private String left_arm = "a left arm";
	private String right_arm = "a right arm";
	private String left_leg = "a left leg";
	private String right_leg = "a right leg";
	private String torso = "a torso";
	private String head = "a head";
	
	private String writeup1 = "Soon you will prove to the world that you truly are a genius!";
	private String writeup2 = "By sending out your minions to rob graves and steal from the town,";
	private String writeup3 = "you can use the body parts and money to build monsters to swell your ranks.";
	private String writeup4 = "Use the exceptional parts to build your Masterpiece monster and gain";
	private String writeup5 = "the respect of the scientific community, along with ruling the countryside";
	private String writeup6 = "with an iron fist!";
	
	private ArrayList<UseableObject> temp = new ArrayList<UseableObject>();
	@Override
	public int getID() {
		
		return ID;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1 )
			throws SlickException {
		mCore = ((SlickGame)arg1).getGameCore();
		
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		arg2.setBackground(Color.black);
		arg2.drawString(writeup1, 30, 50);
		arg2.drawString(writeup2, 30, 70);
		arg2.drawString(writeup3, 30, 90);
		arg2.drawString(writeup4, 30, 110);
		arg2.drawString(writeup5, 30, 130);
		arg2.drawString(writeup6, 30, 150);
		arg2.drawString(equipmentObjective, 50, 220);
		arg2.drawString(equipmentLevel + getEquipmentLevel() + ".", 50, 240);
		arg2.drawString(monsterPartsNeeded, 50, 300);
		arg2.drawString(monsterPartsHave + mCore.getValue("Special Parts"), 50, 320);
		
		
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
		// TODO Auto-generated method stub
		
	}
	
	public void keyPressed(int key, char arg1) {
		if ( key == Input.KEY_ESCAPE )
		{
			mCore.getGame().enterState(CastleScreen.ID);
		}
	}
	
	private int getEquipmentLevel()
	{
		temp = mCore.getObjects();
		for (UseableObject o: temp )
		{
			if(o.getTypeName().equals("Equipment"))
			{
				return ((Equipment)o).getLevel();
			}
		}
		return -1;
	}
	
	

}
