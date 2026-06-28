package com.myexam.parking.view;

import java.util.List;

import com.myexam.parking.model.ParkingZone;

public interface ParkingView {
	void showAllParkingZones(List<ParkingZone> zones);

	void parkingZoneAdded(ParkingZone zone);

	void parkingZoneRemoved(ParkingZone zone);

	void showError(String message, ParkingZone zone);

}
