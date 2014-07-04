package com.smartcampus.roomusagedatabase.local;

import java.util.Date;
import java.sql.Time;


public class Event {
	private String _roomId;
	private Date _date;
	private Time _startTime;
	private Time _endTime;
	private int _expectedPeople;
	private String _eventType;
	public EventData _unnamed_EventData_;

	public String getRoomId() {
		return this._roomId;
	}

	public void setRoomId(String aRoomId) {
		this._roomId = aRoomId;
	}

	public Date getDate() {
		return this._date;
	}

	public void setDate(Date aDate) {
		this._date = aDate;
	}

	public Time getStartTime() {
		return this._startTime;
	}

	public void setStartTime(Time aStartTime) {
		this._startTime = aStartTime;
	}

	public Time getEndTime() {
		return this._endTime;
	}

	public void setEndTime(Time aEndTime) {
		this._endTime = aEndTime;
	}

	public int getExpectedPeople() {
		return this._expectedPeople;
	}

	public void setExpectedPeople(int aExpectedPeople) {
		this._expectedPeople = aExpectedPeople;
	}

	public String getEventType() {
		return this._eventType;
	}

	public void setEventType(String aEventType) {
		this._eventType = aEventType;
	}
}