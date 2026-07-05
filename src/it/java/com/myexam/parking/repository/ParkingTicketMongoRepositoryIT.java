package com.myexam.parking.repository;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.myexam.parking.repository.mongo.ParkingTicketMongoRepository;

public class ParkingTicketMongoRepositoryIT {
	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:5");

	private MongoClient client;
	private ParkingTicketMongoRepository parkingTicketRepository;
	private MongoCollection<Document> parkingTicketCollection;

	private static final String PARKING_DB_NAME = "parking";
	private static final String TICKET_COLLECTION_NAME = "parking_tickets";

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getFirstMappedPort()));
		parkingTicketRepository = new ParkingTicketMongoRepository(client, PARKING_DB_NAME, TICKET_COLLECTION_NAME);

		MongoDatabase database = client.getDatabase(PARKING_DB_NAME);
		database.drop();

		parkingTicketCollection = database.getCollection(TICKET_COLLECTION_NAME);
	}

	@After
	public void tearDown() {
		client.close();
	}

}
