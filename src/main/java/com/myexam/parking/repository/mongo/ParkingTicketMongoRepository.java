package com.myexam.parking.repository.mongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.myexam.parking.model.ParkingTicket;
import com.myexam.parking.repository.ParkingTicketRepository;

public class ParkingTicketMongoRepository implements ParkingTicketRepository {
	private static final String PARKING_ZONE_ID_FIELD = "parkingZoneId";
	private static final String VEHICLE_PLATE_FIELD = "vehiclePlate";
	private static final String ENTRY_FIELD = "entryTime";
	private static final String EXIT_FIELD = "exitTime";
	private static final String PAID_FIELD = "paid";

	private MongoCollection<Document> ticketCollection;

	public ParkingTicketMongoRepository(MongoClient client, String databaseName, String collectionName) {
		this.ticketCollection = client.getDatabase(databaseName).getCollection(collectionName);
	}

	@Override
	public List<ParkingTicket> findAll() {
		return StreamSupport.stream(ticketCollection.find().spliterator(), false).map(this::fromDocumentToParkingTicket)
				.collect(Collectors.toList());
	}

	@Override
	public ParkingTicket findById(String id) {
		Document d = ticketCollection.find(Filters.eq("id", id)).first();

		if (d != null) {
			return fromDocumentToParkingTicket(d);
		}

		return null;
	}

	@Override
	public void save(ParkingTicket ticket) {
		validateParkingTicket(ticket);

		ticketCollection.insertOne(
				new Document().append("id", ticket.getId()).append(VEHICLE_PLATE_FIELD, ticket.getVehiclePlate())
						.append(PARKING_ZONE_ID_FIELD, ticket.getParkingZoneId())
						.append(ENTRY_FIELD, ticket.getEntryTime() != null ? ticket.getEntryTime().toString() : null)
						.append(EXIT_FIELD, ticket.getExitTime() != null ? ticket.getExitTime().toString() : null)
						.append(PAID_FIELD, ticket.isPaid()).append("totalCost", ticket.getTotalCost()));
	}

	@Override
	public void delete(String id) {
		ticketCollection.deleteOne(Filters.eq("id", id));
	}

	@Override
	public long countActiveTicketsByZoneId(String zoneId) {
		return ticketCollection
				.countDocuments(Filters.and(Filters.eq(PARKING_ZONE_ID_FIELD, zoneId), Filters.eq(EXIT_FIELD, null)));
	}

	@Override
	public ParkingTicket findActiveTicketByVehiclePlate(String vehiclePlate) {
		Document d = ticketCollection
				.find(Filters.and(Filters.eq(VEHICLE_PLATE_FIELD, vehiclePlate), Filters.eq(EXIT_FIELD, null))).first();
		if (d != null) {
			return fromDocumentToParkingTicket(d);
		}
		return null;
	}

	private ParkingTicket fromDocumentToParkingTicket(Document d) {
		return new ParkingTicket(d.getString("id"), d.getString("vehiclePlate"), d.getString("parkingZoneId"),
				d.getString("entryTime") != null ? LocalDateTime.parse(d.getString("entryTime")) : null,
				d.getString("exitTime") != null ? LocalDateTime.parse(d.getString("exitTime")) : null,
				d.getBoolean(PAID_FIELD, false), d.getDouble("totalCost") != null ? d.getDouble("totalCost") : 0.0);
	}

	private void validateParkingTicket(ParkingTicket ticket) {
		if (ticket.getId() == null || ticket.getId().trim().isEmpty()) {
			throw new IllegalArgumentException("ID cannot be null or blank");
		}
		if (ticket.getVehiclePlate() == null || ticket.getVehiclePlate().trim().isEmpty()) {
			throw new IllegalArgumentException("Vehicle plate cannot be null or blank");
		}
		if (ticket.getParkingZoneId() == null || ticket.getParkingZoneId().trim().isEmpty()) {
			throw new IllegalArgumentException("Parking zone ID cannot be null or blank");
		}
		if (ticket.getTotalCost() < 0) {
			throw new IllegalArgumentException("Total cost cannot be negative");
		}
	}

}
