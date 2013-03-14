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
		
		MAX_LEVELS = 100; // TODO:read from config
		//
		Gdx.app.setLogLevel(logLevel);
		playLevel(currentLevel);
	}

	private void playLevel(int level) {
		setScreen(new PlayScreen(this, level));		
	}
	
	public void reset() {
		playLevel(currentLevel);
	}
	
	public void win() {
		currentLevel++;
		if (currentLevel <= MAX_LEVELS)
			playLevel(currentLevel);
		else {
			Gdx.app.debug(TAG, "You win game. Congradulations!!");
			// you win
		}
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
