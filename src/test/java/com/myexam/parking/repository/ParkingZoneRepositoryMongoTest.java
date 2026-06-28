package com.myexam.parking.repository;

import static org.assertj.core.api.Assertions.*;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

import com.myexam.parking.model.ParkingZone;
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

	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(parkingZoneRepository.findAll()).isEmpty();
	}

	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestParkingZone("1", "ParkingA", 100, 2.50, true);
		addTestParkingZone("2", "ParkingB", 50, 3.00, false);

		assertThat(parkingZoneRepository.findAll()).containsExactly(new ParkingZone("1", "ParkingA", 100, 2.50, true),
				new ParkingZone("2", "ParkingB", 50, 3.00, false));
	}

	private void addTestParkingZone(String id, String name, Integer capacity, double hourlyRate, boolean isAvailable) {
		parkingZoneCollection.insertOne(new Document().append("id", id).append("name", name)
				.append("capacity", capacity).append("hourlyRate", hourlyRate).append("isAvailable", isAvailable));
	}
}
