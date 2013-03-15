package com.shval.jnpgame;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Background {
	

	class BackgroundLayer {
		Texture texture;

		int screenWidth;
		int screenHeight;

		// all of the following are on 256 x 256 logical scale
		// rendering will calculate graphical coords
		float x;
		float y;
		float vX;
		float vY;
		int wrapX;
		int wrapY;
		int width;
		int height;
		
		void update(float delta) {
			x = x + delta * vX;
			y = y + delta * vY;
			
			if (x > 256) // left motion
				x = -wrapX;
			else if (x < -wrapX) // right motion
				x = 256;

			if (y > 256) // up motion
				y = -wrapY;			
			else if (y < -wrapY) // down motion
				y = 256;
		}
		
		void setResolution(int screenWidth, int screenHeight) {
			this.screenWidth = screenWidth;
			this.screenHeight = screenHeight;;  
		}
		
		void render(SpriteBatch batch) {
			int gX = (int) (screenWidth * x / 256);
			int gY = (int) (screenHeight * y / 256);
 
			int gWidth = screenWidth * width / 256;
			int gHeight = screenHeight * height / 256;
			//Gdx.app.debug(TAG, "Background layer at " + gX + ", " + gY + " width = " + gWidth + " height = " + gHeight);
			batch.draw(texture, gX, gY, gWidth , gHeight);
		}
	}
		
	static private final String TAG = BackgroundLayer.class.getSimpleName();
	private ArrayList <BackgroundLayer> layers;	
	Color color;
	
	Background () {
		layers = new ArrayList <BackgroundLayer>();
		color = new Color();
	}
	
	// add static layer
	void addLayer(Texture texture, float x, float y, int width, int height) {
		addLayer(texture, x, y, 0, 0, 0, 0, width, height);
	}
	
	void addLayer(Texture texture, float x, float y, float vX, float vY,
			int wrapX, int wrapY, int width, int height) {
		
		BackgroundLayer layer = new BackgroundLayer();
		layer.texture = texture;
		layer.x = x;
		layer.y = y;
		layer.vX = vX;
		layer.vY = vY;
		layer.wrapX = wrapX;
		layer.wrapY = wrapY;
		layer.width = width;
		layer.height = height;
		Gdx.app.debug(TAG, "Adding layer with " + 
				layer.x + ", " + layer.y + ", " + 
				layer.vX + ", " + layer.vY + ", " + 
				layer.wrapX + ", " + layer.wrapY + ", ");
		
		layers.add(layer);
	}
	
	void render(float delta, SpriteBatch batch) {
		//Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		for(BackgroundLayer layer : layers) {
			layer.update(delta);
			layer.render(batch);
		}
	}

	public void setResolution(int width, int height) {
		for(BackgroundLayer layer : layers) {
			layer.setResolution(width, height);
		}
	}
	
}
