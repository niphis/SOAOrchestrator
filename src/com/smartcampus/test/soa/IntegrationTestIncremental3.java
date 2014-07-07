/**
 * 
 */
package com.smartcampus.test.soa;

import java.util.PriorityQueue;

import com.smartcampus.acc.ArtificialClimateControl;
import com.smartcampus.acc.ArtificialClimateControlPortType;
import com.smartcampus.luminancemanagement.LuminanceManagement;
import com.smartcampus.luminancemanagement.LuminanceManagementPortType;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystem;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystemPortType;
import com.smartcampus.paths.Paths;
import com.smartcampus.paths.PathsPortType;
import com.smartcampus.roomusagedatabase.RoomUsageDatabase;
import com.smartcampus.roomusagedatabase.RoomUsageDatabasePortType;
import com.smartcampus.test.soa.Orchestrator_part2.Error;
import com.smartcampus.test.soa.Orchestrator_part2.TimerEvent;
import com.smartcampus.test.soa.Orchestrator_part2.WakeReason;

/**
 * @author Tom
 *
 */
public class IntegrationTestIncremental3 {

	/**
	 * @param args
	 */
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
	private static LuminanceManagementPortType lm = new LuminanceManagement()
		.getLuminanceManagementHttpSoap11Endpoint();
	
	public static void testOrchestrator(int maxIterations) {
		
		int climateControlErrorCounter = 0;
		int climateControlEventCounter = 0;
		
		int dailyWakeupErrorCounter = 0;
		int dailyWakeupEventCounter = 0;
		
		int luminanceManagementErrorCounter = 0;
		int luminanceManagementEventCounter = 0;
		
		for (int i=0; i<maxIterations; i++) {
		
			// INITIAL EVENT SCHEDULING
			
			Error res = Error.NO_EVENT;
			TimerEvent a = new TimerEvent(WakeReason.DAILY_WAKEUP);

			timers.add(a);
						
			Orchestrator_part2.setArtificialClimateControlPortType(acc);
			Orchestrator_part2.setNaturalClimateSystemPortType(nc);
			Orchestrator_part2.setPathsPortType(p);
			Orchestrator_part2.setRoomUsageDatabasePortType(rud);
			Orchestrator_part2.setLuminanceManagementPortType(lm);
			
			do {
				
				WakeReason w = timers.element().reason;
				
				// Counter on event
				
				switch(w) {
				case DAILY_WAKEUP:
					dailyWakeupEventCounter++;
					break;
				case CLIMATE_WAKEUP:
					climateControlEventCounter++;
					break;
				case LUMINANCE_WAKEUP:
					luminanceManagementEventCounter++;
					break;
				}
	
				
				res = Orchestrator_part2.wakeUp(timers);
				
				// Counter on error
				
				switch (res) {
				case DAILY_WAKEUP_ERROR:
					climateControlErrorCounter++;
					break;
				case CLIMATE_WAKEUP_ERROR:
					climateControlErrorCounter++;
					break;
				case LUMINANCE_WAKEUP_ERROR:
					luminanceManagementErrorCounter++;
					break;
				}
			}
			
			while (res != Error.NO_EVENT);
			
		}
		
		// Print Statistics
		
	}

	public static void main(String[] args) {
		testOrchestrator(1000);
	}

}
