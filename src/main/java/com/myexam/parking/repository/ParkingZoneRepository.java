package com.myexam.parking.repository;

import java.util.List;

import com.myexam.parking.model.ParkingZone;

public interface ParkingZoneRepository {
	public List<ParkingZone> findAll();

	public ParkingZone findById(String id);

}
