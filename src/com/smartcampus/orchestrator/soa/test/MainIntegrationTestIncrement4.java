/**
 * 
 */
package com.smartcampus.orchestrator.soa.test;

import java.util.PriorityQueue;

import com.smartcampus.acc.ArtificialClimateControl;
import com.smartcampus.acc.ArtificialClimateControlPortType;
import com.smartcampus.luminancemanagement.LuminanceManagement;
import com.smartcampus.luminancemanagement.LuminanceManagementPortType;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystem;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystemPortType;
import com.smartcampus.orchestrator.soa.Orchestrator;
import com.smartcampus.orchestrator.soa.Orchestrator.Error;
import com.smartcampus.orchestrator.soa.Orchestrator.TimerEvent;
import com.smartcampus.orchestrator.soa.Orchestrator.WakeReason;
import com.smartcampus.paths.Paths;
import com.smartcampus.paths.PathsPortType;
import com.smartcampus.roomusagedatabase.RoomUsageDatabase;
import com.smartcampus.roomusagedatabase.RoomUsageDatabasePortType;
import com.smartcampus.servicesfacilities.ServicesAndFacilities;
import com.smartcampus.servicesfacilities.ServicesAndFacilitiesPortType;

/**
 * @author Tom
 *
 */
public class MainIntegrationTestIncrement4 {

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
	private static ServicesAndFacilitiesPortType sf =  new ServicesAndFacilities()
		.getServicesAndFacilitiesHttpSoap11Endpoint();
	
	public static double getPositiveRate(int err, int tot) {
		
		return 100 - (((double)err) / ((double)tot))*100;
	
	} 
	
	public static void printStatistics(Error type, int err, int tot) {
		
		switch(type) {
		case DAILY_WAKEUP_ERROR:
			System.out.println("	Daily WakeUp Services [ROOM USAGE DB & PATH SYSTEM]");
			System.out.println("	Average positive rate:");
			System.out.println("\t" + getPositiveRate(err,tot)  + "%");
			return;
		case CLIMATE_WAKEUP_ERROR:
			System.out.println("	Climate WakeUp Services [ARTIFICIAL & NATURAL REGULATION SYSTEM]");
			System.out.println("	Average positive rate:");
			System.out.println("\t" + getPositiveRate(err,tot)  + "%");
			return;
		case LUMINANCE_WAKEUP_ERROR:
			System.out.println("	Luminance Management Services [LUMINANCE MANAGEMENT SYSTEM]");
			System.out.println("	Average positive rate:");
			System.out.println("\t" + getPositiveRate(err,tot)  + "%");
			return;
		case FOOD_WAKEUP_ERROR:
			System.out.println("	Food Facilities Services [SERVICES AND FACILITIES SYSTEM]");
			System.out.println("	Average positive rate:");
			System.out.println("\t" + getPositiveRate(err,tot)  + "%");
			return;
		case CLEANING_WAKEUP_ERROR:
			System.out.println("	Cleaning Facilities Services [SERVICES AND FACILITIES SYSTEM]");
			System.out.println("	Average positive rate:");
			System.out.println("\t" + getPositiveRate(err,tot)  + "%");
			return;	
		}
	}
	
	private static void testOrchestrator(int maxIterations) {
		
		int climateControlErrorCounter = 0;
		int climateControlEventCounter = 0;
		
		int dailyWakeupErrorCounter = 0;
		int dailyWakeupEventCounter = 0;
		
		int luminanceManagementErrorCounter = 0;
		int luminanceManagementEventCounter = 0;
		
		int foodWakeupErrorCounter = 0;
		int foodWakeupEventCounter = 0;
		
		int cleanWakeupErrorCounter = 0;
		int cleanWakeupEventCounter = 0;
		
		for (int i=0; i<maxIterations; i++) {
		
			// INITIAL EVENT SCHEDULING
			
			Error res = Error.NO_EVENT;
			TimerEvent a = new TimerEvent(WakeReason.DAILY_WAKEUP);

			timers.add(a);
			Orchestrator.setArtificialClimateControlPortType(acc);
			Orchestrator.setNaturalClimateSystemPortType(nc);
			Orchestrator.setPathsPortType(p);
			Orchestrator.setRoomUsageDatabasePortType(rud);
			Orchestrator.setLuminanceManagementPortType(lm);
			Orchestrator.setServicesAndFacilitiesPortType(sf);
			
			do {
				
				if(!timers.isEmpty()) {
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
					case FOOD_WAKEUP:
						foodWakeupEventCounter++;
						break;
					case CLEANING_WAKEUP:
						cleanWakeupEventCounter++;
						break;
					}
				}
		
				res = Orchestrator.wakeUp(timers);
				
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
				case FOOD_WAKEUP_ERROR:
					foodWakeupErrorCounter++;
					break;
				case CLEANING_WAKEUP_ERROR:
					cleanWakeupErrorCounter++;
					break;
				}
			}
			
			while (res != Error.NO_EVENT);
		
		}
		
		// Print statistics
		System.out.println("(3) Integration Test - Orchestrator - Report");
		System.out.println("Tested functions");
		printStatistics(Error.DAILY_WAKEUP_ERROR,dailyWakeupErrorCounter,dailyWakeupEventCounter);
		printStatistics(Error.CLIMATE_WAKEUP_ERROR,climateControlErrorCounter,climateControlEventCounter);
		printStatistics(Error.LUMINANCE_WAKEUP_ERROR,luminanceManagementErrorCounter,luminanceManagementEventCounter);
		printStatistics(Error.FOOD_WAKEUP_ERROR,foodWakeupErrorCounter,foodWakeupEventCounter);
		printStatistics(Error.CLEANING_WAKEUP_ERROR,cleanWakeupErrorCounter,cleanWakeupEventCounter);
	}
	
	public static void main(String[] args) {
		testOrchestrator(1000);
	}

}
