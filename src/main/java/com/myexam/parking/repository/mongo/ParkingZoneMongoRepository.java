package com.myexam.parking.repository.mongo;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.myexam.parking.repository.ParkingZoneRepository;

public class ParkingZoneMongoRepository implements ParkingZoneRepository {
	private MongoCollection<Document> parkingZoneCollection;

	public ParkingZoneMongoRepository(MongoClient client, String databaseName, String collectionName) {
		this.parkingZoneCollection = client.getDatabase(databaseName).getCollection(collectionName);
	}

}
