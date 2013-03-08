package com.shval.jnpgame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BoardView {

	Board board;
	SpriteBatch spriteBatch;
	private OrthographicCamera camera;
	
	public BoardView(Board board) {
		this.board = board;
		spriteBatch = new SpriteBatch();
		camera = new OrthographicCamera(10, 7);
        camera.position.set(5, 3.5f, 0);
        camera.update();
	}
	
	public void render() {
		spriteBatch.begin();
		board.render(spriteBatch);
		spriteBatch.end();
	}
}
