package com.smartcampus.test.soa;

import java.text.DecimalFormat;

import javax.xml.bind.JAXBElement;

import com.smartcampus.acc.ArtificialClimateControlPortType;
import com.smartcampus.acc.xsd.IndoorStatus;
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


public class UnitTestIncrement1 {
	static NaturalClimateSystemPortType s;

	public static double getPositiveRate(int err, int tot) {
		
		return 100 - (((double)err) / ((double)tot))*100;
	} 

	public static void testNCS(int c) {
		int weatherConditionError = 0;
		int openWindowError = 0;
		int closeWindowError = 0;

		for (int i=1; i<=c; i++) {
			s = new NaturalClimateSystem().getNaturalClimateSystemHttpSoap11Endpoint();
			ObjectFactory o = new ObjectFactory();
			Location l = new Location();
			String rid = "A12";
			int wid = 44;
			JAXBElement<String> roomId = o.createLocationRoomId(rid);

			l.setRoomId(roomId);
			l.setWindowsId(wid);
			
			System.out.println("Cicle " + i + "/" + c);
			
			if (s.openWindow(l) == false)
				openWindowError ++;
			if (s.closeWindow(l) == false)
				closeWindowError ++;
	
			WeatherCondition we = s.getWeatherCondition(l);
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

		System.out.println("Unit Test - WeatherCondition - Report");
		System.out.println("Tested methods");
		System.out.println("	openWindow()");
		System.out.println("	closeWindow()");
		System.out.println("	getWeatherCondition()");
		System.out.println("Average positive rate \t" + totAvgRate / countAvgRate  + "%");
	}

	public static void main(String[] args) {
		testNCS(10);



	}
}


