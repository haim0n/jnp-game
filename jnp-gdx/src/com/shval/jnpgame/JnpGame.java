package com.shval.jnpgame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class JnpGame extends Game {
	private final int logLevel = Application.LOG_DEBUG;
	private static String TAG = BoardConfig.class.getSimpleName();
	//private final int logLevel = Application.LOG_ERROR;
	private int MAX_LEVELS;
	public BoardConfig config;
	int currentLevel;
			
	@Override
	public void create() {
		currentLevel = 1;
		
		// load assets
		Assets.load();
		
		//load board config
		config = new BoardConfig();
		
		MAX_LEVELS = config.getLevels();
		//
		Gdx.app.setLogLevel(logLevel);
		playLevel(currentLevel);
	}

	private void playLevel(int level) {
		Gdx.app.debug(TAG, "Playing level " + level);
		config.setLevel(level);
		setScreen(new PlayScreen(config, this));		
	}
		
	public void nextLevel() {
		currentLevel++;
		if (currentLevel > MAX_LEVELS) {
			Gdx.app.debug(TAG, "You win game. Congradulations!!");
			currentLevel = 0;
		}
		
		playLevel(currentLevel);
	}
	
	public void previousLevel() {

		currentLevel--;
		if (currentLevel < 1) {
			// should never be here
			Gdx.app.error(TAG, "Invalid level " + currentLevel);
			currentLevel = 1;
		}
		
		playLevel(currentLevel);

	}
	
	
	@Override
	public void dispose() {
	}
	
	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
