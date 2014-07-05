package com.smartcampus.test.soa;

import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

import com.smartcampus.acc.ArtificialClimateControl;
import com.smartcampus.acc.ArtificialClimateControlPortType;
import com.smartcampus.acc.xsd.IndoorStatus;
import com.smartcampus.luminancemanagement.LuminanceManagement;
import com.smartcampus.luminancemanagement.LuminanceManagementPortType;
import com.smartcampus.luminancemanagement.xsd.Room;
import com.smartcampus.luminancemanagement.xsd.Spotlight;
import com.smartcampus.luminancemanagement.xsd.Window;
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

public class Orchestrator_part2 {

	private enum WakeReason {
		DAILY_WAKEUP, CLIMATE_WAKEUP, LUMINANCE_WAKEUP
	};

	private static ArtificialClimateControlPortType acc;
	private static NaturalClimateSystemPortType nc;
	private static PathsPortType p;
	private static RoomUsageDatabasePortType rud;
	private static LuminanceManagementPortType lm;

	private static com.smartcampus.naturalclimatesystem.xsd.ObjectFactory ncsObjFactory = new com.smartcampus.naturalclimatesystem.xsd.ObjectFactory();
	private static com.smartcampus.acc.xsd.ObjectFactory accObjFactory = new com.smartcampus.acc.xsd.ObjectFactory();
	// private static com.smartcampus.luminancemanagement.xsd.ObjectFactory lmObjFactory = new com.smartcampus.luminancemanagement.xsd.ObjectFactory();

	private static class TimerEvent implements Comparable<TimerEvent> {
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

	private static PriorityQueue<TimerEvent> timers = new PriorityQueue<TimerEvent>();

	static {

		acc = new ArtificialClimateControl()
				.getArtificialClimateControlHttpSoap11Endpoint();
		nc = new NaturalClimateSystem()
				.getNaturalClimateSystemHttpSoap11Endpoint();
		p = new Paths().getPathsHttpSoap11Endpoint();
		rud = new RoomUsageDatabase().getRoomUsageDatabaseHttpSoap11Endpoint();
		lm = new LuminanceManagement()
				.getLuminanceManagementHttpSoap11Endpoint();

		// INITIAL EVENT SCHEDULING
		TimerEvent a = new TimerEvent(WakeReason.DAILY_WAKEUP);

		timers.add(a);
	}

