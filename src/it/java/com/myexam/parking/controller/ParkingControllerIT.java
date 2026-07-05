package com.myexam.parking.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
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

}
