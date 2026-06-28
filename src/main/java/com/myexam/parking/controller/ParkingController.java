package com.myexam.parking.controller;

import com.myexam.parking.model.ParkingTicket;
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

	public void allParkingTickets() {
		parkingView.showAllParkingTickets(parkingTicketRepository.findAll());
	}
	
	public void newParkingTicket(ParkingTicket ticket) {
		ParkingTicket existingTicket = parkingTicketRepository.findById(ticket.getId());
		if (existingTicket != null) {
			parkingView.showError("Already existing parking ticket with id " + ticket.getId(), existingTicket);
			return;
		}

		ParkingTicket activeTicket = parkingTicketRepository.findActiveTicketByVehiclePlate(ticket.getVehiclePlate());
		if (activeTicket != null) {
			parkingView.showError("Vehicle " + ticket.getVehiclePlate() + " already has an active ticket", ticket);
			return;
		}

		ParkingZone zone = parkingZoneRepository.findById(ticket.getParkingZoneId());
		if (zone != null) {
			long activeCount = parkingTicketRepository.countActiveTicketsByZoneId(ticket.getParkingZoneId());
			if (activeCount >= zone.getCapacity()) {
				parkingView.showError("Parking zone " + zone.getName() + " is full", ticket);
				return;
			}
		}

		try {
			parkingTicketRepository.save(ticket);
			parkingView.parkingTicketAdded(ticket);
		} catch (IllegalArgumentException e) {
			parkingView.showError(e.getMessage(), ticket);
		}
	}

	public void deleteParkingTicket(ParkingTicket ticket) {
		if (parkingTicketRepository.findById(ticket.getId()) == null) {
			parkingView.showError("No existing parking ticket with id " + ticket.getId(), ticket);
			return;
		}

		parkingTicketRepository.delete(ticket.getId());
		parkingView.parkingTicketRemoved(ticket);
	}

}
