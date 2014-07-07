package com.smartcampus.orchestrator.soa;


import javax.xml.bind.JAXBElement;

import com.smartcampus.acc.ArtificialClimateControl;
import com.smartcampus.acc.ArtificialClimateControlPortType;
import com.smartcampus.acc.xsd.IndoorStatus;
import com.smartcampus.luminancemanagement.LuminanceManagementPortType;

import com.smartcampus.naturalclimatesystem.NaturalClimateSystem;
import com.smartcampus.naturalclimatesystem.NaturalClimateSystemPortType;
import com.smartcampus.naturalclimatesystem.xsd.Location;
import com.smartcampus.naturalclimatesystem.xsd.ObjectFactory;
import com.smartcampus.naturalclimatesystem.xsd.WeatherCondition;

import com.smartcampus.paths.PathsPortType;
import com.smartcampus.paths.xsd.PathComponent;
import com.smartcampus.paths.xsd.PathData;
import com.smartcampus.roomusagedatabase.RoomUsageDatabasePortType;
import com.smartcampus.roomusagedatabase.xsd.EventData;
import com.smartcampus.servicesfacilities.ServicesAndFacilitiesPortType;


public class UnitTestIncrement1 {
	private static ArtificialClimateControlPortType acc;
	private static NaturalClimateSystemPortType nc;
	private static PathsPortType p;
	private static RoomUsageDatabasePortType rud;
	private static LuminanceManagementPortType lm;
	private static ServicesAndFacilitiesPortType sf;

	public static double getPositiveRate(int err, int tot) {
		return 100 - (((double)err) / ((double)tot))*100;
	} 

	
	public static void printSteps(int i, int c){
		if (i==1) 
			System.out.print("Round: " + i + "/" + c);
		else if (i<c)
			System.out.print(".");
		else
			System.out.println(" " + i + "/" + c);
	}
	
	public static void testNCS(int c) {
		int weatherConditionError = 0;
		int openWindowError = 0;
		int closeWindowError = 0;
		
		nc = new NaturalClimateSystem().getNaturalClimateSystemHttpSoap11Endpoint();
		
		for (int i=1; i<=c; i++) {
			printSteps(i, c);
			
			ObjectFactory o = new ObjectFactory();
			Location l = new Location();
			String rid = "A12";
			int wid = 44;
			JAXBElement<String> roomId = o.createLocationRoomId(rid);

			l.setRoomId(roomId);
			l.setWindowsId(wid);
			
			
			
			if (nc.openWindow(l) == false)
				openWindowError ++;
			if (nc.closeWindow(l) == false)
				closeWindowError ++;
	
			WeatherCondition we = nc.getWeatherCondition(l);
			if (we == null) {
				weatherConditionError ++;
			}
		}

		double totAvgRate = 0;
		int countAvgRate = 0;
		totAvgRate += getPositiveRate(openWindowError,c);
		countAvgRate++;
		totAvgRate += getPositiveRate(closeWindowError,c);
		countAvgRate++;
		totAvgRate += getPositiveRate(weatherConditionError,c);
		countAvgRate++;

		
		System.out.println("Tested methods:  openWindow(); closeWindow(); getWeatherCondition()");
		System.out.println("Average positive rate \t" + totAvgRate / countAvgRate  + "%");
	}

	public static void testACC(int c) {
		int indoorStatusError = 0;
		acc = new ArtificialClimateControl().getArtificialClimateControlHttpSoap11Endpoint();
		
		for (int i=1; i<=c; i++) {
			printSteps(i, c);
			IndoorStatus status = acc.getIndoorStatus("ADInform1");
			
			if (status == null) {
				indoorStatusError++;
			}
		}
		double totAvgRate = 0;
		int countAvgRate = 0;
		totAvgRate += getPositiveRate(indoorStatusError,c);
		countAvgRate++;
		
		System.out.println("Tested methods: getIndoorStatus();");
		System.out.println("Average positive rate \t" + totAvgRate / countAvgRate  + "%");
	}
	
	
	public static void main(String[] args) {
		System.out.println("Unit Test");
		System.out.println("\n   NaturalClimateSystem - Report");
		testNCS(100);
		System.out.println("\n   ArtificialClimateContro - Report");
		testACC(100);



	}
}


