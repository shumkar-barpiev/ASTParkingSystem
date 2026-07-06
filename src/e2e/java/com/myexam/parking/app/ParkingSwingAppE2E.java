package com.myexam.parking.app;

import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.time.LocalDateTime;

import javax.swing.JFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

@RunWith(GUITestRunner.class)
public class ParkingSwingAppE2E extends AssertJSwingJUnitTestCase {

	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:5");

	private static final String DB_NAME = "test-parking-db";
	private static final String PARKING_ZONE_COLLECTION_NAME = "test-parking-zone-collection";
	private static final String PARKING_TICKET_COLLECTION_NAME = "test-parking-ticket-collection";

	private static final String PARKING_ZONE_FIXTURE_1_ID = "1";
	private static final String PARKING_ZONE_FIXTURE_1_NAME = "Parking A";
	private static final String PARKING_ZONE_FIXTURE_2_ID = "2";
	private static final String PARKING_ZONE_FIXTURE_2_NAME = "Parking B";

	private static final String PARKING_TICKET_FIXTURE_1_ID = "t1";
	private static final String PARKING_TICKET_FIXTURE_1_PLATE = "AB123";

	private MongoClient mongoClient;

	private FrameFixture window;

	@Override
	protected void onSetUp() {
		String containerIpAddress = mongo.getHost();
		Integer mappedPort = mongo.getFirstMappedPort();
		mongoClient = new MongoClient(containerIpAddress, mappedPort);

		mongoClient.getDatabase(DB_NAME).drop();

		addTestZoneToDatabase(PARKING_ZONE_FIXTURE_1_ID, PARKING_ZONE_FIXTURE_1_NAME, 100, 2.5, true);
		addTestZoneToDatabase(PARKING_ZONE_FIXTURE_2_ID, PARKING_ZONE_FIXTURE_2_NAME, 50, 3.0, true);
		addTestTicketToDatabase(PARKING_TICKET_FIXTURE_1_ID, PARKING_TICKET_FIXTURE_1_PLATE, PARKING_ZONE_FIXTURE_1_ID,
				LocalDateTime.of(2026, 6, 26, 9, 0), null, false, 0.0);

		application("com.myexam.parking.app.swing.ParkingSwingApp")
				.withArgs("--mongo-host=" + containerIpAddress, "--mongo-port=" + mappedPort.toString(),
						"--db-name=" + DB_NAME, "--db-collection-zones=" + PARKING_ZONE_COLLECTION_NAME,
						"--db-collection-tickets=" + PARKING_TICKET_COLLECTION_NAME)
				.start();

		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Parking Management System".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	private void addTestZoneToDatabase(String id, String name, int capacity, double hourlyRate, boolean isAvailable) {
		mongoClient.getDatabase(DB_NAME).getCollection(PARKING_ZONE_COLLECTION_NAME)
				.insertOne(new Document().append("id", id).append("name", name).append("capacity", capacity)
						.append("hourlyRate", hourlyRate).append("isAvailable", isAvailable));
	}

	private void removeTestZoneFromDatabase(String id) {
		mongoClient.getDatabase(DB_NAME).getCollection(PARKING_ZONE_COLLECTION_NAME).deleteOne(Filters.eq("id", id));
	}

	private void addTestTicketToDatabase(String id, String vehiclePlate, String parkingZoneId, LocalDateTime entryTime,
			LocalDateTime exitTime, boolean paid, double totalCost) {
		mongoClient.getDatabase(DB_NAME).getCollection(PARKING_TICKET_COLLECTION_NAME)
				.insertOne(new Document().append("id", id).append("vehiclePlate", vehiclePlate)
						.append("parkingZoneId", parkingZoneId)
						.append("entryTime", entryTime != null ? entryTime.toString() : null)
						.append("exitTime", exitTime != null ? exitTime.toString() : null).append("paid", paid)
						.append("totalCost", totalCost));
	}

	private void removeTestTicketFromDatabase(String id) {
		mongoClient.getDatabase(DB_NAME).getCollection(PARKING_TICKET_COLLECTION_NAME).deleteOne(Filters.eq("id", id));
	}

}
