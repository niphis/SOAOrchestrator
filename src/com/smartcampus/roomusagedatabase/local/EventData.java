package com.smartcampus.roomusagedatabase.local;

public class EventData {
	public String _roomId;
	public long _date;
	public long _startTime;
	public long _endTime;
	public int _expectedPeople;
	public String _eventType;
	public EventList _unnamed_EventList_;

	public String getRoomId() {
		return this._roomId;
	}

	public void setRoomId(String aRoomId) {
		this._roomId = aRoomId;
	}

	public int getExpectedPeople() {
		return this._expectedPeople;
	}

	public void setExpectedPeople(int aExpecetdPeople) {
		this._expectedPeople = aExpecetdPeople;
	}

	public String getEventType() {
		return this._eventType;
	}

	public void setEventType(String aEventType) {
		this._eventType = aEventType;
	}

	public long getStartTime() {
		return this._startTime;
	}

	public void setStartTime(long aStartTime) {
		this._startTime = aStartTime;
	}

	public long getEndTime() {
		return this._endTime;
	}

	public void setEndTime(long aEndTime) {
		this._endTime = aEndTime;
	}

	public long getDate() {
		return this._date;
	}

	public void setDate(long aDate) {
		this._date = aDate;
	}
}