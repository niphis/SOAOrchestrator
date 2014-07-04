package com.smartcampus.test.soa;
import java.sql.Time;
import java.util.Date;
import java.util.PriorityQueue;

import com.smartcampus.acc.local.ArtificialClimateControlService;
import com.smartcampus.acc.local.indoorStatus;
import com.smartcampus.luminancemanagement.local.LuminanceManagementService;
import com.smartcampus.luminancemanagement.local.RoomSettings;
import com.smartcampus.luminancemanagement.local.Spotlight;
import com.smartcampus.luminancemanagement.local.Window;
import com.smartcampus.naturalclimatesystem.local.Location;
import com.smartcampus.naturalclimatesystem.local.NaturalClimateSystemService;
import com.smartcampus.naturalclimatesystem.local.WeatherCondition;
import com.smartcampus.paths.local.PathComponent;
import com.smartcampus.paths.local.PathData;
import com.smartcampus.paths.local.PathsService;
import com.smartcampus.roomusagedatabase.local.Event;
import com.smartcampus.roomusagedatabase.local.EventData;
import com.smartcampus.roomusagedatabase.local.RoomUsageDatabaseService;
import com.smartcampus.servicesfacilities.local.Food;
import com.smartcampus.servicesfacilities.local.FoodList;
import com.smartcampus.servicesfacilities.local.FoodOrder;
import com.smartcampus.servicesfacilities.local.ServicesAndFacilitiesService;

public class Orchestrator {

	private static float estabilishDesiredTemperature(Date date,
			float outdoorTemperature, String roomId) {
		return 0;
	}

	private static float estabilishDesiredLuminance(String eventType) {
		return 0;
	}

	private static int estabilishQuantityOfFood(String food, int people) {
		return 0;
	}

	private static float estabilishAngle(float indoorLuminance,
			float outdoorLuminance) {
		return 1;
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

	private static enum WakeReason {
		DAILY_WAKEUP, CLIMATE_WAKEUP, LUMINANCE_WAKEUP, FOOD_WAKEUP, CLEANING_WAKEUP
	};

	private static ArtificialClimateControlService acc;
	private static NaturalClimateSystemService nc;
	private static PathsService p;
	private static RoomUsageDatabaseService rud;
	private static ServicesAndFacilitiesService sf;
	private static LuminanceManagementService lm;

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
		sf = new ServicesAndFacilitiesService();
		lm = new LuminanceManagementService();

		// INITIAL EVENT SCHEDULING
		TimerEvent a = new TimerEvent(WakeReason.DAILY_WAKEUP);

		timers.add(a);
	}

	private static void wakeUp(TimerEvent a) {
		switch (a.reason) {

		case DAILY_WAKEUP: {
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
		}
			break;

		// Input State: roomsToConsider, event, room
		case CLIMATE_WAKEUP: {
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
		}
			break;

		case LUMINANCE_WAKEUP: {
			String roomId = a.room;

			// adapt luminance level
			float desiredLuminance = estabilishDesiredLuminance(a.event
					.getEventType());
			float indoorLuminance = lm.getIndoorLuminance(roomId);
			float outdoorLuminance = lm.getOutdoorLuminance(roomId);

			RoomSettings rs = new RoomSettings();
			rs.setRoomId(roomId);

			while (true) {

				if (desiredLuminance > indoorLuminance) {
					// need to increase luminance
					if (desiredLuminance < outdoorLuminance) {
						// use natural system
						if (rs._windows.get(0).getAngle() == 1) { // blind
							// is up
							// switch on spotlight
							for (Spotlight s : rs._spotlights)
								s.setLuminance(desiredLuminance);
							break;
						} else {
							// blind up
							for (Window w : rs._windows) {
								w.setAngle(estabilishAngle(indoorLuminance,
										outdoorLuminance));
								indoorLuminance = lm.getIndoorLuminance(roomId);
							}
						}
					} else {
						// switch on spotlight
						for (Spotlight s : rs._spotlights)
							s.setLuminance(desiredLuminance);
					}
				} else {
					// need to decrease luminance
					if (rs._spotlights.get(0).getLuminance() > 0/*
																 * the light is
																 * switched on
																 */) {
						// switch off spotligth
						for (Spotlight s : rs._spotlights)
							s.setLuminance(0);
						indoorLuminance = lm.getIndoorLuminance(roomId);
					} else {
						// blind down
						for (Window w : rs._windows) {
							w.setAngle(0);
							break;
						}
					}
				}
			}
		}
			break;

		case FOOD_WAKEUP: {
			// set the correct level of food
			FoodList fl = sf.getFoodStocks();
			Food[] ff = fl.getFoods();
			for (int f = 0; f < ff.length; f++) {
				Food food = ff[f];
				int neededQuantity = estabilishQuantityOfFood(food.getLabel(),
						a.event.getExpectedPeople());
				if (food.getQuantity() < neededQuantity) {
					int quantityToOrder = neededQuantity - food.getQuantity();
					ff[f].setQuantity(quantityToOrder);
				} else
					food.setQuantity(0);
			}
			FoodOrder fo = new FoodOrder();
			fo.setFoodList(fl);
			sf.placeFoodOrder(fo);
		}
			break;

		// Input State: expectedPeople
		case CLEANING_WAKEUP: {

			// set the correct level of servicies and facilities based on
			// the
			// event
			int LMThreshold = 50;
			int MHThreshold = 100;
			int medFreq = 2;
			int highFreq = 3;
			int expectedPeople = a.event.getExpectedPeople();

			if (expectedPeople > LMThreshold && expectedPeople < MHThreshold) {
				sf.setCleaningFrequency(medFreq);
			}

			if (expectedPeople > MHThreshold) {
				sf.setCleaningFrequency(highFreq);
			}
		}
			break;
		}
	}

	private static void scheduleTimers(Event ev, String rid) {

		TimerEvent a;

		// CLIMATE_WAKEUP scheduling
		a = new TimerEvent(WakeReason.CLIMATE_WAKEUP, ev.getStartTime(), ev,
				rid);
		timers.add(a);

		// LUMINANCE_WAKEUP scheduling
		a = new TimerEvent(WakeReason.LUMINANCE_WAKEUP, ev.getStartTime(), ev,
				rid);
		timers.add(a);

		// FOOD_WAKEUP scheduling
		a = new TimerEvent(WakeReason.FOOD_WAKEUP, ev.getStartTime(), ev, rid);
		timers.add(a);

		// CLEANING_WAKEUP scheduling
		a = new TimerEvent(WakeReason.CLEANING_WAKEUP, ev.getStartTime(), ev,
				rid);
		timers.add(a);

	}

	public static void main(String[] args) {
		TimerEvent timerEvent;
		while ((timerEvent = timers.poll()) != null)
			wakeUp(timerEvent);
	}
}
