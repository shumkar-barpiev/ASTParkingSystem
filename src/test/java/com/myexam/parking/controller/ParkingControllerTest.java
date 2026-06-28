package com.myexam.parking.controller;

import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

}
