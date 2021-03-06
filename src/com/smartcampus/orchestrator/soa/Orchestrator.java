package com.smartcampus.orchestrator.soa;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import com.smartcampus.acc.ArtificialClimateControlPortType;
import com.smartcampus.acc.xsd.IndoorStatus;
import com.smartcampus.luminancemanagement.LuminanceManagementPortType;
import com.smartcampus.luminancemanagement.xsd.Room;
import com.smartcampus.luminancemanagement.xsd.Spotlight;
import com.smartcampus.luminancemanagement.xsd.Window;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystemPortType;
import com.smartcampus.naturalclimatesystem.xsd.Location;
import com.smartcampus.naturalclimatesystem.xsd.WeatherCondition;
import com.smartcampus.orchestrator.soa.Orchestrator_part2.Error;
import com.smartcampus.paths.PathsPortType;
import com.smartcampus.paths.xsd.PathComponent;
import com.smartcampus.paths.xsd.PathData;
import com.smartcampus.roomusagedatabase.RoomUsageDatabasePortType;
import com.smartcampus.roomusagedatabase.xsd.EventData;
import com.smartcampus.roomusagedatabase.xsd.EventList;
import com.smartcampus.servicesfacilities.ServicesAndFacilitiesPortType;
import com.smartcampus.servicesfacilities.xsd.Food;
import com.smartcampus.servicesfacilities.xsd.FoodList;
import com.smartcampus.servicesfacilities.xsd.FoodOrder;

public class Orchestrator {

	public static enum WakeReason {
		DAILY_WAKEUP, CLIMATE_WAKEUP, LUMINANCE_WAKEUP, FOOD_WAKEUP, CLEANING_WAKEUP
	};

	public enum Error {
		NO_EVENT, SUCCESS, DAILY_WAKEUP_ERROR, CLIMATE_WAKEUP_ERROR, LUMINANCE_WAKEUP_ERROR, FOOD_WAKEUP_ERROR, CLEANING_WAKEUP_ERROR
	};

	private static ArtificialClimateControlPortType acc;
	private static NaturalClimateSystemPortType nc;
	private static PathsPortType p;
	private static RoomUsageDatabasePortType rud;
	private static LuminanceManagementPortType lm;
	private static ServicesAndFacilitiesPortType sf;

	public static void setArtificialClimateControlPortType(
			ArtificialClimateControlPortType a) {
		acc = a;
	}

	public static void setNaturalClimateSystemPortType(
			NaturalClimateSystemPortType n) {
		nc = n;
	}

	public static void setPathsPortType(PathsPortType ps) {
		p = ps;
	}

	public static void setRoomUsageDatabasePortType(RoomUsageDatabasePortType r) {
		rud = r;
	}

	public static void setLuminanceManagementPortType(
			LuminanceManagementPortType l) {
		lm = l;
	}

	public static void setServicesAndFacilitiesPortType(
			ServicesAndFacilitiesPortType s) {
		sf = s;
	}

	private static com.smartcampus.naturalclimatesystem.xsd.ObjectFactory ncsObjFactory = new com.smartcampus.naturalclimatesystem.xsd.ObjectFactory();
	private static com.smartcampus.acc.xsd.ObjectFactory accObjFactory = new com.smartcampus.acc.xsd.ObjectFactory();
	// private static com.smartcampus.luminancemanagement.xsd.ObjectFactory
	// lmObjFactory = new
	// com.smartcampus.luminancemanagement.xsd.ObjectFactory();
	private static com.smartcampus.servicesfacilities.xsd.ObjectFactory sfObjFactory = new com.smartcampus.servicesfacilities.xsd.ObjectFactory();

	public static class TimerEvent implements Comparable<TimerEvent> {
		public WakeReason reason;
		public Long time;
		public EventData event;
		public String room;
		public List<EventData> weekEvents;

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

		public TimerEvent(WakeReason reason, Long unixTimestamp,
				EventData event, String room, List<EventData> weekEvents) {
			this.reason = reason;
			this.time = unixTimestamp;
			this.event = event;
			this.room = room;
			this.weekEvents = weekEvents;
		}

		@Override
		public int compareTo(TimerEvent o) {
			return (int) (time - o.time);
		}
	}

