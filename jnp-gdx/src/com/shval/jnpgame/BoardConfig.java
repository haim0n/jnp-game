package com.shval.jnpgame;

import static com.shval.jnpgame.Globals.*;

import com.example.jnp.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class BoardConfig {

	int ROWS;
	int COLS;
	Resources res;
	private static String TAG = BoardConfig.class.getName();
	private char cells[][];
	
	// level 1
	private String levels[][] = { 
			{ // level 0 - dev playground
			"xxxxxxxxxxxxxx",
			"x    dbrg  rRx",
			"xd   gygr    x",
			"xd   dgdy   Bx",
			"xw   yrydd   x",
			"x    ryby   Yx",
			"x    bdrg    x",
			"xxxxxxxxxxxxxx"
			},
			{ // level 1			
			"xxxxxxxxxxxxxx",
			"x            x",
			"x            x",
			"x      r     x",
			"x      xx    x",
			"x  g     r b x",
			"xxbxxxg xxxxxx",
			"xxxxxxxxxxxxxx"
			},
			{ // level 2
			"xxxxxxxxxxxxxx",
			"x            x",
			"x            x",
			"x            x",
			"x     g   g  x",
			"x   r r   r  x",
			"xxxxx x x xxxx",
			"xxxxxxxxxxxxxx"
			},
			};
	
/*
// level 1
	private char board[][] = {
			 {'w','w','w','w','w','w','w','w','w','w','w','w','w','w'},
			 {'w',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','w'},
			 {'w',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','w'},
			 {'w',' ',' ',' ',' ',' ',' ','r',' ',' ',' ',' ',' ','w'},
			 {'w',' ',' ',' ',' ',' ',' ','w','w',' ',' ',' ',' ','w'},
			 {'w',' ',' ','g',' ',' ',' ',' ',' ','r',' ','b',' ','w'},
			 {'w','w','b','w','w','w','g',' ','w','w','w','w','w','w'},
			 {'w','w','w','w','w','w','w','w','w','w','w','w','w','w'}
	 };
	
	// level2 
	private char board[][] = {
			 {'w','w','w','w','w','w','w','w','w','w','w','w','w','w'},
			 {'w',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','w'},
			 {'w',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','w'},
			 {'w',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','w'},
			 {'w',' ',' ',' ',' ',' ','g',' ',' ',' ','g',' ',' ','w'},
			 {'w',' ',' ',' ','r',' ','r',' ',' ',' ','r',' ',' ','w'},
			 {'w','w','r','w','w',' ','w',' ','w',' ','w','w','w','w'},
			 {'w','w','w','w','w','w','w','w','w','w','w','w','w','w'}
	};
*/	
	/*
	private char board[][] = {
			 {'w','w','w','w','w','w'},
			 {'w','y',' ','b','r','w'},
			 {'w','w',' ','R','w','w'},
			 {'w','y',' ','y',' ','w'},
			 {'w','w','w','w','w','w'}
	};
	*/
	/*
	private char board[][] = {
			 {'w','w','w'},
			 {'w','r','w'},
			 {'w','R','w'},
			 {'w',' ','w'},
			 {'w','w','w'}			 
	};
	*/
	
	public void setLevel(int i) {
		String board[] = levels[i];
		ROWS = board.length;
		COLS = board[0].length();
		cells = new char[COLS][ROWS];
		Log.d(TAG, "boardsize (" + COLS + "," + ROWS + ")");
		transposeBoard(board);
		Log.d(TAG, "Level " + i + "defined");
	}

	private void transposeBoard(String board[]) {
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				cells[x][y] = board[y].charAt(x);
			}
		}
	}
	
	public BoardConfig(Resources res) {
		this.res = res;
		setLevel(1); // default level
	}
	
	Bitmap getBitmap(int x, int y) {
		int type = getType(x, y);
		int imageId;
		switch(type) {
			case WALL:
				imageId = R.drawable.wall1;
				break;
			case JELLY_BLUE:
				imageId = R.drawable.jelly_blue;
				break;
			case JELLY_GREEN:
				imageId = R.drawable.jelly_green;
				break;
			case JELLY_RED:
				imageId = R.drawable.jelly_red;
				break;
			case JELLY_YELLOW:
				imageId = R.drawable.jelly_yellow;
				break;
			case JELLY_BLACK:
				imageId = R.drawable.jelly_black;
				break;
			default:
				imageId = 0;
				break;
		}

		if (imageId == 0) {
			return null;
		}
		return BitmapFactory.decodeResource(res, imageId);
	}
	
	boolean isFixed(int x, int y) {
		 char cell = cells[x][y];
		 boolean ret;
		 switch(cell) {
		 	case 'r' :
		 	case 'b' :
		 	case 'g' :
		 	case 'y' :
		 	case 'd' :		 		
		 		ret = false;
		 		break;
		 	default:
		 		ret = true;
		 		break;
		 }
		 return ret;
	}	
	
	int getType(int x, int y) {
		 char cell = cells[x][y];
		 int ret;
		 switch(cell) {
		 	case 'W' :
		 	case 'w' :		 		
		 	case 'x' :		 		
		 		ret = WALL;
		 		break;
		 	case 'r' :
		 	case 'R' :
		 		ret = JELLY_RED;
		 		break;
		 	case 'b' :
		 	case 'B' :
		 		ret = JELLY_BLUE;
		 		break;
		 	case 'g' :
		 	case 'G' :
		 		ret = JELLY_GREEN;
		 		break;
		 	case 'y' :
		 	case 'Y' :
		 		ret = JELLY_YELLOW;
		 		break;		 		
		 	case 'd' :
		 	case 'D' :		 		
		 		ret = JELLY_BLACK;
		 		break;
		 	default :
		 		ret = NONE;
		 		break;
		 }
		 return ret;
	}
	
}
