package com.smartcampus.luminancemanagement.local;

public class Spotlight {
	private int _id;
	private float _luminance;
	public RoomSettings _spotlights;

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
		return this._luminance;
	}
}