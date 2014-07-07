package com.smartcampus.orchestrator.soa.test;

import java.util.List;

import com.smartcampus.naturalclimatesystem.NaturalClimateSystem;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystemPortType;
import com.smartcampus.naturalclimatesystem.xsd.WeatherCondition;
import com.smartcampus.acc.ArtificialClimateControl;
import com.smartcampus.acc.ArtificialClimateControlPortType;
import com.smartcampus.acc.xsd.IndoorStatus;
import com.smartcampus.luminancemanagement.LuminanceManagement;
import com.smartcampus.luminancemanagement.LuminanceManagementPortType;
import com.smartcampus.paths.Paths;
import com.smartcampus.paths.PathsPortType;
import com.smartcampus.paths.xsd.*;
import com.smartcampus.roomusagedatabase.RoomUsageDatabase;
import com.smartcampus.roomusagedatabase.RoomUsageDatabasePortType;
import com.smartcampus.roomusagedatabase.xsd.EventData;
import com.smartcampus.roomusagedatabase.xsd.EventList;
import com.smartcampus.servicesfacilities.ServicesAndFacilities;
import com.smartcampus.servicesfacilities.ServicesAndFacilitiesPortType;
import com.smartcampus.servicesfacilities.xsd.FoodList;



public class UnitTestIncrement1 {
	private static NaturalClimateSystemPortType nc;
	private static ArtificialClimateControlPortType acc;
	private static LuminanceManagementPortType lm;
	private static PathsPortType p;
	private static RoomUsageDatabasePortType rud;
	private static ServicesAndFacilitiesPortType sf;

	static String ROOM = "ADInform1";

	public static double getPositiveRate(int err, int tot) {
		return 100 - (((double) err) / ((double) tot)) * 100;
	}

	public static void printSteps(int i, int c) {
		if (i == 1)
			System.out.print("Round: " + i + "/" + c);
		else if ((i < c) && (i%100)==0)
			System.out.print(".");
		else if (i == c)
			System.out.println(" " + i + "/" + c);
	
			
	}

	public static void testNCS(int c) {
		int weatherConditionError = 0;
		int openWindowError = 0;
		int closeWindowError = 0;

		nc = new NaturalClimateSystem()
		.getNaturalClimateSystemHttpSoap11Endpoint();

		for (int i = 1; i <= c; i++) {
			printSteps(i, c);
			
			if (nc.openWindow(null) == false)
				openWindowError++;
			if (nc.closeWindow(null) == false)
				closeWindowError++;

			WeatherCondition we = nc.getWeatherCondition(null);
			if (we == null) {
				weatherConditionError++;
			}
		}

		double totAvgRate = 0;
		int countAvgRate = 0;
		totAvgRate += getPositiveRate(openWindowError, c);
		countAvgRate++;
		totAvgRate += getPositiveRate(closeWindowError, c);
		countAvgRate++;
		totAvgRate += getPositiveRate(weatherConditionError, c);
		countAvgRate++;

		System.out
		.println("Tested methods:  openWindow(); closeWindow(); getWeatherCondition()");
		System.out.println("Average positive rate \t" + totAvgRate
				/ countAvgRate + "%");
	}

	public static void testACC(int c) {
		int indoorStatusError = 0;
		acc = new ArtificialClimateControl()
		.getArtificialClimateControlHttpSoap11Endpoint();

		for (int i = 1; i <= c; i++) {
			printSteps(i, c);
			IndoorStatus status = acc.getIndoorStatus(ROOM);

			if (status == null) {
				indoorStatusError++;
			}
		}
		double totAvgRate = 0;
		int countAvgRate = 0;
		totAvgRate += getPositiveRate(indoorStatusError, c);
		countAvgRate++;

		System.out.println("Tested methods: getIndoorStatus();");
		System.out.println("Average positive rate \t" + totAvgRate
				/ countAvgRate + "%");
	}

	public static void testLuminance(int c) {

		int indoorLuminanceError = 0;
		int outdoorLuminanceError = 0;
		double totAvgRate = 0;
		int countAvgRate = 0;

		lm = new LuminanceManagement().getLuminanceManagementHttpSoap11Endpoint();
		for (int i = 1; i <= c; i++) {
			printSteps(i, c);
			lm.getIndoorLuminance(ROOM);

			float indoorLuminance = lm.getIndoorLuminance(ROOM);

			if (indoorLuminance == -1) {
				indoorLuminanceError++;
			}
			float outdoorLuminance = lm.getOutdoorLuminance(ROOM);
			if (outdoorLuminance == -1) {
				outdoorLuminanceError++;
			}
			totAvgRate += getPositiveRate(indoorLuminanceError, c);
			countAvgRate++;
			totAvgRate += getPositiveRate(outdoorLuminanceError, c);
			countAvgRate++;
		}
		System.out.println("Tested methods:  getIndoorLuminance(); getOutdoorLuminance();");
		System.out.println("Average positive rate \t" + totAvgRate / countAvgRate + "%");
	}

