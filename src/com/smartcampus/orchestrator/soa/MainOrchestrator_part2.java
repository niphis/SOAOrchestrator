package com.smartcampus.orchestrator.soa;

import java.util.PriorityQueue;

import com.smartcampus.acc.ArtificialClimateControl;
import com.smartcampus.acc.ArtificialClimateControlPortType;
import com.smartcampus.luminancemanagement.LuminanceManagement;
import com.smartcampus.luminancemanagement.LuminanceManagementPortType;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystem;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystemPortType;
import com.smartcampus.orchestrator.soa.Orchestrator_part2.Error;
import com.smartcampus.orchestrator.soa.Orchestrator_part2.TimerEvent;
import com.smartcampus.orchestrator.soa.Orchestrator_part2.WakeReason;
import com.smartcampus.paths.Paths;
import com.smartcampus.paths.PathsPortType;
import com.smartcampus.roomusagedatabase.RoomUsageDatabase;
import com.smartcampus.roomusagedatabase.RoomUsageDatabasePortType;

public class MainOrchestrator_part2 {

	private static PriorityQueue<TimerEvent> timers = new PriorityQueue<TimerEvent>();
	
	private static ArtificialClimateControlPortType acc = new ArtificialClimateControl()
		.getArtificialClimateControlHttpSoap11Endpoint();
	private static NaturalClimateSystemPortType nc = new NaturalClimateSystem()
		.getNaturalClimateSystemHttpSoap11Endpoint();
	private static PathsPortType p = new Paths().getPathsHttpSoap11Endpoint();
	private static RoomUsageDatabasePortType rud = new RoomUsageDatabase().getRoomUsageDatabaseHttpSoap11Endpoint();
	private static LuminanceManagementPortType lm = new LuminanceManagement()
		.getLuminanceManagementHttpSoap11Endpoint();
	static {
		// INITIAL EVENT SCHEDULING
		TimerEvent a = new TimerEvent(WakeReason.DAILY_WAKEUP);

		timers.add(a);
	}
	
	public static void main(String[] args) {
		Error res;
		Orchestrator_part2.setArtificialClimateControlPortType(acc);
		Orchestrator_part2.setNaturalClimateSystemPortType(nc);
		Orchestrator_part2.setPathsPortType(p);
		Orchestrator_part2.setRoomUsageDatabasePortType(rud);
		Orchestrator_part2.setLuminanceManagementPortType(lm);
		System.out.println("[ORCH] Start handling of events... ");
		
		do {
			res = Orchestrator_part2.wakeUp(timers);
		}
		while (res != Error.NO_EVENT);
		
		System.out.println("[ORCH] Handling of events completed.");

	}
}
