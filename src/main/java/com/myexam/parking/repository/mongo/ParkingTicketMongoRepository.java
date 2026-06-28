package com.myexam.parking.repository.mongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.myexam.parking.model.ParkingTicket;
import com.myexam.parking.repository.ParkingTicketRepository;

public class ParkingTicketMongoRepository implements ParkingTicketRepository {

	private MongoCollection<Document> ticketCollection;

	public ParkingTicketMongoRepository(MongoClient client, String databaseName, String collectionName) {
		this.ticketCollection = client.getDatabase(databaseName).getCollection(collectionName);
	}

	@Override
	public List<ParkingTicket> findAll() {
		return StreamSupport.stream(ticketCollection.find().spliterator(), false).map(this::fromDocumentToParkingTicket)
				.collect(Collectors.toList());
	}

	private ParkingTicket fromDocumentToParkingTicket(Document d) {
		return new ParkingTicket(d.getString("id"), d.getString("vehiclePlate"), d.getString("parkingZoneId"),
				d.getString("entryTime") != null ? LocalDateTime.parse(d.getString("entryTime")) : null,
				d.getString("exitTime") != null ? LocalDateTime.parse(d.getString("exitTime")) : null,
				d.getBoolean("paid", false), d.getDouble("totalCost") != null ? d.getDouble("totalCost") : 0.0);
	}

}
