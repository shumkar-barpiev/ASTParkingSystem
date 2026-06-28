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

}
