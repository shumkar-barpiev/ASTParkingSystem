package com.myexam.parking.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.myexam.parking.model.ParkingZone;
import com.myexam.parking.repository.ParkingZoneRepository;

public class ParkingZoneMongoRepository implements ParkingZoneRepository {
	private MongoCollection<Document> parkingZoneCollection;

	public ParkingZoneMongoRepository(MongoClient client, String databaseName, String collectionName) {
		this.parkingZoneCollection = client.getDatabase(databaseName).getCollection(collectionName);
	}

	@Override
	public List<ParkingZone> findAll() {
		return StreamSupport.stream(parkingZoneCollection.find().spliterator(), false)
				.map(this::fromDocumentToParkingZone).collect(Collectors.toList());
	}

	@Override
	public ParkingZone findById(String id) {
		Document d = parkingZoneCollection.find(Filters.eq("id", id)).first();

		if (d != null) {
			return fromDocumentToParkingZone(d);
		}

		return null;
	}

	@Override
	public void save(ParkingZone parkingZone) {

		parkingZoneCollection.insertOne(new Document().append("id", parkingZone.getId())
				.append("name", parkingZone.getName()).append("capacity", parkingZone.getCapacity())
				.append("hourlyRate", parkingZone.getHourlyRate()).append("isAvailable", parkingZone.isAvailable()));
	}

	@Override
	public void delete(String id) {
		parkingZoneCollection.deleteOne(Filters.eq("id", id));
	}

	private ParkingZone fromDocumentToParkingZone(Document d) {
		return new ParkingZone(d.getString("id"), d.getString("name"), d.getInteger("capacity"),
				d.getDouble("hourlyRate"), d.getBoolean("isAvailable"));
	}

}
