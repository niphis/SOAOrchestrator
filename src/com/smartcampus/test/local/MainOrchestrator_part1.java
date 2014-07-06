package com.smartcampus.test.local;

import java.util.PriorityQueue;

import com.smartcampus.acc.local.ArtificialClimateControlService;
import com.smartcampus.naturalclimatesystem.local.NaturalClimateSystemService;
import com.smartcampus.paths.local.PathsService;
import com.smartcampus.roomusagedatabase.local.RoomUsageDatabaseService;
import com.smartcampus.test.local.Orchestrator_part1.Error;
import com.smartcampus.test.local.Orchestrator_part1.TimerEvent;
import com.smartcampus.test.local.Orchestrator_part1.WakeReason;

public class MainOrchestrator_part1 {

	private static PriorityQueue<TimerEvent> timers = new PriorityQueue<TimerEvent>();
	
	private static ArtificialClimateControlService acc = new ArtificialClimateControlService();;
	private static NaturalClimateSystemService nc = new NaturalClimateSystemService();;
	private static PathsService p = new PathsService();
	private static RoomUsageDatabaseService rud = new RoomUsageDatabaseService();
	
	static {
		
		// INITIAL EVENT SCHEDULING
		TimerEvent a = new TimerEvent(WakeReason.DAILY_WAKEUP);

		timers.add(a);
	}
	
	public static void main(String[] args) {
		Error res;
		Orchestrator_part1.setArtificialClimateControlService(acc);
		Orchestrator_part1.setNaturalClimateSystemService(nc);
		Orchestrator_part1.setPathsService(p);
		Orchestrator_part1.setRoomUsageDatabaseService(rud);
		do {
			res = Orchestrator_part1.wakeUp(timers);
		}
		while (res != Error.NO_EVENT);
	}
	
}
