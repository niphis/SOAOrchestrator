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

public class MainOrchestrator_part1 {

	private static PriorityQueue<TimerEvent> timers = new PriorityQueue<TimerEvent>();
	
	private static ArtificialClimateControlPortType acc = new ArtificialClimateControl()
		.getArtificialClimateControlHttpSoap11Endpoint();
	private static NaturalClimateSystemPortType nc = new NaturalClimateSystem()
		.getNaturalClimateSystemHttpSoap11Endpoint();
	private static PathsPortType p = new Paths().getPathsHttpSoap11Endpoint();
	private static RoomUsageDatabasePortType rud = new RoomUsageDatabase().getRoomUsageDatabaseHttpSoap11Endpoint();

	static {
		// INITIAL EVENT SCHEDULING
		TimerEvent a = new TimerEvent(WakeReason.DAILY_WAKEUP);

		timers.add(a);
	}
	
	public static void main(String[] args) {
		Error res;
		Orchestrator_part1.setArtificialClimateControlPortType(acc);
		Orchestrator_part1.setNaturalClimateSystemPortType(nc);
		Orchestrator_part1.setPathsPortType(p);
		Orchestrator_part1.setRoomUsageDatabasePortType(rud);
		System.out.println("[ORCH] Start handling of events... ");
		
		do {
			res = Orchestrator_part1.wakeUp(timers);
		}
		while (res != Error.NO_EVENT);
		
		System.out.println("[ORCH] Handling of events completed.");

	}
	
}