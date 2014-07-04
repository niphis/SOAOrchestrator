package com.smartcampus.luminancemanagement.local;

import java.util.ListIterator;

public class LuminanceManagementService implements LuminanceManagementSystem {
	private LMClient _user;
	private LuminanceManagementSystem _system;

	public void calibrateSpotlight(RoomSettings aRS) {
		
		System.out.println("Calibrate Spotlight");
		System.out.println("RoomID: " + aRS.getRoomId());
		
		Spotlight spotlights[] = aRS.getSpotlights();
		for (Spotlight s : spotlights) {
			System.out.println("Spotlight (" + s.getId() + ") luminance = " +
					s.getLuminance());
		}
		
	}

	public void regulateBlind(RoomSettings aRS) {
		
		System.out.println("Regulate Blind");
		System.out.println("RoomID: " + aRS.getRoomId());
		
		Window windows[] = aRS.getWindows();
		for (Window w : windows) {
			System.out.println("Window (" + w.getId() + ") angle = " +
					w.getAngle());
		}
		
	}

	public float getIndoorLuminance(String aRoomId) {

		System.out.println("Get Indoor Luminance");
		return (float) Math.random() * 100;
		
	}

	public float getOutdoorLuminance(String aRoomId) {

		System.out.println("Get Outdoor Luminance");
		return (float) Math.random() * 100;
		
	}
}