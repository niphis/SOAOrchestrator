/**
 * 
 */
package com.smartcampus.orchestrator.soa.test;

import java.util.PriorityQueue;

import com.smartcampus.orchestrator.soa.test.IntegrationTestIncrement2.TimerEvent;
import com.smartcampus.orchestrator.soa.test.IntegrationTestIncrement2.Error;
import com.smartcampus.orchestrator.soa.test.IntegrationTestIncrement2.WakeReason;

/**
 * @author Tom
 *
 */
public class MainIntegrationTestIncrement2 {

	/**
	 * @param args
	 */
	private static PriorityQueue<TimerEvent> timers = new PriorityQueue<TimerEvent>();

	public static void main(String[] args) {
		IntegrationTestStaticInput.setTestEnvironment();
		for (int i = 0; i < 100; i++) {
		timers.add(new TimerEvent(WakeReason.DAILY_WAKEUP));
		while (IntegrationTestIncrement2.wakeUp(timers) != Error.NO_EVENT);
		}
	}
}
