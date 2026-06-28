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

import com.myexam.parking.model.ParkingZone;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

import com.myexam.parking.repository.mongo.ParkingZoneMongoRepository;

public class ParkingZoneMongoRepositoryTest {

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

	@Test
	public void testFindByIdNotFound() {
		assertThat(parkingZoneRepository.findById("1")).isNull();
	}

	@Test
	public void testFindByIdFound() {
		addTestParkingZone("1", "ParkingA", 100, 2.50, true);

		assertThat(parkingZoneRepository.findById("1")).isEqualTo(new ParkingZone("1", "ParkingA", 100, 2.50, true));
	}

	@Test
	public void testSave() {
		ParkingZone parkingZone = new ParkingZone("1", "ParkingA", 100, 2.50, true);

		parkingZoneRepository.save(parkingZone);

		assertThat(readAllParkingZonesFromDatabase()).containsExactly(parkingZone);
	}

	@Test
	public void testDelete() {
		addTestParkingZone("1", "ParkingA", 100, 2.50, true);

		parkingZoneRepository.delete("1");

		assertThat(readAllParkingZonesFromDatabase()).isEmpty();
	}

	@Test
	public void testSaveAllowsZeroCapacityAndZeroHourlyRate() {
		ParkingZone parkingZone = new ParkingZone("2", "ParkingA", 0, 0.0, true);

		parkingZoneRepository.save(parkingZone);

		assertThat(parkingZoneRepository.findById("2")).isEqualTo(parkingZone);
	}

	@Test
	public void testSaveThrowsExceptionWhenIdIsNull() {
		ParkingZone invalidZone = new ParkingZone(null, "ParkingA", 100, 2.50, true);

		assertThatThrownBy(() -> parkingZoneRepository.save(invalidZone)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("ID cannot be null or blank");
	}

	@Test
	public void testSaveThrowsExceptionWhenIdIsBlank() {
		ParkingZone invalidZone = new ParkingZone("   ", "ParkingA", 100, 2.50, true);

		assertThatThrownBy(() -> parkingZoneRepository.save(invalidZone)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("ID cannot be null or blank");
	}

	@Test
	public void testSaveThrowsExceptionWhenNameIsNull() {
		ParkingZone invalidZone = new ParkingZone("1", null, 100, 2.50, true);

		assertThatThrownBy(() -> parkingZoneRepository.save(invalidZone)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Name cannot be null or blank");
	}

	@Test
	public void testSaveThrowsExceptionWhenNameIsBlank() {
		ParkingZone invalidZone = new ParkingZone("1", "   ", 100, 2.50, true);

		assertThatThrownBy(() -> parkingZoneRepository.save(invalidZone)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Name cannot be null or blank");
	}

	@Test
	public void testSaveThrowsExceptionWhenCapacityIsNegative() {
		ParkingZone invalidZone = new ParkingZone("1", "ParkingA", -5, 2.50, true);

		assertThatThrownBy(() -> parkingZoneRepository.save(invalidZone)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Capacity cannot be negative or null");
	}

	@Test
	public void testSaveThrowsExceptionWhenCapacityIsNull() {
		ParkingZone invalidZone = new ParkingZone("1", "ParkingA", null, 2.50, true);

		assertThatThrownBy(() -> parkingZoneRepository.save(invalidZone)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Capacity cannot be negative or null");
	}

	@Test
	public void testSaveThrowsExceptionWhenHourlyRateIsNegative() {
		ParkingZone invalidZone = new ParkingZone("1", "ParkingA", 100, -1.50, true);

		assertThatThrownBy(() -> parkingZoneRepository.save(invalidZone)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Hourly rate cannot be negative");
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