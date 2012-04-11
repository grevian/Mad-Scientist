import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;


public class PathMask implements TileBasedMap {

	private int height, width;
	private Tile[][] mTiles;
	private Image sourceImage;
	private int TileSize;
	private String MapFilename;
	private Random mRand = new java.util.Random();
	private GameCore mCore;

	public Coord Grid2Coord(int x, int y)
	{	
		// The center of the grid tile
		int nx = (x * TileSize); // - (TileSize/2);
		int ny = (y * TileSize); // - (TileSize/2);
		Log.info("Translating coordinate ["+x+"]["+y+"] to ["+nx+"]["+ny+"]");
		return new Coord(nx, ny);		
	}
	
	
	public Coord Coord2Grid(int x, int y)
	{
		// A Basic boundry check for now
		int tX = 0, tY = 0;
		if ( x > 0 )
			tX = x/TileSize;
		if ( y > 0 )
			tY = y/TileSize;
		if ( x > width )
			tX = width/TileSize;
		if ( y > height )
			tY = height/TileSize; 
		
		return new Coord(tX, tY);
	}
	
	public boolean GenerateGrid()
	{		
		mTiles = new Tile[width][height];
		
		for ( int x = 0; x <= sourceImage.getWidth()-TileSize; x+=TileSize )
		{			
			for ( int y = 0; y <= sourceImage.getHeight()-TileSize; y+=TileSize )
			{
				// Analyze the pixels in the square and determine it's attributes, COSTLY
				int BlackAMT = 0;
				int RedAMT = 0;
				int BlueAMT = 0;
				int WhiteAMT = 0;
				
				for ( int p = x; p < x+TileSize; p++ )
				{
					for ( int q = y; q < y+TileSize; q++ )
					{ 
						Color mCol = sourceImage.getColor(p, q);
						int blue = mCol.getBlue();
						int red = mCol.getRed();
						int green = mCol.getGreen();
						
						if ( blue >= 250 && red >= 250 && green >= 250 )
							WhiteAMT++;
						else if ( blue > 250 )
							BlueAMT++;
						else if ( red >= 250 )
							RedAMT++;
						else if ( blue < 5 && red < 5 && green < 5 )
							BlackAMT++;	
					}
				}
				
				// TODO: Clean this up, it's a bit of a hack from some troubleshooting
				int xCoord, yCoord;
				if ( x > 0 )
					xCoord = x/TileSize;
				else
					xCoord = x;
				
				if ( y > 0 )
					yCoord = y/TileSize;
				else
					yCoord = y;

				mTiles[xCoord][yCoord] = new Tile();
				
				// 40%+ black means it's impassable, otherwise go with the highest value present
				if ( BlackAMT > (TileSize*TileSize)/40 )
					mTiles[xCoord][yCoord].setType(Tile.IMPASSABLE);
				else if ( Math.max(Math.max(BlueAMT, RedAMT),WhiteAMT) == BlueAMT )
					mTiles[xCoord][yCoord].setType(Tile.RESOURCES);
				else if ( Math.max(Math.max(BlueAMT, RedAMT),WhiteAMT) == RedAMT )
					mTiles[xCoord][yCoord].setType(Tile.BUILDABLE);
				else if ( Math.max(Math.max(BlueAMT, RedAMT),WhiteAMT) == WhiteAMT )
					mTiles[xCoord][yCoord].setType(Tile.NORMAL);
				
			}
		}
		save();
		return true;
	}
	
	public PathMask(String sourceMaskPath, int mTileSize, GameCore mCore) throws SlickException
	{
		this.mCore = mCore;
		MapFilename = sourceMaskPath;
		sourceImage = new Image(sourceMaskPath);
		TileSize = mTileSize;
		height = sourceImage.getHeight()/TileSize;
		width = sourceImage.getWidth()/TileSize;
		
		// Try to load a pregenerated data grid, or generate and save one if we can't
		System.out.println("Attempting to load " + MapFilename+".map");
		if ( !load(MapFilename+".map") )
		{
			Log.info("Could not find Pregenerated Map Data, Generating Now...");
			GenerateGrid();
		}
	}
		
