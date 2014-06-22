import java.util.Date; 

import com.smartcampus.acc.ArtificialClimateControlService;
import com.smartcampus.acc.indoorStatus;
import com.smartcampus.luminancemanagement.LuminanceManagementService;
import com.smartcampus.paths.PathsService;
import com.smartcampus.roomusagedatabase.Event;
import com.smartcampus.roomusagedatabase.EventData;
import com.smartcampus.roomusagedatabase.RoomUsageDatabaseService;
import com.smartcampus.servicesfacilities.ServicesAndFacilitiesService;


public class SOA {

	private static float estabilishDesiredTemperature(Date date, float outdoorTemperature) {
		return 0;
	}
	
	private static boolean naturalClimateUsable(float desired, float indoor, float outdoor, int pollutionLevel) {
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
		NaturalClimateSystemService nc = new NaturalClimateSystemServce();
		PathsService p = new PathsService();
		RoomUsageDatabaseService rud = new RoomUsageDatabaseService();
		ServicesAndFacilitiesService sf = new ServicesAndFacilitiesService(); 
		LuminanceManagementService lm = new LuminanceManagementService();
		
		
		EventData[] events = rud.searchEvent(null, null, null, null);
		WeatherCondition wc = nc.getWeatherCondition("Pisa, via Bonanno");
		float outdoorTemperature = wc.getTemperature();
		int pollutionLevel = wc.getPollution();
		
		for (int i=0; i<events.length; i++) {
			Event event = events[i].getEvents();
			String room = event.getRoomId();
			
			indoorStatus is = acc.getIndoorStatus(room);
			float indoorTemperature = is.getTemperaure();
			
			int[] pathsToRoom = p.getPaths(room);
			for (int j=0 ; i<pathsToRoom.length; i++) {
				float desiredTemperature = estabilishDesiredTemperature(event.getDate(), outdoorTemperature);
				if (naturalClimateUsable(desiredTemperature, indoorTemperature, outdoorTemperature, pollutionLevel)) {
					if (nc.openWindow()){
						//TODO: show alert
					}
				} else {
					acc.setIndoorParameters(new indoorStatus(room, desiredTemperature, 0, 0, 0));//TODO: passare valori NULL
					if (nc.closeWindow()) {
						//TODO: show alert
					}
				}
			}
			
			
			}
		
	}
}
