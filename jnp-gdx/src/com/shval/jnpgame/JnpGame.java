package com.shval.jnpgame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class JnpGame extends Game {
	private final int logLevel = Application.LOG_DEBUG;
	//private final int logLevel = Application.LOG_ERROR;
	private int MAX_LEVELS;
	public BoardConfig config;
	int currentLevel;
			
	@Override
	public void create() {
		currentLevel = 0;
		
		// load assets
		Assets.load();
		
		MAX_LEVELS = 5; // TODO:read from config
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
