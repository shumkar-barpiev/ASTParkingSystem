package com.myexam.parking.repository.mongo;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.myexam.parking.repository.ParkingTicketRepository;

public class ParkingTicketMongoRepository implements ParkingTicketRepository {

	private MongoCollection<Document> ticketCollection;

	public ParkingTicketMongoRepository(MongoClient client, String databaseName, String collectionName) {
		this.ticketCollection = client.getDatabase(databaseName).getCollection(collectionName);
	}
}
