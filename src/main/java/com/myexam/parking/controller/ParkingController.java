package com.myexam.parking.controller;

import com.myexam.parking.model.ParkingZone;
import com.myexam.parking.repository.ParkingTicketRepository;
import com.myexam.parking.repository.ParkingZoneRepository;
import com.myexam.parking.view.ParkingView;

public class ParkingController {
	private ParkingView parkingView;
	private ParkingZoneRepository parkingZoneRepository;
	private ParkingTicketRepository parkingTicketRepository;

	public ParkingController(ParkingView parkingView, ParkingZoneRepository parkingZoneRepository,
			ParkingTicketRepository parkingTicketRepository) {
		this.parkingView = parkingView;
		this.parkingZoneRepository = parkingZoneRepository;
		this.parkingTicketRepository = parkingTicketRepository;
	}

	public void allParkingZones() {
		parkingView.showAllParkingZones(parkingZoneRepository.findAll());
	}

	public void newParkingZone(ParkingZone parkingZone) {
		ParkingZone existingZone = parkingZoneRepository.findById(parkingZone.getId());
		if (existingZone != null) {
			parkingView.showError("Already existing parking zone with id " + parkingZone.getId(), existingZone);
			return;
		}

		try {
			parkingZoneRepository.save(parkingZone);
			parkingView.parkingZoneAdded(parkingZone);

		} catch (IllegalArgumentException e) {
			parkingView.showError(e.getMessage(), parkingZone);
		}
	}

	public void deleteParkingZone(ParkingZone parkingZone) {
		if (parkingZoneRepository.findById(parkingZone.getId()) == null) {
			parkingView.showError("No existing parking zone with id " + parkingZone.getId(), parkingZone);
			return;
		}

		parkingZoneRepository.delete(parkingZone.getId());
		parkingView.parkingZoneRemoved(parkingZone);
	}

}
