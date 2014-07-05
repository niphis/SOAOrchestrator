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
import com.smartcampus.servicesfacilities.ServicesAndFacilities;
import com.smartcampus.servicesfacilities.ServicesAndFacilitiesPortType;
import com.smartcampus.test.soa.Orchestrator.TimerEvent;
import com.smartcampus.test.soa.Orchestrator.WakeReason;

public class MainOrchestrator {
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
		int res;
		Orchestrator.setArtificialClimateControlPortType(acc);
		Orchestrator.setNaturalClimateSystemPortType(nc);
		Orchestrator.setPathsPortType(p);
		Orchestrator.setRoomUsageDatabasePortType(rud);
		Orchestrator.setLuminanceManagementPortType(lm);
		Orchestrator.setServicesAndFacilitiesPortType(sf);
		System.out.println("[ORCH] Start handling of events... ");
		while ((res = Orchestrator.wakeUp(timers)) != -1);
		System.out.println("[ORCH] Handling of events completed.");

	}

}
