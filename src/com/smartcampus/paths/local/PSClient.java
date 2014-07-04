package com.smartcampus.paths.local;

public interface PSClient {

	public void pathsToRoom(Object aPathsIdList);

	public void pathAttributes(Object aPathData);

	public void insertPathResult(Object aResult);

	public void deletePathResult(Object aResult);
}