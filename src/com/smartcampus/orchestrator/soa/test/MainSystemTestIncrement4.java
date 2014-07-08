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

public class MainSystemTestIncrement4 {
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
	
	static {
		// INITIAL EVENT SCHEDULING
		TimerEvent a = new TimerEvent(WakeReason.DAILY_WAKEUP);

		timers.add(a);
	}
	
	public static void main(String[] args) {
		Error res;
		Orchestrator.setArtificialClimateControlPortType(acc);
		Orchestrator.setNaturalClimateSystemPortType(nc);
		Orchestrator.setPathsPortType(p);
		Orchestrator.setRoomUsageDatabasePortType(rud);
		Orchestrator.setLuminanceManagementPortType(lm);
		Orchestrator.setServicesAndFacilitiesPortType(sf);
		
		System.out.println("[ORCH] Start handling of events... ");
		
		do {
			res = Orchestrator.wakeUp(timers);
		}
		while (res != Error.NO_EVENT);
		
		System.out.println("[ORCH] Handling of events completed.");

	}

}