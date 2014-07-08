package com.smartcampus.orchestrator.soa.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.smartcampus.acc.xsd.IndoorStatus;
import com.smartcampus.naturalclimatesystem.xsd.WeatherCondition;
import com.smartcampus.paths.xsd.PathData;
import com.smartcampus.roomusagedatabase.xsd.EventData;

public class IntegrationTestStaticInput {
	
	public static EventData[] events = new EventData[3];
	
	public static List<EventData> evs1 = new ArrayList<EventData>();
	
	public static List<EventData> evs2 = new ArrayList<EventData>();

	
	public static List<EventData> evs3 = new ArrayList<EventData>();
	
	public static LinkedList<List<EventData>> eventListTest = 
		new LinkedList<List<EventData>>();
	
	public static int pathID[] = {1,2,3};
	
	public static List<Integer> pr1 = new ArrayList<Integer>();
	
	public static List<Integer> pr2 = new ArrayList<Integer>();
	
	public static List<Integer> pr3 = new ArrayList<Integer>();

	public static LinkedList<List<Integer>> pathListTest = 
			new LinkedList<List<Integer>>();
		
	public static LinkedList<PathData> pathDataListTest = 
			new LinkedList<PathData>();
	
	public static LinkedList<Integer> componentsNumberListTest = 
			new LinkedList<Integer>();
	
	public static LinkedList<WeatherCondition> weatherConditionListTest = 
			new LinkedList<WeatherCondition>();
	
	public static LinkedList<IndoorStatus> indoorStatusListTest = 
			new LinkedList<IndoorStatus>();
	
	public static LinkedList<Boolean> rangesInListTest = 
			new LinkedList<Boolean>();

	public static LinkedList<Boolean> naturalClimateListTest = 
			new LinkedList<Boolean>();
	
	public static LinkedList<Boolean> errorListTest = 
			new LinkedList<Boolean>();
	
	public static boolean error() {
		boolean ret = errorListTest.pollFirst();
		errorListTest.offerLast(ret);
		return ret;
	
	}
	
