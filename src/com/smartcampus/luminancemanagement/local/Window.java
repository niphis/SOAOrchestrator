package com.smartcampus.luminancemanagement.local;

public class Window {
	private int _id;
	private float _angle;
	private static final double FAILURE_PROBABILITY = 0.005;
	
	public Window(int aId, float aAngle) {
		this._id = aId;
		this._angle = aAngle;
	}
	
	public void setId(int aId) {
		this._id = aId;
	}

	public int getId() {
		return this._id;
	}

	public void setAngle(float aAngle) {
		this._angle = aAngle;
	}

	public float getAngle() {
		if (failed())
			return -1;
		else 
			return this._angle;
	}
	
	private boolean failed() {
		if (Math.random() < FAILURE_PROBABILITY)
			return true;
		else
			return false;
	}
}