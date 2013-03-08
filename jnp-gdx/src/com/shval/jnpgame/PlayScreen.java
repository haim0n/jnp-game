package com.shval.jnpgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.physics.box2d.World;

public class PlayScreen implements Screen, InputProcessor {

	private Board board; // this is our world now
	private BoardView boardView;
	private JnpGame game;
	
	public PlayScreen(JnpGame game, int level) {
		this.game = game;
		
		// create board & view
		board = new Board(level);
		boardView = new BoardView(board);
		board.start();
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
		board.setResolution(width, height);

	}

	@Override
	// this is called when the main game makes this screen active
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	// this is called when the main game makes another screen active
	public void hide() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		// TODO: in the tutorial we saw Gdx.input.setInputProcessor(null);
	}

	// * InputProcessor methods ***************************//
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
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
