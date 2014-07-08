package com.smartcampus.orchestrator.soa.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import com.smartcampus.acc.ArtificialClimateControlPortType;
import com.smartcampus.acc.xsd.IndoorStatus;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystemPortType;
import com.smartcampus.naturalclimatesystem.xsd.Location;
import com.smartcampus.naturalclimatesystem.xsd.WeatherCondition;
import com.smartcampus.orchestrator.soa.Orchestrator_part1.Error;
import com.smartcampus.orchestrator.soa.Orchestrator_part1.TimerEvent;
import com.smartcampus.orchestrator.soa.Orchestrator_part1.WakeReason;
import com.smartcampus.paths.PathsPortType;
import com.smartcampus.paths.xsd.PathComponent;
import com.smartcampus.paths.xsd.PathData;
import com.smartcampus.roomusagedatabase.RoomUsageDatabasePortType;
import com.smartcampus.roomusagedatabase.xsd.EventData;
import com.smartcampus.roomusagedatabase.xsd.EventList;

public class IntegrationTestIncrement2 {

	public enum WakeReason {
		DAILY_WAKEUP, 
		CLIMATE_WAKEUP
	};

	public enum Error {
		NO_EVENT, 
		SUCCESS,
		DAILY_WAKEUP_ERROR, 
		CLIMATE_WAKEUP_ERROR, 
	};
	
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
	
	public static Error wakeUp(PriorityQueue<TimerEvent> timers) {

		System.out.println("######### START #########");

		TimerEvent a = timers.poll();
		if (a == null) {
			System.out.println("[ 1  ] timer is empty");

			System.out.println("########## END ##########");

			return Error.NO_EVENT;
		}
		
		switch (a.reason) {

		case DAILY_WAKEUP: {
			System.out.println("[ 2  ] Daily WakeUP");
			
			// Queue. Pop in head, push in tail.
			List<EventData> events = IntegrationTestStaticInput.eventListTest.pollFirst();
			IntegrationTestStaticInput.eventListTest.offerLast(events);
			
			for (int i = 0; i < events.size(); i++) {
				
				System.out.println("[ 4." + i + "]\tThe list of event is not empty");
				
				EventData event = events.get(i);
				String room = "TestRoom";
				
				int expectedPeople = event.getExpectedPeople();
				
				scheduleTimers(timers, event, room);
				
				
				List<Integer> pathsToRoom = IntegrationTestStaticInput.pathListTest.pollFirst();
				IntegrationTestStaticInput.pathListTest.offerLast(pathsToRoom);
				
				if (pathsToRoom == null) {
					System.out.println("[ 5." + i + "]\tPath to room is null"); 
					return Error.DAILY_WAKEUP_ERROR;
				}
				
				System.out.println("[ 6." + i + "]\tPath to room is not null");
				int satisfiedCapacity = 0;

				// for all the needed paths I want to obtain the id of the rooms
				// in
				// each one
				int s;
				for (s = 0; (s < pathsToRoom.size())
						&& (satisfiedCapacity < expectedPeople); s++) {
					System.out.println("[ 8." + i + "." + s + "]\t Path Room List is not empty");
					System.out.println("[10." + i + "." + s + "]\t Capacity does not satisfy expected people");
					
					Integer pathId = pathsToRoom.get(s);
					
					PathData pd = IntegrationTestStaticInput.pathDataListTest.pollFirst();
					IntegrationTestStaticInput.pathDataListTest.offerLast(pd);
					
					if (pd == null) {
						System.out.println("[11." + i + "." + s + "]\t Path Data is null");
						return Error.DAILY_WAKEUP_ERROR;
					}
					
					System.out.println("[12." + i + "." + s + "]\t Path Data is not null");
					
					//satisfiedCapacity += pd.getCapacity();
					
					//List<PathComponent> componentArray = pd.getPath().getValue().getComponents();
					int maxComponents = IntegrationTestStaticInput.componentsNumberListTest.pollFirst();
					IntegrationTestStaticInput.componentsNumberListTest.offerLast(maxComponents);
					
					for (int r = 0; r < maxComponents; r++) {
						System.out.println("[14." + i + "." + s + "]\t Path Component: " + r);
						String rid = "TestRoom";
						scheduleTimers(timers, event, rid);
					}
					System.out.println("[13." + i + "." + s + "]\t Path Component is empty");
				}
				if (!(s < pathsToRoom.size()))
					System.out.println("[ 7." + i + "]\t Path Room List is empty");
				if (IntegrationTestStaticInput.error()) // Capacity
					System.out.println("[10." + i + "]\t Capacity satisfies expected people");
				
				
			}
			System.out.println("[ 3  ] The list of event is empty");
		}
		
			break;

		// Input State: roomsToConsider, event, room
		case CLIMATE_WAKEUP: {

			String roomId = a.room;
			System.out.println("[15  ] Climate wakeUP");

			WeatherCondition wc = IntegrationTestStaticInput.weatherConditionListTest.pollFirst();
			IntegrationTestStaticInput.weatherConditionListTest.offerLast(wc);

			if (wc == null) {
				System.out.println("[16  ] Weather Condition is null");
				return Error.CLIMATE_WAKEUP_ERROR;
			}

			System.out.println("[17  ] Weather Condition is not null");
			
			IndoorStatus is = IntegrationTestStaticInput.indoorStatusListTest.pollFirst();
			IntegrationTestStaticInput.indoorStatusListTest.offerLast(is);
					
			float desiredTemperature = establishDesiredTemperature(wc);
			float desiredHumidity = establishDesiredHumidity(is);
			float desiredCo2level = establishDesiredCo2level(wc);
			
			if (!valuesOutOfRange(is, wc)) {
				System.out.println("[18  ] Values not in the range");
				break;
			}
			
			System.out.println("[18  ] Values in the range");
			// choose if use natural or artificial climate control system
			if (naturalClimateUsable(desiredTemperature, desiredHumidity, desiredCo2level, is, wc)) {
				System.out.println("[20  ] Natural Regulation");
				if (IntegrationTestStaticInput.error()) {
					System.out.println("[22  ] Error"); 
					return Error.CLIMATE_WAKEUP_ERROR;
				}
				else
					System.out.println("[23  ] No Error"); 
			} else {


				System.out.println("[21  ] Artificial Regulation");
				//acc.setIndoorParameters(is);

				System.out.print("[NC] Closing windows in room " + roomId
						+ "... ");
				if (IntegrationTestStaticInput.error()) {
					System.out.println("[24  ] Error"); 
					return Error.CLIMATE_WAKEUP_ERROR;
				}
				else
					System.out.println("[25  ] No Error"); 
			}
		}
			break;
		}

		System.out.println("########## END ##########");

		return Error.SUCCESS;
	}

