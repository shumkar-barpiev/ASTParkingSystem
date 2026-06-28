package com.myexam.parking.repository;

import static org.assertj.core.api.Assertions.*;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;

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

}
