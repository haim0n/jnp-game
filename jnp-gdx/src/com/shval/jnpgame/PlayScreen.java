package com.shval.jnpgame;

import static com.shval.jnpgame.Globals.*;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlayScreen implements Screen, InputProcessor {

	private static final String TAG = PlayScreen.class.getSimpleName();
	
	private Background background;
	private Board board; // this is our world now
	private JnpGame game;
	private Sound buttonSound;
	private float soundVolume; // in [0,1]
	private ArrayList<Button> buttons;
	private ArrayList<JNPLabel> labels;
	//private int boardWidth;
	private int boardHeight;
	SpriteBatch spriteBatch;
	private OrthographicCamera camera;
	private boolean isPrevRevertable;
	
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
		
		Gdx.app.debug(TAG, "cellwidth is:" + cellWidth);
		initButtons(config);
		initLabels(config);

		this.isPrevRevertable = false;
		
		// ex BoardView
		spriteBatch = new SpriteBatch();
		camera = new OrthographicCamera(10, 7);
        camera.position.set(5, 3.5f, 0);
        camera.update();
        
		// the action begins (here, and not in Screen's constructor!)
        board.start();
	}

	private void initButtons(BoardConfig config) {
		buttons = new ArrayList<Button>();
		Button butt;
		float fineOffset = 0.2f;
		
		// reset button
		butt = new Button(board.getCols() - 4, 0 - fineOffset, 3, Button.BLACK_BG_BLUE_FRAME, Button.ICON_NONE, "Reset", new String("btnReset"));
		buttons.add(butt);
		
		// next button
		if (!config.lastLevel()) {
			butt = new Button(board.getCols() - 1, board.getRows() - 1 + fineOffset, 1, Button.BLACK_BG_BLUE_FRAME, Button.ICON_ARROW_RIGHT, null, new String("btnNext"));
			buttons.add(butt);
		}		
		
		// prev button
		if (!config.firstLevel()) {
			butt = new Button(0, board.getRows() - 1 + fineOffset, 1, Button.BLACK_BG_BLUE_FRAME, Button.ICON_ARROW_LEFT, null, new String("btnPrevious"));
			buttons.add(butt);
		}
		
		// revert button
		butt = new Button(board.getCols() - 6, 0 - fineOffset, 1, Button.BLACK_BG_BLUE_FRAME, Button.ICON_NONE, null, new String("btnRevert"));
		butt.setIsEnabled(false);
		buttons.add(butt);

	}

	
	private void initLabels(BoardConfig config) {
		labels = new ArrayList<JNPLabel>();
		String text = "Level " + config.getLevel();
		JNPLabel label = new JNPLabel(text, board.getCols() / 2 - text.length() / 6 , board.getRows() - 1, false);
		labels.add(label);
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

		updateRevertIcon();		
		// buttons
		for (Button button: buttons) {
			button.render(spriteBatch);	
		}
		
		// labels
		for (JNPLabel label: labels) {
			label.render(spriteBatch);
		}
		
		spriteBatch.end();
	}

	private void updateRevertIcon() {
		boolean isRevertable = board.isRevertable();
		if (isRevertable && !isPrevRevertable) {// just turned revertable
			Button button = getButtonById("btnRevert");
			button.setIconType(Button.ICON_ARROW_CIRC);
			button.setIsEnabled(true);
		}
		if (!isRevertable && isPrevRevertable) {// just turned un-revertable
			Button button = getButtonById("btnRevert");
			button.setIconType(Button.ICON_NONE);
			button.setIsEnabled(false);
		} 
		isPrevRevertable = isRevertable;
	}
	
	Button getButtonById(String id) {
		for (Button b: buttons) {
			if (id.equals(b.getId()))
				return b;
		}
		return null;
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		Gdx.app.debug(TAG, "Resizing screen to " + width + " x " + height);
		//this.boardWidth = width; we need only height to flip vertical orientation
		this.boardHeight = height;
		board.setResolution(width, height);
		background.setResolution(width, height);
		cellWidth = board.getSpriteWidth();
		cellHeight = board.getSpriteHeight();
		
		uiThreshold = cellWidth/UI_FACTOR;
		for (Button button: buttons) {
			button.setResolution(cellWidth, cellHeight);	
		}
		for (JNPLabel label: labels) {
			label.setResolution(cellWidth, cellHeight);
		}
		
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
		 
		float x = (float) xDown/ (float) cellWidth;
		float y = (float) yDown/ (float) cellHeight;
		
		Gdx.app.debug(TAG, "Action down spotted. boardHeight: " + boardHeight + ". x = " + x + " y = " + y);

		// ask buttons
		String type = null;
		for (Button butt: buttons) {
			if (butt.isPressed(x, y)) {
				type = butt.getId();
				break;
			}
		}
		
		if (type == null) { // no button pressed
			down = true;
			return true;
		}
		
		// button pressed - who?
		buttonSound.play(soundVolume);
		
		if (type.equals(new String("btnReset"))) // TODO: implement buttons with cb like humans
			board.start();	
		if (type.equals(new String("btnRevert")))
			board.revert();
		if (type.equals(new String("btnPrevious")))
			game.previousLevel();
		if (type.equals(new String("btnNext")))
			game.nextLevel();

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

	public void win() {
		String text = "Excellent !!!";
		// make it large by setting
		JNPLabel label = new JNPLabel(text, board.getCols() / 2 - text.length() / 6 , board.getRows() - 3, true);
		label.setResolution(cellWidth, cellHeight);
		
		labels.add(label);
		Button button = getButtonById("btnReset");
		buttons.remove(button);
		button = getButtonById("btnRevert");
		buttons.remove(button);

		// next button
		{
			float fineOffset = 0.2f;
			button = new Button(board.getCols() - 4, 0 - fineOffset, 3, Button.BLACK_BG_BLUE_FRAME, Button.ICON_NONE, "Next", new String("btnNext"));
			button.setResolution(cellWidth, cellHeight);
			buttons.add(button);
		}		

	}

}
