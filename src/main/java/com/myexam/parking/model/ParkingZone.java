package com.myexam.parking.model;

import java.util.Objects;

public class ParkingZone {
	private String id;
	private String name;
	private Integer capacity;
	private double hourlyRate;
	private boolean isAvailable;

	public ParkingZone() {
	}

	public ParkingZone(String id, String name, Integer capacity, double hourlyRate, boolean isAvailable) {
		super();
		this.id = id;
		this.name = name;
		this.capacity = capacity;
		this.hourlyRate = hourlyRate;
		this.isAvailable = isAvailable;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public double getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(double hourlyRate) {
		this.hourlyRate = hourlyRate;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	@Override
	public int hashCode() {
		return Objects.hash(capacity, hourlyRate, id, isAvailable, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParkingZone other = (ParkingZone) obj;
		return Objects.equals(capacity, other.capacity)
				&& Double.doubleToLongBits(hourlyRate) == Double.doubleToLongBits(other.hourlyRate)
				&& Objects.equals(id, other.id) && isAvailable == other.isAvailable && Objects.equals(name, other.name);
	}

}
