package com.smartcampus.paths.local;

public interface PathsSystem {

	public int[] getPaths(String aRoomId);

	public PathData getPathAttributes(int aPathId);

	public void insertPath(PathData aPathData);

	public void deletePath(int aPathId);
}