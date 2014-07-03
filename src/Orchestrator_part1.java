import java.util.ArrayList;
import java.util.Date;

import com.smartcampus.acc.ArtificialClimateControlService;
import com.smartcampus.acc.indoorStatus;
import com.smartcampus.naturalclimatesystem.Location;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystemService;
import com.smartcampus.naturalclimatesystem.WeatherCondition;
import com.smartcampus.paths.PathComponent;
import com.smartcampus.paths.PathData;
import com.smartcampus.paths.PathsService;
import com.smartcampus.roomusagedatabase.Event;
import com.smartcampus.roomusagedatabase.EventData;
import com.smartcampus.roomusagedatabase.RoomUsageDatabaseService;

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
	
	private enum WakeReason { DAILY_WAKEUP, CLIMATE_WAKEUP };
	
	private static ArtificialClimateControlService acc;
	private static NaturalClimateSystemService nc;
	private static PathsService p;
	private static RoomUsageDatabaseService rud;

	static {
		acc = new ArtificialClimateControlService();
		nc = new NaturalClimateSystemService();
		p = new PathsService();
		rud = new RoomUsageDatabaseService();
	}

	private static EventData[] events;
	private static ArrayList<ArrayList<String>> roomsPerEvent = new ArrayList<ArrayList<String>>();

	// current state
	private static Event event;
	private static ArrayList<String> roomsToConsider;
	private static String room;
	
	private static void wakeUp(WakeReason reason) {
		switch (reason) {
		
		case DAILY_WAKEUP:
			events = rud.searchEvent(null, null, null, null);

			for (int i = 0; i < events.length; i++) {
				Event event = events[i].getEvents();
				String room = event.getRoomId(); // room in which the event is
													// done
				int expectedPeople = event.getExpectedPeople();

				ArrayList<String> roomsToConsider = new ArrayList<String>();
				roomsToConsider.add(room);

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
						roomsToConsider.add(componentArray[r].getId());
					}
				}

				roomsPerEvent.add(roomsToConsider);
			}
			break;

		// Input State: roomsToConsider, event, room
		case CLIMATE_WAKEUP:

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

			break;
		}
	}

	public static void main(String[] args) {
		
		wakeUp(WakeReason.DAILY_WAKEUP);
		
		for (int i = 0; i < events.length; i++) {
			event = events[i].getEvents();
			room = event.getRoomId(); // room in which the event is done
			roomsToConsider = roomsPerEvent.get(i);
			
			wakeUp(WakeReason.CLIMATE_WAKEUP);			
		}
	}
}
