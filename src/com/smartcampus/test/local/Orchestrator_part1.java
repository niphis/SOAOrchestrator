package com.smartcampus.test.local;

import java.util.PriorityQueue;

import com.smartcampus.acc.local.ArtificialClimateControlService;
import com.smartcampus.acc.local.IndoorStatus;
import com.smartcampus.naturalclimatesystem.local.Location;
import com.smartcampus.naturalclimatesystem.local.NaturalClimateSystemService;
import com.smartcampus.naturalclimatesystem.local.WeatherCondition;
import com.smartcampus.paths.local.PathComponent;
import com.smartcampus.paths.local.PathData;
import com.smartcampus.paths.local.PathsService;
import com.smartcampus.roomusagedatabase.local.EventData;
import com.smartcampus.roomusagedatabase.local.RoomUsageDatabaseService;

public class Orchestrator_part1 {

	private static float estabilishDesiredCo2level(WeatherCondition wc) {

		return Math.min(1000, wc.getCo2level());
	}

	private static float estabilishDesiredTemperature(WeatherCondition outdoorWc) {
		float out = outdoorWc.getTemperature();

		float tmin = Math.max(18, out / 5 + 17);
		float tmax = Math.min(30, out / 5 + 22);

		return Math.min(Math.max(tmin, out), tmax);
	}

	private static float estabilishDesiredHumidity(WeatherCondition outdoorWc) {
		float out = outdoorWc.getHumidity();

		return Math.min(Math.max(30, out), 50);
	}

	private static boolean naturalClimateUsable(float desiredT, float desiredH,
			float desiredC, IndoorStatus is, WeatherCondition wc) {

		// pollution check
		float pollutionLevel = wc.getCo2level();
		float pollutionThreshold = 1000;
		if (pollutionLevel > pollutionThreshold)
			return false;

		if (pollutionLevel > desiredC)
			return false;

		// noise check
		float noiseThreshold = 80;
		float noise = wc.getNoiseLevel();
		if (noise > noiseThreshold)
			return false;

		// temperature check
		float indoorT = is.getTemperature();
		float outdoorT = wc.getTemperature();

		boolean tempOK = false;

		if ((outdoorT > desiredT) && (desiredT > indoorT))
			tempOK = true;
		if ((outdoorT < desiredT) && (desiredT < indoorT))
			tempOK = true;

		if (!tempOK)
			return false;

		// humidity check
		float indoorH = is.getHumidity();
		float outdoorH = wc.getHumidity();

		boolean humOK = false;

		if ((outdoorH > desiredH) && (desiredH > indoorH))
			humOK = true;
		if ((outdoorH < desiredH) && (desiredH < indoorH))
			humOK = true;

		if (!humOK)
			return false;

		return true;
	}

	public enum WakeReason {
		DAILY_WAKEUP, CLIMATE_WAKEUP
	};

	public static void setArtificialClimateControlService
		(ArtificialClimateControlService a) {
		acc = a;
	}
	
	public static void setNaturalClimateSystemService (NaturalClimateSystemService n) {
		nc = n;
	}
	
	public static void setPathsService(PathsService ps) {
		p = ps;
	}
	
	public static void setRoomUsageDatabaseService(RoomUsageDatabaseService r) {
		rud = r;
	}
	
	private static ArtificialClimateControlService acc;
	private static NaturalClimateSystemService nc;
	private static PathsService p;
	private static RoomUsageDatabaseService rud;

	public static class TimerEvent implements Comparable<TimerEvent> {
		public WakeReason reason;
		public Long time;
		public EventData event;
		public String room;

		public TimerEvent(WakeReason reason) {
			this.reason = reason;
			this.time = System.currentTimeMillis();
		}

		public TimerEvent(WakeReason reason, Long unixTimestamp,
				EventData event, String room) {
			this.reason = reason;
			this.time = unixTimestamp;
			this.event = event;
			this.room = room;
		}

		@Override
		public int compareTo(TimerEvent o) {
			return (int) (time - o.time);
		}
	}

	public static int wakeUp(PriorityQueue<TimerEvent> timers) {
		TimerEvent a = timers.poll();
		if (a == null) return -1;
		
		switch (a.reason) {

		case DAILY_WAKEUP:
			EventData[] events = rud.searchEvent(null).getEvents();

			for (int i = 0; i < events.length; i++) {
				EventData event = events[i];
				String room = event.getRoomId(); // room in which the event is
													// done
				int expectedPeople = event.getExpectedPeople();

				int[] pathsToRoom = p.getPaths(room);
				int satisfiedCapacity = 0;

				// for all the needed paths I want to obtain the id of the rooms
				// in
				// each one
				for (int j = 0; j < pathsToRoom.length
						&& satisfiedCapacity < expectedPeople; j++) {
					Integer pathId = pathsToRoom[j];
					PathData pd = p.getPathAttributes(pathId);
					satisfiedCapacity += pd.getCapacity();
					PathComponent[] componentArray = pd.getPath()
							.getComponents();
					for (int r = 0; r < componentArray.length; r++) {
						String rid = componentArray[r].getId();
						scheduleTimers(timers, event, rid);
					}
				}
			}
			break;

		// Input State: roomsToConsider, event, room
		case CLIMATE_WAKEUP:

			String roomId = a.room;

			Location l = new Location();
			l.setRoomId(roomId);
			WeatherCondition wc = nc.getWeatherCondition(l);
			IndoorStatus is = acc.getIndoorStatus(roomId);

			float desiredTemperature = estabilishDesiredTemperature(wc);
			float desiredHumidity = estabilishDesiredHumidity(wc);
			float desiredCo2level = estabilishDesiredCo2level(wc);

			// choose if use natural or artificial climate control system
			if (naturalClimateUsable(desiredTemperature, desiredHumidity,
					desiredCo2level, is, wc)) {
				nc.openWindow(l);
			} else {
				is.setRoomID(roomId);
				is.setTemperature(desiredTemperature);
				is.setFanSpeed(0.0f);
				is.setHumidity(desiredHumidity);
				is.setTimer(0);
				acc.setIndoorParameters(is);

				nc.closeWindow(l);
			}

			break;
		}
		
		return 0;
	}

	private static void scheduleTimers(PriorityQueue<TimerEvent> timers, EventData ev, String rid) {

		// CLIMATE_WAKEUP scheduling
		TimerEvent a = new TimerEvent(WakeReason.CLIMATE_WAKEUP,
				ev.getStartTime(), ev, rid);

		timers.add(a);
	}

}