	private static void wakeUp(TimerEvent a) {
		switch (a.reason) {

		case DAILY_WAKEUP: {
			System.out.print("[RU] Searching for events... ");
			List<EventData> events = rud.searchEvent(null).getEvents();
			System.out.println("done");
			
			for (int i = 0; i < events.size(); i++) {
				EventData event = events.get(i);
				String room = event.getRoomId().getValue(); // room in which the
															// event is
				// done
				int expectedPeople = event.getExpectedPeople();
				
				System.out.println("Event " + event.getEventType().getValue()
						+ " @ " + event.getRoomId().getValue()
						+ " (expected people = " + event.getExpectedPeople() + ")"
						+ "\n\tfrom " + new Date(event.getStartTime())
						+ "\n\tto " + new Date(event.getEndTime()));
				
				scheduleTimers(event, room);
				
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
					System.out.print("[P] Getting path attributes for path "
							+ pathId + "... ");
					PathData pd = p.getPathAttributes(pathId);
					if (pd == null) {
						System.out.println("FAILED!");
						break;
					}
					System.out.println("done");
					satisfiedCapacity += pd.getCapacity();
					List<PathComponent> componentArray = pd.getPath()
							.getValue().getComponents();
					for (int r = 0; r < componentArray.size(); r++) {
						String rid = componentArray.get(r).getId().getValue();
						scheduleTimers(event, rid);
					}
				}
			}
		}
			break;

		// Input State: roomsToConsider, event, room
		case CLIMATE_WAKEUP: {

			String roomId = a.room;

			Location l = ncsObjFactory.createLocation();
			l.setRoomId(ncsObjFactory.createLocationRoomId(roomId));

			System.out.print("[NC] Getting weather conditions for room "
					+ roomId + "... ");
			WeatherCondition wc = nc.getWeatherCondition(l);
			if (wc == null) {
				System.out.println("FAILED!");
				break;
			}
			System.out.println("done");
			
			System.out.println("Outdoor Conditions:"
					+ "\n\t Temperature = " + wc.getTemperature()
					+ "\n\t Humidity = " + wc.getHumidity()
					+ "\n\t CO2 Level = " + wc.getCo2Level()
					+ "\n\t Noise Level = " + wc.getNoiseLevel());
			
			
			System.out.print("[AC] Getting indoor status for room " + roomId
					+ "... ");
			
			IndoorStatus is = acc.getIndoorStatus(roomId);
			if (is == null) {
				System.out.println("FAILED!");
				break;
			}
			System.out.println("done");
		
			System.out.println("Indoor Status:"
					+ "\n\t Temperature = " + is.getTemperature()
					+ "\n\t Humidity = " + is.getHumidity()
					+ "\n\t CO2 Level = " + is.getCo2Level());
			
			float desiredTemperature = estabilishDesiredTemperature(wc);
			float desiredHumidity = estabilishDesiredHumidity(wc);
			float desiredCo2level = estabilishDesiredCo2level(wc);
			
			// choose if use natural or artificial climate control system
			if (naturalClimateUsable(desiredTemperature, desiredHumidity, desiredCo2level, is, wc)) {
				System.out.print("[NC] Opening windows in room " + roomId
						+ "... ");
				if (!nc.openWindow(l))
					System.out.println("FAILED!");
				else
					System.out.println("done");
			} else {

				is.setRoomID(accObjFactory.createIndoorStatusRoomID(roomId));
				is.setTemperature(desiredTemperature);
				is.setFanSpeed(0.0f);
				is.setHumidity(0.0f);
				is.setTimer(0);
				System.out.print("[AC] Setting indoor parameters for room "
						+ roomId + "... ");
				acc.setIndoorParameters(is);
				System.out.println("done");

				System.out.print("[NC] Closing windows in room " + roomId
						+ "... ");
				if (!nc.closeWindow(l))
					System.out.println("FAILED!");
				else
					System.out.println("done");

			}
		}
			break;
		case LUMINANCE_WAKEUP: {
			String roomId = a.room;

			// adapt luminance level
			float desiredLuminance = estabilishDesiredLuminance(a.event
					.getEventType().getValue());
			System.out.print("[LM] Getting indoor luminance for room " + roomId
					+ "... ");
			float indoorLuminance = lm.getIndoorLuminance(roomId);
			System.out.println("done");
			System.out.println("Indoor Luminance: " + indoorLuminance);
			
			System.out.print("[LM] Getting outdoor luminance for room "
					+ roomId + "... ");
			float outdoorLuminance = lm.getOutdoorLuminance(roomId);
			System.out.println("done");
			System.out.println("Outdoor Luminance: " + outdoorLuminance);
			
			System.out.print("[LM] Getting room settings for room "
					+ roomId + "... ");
			Room rs = lm.getCurrentRoomSettings(roomId);
			System.out.println("done");
			

			while (true) {

				if (desiredLuminance > indoorLuminance) {
					// need to increase luminance
					if (desiredLuminance < outdoorLuminance) {
						// use natural system
						if (rs.getWindows().get(0).getAngle() == 1) { // blind
							// is up
							// switch on spotlight
							for (Spotlight s : rs.getSpotlights()) {
								s.setLuminance(desiredLuminance);
								System.out
								.print("[LM] Calibrating spotlights for room "
										+ roomId + "... ");
								lm.calibrateSpotlight(rs);
								System.out.println("done");
							}
							
							break;
						} else {
							// blind up
							for (Window w : rs.getWindows()) {
								w.setAngle(estabilishAngle(indoorLuminance,
										outdoorLuminance));
								
								System.out
								.print("[LM] Regulating blinds for room "
										+ roomId + "... ");
								lm.regulateBlind(rs);
								System.out.println("done");
								
								System.out
										.print("[LM] Getting indoor luminance for room "
												+ roomId + "... ");
								indoorLuminance = lm.getIndoorLuminance(roomId);
								System.out.println("done");
								System.out.println("Indoor Luminance: " + indoorLuminance);
							}
						}
					} else {
						// switch on spotlight
						for (Spotlight s : rs.getSpotlights()) {
							s.setLuminance(desiredLuminance);
							System.out
							.print("[LM] Calibrating spotlights for room "
									+ roomId + "... ");
							lm.calibrateSpotlight(rs);
							System.out.println("done");
						}
						break;
					}
				} else {
					// need to decrease luminance
					if (rs.getSpotlights().get(0).getLuminance() > 0/*
																 * the light is
																 * switched on
																 */) {
						// switch off spotligth
						for (Spotlight s : rs.getSpotlights()) {
							s.setLuminance(0.0f);
							System.out
							.print("[LM] Calibrating spotlights for room "
									+ roomId + "... ");
							lm.calibrateSpotlight(rs);
							System.out.println("done");
						}
						System.out
								.print("[LM] Getting indoor luminance for room "
										+ roomId + "... ");
						indoorLuminance = lm.getIndoorLuminance(roomId);
						System.out.println("done");
						System.out.println("Indoor Luminance: " + indoorLuminance);

					} else {
						// blind down
						for (Window w : rs.getWindows()) {
							w.setAngle(0.0f);
							System.out
							.print("[LM] Regulating blinds for room "
									+ roomId + "... ");
							lm.regulateBlind(rs);
							System.out.println("done");
						}
						break;
					}
				}
			}
		}
			break;
		}
	}

