package com.shval.jnpgame;

import static com.shval.jnpgame.Globals.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayScreen implements Screen, InputProcessor {

	private static final String TAG = PlayScreen.class.getSimpleName();
	
	private Background background;
	private Board board; // this is our world now
	private JnpGame game;
	
	private TextureRegion resetButtonTextureR;
	private Sound buttonSound;
	private float soundVolume; // in [0,1]
	
	private int boardWidth;
	private int boardHeight;
	SpriteBatch spriteBatch;
	private OrthographicCamera camera;
	
	// UI
	private final int UI_FACTOR = 8; // higher is more sensitive
	int uiThreshold;	
	private int cellWidth;
	private int cellHeight;

	// panel state
	int xDown;
	int yDown;
	boolean down;
	
	public PlayScreen(BoardConfig config, JnpGame game) {
		this.game = game;
		// this.level = level;
		board = new Board(config, this);
		background = config.getBackground();
		buttonSound = config.getSound(SOUND_BUTTON);
		soundVolume = config.getSoundVolume();
		
		Texture resetButtonsTexture = config.getResetButtonsTexture();
		resetButtonTextureR = new TextureRegion(resetButtonsTexture, 0, 0, 256, 128);
		
		// ex BoardView
		spriteBatch = new SpriteBatch();
		camera = new OrthographicCamera(10, 7);
        camera.position.set(5, 3.5f, 0);
        camera.update();
	}
	
	@Override
	public void render(float delta) {
		spriteBatch.begin();
		
		// first update
		board.update(delta);
		
		// render
		
		// background (render & update)
		background.render(delta, spriteBatch);
		
		// board
		board.render(spriteBatch);

		// buttons
		int COLS = board.getCols();
		spriteBatch.draw(resetButtonTextureR, (COLS - 4) * cellWidth, 0 * cellHeight,
				3 * cellWidth, cellHeight * 6 / 8);

		spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		Gdx.app.debug(TAG, "Resizing screen to " + width + " x " + height);
		this.boardWidth = width;
		this.boardHeight = height;
		board.setResolution(width, height);
		background.setResolution(width, height);
		cellWidth = board.getSpriteWidth();
		cellHeight = board.getSpriteHeight();
		uiThreshold = cellWidth/UI_FACTOR;
		// the action begins (here, and not in Screen's constructor!)
		board.start();
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
		yDown = boardHeight - screenY;
		 
		
		int x = xDown/cellWidth;
		int y = yDown/cellHeight;
		
		int max_col = board.getCols() - 1;
		int max_row = board.getRows() - 1;
		
		Gdx.app.debug(TAG, "Action down spotted. boardHeight: " + boardHeight + ". x = " + x + " y = " + y);
		if (y == 0 && x >= max_col - 3 && x <= max_col - 1)
			game.reset();
		if (y == 0 && x == max_col - 5)
			board.revert();
		if (y == max_row && x == 0)
			previousLevel();
		if (y == max_row && x == max_col)
			nextLevel();
		
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
		int y = boardHeight - screenY;
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

	public void nextLevel() {
		game.nextLevel();
	}

	public void previousLevel() {
		game.previousLevel();
	}

	public void win() {
		nextLevel();
	}

}
