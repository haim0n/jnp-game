package com.shval.jnpgame;

 
public class Globals {
	
	// directions
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int RIGHT = 2;
	public static final int LEFT = 3;

	// cell types
	
	// types are used as indexes in some cases so keep them contiguous	
	public static final int NONE = -1;
	public static final int WALL = 0;
	public static final int JELLY_BLACK = 1;
	public static final int JELLY_RED = 2;
	public static final int JELLY_GREEN = 3;
	public static final int JELLY_BLUE = 4;
	public static final int JELLY_YELLOW = 5;
	public static final int MAX_JELLY_TYPES = 6; 


}