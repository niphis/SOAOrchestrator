package com.smartcampus.test.soa;
import java.util.List;
import java.util.PriorityQueue;

import com.smartcampus.acc.ArtificialClimateControl;
import com.smartcampus.acc.ArtificialClimateControlPortType;
import com.smartcampus.acc.xsd.IndoorStatus;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystem;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystemPortType;
import com.smartcampus.naturalclimatesystem.xsd.Location;
import com.smartcampus.naturalclimatesystem.xsd.WeatherCondition;
import com.smartcampus.paths.Paths;
import com.smartcampus.paths.PathsPortType;
import com.smartcampus.paths.xsd.PathComponent;
import com.smartcampus.paths.xsd.PathData;
import com.smartcampus.roomusagedatabase.RoomUsageDatabase;
import com.smartcampus.roomusagedatabase.RoomUsageDatabasePortType;
import com.smartcampus.roomusagedatabase.xsd.EventData;

public class Orchestrator_part1 {

	private static float estabilishDesiredTemperature(Long unixTime,
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

	private static ArtificialClimateControlPortType acc;
	private static NaturalClimateSystemPortType nc;
	private static PathsPortType p;
	private static RoomUsageDatabasePortType rud;
	
	private static com.smartcampus.naturalclimatesystem.xsd.ObjectFactory ncsObjFactory = new com.smartcampus.naturalclimatesystem.xsd.ObjectFactory();
	private static com.smartcampus.acc.xsd.ObjectFactory accObjFactory = new com.smartcampus.acc.xsd.ObjectFactory();


	private static class TimerEvent implements Comparable<TimerEvent> {
		public WakeReason reason;
		public Long time;
		public EventData event;
		public String room;

		public TimerEvent(WakeReason reason) {
			this.reason = reason;
		}

		public TimerEvent(WakeReason reason, Long unixTimestamp, EventData event, String room) {
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

	private static PriorityQueue<TimerEvent> timers = new PriorityQueue<TimerEvent>();
	
	static {
		
		acc = new ArtificialClimateControl().getArtificialClimateControlHttpSoap11Endpoint();
		nc = new NaturalClimateSystem().getNaturalClimateSystemHttpSoap11Endpoint();
		p = new Paths().getPathsHttpSoap11Endpoint();
		rud = new RoomUsageDatabase().getRoomUsageDatabaseHttpSoap11Endpoint();
		
		// INITIAL EVENT SCHEDULING
		TimerEvent a = new TimerEvent(WakeReason.DAILY_WAKEUP);
		
		timers.add(a);
	}

	private static void wakeUp(TimerEvent a) {
		switch (a.reason) {

		case DAILY_WAKEUP:
			System.out.print("[RU] Searching for events... ");
			List<EventData> events = rud.searchEvent(null).getEvents();
			System.out.println("done");
			for (int i = 0; i < events.size(); i++) {
				EventData event = events.get(i);
				String room = event.getRoomId().getValue(); // room in which the event is
													// done
				int expectedPeople = event.getExpectedPeople();
				
				System.out.print("[P] Getting paths to room " + room + "... ");
				List<Integer> pathsToRoom = p.getPaths(room);
				if (pathsToRoom == null) {
					System.out.println("FAILED!");
					break;
				}
				System.out.println("done");
				int satisfiedCapacity = 0;

				// for all the needed paths I want to obtain the id of the rooms
				// in
				// each one
				for (int s = 0; (s < pathsToRoom.size())
						&& (satisfiedCapacity < expectedPeople); s++) {
					Integer pathId = pathsToRoom.get(s);
					System.out.print("[P] Getting path attributes for path " + pathId + "... ");
					PathData pd = p.getPathAttributes(pathId);
					if (pd == null) {
						System.out.println("FAILED!");
						break;
					}
					System.out.println("done");
					satisfiedCapacity += pd.getCapacity();
					List<PathComponent> componentArray = pd.getPath().getValue()
							.getComponents();
					for (int r = 0; r < componentArray.size(); r++) {
						String rid = componentArray.get(r).getId().getValue();
						scheduleTimers(event, rid);
					}
				}
			}
			break;

		// Input State: roomsToConsider, event, room
		case CLIMATE_WAKEUP:

			String roomId = a.room;

			Location l = ncsObjFactory.createLocation();
			l.setRoomId(ncsObjFactory.createLocationRoomId(roomId));
			
			System.out.print("[NC] Getting weather conditions for room " + roomId + "... ");
			WeatherCondition wc = nc.getWeatherCondition(l);
			if (wc == null) {
				System.out.println("FAILED!");
				break;
			}
			System.out.println("done");
			float outdoorTemperature = wc.getTemperature();
			float desiredTemperature = estabilishDesiredTemperature(
					a.event.getDate(), outdoorTemperature, roomId);
			System.out.print("[AC] Getting indoor status for room " + roomId + "... ");
			float indoorTemperature = acc.getIndoorStatus(roomId)
					.getTemperaure();
			if (indoorTemperature < -50) {
				System.out.println("FAILED!");
				break;
			}
			System.out.println("done");
			// choose if use natural or artificial climate control system
			if (naturalClimateUsable(desiredTemperature, indoorTemperature,
					outdoorTemperature, wc.getPollution())) {
				System.out.print("[NC] Opening windows in room " + roomId + "... ");
				if (!nc.openWindow(l))
					System.out.print("FAILED!");
				else
					System.out.println("done");
			} else {
				
				IndoorStatus is = accObjFactory.createIndoorStatus();
				is.setRoomID(accObjFactory.createIndoorStatusRoomID(roomId));
				is.setTemperaure(desiredTemperature);
				is.setFanSpeed(0.0f);
				is.setHumidity(0.0f);
				is.setTimer(0);
				System.out.print("[AC] Setting indoor parameters for room " + roomId + "... ");
				acc.setIndoorParameters(is);
				System.out.println("done");
				
				System.out.print("[NC] Closing windows in room " + roomId + "... ");							
				if (!nc.closeWindow(l))
					System.out.print("FAILED!");
				else
					System.out.println("done");
				
			}

			break;
		}
	}

	private static void scheduleTimers(EventData ev, String rid) {
		
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
