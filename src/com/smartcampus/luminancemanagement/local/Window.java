package com.smartcampus.luminancemanagement.local;

public class Window {
	private int _id;
	private float _angle;
	public RoomSettings _windows;

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
		return this._angle;
	}
}