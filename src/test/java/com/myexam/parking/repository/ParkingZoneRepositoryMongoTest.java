package com.myexam.parking.repository;

import static org.assertj.core.api.Assertions.*;

import java.net.InetSocketAddress;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

import com.myexam.parking.repository.mongo.ParkingZoneMongoRepository;

public class ParkingZoneRepositoryMongoTest {
	private static MongoServer server;
	private static InetSocketAddress serverAddress;

	private MongoClient client;
	private ParkingZoneMongoRepository parkingZoneRepository;
	private MongoCollection<Document> parkingZoneCollection;

	private static final String DB_NAME = "parking";
	private static final String PARKING_ZONE_COLLECTION = "ParkingZone";

	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}

	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(serverAddress));

		parkingZoneRepository = new ParkingZoneMongoRepository(client, DB_NAME, PARKING_ZONE_COLLECTION);

		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();

		parkingZoneCollection = database.getCollection(PARKING_ZONE_COLLECTION);
	}

	@After
	public void tearDown() {
		client.close();
	}
}
