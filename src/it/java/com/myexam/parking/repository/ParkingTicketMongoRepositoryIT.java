package com.myexam.parking.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.myexam.parking.model.ParkingTicket;
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

	@Test
	public void testFindAll() {
		LocalDateTime now = LocalDateTime.now();
		addTestTicketToDatabase("1", "ABC123", "parkingZoneId-1", now, null, false, 0.0);
		addTestTicketToDatabase("2", "EFG456", "parkingZoneId-2", now, now.plusHours(2), true, 5.0);

		assertThat(parkingTicketRepository.findAll()).containsExactly(
				new ParkingTicket("1", "ABC123", "parkingZoneId-1", now, null, false, 0.0),
				new ParkingTicket("2", "EFG456", "parkingZoneId-2", now, now.plusHours(2), true, 5.0));
	}

	@Test
	public void testFindByIdFound() {
		LocalDateTime now = LocalDateTime.now();
		addTestTicketToDatabase("1", "ABC123", "parkingZoneId-1", now, null, false, 0.0);

		assertThat(parkingTicketRepository.findById("1"))
				.isEqualTo(new ParkingTicket("1", "ABC123", "parkingZoneId-1", now, null, false, 0.0));
	}

	@Test
	public void testFindByIdNotFound() {
		assertThat(parkingTicketRepository.findById("999")).isNull();
	}

	@Test
	public void testSaveValidTicket() {
		LocalDateTime now = LocalDateTime.now();
		ParkingTicket ticket = new ParkingTicket("1", "ABC123", "parkingZoneId-1", now, null, false, 0.0);

		parkingTicketRepository.save(ticket);

		assertThat(readAllTicketsFromDatabase()).containsExactly(ticket);
	}

	@Test
	public void testDelete() {
		addTestTicketToDatabase("1", "ABC123", "parkingZoneId-1", LocalDateTime.now(), null, false, 0.0);

		parkingTicketRepository.delete("1");

		assertThat(readAllTicketsFromDatabase()).isEmpty();
	}

	@Test
	public void testCountActiveTicketsByZoneId() {
		LocalDateTime now = LocalDateTime.now();
		addTestTicketToDatabase("1", "ABC123", "parkingZoneId-1", now, null, false, 0.0);
		addTestTicketToDatabase("2", "EFG456", "parkingZoneId-1", now, now.plusHours(1), true, 2.5);
		addTestTicketToDatabase("3", "QWERTY1", "parkingZoneId-2", now, null, false, 0.0);

		assertThat(parkingTicketRepository.countActiveTicketsByZoneId("parkingZoneId-1")).isEqualTo(1);
	}

	@Test
	public void testFindActiveTicketByVehiclePlateFound() {
		LocalDateTime now = LocalDateTime.now();
		addTestTicketToDatabase("1", "ABC123", "parkingZoneId-1", now.minusDays(1), now.minusDays(1).plusHours(2), true,
				5.0);
		addTestTicketToDatabase("2", "ABC123", "parkingZoneId-2", now, null, false, 0.0);

		ParkingTicket activeTicket = parkingTicketRepository.findActiveTicketByVehiclePlate("ABC123");

		assertThat(activeTicket).isNotNull();
		assertThat(activeTicket.getId()).isEqualTo("2");
	}

	@Test
	public void testFindActiveTicketByVehiclePlateNotFound() {
		addTestTicketToDatabase("1", "ABC123", "parkingZoneId-1", LocalDateTime.now(), LocalDateTime.now(), true, 5.0);

		assertThat(parkingTicketRepository.findActiveTicketByVehiclePlate("ABC123")).isNull();
	}

	@Test
	public void testSaveThrowsExceptionForNullId() {
		ParkingTicket invalidTicket = new ParkingTicket(null, "ABC123", "parkingZoneId-1", LocalDateTime.now(), null,
				false, 0.0);
		assertThatThrownBy(() -> parkingTicketRepository.save(invalidTicket))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("ID cannot be null or blank");
	}

	@Test
	public void testSaveThrowsExceptionForNegativeCost() {
		ParkingTicket invalidTicket = new ParkingTicket("1", "ABC123", "parkingZoneId-1", LocalDateTime.now(), null,
				false, -5.0);
		assertThatThrownBy(() -> parkingTicketRepository.save(invalidTicket))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Total cost cannot be negative");
	}

	private void addTestTicketToDatabase(String id, String vehiclePlate, String parkingZoneId, LocalDateTime entryTime,
			LocalDateTime exitTime, boolean paid, double totalCost) {
		parkingTicketCollection.insertOne(new Document().append("id", id).append("vehiclePlate", vehiclePlate)
				.append("parkingZoneId", parkingZoneId)
				.append("entryTime", entryTime != null ? entryTime.toString() : null)
				.append("exitTime", exitTime != null ? exitTime.toString() : null).append("paid", paid)
				.append("totalCost", totalCost));
	}

	private List<ParkingTicket> readAllTicketsFromDatabase() {
		return StreamSupport.stream(parkingTicketCollection.find().spliterator(), false)
				.map(d -> new ParkingTicket(d.getString("id"), d.getString("vehiclePlate"),
						d.getString("parkingZoneId"),
						d.getString("entryTime") != null ? LocalDateTime.parse(d.getString("entryTime")) : null,
						d.getString("exitTime") != null ? LocalDateTime.parse(d.getString("exitTime")) : null,
						d.getBoolean("paid", false), d.getDouble("totalCost") != null ? d.getDouble("totalCost") : 0.0))
				.collect(Collectors.toList());
	}
}
