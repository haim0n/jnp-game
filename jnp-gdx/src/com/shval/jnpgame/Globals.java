package com.shval.jnpgame;

 
public class Globals {
	
	// fits for every hole
	public static final int NONE 	= -1;
	
	// directions
	// those are used as indexes in some cases so keep them contiguous
	public static final int UP 		= 0;
	public static final int DOWN 	= 1;
	public static final int RIGHT 	= 2;
	public static final int LEFT 	= 3;

	// neighbours those are used as indexes in some cases so keep them contiguous	
	public static final int NW 		= 0; // north west
	public static final int NORTH 	= 1;
	public static final int NE 		= 2;
	public static final int WEST 	= 3;
	public static final int EAST 	= 4;
	public static final int SW 		= 5;
	public static final int SOUTH 	= 6;
	public static final int SE 		= 7;
	
	// cell types
	// types are used as indexes in some cases so keep them contiguous	
	public static final int WALL 			= 0;
	
	public static final int JELLY_RED 		= 2;
	public static final int JELLY_GREEN 	= 3;
	public static final int JELLY_BLUE 		= 4;
	public static final int JELLY_YELLOW 	= 5;
	public static final int MAX_COLORED_JELLY_TYPES = 6; 
	// reserved ranged for futuristic space jellies
	// black jelly are mapped to type >= 100
	public static final int JELLY_BLACK_MIN	= 100;
	
	// sounds
	public static final int SOUND_FALL = 0;
	public static final int SOUND_SLIDE = 1;
	public static final int SOUND_MERGE_START = 2;
	public static final int SOUND_MERGE_FINISH = 3;
	public static final int SOUND_WIN = 4;
	public static final int MAX_BOARD_SOUNDS = 5;
	
	public static final int SOUND_BUTTON = 10;
}