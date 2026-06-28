package com.myexam.parking.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class ParkingTicket {
	private String id;
	private String vehiclePlate;
	private String parkingZoneId;
	private LocalDateTime entryTime;
	private LocalDateTime exitTime;
	private boolean paid;
	private double totalCost;

	public ParkingTicket() {
	}

	public ParkingTicket(String id, String vehiclePlate, String parkingZoneId, LocalDateTime entryTime,
			LocalDateTime exitTime, boolean paid, double totalCost) {
		this.id = id;
		this.vehiclePlate = vehiclePlate;
		this.parkingZoneId = parkingZoneId;
		this.entryTime = entryTime;
		this.exitTime = exitTime;
		this.paid = paid;
		this.totalCost = totalCost;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVehiclePlate() {
		return vehiclePlate;
	}

	public void setVehiclePlate(String vehiclePlate) {
		this.vehiclePlate = vehiclePlate;
	}

	public String getParkingZoneId() {
		return parkingZoneId;
	}

	public void setParkingZoneId(String parkingZoneId) {
		this.parkingZoneId = parkingZoneId;
	}

	public LocalDateTime getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(LocalDateTime entryTime) {
		this.entryTime = entryTime;
	}

	public LocalDateTime getExitTime() {
		return exitTime;
	}

	public void setExitTime(LocalDateTime exitTime) {
		this.exitTime = exitTime;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	@Override
	public int hashCode() {
		return Objects.hash(entryTime, exitTime, id, paid, parkingZoneId, totalCost, vehiclePlate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParkingTicket other = (ParkingTicket) obj;
		return Objects.equals(entryTime, other.entryTime) && Objects.equals(exitTime, other.exitTime)
				&& Objects.equals(id, other.id) && paid == other.paid
				&& Objects.equals(parkingZoneId, other.parkingZoneId)
				&& Double.doubleToLongBits(totalCost) == Double.doubleToLongBits(other.totalCost)
				&& Objects.equals(vehiclePlate, other.vehiclePlate);
	}

}