	public static void testPaths(int c) {
		int getPathsError = 0;
		int getPathAttributesError = 0;
		double totAvgRate = 0;
		int countAvgRate = 0;

		p = new Paths().getPathsHttpSoap11Endpoint();

		for (int i = 1; i <= c; i++) {
			printSteps(i, c);

			List<Integer> pathsToRoom = p.getPaths(ROOM);
			if (pathsToRoom == null) {
				getPathsError++;
			} else {
				for (int s = 0; s < pathsToRoom.size(); s++) {
					Integer pathId = pathsToRoom.get(s);

					PathData pd = p.getPathAttributes(pathId);
					if (pd == null) {
						getPathAttributesError++;
					}
				}
			}
			totAvgRate += getPositiveRate(getPathsError, c);
			countAvgRate++;
			totAvgRate += getPositiveRate(getPathAttributesError, c);
			countAvgRate++;

		}
		System.out.println("Tested methods:  getPaths(); getPathAttributes();");
		System.out.println("Average positive rate \t" + totAvgRate / countAvgRate + "%");

	}

	public static void testRUD(int c) {
		int searchEventError = 0;
		int createEventError = 0;
		int modifyEventError = 0;
		int getRoomCharacteristicsError = 0;
		double totAvgRate = 0;
		int countAvgRate = 0;

		rud = new RoomUsageDatabase().getRoomUsageDatabaseHttpSoap11Endpoint();

		for (int i = 1; i <= c; i++) {
			printSteps(i, c);

			EventList events = rud.searchEvent(null);

			if (events == null) {
				searchEventError++;
			}

			if (rud.createEvent(new EventData()) == false) {
				createEventError++;
			}

			if (rud.modifyEvent(new EventData()) == false) {
				modifyEventError++;
			}

			if (rud.getRoomCharacteristics(ROOM) == null) {
				getRoomCharacteristicsError++;
			}

			totAvgRate += getPositiveRate(searchEventError, c);
			countAvgRate++;
			totAvgRate += getPositiveRate(createEventError, c);
			countAvgRate++;
			totAvgRate += getPositiveRate(modifyEventError, c);
			countAvgRate++;
			totAvgRate += getPositiveRate(getRoomCharacteristicsError, c);
			countAvgRate++;

		}
		System.out.println("Tested methods:  searchEvent(); "
				+ "createEvent(); "
				+ "modifyEvent(); getRoomCharacteristics();");
		System.out.println("Average positive rate \t" + totAvgRate
				/ countAvgRate + "%");

	}

	public static void testServicesAndFacilities(int c) {
		int getFoodStocksError = 0;
		int placeFoodOrderError = 0;
		int setCleaningFrequencyError = 0;
		double totAvgRate = 0;
		int countAvgRate = 0;

		sf = new ServicesAndFacilities().getServicesAndFacilitiesHttpSoap11Endpoint();

		for (int i = 1; i <= c; i++) {
			printSteps(i, c);

			FoodList p = sf.getFoodStocks();

			if (p == null) {
				getFoodStocksError++;
			}

			if (sf.placeFoodOrder(null) == false) {
				placeFoodOrderError++;
			}

			if (sf.setCleaningFrequency(2) == false) {
				setCleaningFrequencyError++;
			}

			totAvgRate += getPositiveRate(getFoodStocksError, c);
			countAvgRate++;
			totAvgRate += getPositiveRate(placeFoodOrderError, c);
			countAvgRate++;
			totAvgRate += getPositiveRate(setCleaningFrequencyError, c);
			countAvgRate++;
		}
		System.out.println("Tested methods:  getFoodStocks(); placeFoodOrder(); setCleaningFrequency()");
		System.out.println("Average positive rate \t" + totAvgRate / countAvgRate + "%");
	}

	public static void main(String[] args) {
		int runs = 10000;
		
		System.out.println("Unit Tests");

		System.out.println("\n   NaturalClimateSystem - Report");
		testNCS(runs);

		System.out.println("\n   ArtificialClimateContro - Report");
		testACC(runs);

		System.out.println("\n   Luminance - Report");
		testLuminance(runs);

		System.out.println("\n   Paths - Report");
		testPaths(runs);

		System.out.println("\n   RUD - Report");
		testRUD(runs);

		System.out.println("\n   ServicesAndFacilities - Report");
		testServicesAndFacilities(runs);
	}

}
