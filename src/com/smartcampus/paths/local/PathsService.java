package com.smartcampus.paths.local;

import java.util.ArrayList;

public class PathsService implements PathsSystem {
	final static private int pathMaxId = 10;
	final static private int[] pathsCapacities = { 1000, 500, 100, 350, 900,
			200, 100, 800, 200, 900 };
	final static private int[] pathsPriorities = { 1, 3, 1, 2, 3, 4, 2, 1, 4, 2 };

	public int[] getPaths(String aRoomId) {
		ArrayList<Integer> paths = new ArrayList<Integer>();

		int pathCount = hash(aRoomId);

		for (int i = 0; i < pathCount; i++) {
			int pathId = hash(aRoomId + i);

			while (paths.contains(pathId))
				pathId = (pathId + 1) % pathMaxId;

			paths.add(pathId);
		}

		int[] ret = new int[paths.size()];
		for (int i = 0; i < paths.size(); i++)
			ret[i] = paths.get(i);

		return ret;
	}

	public PathData getPathAttributes(int aPathId) {
		PathData data = new PathData();

		Path _path = new Path();

		int pathLength = (aPathId % pathMaxId) + 1;
		PathComponent[] components = new PathComponent[pathLength];

		for (int i = 0, l = components.length; i < l; i++) {
			components[i] = new PathComponent();
			components[i].setId(String.valueOf(i));
		}

		_path.setComponents(components);

		data.setPath(_path);
		data.setCapacity(pathsCapacities[aPathId]);
		data.setPriority(pathsPriorities[aPathId]);

		return data;
	}

	public void insertPath(PathData aPathData) {
		return;
	}

	public void deletePath(int aPathId) {
		return;
	}

	private int hash(String value) {
		int sum = 0;

		for (int i = 0; i < value.length(); i++)
			sum += value.charAt(i);

		return sum % pathMaxId;
	}
}