	private static float estabilishDesiredCo2level(WeatherCondition wc) {
		
		return Math.min(1000, wc.getCo2Level());
	}
	private static float estabilishDesiredTemperature(
			WeatherCondition outdoorWc) {
		float out = outdoorWc.getTemperature();
		
		float tmin = Math.max(18, out/5 + 17);
		float tmax = Math.min(30, out/5 + 22);
		
		return Math.min(Math.max(tmin, out), tmax);
	}
	
	private static float estabilishDesiredHumidity(
			WeatherCondition outdoorWc) {
		float out = outdoorWc.getHumidity();
		
		return Math.min(Math.max(30, out), 50);
	}

	private static boolean naturalClimateUsable(float desiredT, float desiredH, float desiredC, IndoorStatus is,
			WeatherCondition wc) {

		// pollution check
		float pollutionLevel = wc.getCo2Level();
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
		
		if(!tempOK)
			return false;
		
		// humidity check
		float indoorH = is.getHumidity();
		float outdoorH = wc.getHumidity();
		
		boolean humOK = false;
		
		if ((outdoorH > desiredH) && (desiredH > indoorH))
			humOK = true;
		if ((outdoorH < desiredH) && (desiredH < indoorH))
			humOK = true;
		
		if(!humOK)
			return false;
		
		return true;
	}

	private static float estabilishDesiredLuminance(String eventType) {
		return 350f;
	}

	private static float estabilishAngle(float indoorLuminance,
			float outdoorLuminance) {
		return 1;
	}

	private static void scheduleTimers(EventData ev, String rid) {

		// CLIMATE_WAKEUP scheduling
		TimerEvent a = new TimerEvent(WakeReason.CLIMATE_WAKEUP,
				ev.getStartTime(), ev, rid);
		timers.add(a);

		// LUMINANCE_WAKEUP scheduling
		a = new TimerEvent(WakeReason.LUMINANCE_WAKEUP, ev.getStartTime(), ev,
				rid);
		timers.add(a);

	}

	public static void main(String[] args) {
		TimerEvent timerEvent;
		System.out.println("[ORCH] Start handling of events... ");
		while ((timerEvent = timers.poll()) != null)
			wakeUp(timerEvent);
		System.out.println("[ORCH] Handling of events completed.");
		
	}
}
