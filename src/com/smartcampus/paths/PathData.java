package com.smartcampus.paths;

public class PathData {
	public int _id;
	public Path _path;
	public int _capacity;
	public int _priority;

	public int getId() {
		return this._id;
	}

	public void setId(int aId) {
		this._id= aId;
	}

	public Path getPath() {
		return _path;
	}

	public void setPath(Path _path) {
		this._path = _path;
	}

	public int getCapacity() {
		return this._capacity;
	}

	public void setCapacity(int aCapacity) {
		this._capacity = aCapacity;
	}

	public int getPriority() {
		return this._priority;
	}

	public void setPriority(int aPriority) {
		this._priority = aPriority;
	}
}