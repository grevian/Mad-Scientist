public class Coord 
	{
		private int x, y;
		protected Coord(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		
		public int getX() { return x; }
		public int getY() { return y; }
		
		public String toString()
		{
			return "[" + String.valueOf(x) + "|" + String.valueOf(y) + "]";
		}

		public int rangeOf(Coord position) {
			int range = 0;
			range += Math.abs(position.getX() - x);
			range += Math.abs(position.getY() - y);
			return range;
		}
	}