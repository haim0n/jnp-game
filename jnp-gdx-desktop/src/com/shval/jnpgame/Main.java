package com.shval.jnpgame;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Jelly No Puzlle";
		cfg.useGL20 = false;
		cfg.width = 800;//640;
		cfg.height = 480;
		
		new LwjglApplication(new JnpGame(), cfg);
	}
}
