package com.myexam.parking.repository;

import java.util.List;

import com.myexam.parking.model.ParkingTicket;

public interface ParkingTicketRepository {
	public List<ParkingTicket> findAll();

	public ParkingTicket findById(String id);

	public void save(ParkingTicket parkingTicket);

	public void delete(String id);

}