	private static boolean valuesOutOfRange(IndoorStatus is, WeatherCondition wc) {
		
		boolean ret = IntegrationTestStaticInput.rangesInListTest.pollFirst();
		IntegrationTestStaticInput.rangesInListTest.offerLast(ret);
		return ret;
		/*
		if (is.getTemperature() != establishDesiredTemperature(wc))
			return true;
		if (is.getHumidity() != establishDesiredHumidity(is))
			return true;
		if (is.getCo2Level() != establishDesiredCo2level(wc))
			return true;

		return false;
		*/
	}

	private static float establishDesiredCo2level(WeatherCondition wc) {
		
		return Math.min(1000, /*wc.getCo2Level() +*/ 500);
	}
	private static float establishDesiredTemperature(
			WeatherCondition outdoorWc) {
		float out = outdoorWc.getTemperature();
		
		float tmin = Math.max(18, out/5 + 17);
		float tmax = Math.min(30, out/5 + 22);
		
		return Math.min(Math.max(tmin, out), tmax);
	}
	
	private static float establishDesiredHumidity(
			IndoorStatus is) {
		//float in = is.getHumidity();
		
		return Math.min(Math.max(30, 20/*in*/), 50);
	}

	private static boolean naturalClimateUsable(float desiredT, float desiredH, float desiredC, IndoorStatus is,
			WeatherCondition wc) {
		
		boolean ret = IntegrationTestStaticInput.naturalClimateListTest.pollFirst();
		IntegrationTestStaticInput.naturalClimateListTest.offerLast(ret);
		return ret;
		/*
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
		*/
	}

	private static void scheduleTimers(PriorityQueue<TimerEvent> timers, EventData ev, String rid) {
		// CLIMATE_WAKEUP scheduling
		TimerEvent a = new TimerEvent(WakeReason.CLIMATE_WAKEUP,
				ev.getStartTime() - 3*60*60*1000, ev, rid);
		timers.add(a);
	}

	
}
