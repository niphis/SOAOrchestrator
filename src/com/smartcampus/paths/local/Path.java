package com.smartcampus.paths.local;

import java.util.Vector;

public class Path {
	private PathComponent[] _components;

	public PathComponent[] getComponents() {
		return this._components;
	}

	public void setComponents(PathComponent[] aComponents) {
		this._components = aComponents;
	}
}