	public static void setTestEnvironment() {
		
		pathListTest.add(pr1);
		pathListTest.add(pr2);
		pathListTest.add(pr3);
		
		float temp,hum,wind,noise,poll;
		
		componentsNumberListTest.add(2);
		componentsNumberListTest.add(5);
		componentsNumberListTest.add(4);
		componentsNumberListTest.add(0);
		componentsNumberListTest.add(3);
		componentsNumberListTest.add(1);
		
		for (int i = 0; i < 5; i++ ) {
			temp = 10 + (float) Math.random() * 50;
			hum = 1 + (float) Math.random() * 100;
			wind = 1 + (float) Math.random() * 50;
			noise = 1 + (float) Math.random() * 100;
			poll = 1 + (float) Math.random() * 1000;
		
			WeatherCondition w = new WeatherCondition();
			
			w.setTemperature(temp);
			w.setHumidity(hum);
			w.setWind(wind);
			w.setNoiseLevel(noise);
			//w.setCo2level(poll);
			
			if (i%2 != 0)
				weatherConditionListTest.add(null);
			weatherConditionListTest.add(w);
		}
		
		pathDataListTest.add(null);
		pathDataListTest.add(null);
		pathDataListTest.add(new PathData());
		pathDataListTest.add(null);
		pathDataListTest.add(new PathData());
		pathDataListTest.add(null);
		pathDataListTest.add(new PathData());
		pathDataListTest.add(new PathData());
		pathDataListTest.add(new PathData());
		
		for (int i=0; i<8; i++) {		
			IndoorStatus is = new IndoorStatus();
				
			is.setTemperature(10 + (float)0.1*i * 20);
			is.setHumidity(20 + (float)0.1 * 60);
			is.setFanSpeed(0.0f);
			is.setTimer(0);
			//is.setCo2level(100 + (float) Math.random() * 1000);
			//is.setRoomID(aRoomId);
			
			indoorStatusListTest.add(is);
			if (i%4 == 0)
				indoorStatusListTest.add(null);
		}
		
		errorListTest.add(false);
		errorListTest.add(false);
		errorListTest.add(true);
		errorListTest.add(true);
		errorListTest.add(true);
		errorListTest.add(true);
		errorListTest.add(true);
		errorListTest.add(false);
		errorListTest.add(true);
		errorListTest.add(false);
		errorListTest.add(true);
		errorListTest.add(false);
		errorListTest.add(true);
		errorListTest.add(true);
		errorListTest.add(false);
		errorListTest.add(true);
		errorListTest.add(false);
		
		rangesInListTest.add(false);
		rangesInListTest.add(false);
		rangesInListTest.add(false);
		rangesInListTest.add(true);
		rangesInListTest.add(true);
		rangesInListTest.add(true);
		rangesInListTest.add(false);
		rangesInListTest.add(false);
		rangesInListTest.add(true);
		rangesInListTest.add(true);
		rangesInListTest.add(false);
		rangesInListTest.add(true);
		rangesInListTest.add(false);
		rangesInListTest.add(false);
		rangesInListTest.add(false);
		
		naturalClimateListTest.add(false);
		naturalClimateListTest.add(false);
		naturalClimateListTest.add(true);
		naturalClimateListTest.add(true);
		naturalClimateListTest.add(true);
		naturalClimateListTest.add(false);
		naturalClimateListTest.add(true);
		naturalClimateListTest.add(false);
		naturalClimateListTest.add(true);
		naturalClimateListTest.add(false);
		naturalClimateListTest.add(false);
		naturalClimateListTest.add(true);
		naturalClimateListTest.add(false);
		naturalClimateListTest.add(false);
		naturalClimateListTest.add(false);
		naturalClimateListTest.add(false);
		naturalClimateListTest.add(true);
		naturalClimateListTest.add(true);
		naturalClimateListTest.add(true);
		naturalClimateListTest.add(false);
		naturalClimateListTest.add(false);
		naturalClimateListTest.add(false);
		naturalClimateListTest.add(false);
		
		
		events[0] = new EventData();
		//events[0].setRoomId("ADInform1");
		events[0].setDate(System.currentTimeMillis());
		events[0].setStartTime(System.currentTimeMillis());
		events[0].setEndTime(System.currentTimeMillis() + 1000*60*60*2);
		events[0].setExpectedPeople(45);
		// events[0].setEventType("Conference");
		
		events[1] = new EventData();
		//events[1].setRoomId("Aula Magna");
		events[1].setDate(System.currentTimeMillis());
		events[1].setStartTime(System.currentTimeMillis() + 1000*60*60*3);
		events[1].setEndTime(System.currentTimeMillis() + 1000*60*60*5);
		events[1].setExpectedPeople(200);
		//events[1].setEventType("Degree");
		
		events[2] = new EventData();
		//events[2].setRoomId("ADInform1");
		events[2].setDate(System.currentTimeMillis() + 1000*60*60*24*2);
		events[2].setStartTime(System.currentTimeMillis() + 1000*60*60*24*2);
		events[2].setEndTime(System.currentTimeMillis() + 1000*60*60*24*2 + 1000*60*60*3);
		events[2].setExpectedPeople(75);
		//events[2].setEventType("Conference");
		evs1.add(events[0]);
		
		evs2.add(events[0]);
		evs2.add(events[1]);
		evs3.add(events[0]);
		evs3.add(events[1]);
		evs3.add(events[2]);	
		
		eventListTest.add(evs1);
		eventListTest.add(evs2);
		eventListTest.add(evs3);
		
		pr1.add(pathID[0]);
		pr2.add(pathID[0]);
		pr2.add(pathID[1]);
		pr3.add(pathID[0]);
		pr3.add(pathID[1]);
		pr3.add(pathID[2]);
	}
}
