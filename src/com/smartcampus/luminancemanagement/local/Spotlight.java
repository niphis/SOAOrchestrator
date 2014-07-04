package com.smartcampus.luminancemanagement.local;

public class Spotlight {
	private int _id;
	private float _luminance;
	private static final double FAILURE_PROBABILITY = 0.01;

	public Spotlight(int aId, float aLuminance) {
		this._id = aId;
		this._luminance = aLuminance;
	}
	
	public void setId(int aId) {
		this._id = aId;
	}

	public int getId() {
		return this._id;
	}

	public void setLuminance(float aLuminance) {
		this._luminance = aLuminance;
	}

	public float getLuminance() {
		if (failed())
			return -1;
		return this._luminance;
	}
	
	private boolean failed() {
		if (Math.random() < FAILURE_PROBABILITY)
			return true;
		else
			return false;
	}
}
