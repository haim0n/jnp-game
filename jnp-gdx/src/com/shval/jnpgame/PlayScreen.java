package com.shval.jnpgame;

import static com.shval.jnpgame.Globals.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;

public class PlayScreen implements Screen, InputProcessor {

	private static final String TAG = Board.class.getSimpleName();
	
	private Board board; // this is our world now
	private BoardView boardView;
	private JnpGame game;
	private int level;

	
	// UI
	private final int UI_FACTOR = 8; // higher is more sensitive
	int uiThreshold;	
	private int cellWidth;
	private int cellHeight;

	// panel state
	int xDown;
	int yDown;
	boolean down;
	

	public PlayScreen(JnpGame game, int level) {
		this.game = game;
		this.level = level;
		Gdx.app.debug(TAG, "Rseting level " + level);
		board = new Board(level);
		board.start();
		boardView = new BoardView();
		boardView.setBoard(board);
	}
	
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		board.update(delta);
	       Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
	       Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		boardView.render();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		Gdx.app.debug(TAG, "Resizing screen to " + width + " x " + height);
		board.setResolution(width, height);
		cellWidth = board.getSpriteWidth();
		cellHeight = board.getSpriteHeight();
		uiThreshold = cellWidth/UI_FACTOR;
	}

	@Override
	// this is called when the main game makes this screen active
	public void show() {
		 Gdx.input.setInputProcessor(this);
	}

	@Override
	// this is called when the main game makes another screen active
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		Gdx.input.setInputProcessor(null);
	}

	// * InputProcessor methods ***************************//
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		Gdx.app.debug(TAG, "Action keydown spotted. keycode = " + keycode);
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Gdx.app.debug(TAG, "Action down spotted. coords: x = " + screenX + " y = " + screenY);
		xDown = screenX;
		
		// here (0, 0) is in the upper left, thats nasty
		yDown = this.board.boardHeight - screenY;
		 
		
		int x = xDown/cellWidth;
		int y = yDown/cellHeight;
		
		Gdx.app.debug(TAG, "Action down spotted. boardHeight: " + this.board.boardHeight + ". x = " + x + " y = " + y);
		if (y == 0 && x >= board.getCols() - 5)
			game.reset();
		
		down = true;
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		down = false;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		int x = screenX;
		// here (0, 0) is in the upper left, thats nasty
		int y = this.board.boardHeight - screenY;
		int dir;
		int uiThreshold = cellWidth/UI_FACTOR;
		Gdx.app.debug(TAG, "Action move spotted. coords: x = " + x + " y = " + y);
		
		// apply thresholds for good UI
		if (x > xDown + uiThreshold)
			dir = RIGHT;
		else if (x < xDown - uiThreshold)
			dir = LEFT;
		else
			return true;

		Gdx.app.debug(TAG, "Attempting to slide cell (" + xDown/cellWidth + ", " + yDown/cellHeight + ")" + "in direction " + dir);
		board.attemptSlide(dir, xDown/cellWidth, yDown/cellHeight);
		xDown = x;
		yDown = y;
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
