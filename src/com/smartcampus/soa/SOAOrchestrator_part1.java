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

public class SOAOrchestrator_part1 {

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

				
			}

		}
	}
}