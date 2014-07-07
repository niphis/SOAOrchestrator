/**
 * 
 */
package com.smartcampus.orchestrator.soa.test;

import java.util.PriorityQueue;

import com.smartcampus.acc.ArtificialClimateControl;
import com.smartcampus.acc.ArtificialClimateControlPortType;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystem;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystemPortType;
import com.smartcampus.orchestrator.soa.Orchestrator_part1;
import com.smartcampus.orchestrator.soa.Orchestrator_part1.Error;
import com.smartcampus.orchestrator.soa.Orchestrator_part1.TimerEvent;
import com.smartcampus.orchestrator.soa.Orchestrator_part1.WakeReason;
import com.smartcampus.paths.Paths;
import com.smartcampus.paths.PathsPortType;
import com.smartcampus.roomusagedatabase.RoomUsageDatabase;
import com.smartcampus.roomusagedatabase.RoomUsageDatabasePortType;

/**
 * @author Tom
 *
 */
public class IntegrationTestIncremental2 {

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
	
	public static double getPositiveRate(int err, int tot) {
		
		return 100 - (((double)err) / ((double)tot))*100;
	
	}
	
	public static void printStatistics(Error type, int err, int tot) {
		
		switch(type) {
		case DAILY_WAKEUP_ERROR:
			System.out.println("	Daily WakeUp Services [ROOM_USAGE_DB & PATH_SYSTEM]");
			System.out.println("	Average positive rate:");
			System.out.println("\t" + getPositiveRate(err,tot)  + "%");
			return;
		case CLIMATE_WAKEUP_ERROR:
			System.out.println("	Climate WakeUp Services [ARTIFICIAL & NATURAL REGULATION SYSTEM]");
			System.out.println("	Average positive rate:");
			System.out.println("\t" + getPositiveRate(err,tot)  + "%");
			return;
		}
	
	}
	
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
					break;
				case CLIMATE_WAKEUP:
					climateControlEventCounter++;
					break;
				}
				
				res = Orchestrator_part1.wakeUp(timers);
				
				// Counter on error
				
				switch (res) {
				case DAILY_WAKEUP_ERROR:
					dailyWakeupErrorCounter++;
					break;
				case CLIMATE_WAKEUP_ERROR:
					climateControlErrorCounter++;
					break;
				}
			}
			
			while (res != Error.NO_EVENT);
			
			// Print statistics
			System.out.println("(1) Integration Test - Orchestrator - Report");
			System.out.println("Tested functions");
			printStatistics(Error.DAILY_WAKEUP_ERROR,dailyWakeupErrorCounter,dailyWakeupEventCounter);
			printStatistics(Error.CLIMATE_WAKEUP_ERROR,climateControlErrorCounter,climateControlEventCounter);
			
		}
	}
	
	public static void main(String[] args) {
		testOrchestrator(1000);
	}
}
