package com.myexam.parking.controller;

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

}
