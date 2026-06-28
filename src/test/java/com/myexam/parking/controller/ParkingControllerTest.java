package com.myexam.parking.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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

}
