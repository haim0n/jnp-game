/**
 * 
 */
package com.shval.jnpgame;

/**
 * The Speed class keeps track of the bearing of an object
 * in the 2D plane. It holds the speed values on both axis 
 * and the directions on those. An object with the ability
 * to move will contain this class and the move method will
 * update its position according to the speed. 
 *   
 *
 */
public class Speed {
	
	/*
	 * Positive x velocity - right direction 
	 * Positive y velocity - downwards 
	 */
	
	private float vx;	// velocity value on the X axis
	private float vy;	// velocity value on the Y axis
	
	public Speed() {
		this.vx = 0;
		this.vy = 0;
	}

	public Speed(float xv, float yv) {
		this.vx = xv;
		this.vy = yv;
	}

	public float getXv() {
		return vx;
	}
	public void setXv(float xv) {
		this.vx = xv;
	}
	public void set(float vx, float vy) {
		this.vx = vx;
		this.vy = vy;		
	}
	public float getYv() {
		return vy;
	}
	public void setYv(float yv) {
		this.vy = yv;
	}

	// changes the direction on the X axis
	public void toggleXDirection() {
		vx = -1 * vx;
	}

	// changes the direction on the Y axis
	public void toggleYDirection() {
		vy = -1 * vy;
	}

}
