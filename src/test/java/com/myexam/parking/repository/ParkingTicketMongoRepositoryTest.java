package com.myexam.parking.repository;

import static org.assertj.core.api.Assertions.*;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
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
import com.myexam.parking.model.ParkingTicket;
import com.myexam.parking.repository.mongo.ParkingTicketMongoRepository;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class ParkingTicketMongoRepositoryTest {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;

	private MongoClient client;
	private ParkingTicketMongoRepository ticketRepository;
	private MongoCollection<Document> ticketCollection;

	private static final String DB_NAME = "parking";
	private static final String TICKET_COLLECTION = "ParkingTicket";

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

		ticketRepository = new ParkingTicketMongoRepository(client, DB_NAME, TICKET_COLLECTION);

		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();

		ticketCollection = database.getCollection(TICKET_COLLECTION);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(ticketRepository.findAll()).isEmpty();
	}

	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		LocalDateTime entry1 = LocalDateTime.of(2026, 6, 20, 10, 0);
		LocalDateTime entry2 = LocalDateTime.of(2026, 6, 20, 11, 0);

		addTestTicket("1", "ABC123", "ParkingAId", entry1, null, false, 0.0);
		addTestTicket("2", "DFE456", "ParkingBId", entry2, null, false, 0.0);

		assertThat(ticketRepository.findAll()).containsExactly(
				new ParkingTicket("1", "ABC123", "ParkingAId", entry1, null, false, 0.0),
				new ParkingTicket("2", "DFE456", "ParkingBId", entry2, null, false, 0.0));
	}

	@Test
	public void testFindByIdNotFound() {
		assertThat(ticketRepository.findById("999")).isNull();
	}

	@Test
	public void testFindByIdFound() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 14, 30);
		addTestTicket("1", "ABC123", "ParkingAId", entry, null, false, 0.0);

		assertThat(ticketRepository.findById("1"))
				.isEqualTo(new ParkingTicket("1", "ABC123", "ParkingAId", entry, null, false, 0.0));
	}

	@Test
	public void testSave() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 8, 15);
		ParkingTicket ticket = new ParkingTicket("1", "ABC123", "ParkingAId", entry, null, false, 0.0);

		ticketRepository.save(ticket);

		assertThat(readAllTicketsFromDatabase()).containsExactly(ticket);
	}

	@Test
	public void testDelete() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 10, 0);
		addTestTicket("1", "ABC123", "ParkingAId", entry, null, false, 0.0);

		ticketRepository.delete("1");

		assertThat(readAllTicketsFromDatabase()).isEmpty();
	}

	@Test
	public void testSaveWithNullEntryAndNonNullExit() {
		LocalDateTime exit = LocalDateTime.of(2026, 6, 20, 15, 0);
		ParkingTicket ticket = new ParkingTicket("1", "ABC12345", "ParkingC", null, exit, false, 0.0);

		ticketRepository.save(ticket);

		assertThat(ticketRepository.findById("1")).isEqualTo(ticket);
	}

	@Test
	public void testFindByIdWithMissingCostAndNullDates() {
		Document doc = new Document().append("id", "2").append("vehiclePlate", "QWE123")
				.append("parkingZoneId", "ParkingX").append("paid", false);

		ticketCollection.insertOne(doc);

		ParkingTicket retrieved = ticketRepository.findById("2");

		assertThat(retrieved.getEntryTime()).isNull();
		assertThat(retrieved.getExitTime()).isNull();
		assertThat(retrieved.getTotalCost()).isEqualTo(0.0);
	}

	@Test
	public void testSaveThrowsExceptionWhenIdIsNull() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 10, 0);
		ParkingTicket invalidTicket = new ParkingTicket(null, "ABC1234", "ParkingAId", entry, null, false, 0.0);

		assertThatThrownBy(() -> ticketRepository.save(invalidTicket)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("ID cannot be null or blank");
	}

	@Test
	public void testSaveThrowsExceptionWhenIdIsBlank() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 10, 0);
		ParkingTicket invalidTicket = new ParkingTicket("   ", "ABC1234", "ParkingAId", entry, null, false, 0.0);

		assertThatThrownBy(() -> ticketRepository.save(invalidTicket)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("ID cannot be null or blank");
	}

	@Test
	public void testSaveThrowsExceptionWhenVehiclePlateIsNull() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 10, 0);
		ParkingTicket invalidTicket = new ParkingTicket("1", null, "ParkingAId", entry, null, false, 0.0);

		assertThatThrownBy(() -> ticketRepository.save(invalidTicket)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Vehicle plate cannot be null or blank");
	}

	@Test
	public void testSaveThrowsExceptionWhenVehiclePlateIsBlank() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 10, 0);
		ParkingTicket invalidTicket = new ParkingTicket("1", "   ", "ParkingAId", entry, null, false, 0.0);

		assertThatThrownBy(() -> ticketRepository.save(invalidTicket)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Vehicle plate cannot be null or blank");
	}

	@Test
	public void testSaveThrowsExceptionWhenParkingZoneIdIsNull() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 10, 0);
		ParkingTicket invalidTicket = new ParkingTicket("1", "ABC1234", null, entry, null, false, 0.0);

		assertThatThrownBy(() -> ticketRepository.save(invalidTicket)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Parking zone ID cannot be null or blank");
	}

	@Test
	public void testSaveThrowsExceptionWhenParkingZoneIdIsBlank() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 10, 0);
		ParkingTicket invalidTicket = new ParkingTicket("1", "ABC1234", "   ", entry, null, false, 0.0);

		assertThatThrownBy(() -> ticketRepository.save(invalidTicket)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Parking zone ID cannot be null or blank");
	}

	@Test
	public void testSaveThrowsExceptionWhenTotalCostIsNegative() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 10, 0);
		ParkingTicket invalidTicket = new ParkingTicket("1", "ABC1234", "ParkingAId", entry, null, false, -5.00);

		assertThatThrownBy(() -> ticketRepository.save(invalidTicket)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Total cost cannot be negative");
	}

	@Test
	public void testCountActiveTicketsByZoneIdShouldReturnCountOfActiveTickets() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 26, 9, 0);
		LocalDateTime exit = LocalDateTime.of(2026, 6, 26, 11, 0);

		addTestTicket("t1", "AB123", "parkingId1", entry, null, false, 0.0);
		addTestTicket("t2", "CD456", "parkingId1", entry, null, false, 0.0);
		addTestTicket("t3", "EF789", "parkingId1", entry, exit, true, 5.0);
		addTestTicket("t4", "GH012", "parkingId2", entry, null, false, 0.0);

		assertThat(ticketRepository.countActiveTicketsByZoneId("parkingId1")).isEqualTo(2L);
	}

	@Test
	public void testCountActiveTicketsByZoneIdShouldReturnZeroWhenNoActiveTickets() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 26, 9, 0);
		LocalDateTime exit = LocalDateTime.of(2026, 6, 26, 11, 0);

		addTestTicket("t1", "AB123", "parkingId1", entry, exit, true, 5.0);

		assertThat(ticketRepository.countActiveTicketsByZoneId("parkingId1")).isEqualTo(0L);
	}

	@Test
	public void testCountActiveTicketsByZoneIdShouldReturnZeroWhenZoneIsEmpty() {
		assertThat(ticketRepository.countActiveTicketsByZoneId("parkingId1")).isEqualTo(0L);
	}

	@Test
	public void testFindActiveTicketByVehiclePlateShouldReturnTicketWhenActiveTicketExists() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 26, 9, 0);

		addTestTicket("t1", "AB123", "parkingId1", entry, null, false, 0.0);

		ParkingTicket result = ticketRepository.findActiveTicketByVehiclePlate("AB123");

		assertThat(result).isEqualTo(new ParkingTicket("t1", "AB123", "parkingId1", entry, null, false, 0.0));
	}

	@Test
	public void testFindActiveTicketByVehiclePlateShouldReturnNullWhenTicketIsCompleted() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 26, 9, 0);
		LocalDateTime exit = LocalDateTime.of(2026, 6, 26, 11, 0);

		addTestTicket("t1", "AB123", "parkingId1", entry, exit, true, 5.0);

		assertThat(ticketRepository.findActiveTicketByVehiclePlate("AB123")).isNull();
	}

	@Test
	public void testFindActiveTicketByVehiclePlateShouldReturnNullWhenPlateNotFound() {
		assertThat(ticketRepository.findActiveTicketByVehiclePlate("UnknownPlate")).isNull();
	}

	private void addTestTicket(String id, String vehiclePlate, String parkingZoneId, LocalDateTime entryTime,
			LocalDateTime exitTime, boolean paid, double totalCost) {

		Document doc = new Document().append("id", id).append("vehiclePlate", vehiclePlate)
				.append("parkingZoneId", parkingZoneId).append("paid", paid).append("totalCost", totalCost);

		if (entryTime != null) {
			doc.append("entryTime", entryTime.toString());
		}
		if (exitTime != null) {
			doc.append("exitTime", exitTime.toString());
		}

		ticketCollection.insertOne(doc);
	}

	private List<ParkingTicket> readAllTicketsFromDatabase() {
		return StreamSupport.stream(ticketCollection.find().spliterator(), false)
				.map(d -> new ParkingTicket(d.getString("id"), d.getString("vehiclePlate"),
						d.getString("parkingZoneId"),
						d.getString("entryTime") != null ? LocalDateTime.parse(d.getString("entryTime")) : null,
						d.getString("exitTime") != null ? LocalDateTime.parse(d.getString("exitTime")) : null,
						d.getBoolean("paid", false), d.getDouble("totalCost") != null ? d.getDouble("totalCost") : 0.0))
				.collect(Collectors.toList());
	}

}
