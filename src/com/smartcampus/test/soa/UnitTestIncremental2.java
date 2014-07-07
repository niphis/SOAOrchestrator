/**
 * 
 */
package com.smartcampus.test.soa;

import java.util.PriorityQueue;

import com.smartcampus.acc.ArtificialClimateControl;
import com.smartcampus.acc.ArtificialClimateControlPortType;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystem;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystemPortType;
import com.smartcampus.paths.Paths;
import com.smartcampus.paths.PathsPortType;
import com.smartcampus.roomusagedatabase.RoomUsageDatabase;
import com.smartcampus.roomusagedatabase.RoomUsageDatabasePortType;
import com.smartcampus.test.soa.Orchestrator_part1.Error;
import com.smartcampus.test.soa.Orchestrator_part1.TimerEvent;
import com.smartcampus.test.soa.Orchestrator_part1.WakeReason;

/**
 * @author Tom
 *
 */
public class UnitTestIncremental2 {

	/**
	 * @param args
	 */
private static PriorityQueue<TimerEvent> timers = new PriorityQueue<TimerEvent>();
	
	private static ArtificialClimateControlPortType acc = new ArtificialClimateControl()
		.getArtificialClimateControlHttpSoap11Endpoint();
	private static NaturalClimateSystemPortType nc = new NaturalClimateSystem()
		.getNaturalClimateSystemHttpSoap11Endpoint();
	private static PathsPortType p = new Paths().getPathsHttpSoap11Endpoint();
	private static RoomUsageDatabasePortType rud = new RoomUsageDatabase().getRoomUsageDatabaseHttpSoap11Endpoint();

	public static void testOrchestrator(int maxIterations) {
		
		int climateControlErrorCounter = 0;
		int climateControlEventCounter = 0;
		int dailyWakeupErrorCounter = 0;
		int dailyWakeupEventCounter = 0;
		
		for (int i=0; i<maxIterations; i++) {
		
			// INITIAL EVENT SCHEDULING
			
			Error res = Error.NO_EVENT;
			TimerEvent a = new TimerEvent(WakeReason.DAILY_WAKEUP);

			timers.add(a);
						
			Orchestrator_part1.setArtificialClimateControlPortType(acc);
			Orchestrator_part1.setNaturalClimateSystemPortType(nc);
			Orchestrator_part1.setPathsPortType(p);
			Orchestrator_part1.setRoomUsageDatabasePortType(rud);
			
			do {
				
				WakeReason w = timers.element().reason;
				// Counter on event
				switch(w) {
				case DAILY_WAKEUP:
					dailyWakeupEventCounter++;
				case CLIMATE_WAKEUP:
					climateControlEventCounter++;
				}
				
				res = Orchestrator_part1.wakeUp(timers);
				
				// Counter on error
				switch (res) {
				case DAILY_WAKEUP_ERROR:
					climateControlErrorCounter++;
				case CLIMATE_WAKEUP_ERROR:
					climateControlErrorCounter++;
				}
			}
			
			while (res != Error.NO_EVENT);
			
			
		}
	}
	
	public static void main(String[] args) {
		testOrchestrator(1000);
	}
}
