package com.shval.jnpgame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class JnpGame extends Game {
	private final int logLevel = Application.LOG_DEBUG;
	public BoardConfig config;
			
	@Override
	public void create() {
		// load assets
		Assets.load();
		
		//
		Gdx.app.setLogLevel(logLevel);
		setScreen(new PlayScreen(this, 1));
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
