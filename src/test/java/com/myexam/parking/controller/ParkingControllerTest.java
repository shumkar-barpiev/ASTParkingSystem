package com.myexam.parking.controller;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.myexam.parking.model.ParkingZone;
import com.myexam.parking.repository.*;
import com.myexam.parking.view.ParkingView;

public class ParkingControllerTest {

	@Mock
	private ParkingZoneRepository parkingZoneRepository;

	@Mock
	private ParkingTicketRepository parkingTicketRepository;

	@Mock
	private ParkingView parkingView;

	@InjectMocks
	private ParkingController parkingController;

	private AutoCloseable closeable;

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testAllParkingZones() {
		List<ParkingZone> zones = asList(new ParkingZone("1", "ParkingA", 100, 2.50, true));

		when(parkingZoneRepository.findAll()).thenReturn(zones);

		parkingController.allParkingZones();

		verify(parkingView).showAllParkingZones(zones);
	}

	@Test
	public void testNewParkingZoneWhenZoneDoesNotAlreadyExist() {
		ParkingZone zone = new ParkingZone("1", "ParkingA", 100, 2.50, true);

		when(parkingZoneRepository.findById("1")).thenReturn(null);

		parkingController.newParkingZone(zone);

		InOrder inOrder = inOrder(parkingZoneRepository, parkingView);
		inOrder.verify(parkingZoneRepository).save(zone);
		inOrder.verify(parkingView).parkingZoneAdded(zone);
	}

	@Test
	public void testNewParkingZoneWhenZoneAlreadyExists() {
		ParkingZone zoneToAdd = new ParkingZone("1", "ParkingNew", 50, 3.0, true);
		ParkingZone existingZone = new ParkingZone("1", "ParkingExisting", 100, 2.50, true);

		when(parkingZoneRepository.findById("1")).thenReturn(existingZone);

		parkingController.newParkingZone(zoneToAdd);

		verify(parkingView).showError("Already existing parking zone with id 1", existingZone);
		verifyNoMoreInteractions(ignoreStubs(parkingZoneRepository));
	}

	@Test
	public void testNewParkingZoneShouldShowErrorWhenRepositoryThrowsIllegalArgument() {
		ParkingZone zone = new ParkingZone("1", "ParkingA", 10, 2.5, true);
		when(parkingZoneRepository.findById("1")).thenReturn(null);

		doThrow(new IllegalArgumentException("Name cannot be null or blank")).when(parkingZoneRepository).save(zone);

		parkingController.newParkingZone(zone);

		verify(parkingView).showError("Name cannot be null or blank", zone);
		verify(parkingView, never()).parkingZoneAdded(any());
	}

	@Test
	public void testDeleteParkingZoneWhenZoneExists() {
		ParkingZone zoneToDelete = new ParkingZone("1", "ParkingA", 100, 2.50, true);

		when(parkingZoneRepository.findById("1")).thenReturn(zoneToDelete);

		parkingController.deleteParkingZone(zoneToDelete);

		InOrder inOrder = inOrder(parkingZoneRepository, parkingView);
		inOrder.verify(parkingZoneRepository).delete("1");
		inOrder.verify(parkingView).parkingZoneRemoved(zoneToDelete);
	}

	@Test
	public void testDeleteParkingZoneWhenZoneDoesNotExist() {
		ParkingZone zone = new ParkingZone("1", "ParkingA", 100, 2.50, true);

		when(parkingZoneRepository.findById("1")).thenReturn(null);

		parkingController.deleteParkingZone(zone);

		verify(parkingView).showError("No existing parking zone with id 1", zone);
		verifyNoMoreInteractions(ignoreStubs(parkingZoneRepository));
	}

}
