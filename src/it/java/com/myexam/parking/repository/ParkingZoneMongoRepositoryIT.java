package com.myexam.parking.repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.myexam.parking.model.*;
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

	private ParkingZone zone(String id, String name) {
		return new ParkingZone(id, name, 100, 2.5, true);
	}

	@Test
	public void testFindAll() {
		addTestParkingZone("1", "ParkingZone A", 100, 2.50, true);
		addTestParkingZone("2", "ParkingZone B", 100, 2.50, true);

		assertThat(parkingZoneRepository.findAll()).containsExactly(zone("1", "ParkingZone A"),
				zone("2", "ParkingZone B"));
	}

	@Test
	public void testFindById() {
		addTestParkingZone("1", "ParkingZone A", 100, 2.50, true);
		addTestParkingZone("2", "ParkingZone B", 100, 2.50, true);

		assertThat(parkingZoneRepository.findById("2")).isEqualTo(zone("2", "ParkingZone B"));
	}

	@Test
	public void testSave() {
		ParkingZone zone = zone("1", "New Zone");

		parkingZoneRepository.save(zone);

		assertThat(readAllParkingZonesFromDatabase()).containsExactly(zone);
	}

	@Test
	public void testDelete() {
		addTestParkingZone("1", "ParkingZone A", 100, 2.50, true);

		parkingZoneRepository.delete("1");

		assertThat(readAllParkingZonesFromDatabase()).isEmpty();
	}

	private void addTestParkingZone(String id, String name, Integer capacity, double hourlyRate, boolean isAvailable) {
		parkingZoneCollection.insertOne(new Document().append("id", id).append("name", name)
				.append("capacity", capacity).append("hourlyRate", hourlyRate).append("isAvailable", isAvailable));
	}

	private List<ParkingZone> readAllParkingZonesFromDatabase() {
		return StreamSupport.stream(parkingZoneCollection.find().spliterator(), false)
				.map(d -> new ParkingZone(d.getString("id"), d.getString("name"), d.getInteger("capacity"),
						d.getDouble("hourlyRate"), d.getBoolean("isAvailable")))
				.collect(Collectors.toList());
	}
}