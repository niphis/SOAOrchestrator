package com.smartcampus.test.local;
import java.sql.Time;
import java.util.Date;
import java.util.PriorityQueue;

import com.smartcampus.acc.local.ArtificialClimateControlService;
import com.smartcampus.acc.local.indoorStatus;
import com.smartcampus.naturalclimatesystem.local.Location;
import com.smartcampus.naturalclimatesystem.local.NaturalClimateSystemService;
import com.smartcampus.naturalclimatesystem.local.WeatherCondition;
import com.smartcampus.paths.local.PathComponent;
import com.smartcampus.paths.local.PathData;
import com.smartcampus.paths.local.PathsService;
import com.smartcampus.roomusagedatabase.local.Event;
import com.smartcampus.roomusagedatabase.local.EventData;
import com.smartcampus.roomusagedatabase.local.RoomUsageDatabaseService;

public class Orchestrator_part1 {

	private static float estabilishDesiredTemperature(Date date,
			float outdoorTemperature, String roomId) {
		return 0;
	}

	private static boolean naturalClimateUsable(float desired, float indoor,
			float outdoor, int pollutionLevel) {
		int pollutionThreshold = 100;
		if (pollutionLevel > pollutionThreshold)
			return false;
		if ((outdoor > desired) && (desired > indoor))
			return true;
		if ((outdoor < desired) && (desired < indoor))
			return true;
		return false;
	}

	private enum WakeReason {
		DAILY_WAKEUP, CLIMATE_WAKEUP
	};

	private static ArtificialClimateControlService acc;
	private static NaturalClimateSystemService nc;
	private static PathsService p;
	private static RoomUsageDatabaseService rud;

	private static class TimerEvent implements Comparable<TimerEvent> {
		public WakeReason reason;
		public Time time;
		public Event event;
		public String room;

		public TimerEvent(WakeReason reason) {
			this.reason = reason;
		}

		public TimerEvent(WakeReason reason, Time time, Event event, String room) {
			this.reason = reason;
			this.time = time;
			this.event = event;
			this.room = room;
		}

		@Override
		public int compareTo(TimerEvent o) {
			return time.compareTo(o.time);
		}
	}

	private static PriorityQueue<TimerEvent> timers = new PriorityQueue<TimerEvent>();
	
	static {
		acc = new ArtificialClimateControlService();
		nc = new NaturalClimateSystemService();
		p = new PathsService();
		rud = new RoomUsageDatabaseService();
		
		// INITIAL EVENT SCHEDULING
		TimerEvent a = new TimerEvent(WakeReason.DAILY_WAKEUP);
		
		timers.add(a);
	}

	private static void wakeUp(TimerEvent a) {
		switch (a.reason) {

		case DAILY_WAKEUP:
			EventData[] events = rud.searchEvent(null, null, null, null);

			for (int i = 0; i < events.length; i++) {
				Event event = events[i].getEvents();
				String room = event.getRoomId(); // room in which the event is
													// done
				int expectedPeople = event.getExpectedPeople();

				int[] pathsToRoom = p.getPaths(room);
				int satisfiedCapacity = 0;

				// for all the needed paths I want to obtain the id of the rooms
				// in
				// each one
				for (int s = 0; (s < pathsToRoom.length)
						&& (satisfiedCapacity < expectedPeople); s++) {
					PathData pd = p.getPathAttributes(pathsToRoom[s]);
					satisfiedCapacity += pd.getCapacity();
					PathComponent[] componentArray = pd.getPath()
							.getComponents();
					for (int r = 0; r < componentArray.length; r++) {
						String rid = componentArray[r].getId();
						scheduleTimers(event, rid);
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
			float outdoorTemperature = wc.get_temperature();
			float desiredTemperature = estabilishDesiredTemperature(
					a.event.getDate(), outdoorTemperature, roomId);
			float indoorTemperature = acc.getIndoorStatus(roomId)
					.getTemperaure();
			// choose if use natural or artificial climate control system
			if (naturalClimateUsable(desiredTemperature, indoorTemperature,
					outdoorTemperature, wc.get_pollution())) {
				if (nc.openWindow(l)) {
					// TODO: show alert
				}
			} else {
				acc.setIndoorParameters(new indoorStatus(roomId,
						desiredTemperature, 0, 0, 0));// TODO: passare
														// valori NULL
				if (nc.closeWindow(l)) {
					// TODO: show alert
				}
			}

			break;
		}
	}

	private static void scheduleTimers(Event ev, String rid) {
		
		// CLIMATE_WAKEUP scheduling
		TimerEvent a = new TimerEvent(
				WakeReason.CLIMATE_WAKEUP,
				ev.getStartTime(),
				ev,
				rid);
		
		timers.add(a);
	}

	public static void main(String[] args) {
		TimerEvent timerEvent;
		while((timerEvent = timers.poll()) != null)
			wakeUp(timerEvent);
	}
}
