package com.smartcampus.soa;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.smartcampus.acc.ArtificialClimateControlService;
import com.smartcampus.acc.indoorStatus;
import com.smartcampus.luminancemanagement.LuminanceManagementService;
import com.smartcampus.luminancemanagement.RoomSettings;
import com.smartcampus.luminancemanagement.Spotlight;
import com.smartcampus.luminancemanagement.Window;
import com.smartcampus.naturalclimatesystem.Location;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystemService;
import com.smartcampus.naturalclimatesystem.WeatherCondition;
import com.smartcampus.paths.PathComponent;
import com.smartcampus.paths.PathData;
import com.smartcampus.paths.PathsService;
import com.smartcampus.roomusagedatabase.Event;
import com.smartcampus.roomusagedatabase.EventData;
import com.smartcampus.roomusagedatabase.RoomUsageDatabaseService;
import com.smartcampus.servicesfacilities.Food;
import com.smartcampus.servicesfacilities.FoodList;
import com.smartcampus.servicesfacilities.FoodOrder;
import com.smartcampus.servicesfacilities.ServicesAndFacilitiesService;

public class SOAWithStrings {
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

	public static void main(String[] args) {

		ArtificialClimateControlService acc = new ArtificialClimateControlService();
		NaturalClimateSystemService nc = new NaturalClimateSystemService();
		PathsService p = new PathsService();
		RoomUsageDatabaseService rud = new RoomUsageDatabaseService();
		ServicesAndFacilitiesService sf = new ServicesAndFacilitiesService();
		LuminanceManagementService lm = new LuminanceManagementService();

		// get all the events with a criteria
		EventData[] events = rud.searchEvent(null, null, null, null);

		for (int i = 0; i < events.length; i++) {
			Event event = events[i].getEvents();
			String eventType = event.getEventType();
			String room = event.getRoomId(); // room in which the event is done
			int expectedPeople = event.getExpectedPeople();

			// set the correct level of food
			FoodList fl = sf.getFoodStocks();
			Food[] ff = fl.getFoods();
			for (int f = 0; f < ff.length; f++) {
				Food food = ff[f];
				int neededQuantity = estabilishQuantityOfFood(food.getLabel(),
						expectedPeople);
				if (food.getQuantity() < neededQuantity) {
					int quantityToOrder = neededQuantity - food.getQuantity();
					ff[f].setQuantity(quantityToOrder);
				} else
					food.setQuantity(0);
			}
			FoodOrder fo = new FoodOrder();
			fo.setFoodList(fl);
			sf.placeFoodOrder(fo);

			// set the correct level of servicies and facilities based on the
			// event
			int LMThreshold = 50;
			int MHThreshold = 100;
			int medFreq = 2;
			int highFreq = 3;
			if (expectedPeople > LMThreshold && expectedPeople < MHThreshold) {
				sf.setCleaningFrequency(medFreq);
			}
			if (expectedPeople > MHThreshold) {
				sf.setCleaningFrequency(highFreq);
			}

			ArrayList<String> roomsToConsider = new ArrayList<String>();
			roomsToConsider.add(room);

			int[] pathsToRoom = p.getPaths(room);
			int satisfiedCapacity = 0;

			// for all the needed paths I want to obtain the id of the rooms in
			// each one
			for (int s = 0; (s < pathsToRoom.length)
					&& (satisfiedCapacity < expectedPeople); s++) {
				PathData pd = p.getPathAttributes(pathsToRoom[s]);
				satisfiedCapacity += pd.getCapacity();
				PathComponent[] componentArray = pd.getPath().getComponents();
				for (int r = 0; r < componentArray.length; r++) {
					roomsToConsider.add(componentArray[r].getId());
				}
			}

			// for each room that has to be considered I have to obtain the
			// correct luminance and temperature level
			for (int k = 0; k < roomsToConsider.size(); k++) {

				String roomId = roomsToConsider.get(k);

				Location l = new Location();
				l.setRoomId(roomId);
				WeatherCondition wc = nc.getWeatherCondition(l);
				float outdoorTemperature = wc.get_temperature();
				float desiredTemperature = estabilishDesiredTemperature(
						event.getDate(), outdoorTemperature, roomId);
				float indoorTemperature = acc.getIndoorStatus(roomId)
						.getTemperaure();
				// choose if use natural or artificial climate control system
				if (naturalClimateUsable(desiredTemperature, indoorTemperature,
						outdoorTemperature, wc.get_pollution())) {
					if (nc.openWindow(l)) {
						// TODO: show alert
					}
				} else {
					acc.setIndoorParameters(new indoorStatus(room,
							desiredTemperature, 0, 0, 0));// TODO: passare
															// valori NULL
					if (nc.closeWindow(l)) {
						// TODO: show alert
					}
				}

				// adapt luminance level
				float desiredLuminance = estabilishDesiredLuminance(eventType);
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
									indoorLuminance = lm
											.getIndoorLuminance(roomId);
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
																	 * the light
																	 * is
																	 * switched
																	 * on
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

		}
	}
}
