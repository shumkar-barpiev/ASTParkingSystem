package com.myexam.parking.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.myexam.parking.model.ParkingZone;
import com.myexam.parking.repository.ParkingTicketRepository;
import com.myexam.parking.repository.ParkingZoneRepository;
import com.myexam.parking.repository.mongo.ParkingTicketMongoRepository;
import com.myexam.parking.repository.mongo.ParkingZoneMongoRepository;
import com.myexam.parking.view.ParkingView;

public class ParkingControllerIT {
	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:5");

	@Mock
	private ParkingView parkingView;

	private ParkingZoneRepository zoneRepository;
	private ParkingTicketRepository ticketRepository;
	private ParkingController parkingController;

	private MongoClient client;
	private AutoCloseable closeable;

	private static final String PARKING_DB_NAME = "parking";
	private static final String ZONE_COLLECTION_NAME = "parking_zones";
	private static final String TICKET_COLLECTION_NAME = "parking_tickets";

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);

		client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getFirstMappedPort()));

		zoneRepository = new ParkingZoneMongoRepository(client, PARKING_DB_NAME, ZONE_COLLECTION_NAME);
		ticketRepository = new ParkingTicketMongoRepository(client, PARKING_DB_NAME, TICKET_COLLECTION_NAME);

		client.getDatabase(PARKING_DB_NAME).drop();

		parkingController = new ParkingController(parkingView, zoneRepository, ticketRepository);
	}

	@After
	public void tearDown() throws Exception {
		client.close();
		closeable.close();
	}

	@Test
	public void testAllParkingZones() {
		ParkingZone zone = new ParkingZone("1", "parkingZone A", 100, 2.5, true);
		zoneRepository.save(zone);

		parkingController.allParkingZones();

		verify(parkingView).showAllParkingZones(asList(zone));
	}

	@Test
	public void testNewParkingZone() {
		ParkingZone zone = new ParkingZone("1", "parkingZone A", 100, 2.5, true);

		parkingController.newParkingZone(zone);

		verify(parkingView).parkingZoneAdded(zone);
	}

	@Test
	public void testNewParkingZoneAlreadyExisting() {
		ParkingZone existingZone = new ParkingZone("1", "parkingZone A", 100, 2.5, true);
		zoneRepository.save(existingZone);

		ParkingZone newZone = new ParkingZone("1", "parkingZone B", 100, 2.5, true);

		parkingController.newParkingZone(newZone);

		verify(parkingView).showError("Already existing parking zone with id 1", existingZone);
	}

	@Test
	public void testDeleteParkingZone() {
		ParkingZone zone = new ParkingZone("1", "parkingZone A", 100, 2.5, true);
		zoneRepository.save(zone);

		parkingController.deleteParkingZone(zone);

		verify(parkingView).parkingZoneRemoved(zone);
	}

	@Test
	public void testDeleteParkingZoneNotFound() {
		ParkingZone zone = new ParkingZone("1", "parkingZone A", 100, 2.5, true);

		parkingController.deleteParkingZone(zone);

		verify(parkingView).showError("No existing parking zone with id 1", zone);
	}

}
