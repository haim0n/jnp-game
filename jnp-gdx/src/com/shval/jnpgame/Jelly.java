package com.shval.jnpgame;

import static com.shval.jnpgame.Globals.*;

import java.util.ArrayList;


public class Jelly {
	
	private Board board;
	private ArrayList<Cell> cells;
	private boolean isFixed = false;

	public Board getBoard() {
		return board;
	}
	public Jelly(Board board) {
		this.board = board;
		cells = new ArrayList<Cell>();
	}
	
	public void join(Cell cell) {
		cells.add(cell);
		isFixed |= cell.getIsFixed();
	}

	public void merge(Jelly neighbour) {
		for (Cell c : cells) {
			c.setJelly(neighbour);
			neighbour.join(c);
		}
	}
	
	public boolean canMove(int dir) {
		boolean ret = true;
		int dx = 0, dy = 0;
		if (isFixed)
			return false;
		switch (dir) {
		case LEFT:
			dx = -1;
			break;
		case RIGHT:		
			dx = 1;
			break;
		case DOWN:
			dy = 1;
			break;
		case UP:
			dy = -1;
			break;
		default:
			// should never be here
			return false;
		}
		
		for (Cell c : cells) {
			// iff we can move all jelly cells
			int x = c.getX();
			int y = c.getY();
			Cell neighbour = board.getCell(x + dx, y + dy);
			if ((neighbour == null) || (neighbour.getJelly() == this) )
				continue;
			ret &= neighbour.canMove(dir);
		}
		return ret;
	}

	public void move(int dir) {
		// move jelly and its neighbors
		
		int dx = 0, dy = 0;
		switch (dir) {
		case LEFT:
			dx = -1;
			break;
		case RIGHT:		
			dx = 1;
			break;
		case DOWN:
			dy = 1;
			break;
		case UP:
			dy = -1;
			break;
		default:
			// should never be here
			return;
		}
		
		for (Cell c : cells) {
			//move cell. call not looped due to cell traversing flag
			c.move(dir);
			
			//check neighbors
			int x = c.getX();
			int y = c.getY();
			Cell neighbour = board.getCell(x + dx, y + dy);
			if (neighbour == null)
				continue;
			neighbour.move(dir);
		}
	}
}
