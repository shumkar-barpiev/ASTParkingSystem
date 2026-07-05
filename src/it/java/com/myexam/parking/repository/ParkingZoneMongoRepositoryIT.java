package com.myexam.parking.repository;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.testcontainers.containers.MongoDBContainer;

import com.myexam.parking.repository.mongo.*;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ParkingZoneMongoRepositoryIT {

	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:5");

	private MongoClient client;
	private ParkingZoneMongoRepository parkingZoneRepository;
	private MongoCollection<Document> parkingZoneCollection;

	private static final String PARKING_DB_NAME = "parking";
	private static final String ZONE_COLLECTION_NAME = "parking_zones";

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getFirstMappedPort()));

		parkingZoneRepository = new ParkingZoneMongoRepository(client, PARKING_DB_NAME, ZONE_COLLECTION_NAME);

		MongoDatabase database = client.getDatabase(PARKING_DB_NAME);

		database.drop();

		parkingZoneCollection = database.getCollection(ZONE_COLLECTION_NAME);
	}

	@After
	public void tearDown() {
		client.close();
	}
}
