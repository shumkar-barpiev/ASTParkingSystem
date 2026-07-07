package com.myexam.parking.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
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
				LocalDateTime.of(2026, Month.JUNE, 26, 9, 0), null, false, 0.0);

		application("com.myexam.parking.app.ParkingSwingApp")
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

	@Test
	@GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		String[][] zoneRows = window.table("parkingZoneTable").contents();
		assertThat(Arrays.asList(zoneRows))
				.anySatisfy(row -> assertThat(row).contains(PARKING_ZONE_FIXTURE_1_ID, PARKING_ZONE_FIXTURE_1_NAME))
				.anySatisfy(row -> assertThat(row).contains(PARKING_ZONE_FIXTURE_2_ID, PARKING_ZONE_FIXTURE_2_NAME));

		window.tabbedPane().selectTab("Ticket Management");
		String[][] ticketRows = window.table("parkingTicketTable").contents();
		assertThat(Arrays.asList(ticketRows)).anySatisfy(
				row -> assertThat(row).contains(PARKING_TICKET_FIXTURE_1_ID, PARKING_TICKET_FIXTURE_1_PLATE));
	}

	@Test
	@GUITest
	public void testAddParkingZoneButtonSuccess() {
		window.textBox("nameTextField").enterText("Parking C");
		window.textBox("capacityTextField").enterText("20");
		window.textBox("rateTextField").enterText("4.0");
		window.checkBox("isAvailableCheckBox").check();
		window.button("parkingZoneSaveButton").click();

		assertThat(Arrays.asList(window.table("parkingZoneTable").contents()))
				.anySatisfy(row -> assertThat(row).contains("Parking C"));
	}

	@Test
	@GUITest
	public void testAddParkingZoneButtonError() {
		window.textBox("nameTextField").enterText("Parking Zone");
		window.textBox("rateTextField").enterText("1.0");
		window.button("parkingZoneSaveButton").click();

		window.label("errorMessageLabel").requireText("Capacity and Rate must be valid numbers.");
		assertThat(window.label("errorMessageLabel").text()).isEqualTo("Capacity and Rate must be valid numbers.");
	}

	@Test
	@GUITest
	public void testDeleteParkingZoneButtonSuccess() {
		window.table("parkingZoneTable").cell(TableCell.row(0).column(5)).click();

		assertThat(Arrays.asList(window.table("parkingZoneTable").contents()))
				.noneMatch(row -> row[1].equals(PARKING_ZONE_FIXTURE_1_NAME));
	}

	@Test
	@GUITest
	public void testDeleteParkingZoneButtonError() {
		removeTestZoneFromDatabase(PARKING_ZONE_FIXTURE_1_ID);

		window.table("parkingZoneTable").cell(TableCell.row(0).column(5)).click();

		window.label("errorMessageLabel").requireText("No existing parking zone with id " + PARKING_ZONE_FIXTURE_1_ID);
		assertThat(window.label("errorMessageLabel").text())
				.isEqualTo("No existing parking zone with id " + PARKING_ZONE_FIXTURE_1_ID);

	}

	@Test
	@GUITest
	public void testAddTicketButtonSuccess() {
		window.tabbedPane().selectTab("Ticket Management");
		window.textBox("vehiclePlateTextField").enterText("XYZ123");
		window.comboBox("parkingZoneComboBox").selectItem(PARKING_ZONE_FIXTURE_2_ID);
		window.textBox("entryTimeTextField").enterText("2026-06-26 09:00");
		window.textBox("exitTimeTextField").enterText("2026-06-26 11:00");
		window.button("ticketSaveButton").click();

		assertThat(Arrays.asList(window.table("parkingTicketTable").contents()))
				.anySatisfy(row -> assertThat(row).contains("XYZ123"));
	}

	@Test
	@GUITest
	public void testAddTicketButtonError() {
		window.tabbedPane().selectTab("Ticket Management");
		window.textBox("vehiclePlateTextField").enterText(PARKING_TICKET_FIXTURE_1_PLATE);
		window.comboBox("parkingZoneComboBox").selectItem(PARKING_ZONE_FIXTURE_2_ID);
		window.textBox("entryTimeTextField").enterText("2026-06-26 09:00");
		window.textBox("exitTimeTextField").enterText("2026-06-26 11:00");
		window.button("ticketSaveButton").click();

		window.label("errorMessageLabel")
				.requireText("Vehicle " + PARKING_TICKET_FIXTURE_1_PLATE + " already has an active ticket");

		assertThat(window.label("errorMessageLabel").text())
				.isEqualTo("Vehicle " + PARKING_TICKET_FIXTURE_1_PLATE + " already has an active ticket");
	}

	@Test
	@GUITest
	public void testDeleteTicketButtonSuccess() {
		window.tabbedPane().selectTab("Ticket Management");
		window.table("parkingTicketTable").cell(TableCell.row(0).column(7)).click();

		assertThat(Arrays.asList(window.table("parkingTicketTable").contents()))
				.noneMatch(row -> row[1].equals(PARKING_TICKET_FIXTURE_1_PLATE));
	}

	@Test
	@GUITest
	public void testDeleteTicketButtonError() {
		removeTestTicketFromDatabase(PARKING_TICKET_FIXTURE_1_ID);

		window.tabbedPane().selectTab("Ticket Management");
		window.table("parkingTicketTable").cell(TableCell.row(0).column(7)).click();

		window.label("errorMessageLabel")
				.requireText("No existing parking ticket with id " + PARKING_TICKET_FIXTURE_1_ID);
		assertThat(window.label("errorMessageLabel").text())
				.isEqualTo("No existing parking ticket with id " + PARKING_TICKET_FIXTURE_1_ID);

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