	public static Error wakeUp(PriorityQueue<TimerEvent> timers) {
		TimerEvent a = timers.poll();
		if (a == null)
			return Error.NO_EVENT;

		switch (a.reason) {

		case DAILY_WAKEUP: {

			EventData conditions = new EventData();
			conditions.setStartTime(System.currentTimeMillis());
			conditions.setEndTime(System.currentTimeMillis() + 1000 * 60 * 60
					* 24 * 7); // plus one week

			System.out.print("[RU] Searching for events ... ");
			EventList eventList = rud.searchEvent(null);
			if (eventList == null) {
				System.out.println("FAILED!"); 
				return Error.DAILY_WAKEUP_ERROR;
			}
			List<EventData> events = eventList.getEvents();
			if (events == null) {
				System.out.println("FAILED!"); 
				return Error.DAILY_WAKEUP_ERROR;
			}
			System.out.println("done");
			System.out.println("done");

			for (int i = 0; i < events.size(); i++) {
				EventData event = events.get(i);
				String room = event.getRoomId().getValue(); // room in which the
															// event is
				// done
				int expectedPeople = event.getExpectedPeople();

				System.out.println("Event " + event.getEventType().getValue()
						+ " @ " + event.getRoomId().getValue()
						+ " (expected people = " + event.getExpectedPeople()
						+ ")" + "\n\tfrom " + new Date(event.getStartTime())
						+ "\n\tto " + new Date(event.getEndTime()));

				// if is a future event (more than tomorrow), do not set timers
				if (event.getDate() > System.currentTimeMillis() + 1000 * 60
						* 60 * 24)
					continue;

				scheduleEventTimers(timers, event, room);

				System.out.print("[P] Getting paths to room " + room + "... ");
				List<Integer> pathsToRoom = p.getPaths(room);
				if (pathsToRoom == null) {
					System.out.println("FAILED!");
					return Error.DAILY_WAKEUP_ERROR;
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
						return Error.DAILY_WAKEUP_ERROR;
					}
					System.out.println("done");
					satisfiedCapacity += pd.getCapacity();
					List<PathComponent> componentArray = pd.getPath()
							.getValue().getComponents();
					for (int r = 0; r < componentArray.size(); r++) {
						String rid = componentArray.get(r).getRoomId()
								.getValue();
						scheduleTimers(timers, event, rid);
					}
				}
			}

			// schedule food order for next weeks events
			scheduleFoodTimer(timers, events);

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
				return Error.CLIMATE_WAKEUP_ERROR;
			}
			System.out.println("done");

			System.out.println("Outdoor Conditions:" + "\n\t Temperature = "
					+ wc.getTemperature() + "\n\t Humidity = "
					+ wc.getHumidity() + "\n\t CO2 Level = " + wc.getCo2Level()
					+ "\n\t Noise Level = " + wc.getNoiseLevel());

			System.out.print("[AC] Getting indoor status for room " + roomId
					+ "... ");

			IndoorStatus is = acc.getIndoorStatus(roomId);
			if (is == null) {
				System.out.println("FAILED!");
				return Error.CLIMATE_WAKEUP_ERROR;
			}
			System.out.println("done");

			System.out
					.println("Indoor Status:" + "\n\t Temperature = "
							+ is.getTemperature() + "\n\t Humidity = "
							+ is.getHumidity() + "\n\t CO2 Level = "
							+ is.getCo2Level());

			float desiredTemperature = establishDesiredTemperature(wc);
			float desiredHumidity = establishDesiredHumidity(is);
			float desiredCo2level = establishDesiredCo2level(wc);

			if (!valuesOutOfRange(is, wc))
				break;

			// choose if use natural or artificial climate control system
			if (naturalClimateUsable(desiredTemperature, desiredHumidity,
					desiredCo2level, is, wc)) {
				System.out.print("[NC] Opening windows in room " + roomId
						+ "... ");
				if (!nc.openWindow(l)) {
					System.out.println("FAILED!");
					return Error.CLIMATE_WAKEUP_ERROR;
				} else
					System.out.println("done");
			} else {

				is.setRoomID(accObjFactory.createIndoorStatusRoomID(roomId));
				is.setTemperature(desiredTemperature);
				is.setCo2Level(desiredCo2level);
				is.setHumidity(desiredHumidity);
				is.setTimer(0);
				System.out.print("[AC] Setting indoor parameters for room "
						+ roomId + "... ");
				acc.setIndoorParameters(is);
				System.out.println("done");

				System.out.print("[NC] Closing windows in room " + roomId
						+ "... ");
				if (!nc.closeWindow(l)) {
					System.out.println("FAILED!");
					return Error.CLIMATE_WAKEUP_ERROR;
				} else
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
			if (indoorLuminance == -1) {
				System.out.println("FAILED!");
				return Error.LUMINANCE_WAKEUP_ERROR;
			} else
				System.out.println("done");

			System.out.println("Indoor Luminance: " + indoorLuminance);

			if (desiredLuminance < indoorLuminance)
				break;

			System.out.print("[LM] Getting outdoor luminance for room "
					+ roomId + "... ");

			float outdoorLuminance = lm.getOutdoorLuminance(roomId);
			if (outdoorLuminance == -1) {
				System.out.println("FAILED!");
				return Error.LUMINANCE_WAKEUP_ERROR;
			} else
				System.out.println("done");

			System.out.println("Outdoor Luminance: " + outdoorLuminance);

			System.out.print("[LM] Getting room settings for room " + roomId
					+ "... ");
			Room rs = lm.getCurrentRoomSettings(roomId);
			if (rs == null) {
				System.out.println("FAILED!");
				return Error.CLIMATE_WAKEUP_ERROR;
			} else
				System.out.println("done");

			while (desiredLuminance > indoorLuminance) {
				// need to increase luminance
				if (desiredLuminance < outdoorLuminance) {
					// use natural system
					if (rs.getWindows().get(0).getAngle() == 1) {
						// blind is up
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
						// blind is up
						for (Window w : rs.getWindows()) {
							w.setAngle(estabilishAngle(indoorLuminance,
									outdoorLuminance));

							System.out.print("[LM] Regulating blinds for room "
									+ roomId + "... ");
							lm.regulateBlind(rs);
							System.out.println("done");

							System.out
									.print("[LM] Getting indoor luminance for room "
											+ roomId + "... ");
							indoorLuminance = lm.getIndoorLuminance(roomId);
							if (indoorLuminance == -1) {
								System.out.println("FAILED!");
								return Error.LUMINANCE_WAKEUP_ERROR;
							} else
								System.out.println("done");

							System.out.println("Indoor Luminance: "
									+ indoorLuminance);
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
			}
		}
			break;

		case FOOD_WAKEUP: {
			// set the correct level of food

			HashMap<String, Float> map = estalishFoodNeeds(a.weekEvents);

			System.out.println("Food needs:");
			for (Entry<String, Float> f : map.entrySet())
				System.out.println("\t" + f.getKey() + " = " + f.getValue());

			System.out.print("[SF] Getting food stocks... ");
			FoodList fl = sf.getFoodStocks();
			if (fl == null) {
				System.out.println("FAILED!");
				return Error.FOOD_WAKEUP_ERROR;
			}
			System.out.println("done");

			List<Food> ff = fl.getFoods();
			if (ff == null) {
				System.out.println("FAILED!");
				return Error.FOOD_WAKEUP_ERROR;
			}
			System.out.println("done");
			
			System.out.println("Food stocks:");
			for (int f = 0; f < ff.size(); f++) {
				Food food = ff.get(f);

				System.out.println("\t" + food.getLabel().getValue() + " "
						+ food.getQuantity());

				int neededQuantity = map.get(food.getLabel().getValue())
						.intValue();

				if (food.getQuantity() < neededQuantity) {
					int quantityToOrder = neededQuantity - food.getQuantity();
					food.setQuantity(quantityToOrder);
				} else
					food.setQuantity(0);
			}
			FoodOrder fo = new FoodOrder();
			fo.setFoodList(sfObjFactory.createFoodOrderFoodList(fl));

			System.out.println("Food order:");
			for (Food food : ff)
				System.out.println("\t" + food.getLabel().getValue() + " "
						+ food.getQuantity());

			System.out.print("[SF] Placing food order... ");
			sf.placeFoodOrder(fo);
			System.out.println("done");
		}

			break;

		// Input State: expectedPeople
		case CLEANING_WAKEUP: {

			// set the correct level of services and facilities based on
			// the
			// event

			int expectedPeople = a.event.getExpectedPeople();

			int desiredCleaningFreq = establishDesiredCleaningFrequency(expectedPeople);
			
			if (desiredCleaningFreq == 1)
				break;
			
			System.out.print("[SF] Setting cleaning frequency... ");
			sf.setCleaningFrequency(desiredCleaningFreq);
			System.out.println("done");
		}
			break;
		}
		return Error.SUCCESS;
	}

	private static int establishDesiredCleaningFrequency(int expectedPeople) {
		final int LMThreshold = 50;
		final int MHThreshold = 100;
		final int lowFreq = 1;
		final int medFreq = 2;
		final int highFreq = 3;

		if (expectedPeople > LMThreshold && expectedPeople < MHThreshold)
			return medFreq;

		if (expectedPeople > MHThreshold)
			return highFreq;

		return lowFreq;
	}

	private static HashMap<String, Float> estalishFoodNeeds(
			List<EventData> events) {
		HashMap<String, Float> map = new HashMap<String, Float>();

		float neededMeals = 0;
		float neededDrinks = 0;

		for (EventData e : events) {
			switch (e.getEventType().getValue()) {
			case "Degree":
				neededMeals += 0.75 * e.getExpectedPeople();
				neededDrinks += 0.75 * e.getExpectedPeople();
				break;
			case "Conference":
				neededMeals += 0.5 * e.getExpectedPeople();
				neededDrinks += 0.5 * e.getExpectedPeople();
				break;
			}
		}

		map.put("Meals", neededMeals);
		map.put("Drinks", neededDrinks);

		return map;
	}

	private static boolean valuesOutOfRange(IndoorStatus is, WeatherCondition wc) {

		if (is.getTemperature() != establishDesiredTemperature(wc))
			return true;
		if (is.getHumidity() != establishDesiredHumidity(is))
			return true;
		if (is.getCo2Level() != establishDesiredCo2level(wc))
			return true;

		return false;
	}

	private static float establishDesiredCo2level(WeatherCondition wc) {

		return Math.min(1000, wc.getCo2Level() + 500);
	}

	private static float establishDesiredTemperature(WeatherCondition outdoorWc) {
		float out = outdoorWc.getTemperature();

		float tmin = Math.max(18, out / 5 + 17);
		float tmax = Math.min(30, out / 5 + 22);

		return Math.min(Math.max(tmin, out), tmax);
	}

	private static float establishDesiredHumidity(IndoorStatus is) {
		float in = is.getHumidity();

		return Math.min(Math.max(30, in), 50);
	}

	private static boolean naturalClimateUsable(float desiredT, float desiredH,
			float desiredC, IndoorStatus is, WeatherCondition wc) {

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

	private static float estabilishDesiredLuminance(String eventType) {
		return 350f;
	}

	private static float estabilishAngle(float indoorLuminance,
			float outdoorLuminance) {
		return 1;
	}

	private static void scheduleTimers(PriorityQueue<TimerEvent> timers,
			EventData ev, String rid) {
		TimerEvent a;
		
		// CLIMATE_WAKEUP scheduling
		a = new TimerEvent(WakeReason.CLIMATE_WAKEUP, ev.getStartTime() - 3*60*60*1000, ev,
				rid);
		timers.add(a);

		// LUMINANCE_WAKEUP scheduling
		a = new TimerEvent(WakeReason.LUMINANCE_WAKEUP, ev.getStartTime(), ev,
				rid);
		timers.add(a);
	}

	private static void scheduleEventTimers(PriorityQueue<TimerEvent> timers,
			EventData ev, String rid) {
		TimerEvent a;

		scheduleTimers(timers, ev, rid);

		// CLEANING_WAKEUP scheduling
		a = new TimerEvent(WakeReason.CLEANING_WAKEUP, ev.getStartTime() - 2*60*60*1000, ev,
				rid);
		timers.add(a);
	}

	private static void scheduleFoodTimer(PriorityQueue<TimerEvent> timers,
			List<EventData> events) {
		TimerEvent a;

		// FOOD_WAKEUP scheduling
		a = new TimerEvent(WakeReason.FOOD_WAKEUP, System.currentTimeMillis(),
				null, null, events);
		timers.add(a);
	}

}