	private boolean save()
	{
		// Serialize our level for easy loading later
		StringBuilder mLine = new StringBuilder();
		for ( int y = 0; y < mTiles[0].length; y++ )
		{
			for ( int x = 0; x < mTiles.length; x++ )
			{
				mLine.append(mTiles[x][y].getType());
				if ( x == mTiles.length-1 )
					mLine.append("\n");
				else
					mLine.append(",");
			}
		}
		
		try {
			FileWriter fstream;
			fstream = new FileWriter(MapFilename+".map");
			BufferedWriter out = new BufferedWriter(fstream);
	        out.write(mLine.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean load(String Filename)
	{
		try {
			boolean created = false;
			
			FileInputStream fstream = new FileInputStream(Filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));			
						
			// Count the number of lines in the file, this tells us our grid height
			int lineCount = 0;
			while ( br.readLine() != null )
				lineCount++;
			br.close();
			
			// TODO: Uh oh it's 4:30am and my comments are getting mean, better recheck this code later...
			// Basically, Fuck you java. you suck. learn to seekg newb.
			fstream = new FileInputStream(Filename);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));		
			
			// Read in the lines and create the grid
			String strLine;
			int currentLine = 0;
			while ((strLine = br.readLine()) != null) 
			{
				String[] dataLine = strLine.split(",");
				int lineLength = dataLine.length; // This tells us the width of our grid
				if (!created)
				{
					created = true;
					mTiles = new Tile[lineCount+1][lineLength+1];
				}
				
				for ( int i = 0; i < dataLine.length; i++ )
				{
					mTiles[currentLine][i] = new Tile();
					mTiles[currentLine][i].setType(Integer.parseInt(dataLine[i]));
				}
			
				currentLine++;
			}
			br.close();
			return true;
		} 
		catch (Exception e) // Handles IOExceptions and NumberFormatExceptions, I don't really care what went wrong, just that anything did.
		{
			System.err.println("Error: " + e.getMessage());
			return false;
		}
	}
	
	@Override
	public boolean blocked(PathFindingContext arg0, int arg1, int arg2) {
		// FIXME: This will require work for the ENEMY pathfinding to let them group up...		
		// Cannot pass through enemies
		for ( Entity e: mCore.getEnemies() )
		{
			Coord mPos = e.getPosition();
			if ( mPos.getX() == arg1 && mPos.getY() == arg2 )
				return true;
		}
		
		return mTiles[arg1][arg2].getType() == Tile.IMPASSABLE;
	}

	@Override
	public float getCost(PathFindingContext arg0, int arg1, int arg2) {
		// FIXME: This is horribly HORRIBLY inefficient, we should store a cost in the tile and update
		// it when entities enter the tile
		
		// Increase cost slightly near fellow minions to avoid crowding
		for ( Entity e: mCore.getMinions() )
		{
			Coord mPos = e.getPosition();
			if ( mPos.getX() == arg1 && mPos.getY() == arg2 )
				return 15;
		}
		
		// Otherwise tiles are base cost 10 to cross
		return 10;
	}

	@Override
	public int getHeightInTiles() {
		return height;
	}

	@Override
	public int getWidthInTiles() {
		return width;
	}

	@Override
	public void pathFinderVisited(int arg0, int arg1) {
		// Do nothing, this is a debugging hook for new pathfinding algos
	}

	// TODO: These functions should be using TileSize
	public static int getTileX(int dx) {
		if ( dx < 10 )
			return 0;
		return dx/10;
	}
	public static int getTileY(int dy) {
		if ( dy < 10 )
			return 0;
		return dy/10;
	}

	public int getGridXAtPoint(int x) {
		return Math.round(x/TileSize);
	}

	public int getGridYAtPoint(int y) {
		return Math.round(y/TileSize);
	}

	public Coord RandomLocation() {
		for ( int i = 0; i < 100; i++ )
			{
				int p = mRand.nextInt(mTiles.length - 1);
				int q = mRand.nextInt(mTiles[p].length - 1);
				if ( mTiles[p][q].getType() == Tile.NORMAL )
					return new Coord(p, q);
			}
		Log.warn("Could not find passable random location to walk to after 100 tries O_O");
		return null;
	}
	
	public Coord RandomLocation(Coord src, int range) {
		for ( int i = 0; i < 1000; i++ )
			{
				// Wiggle around the src within +/- of the range
				int p = src.getX() + ( mRand.nextBoolean() ? mRand.nextInt(range) : mRand.nextInt(range) * (-1) );
				int q = src.getY() + ( mRand.nextBoolean() ? mRand.nextInt(range) : mRand.nextInt(range) * (-1) );
				// Do some sanity checking to make sure we're still within bounds
				p = Math.max(0, p);
				p = Math.min(mTiles.length-1, p);	
				q = Math.max(0, q);
				q = Math.min(mTiles[p].length-1, q);
				
				if ( mTiles[p][q].getType() == Tile.NORMAL )
					return new Coord(p, q);
			}
		Log.warn("Could not find passable random location to walk to after 1000 tries O_O");
		return src;
	}


	public  UseableObject findObjectType(String type) {
		for ( UseableObject uo: mCore.getObjects() )
		{
			if ( uo.getTypeName().equalsIgnoreCase(type) )
				return uo;
		}
		Log.warn("Minion attempted to find object of type " + type + ", but none were available!");
		return null;
	}
}
