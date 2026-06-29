package com.myexam.parking.view.swing;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import org.mockito.ArgumentCaptor;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.myexam.parking.controller.ParkingController;
import com.myexam.parking.model.ParkingTicket;
import com.myexam.parking.model.ParkingZone;
import com.myexam.parking.view.swing.ParkingSwingView;

import java.time.LocalDateTime;

@RunWith(GUITestRunner.class)
public class ParkingSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private ParkingSwingView parkingSwingView;

	@Mock
	private ParkingController parkingController;

	private AutoCloseable closeable;

	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			parkingSwingView = new ParkingSwingView();
			parkingSwingView.setParkingController(parkingController);
			return parkingSwingView;
		});
		window = new FrameFixture(robot(), parkingSwingView);
		window.show();
	}

	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	private ParkingZone zone(String id, String name) {
		return new ParkingZone(id, name, 10, 2.5, true);
	}

	private ParkingTicket ticket(String id, String plate, String zoneId) {
		return new ParkingTicket(id, plate, zoneId, LocalDateTime.of(2026, 6, 26, 9, 0),
				LocalDateTime.of(2026, 6, 26, 11, 0), false, 5.0);
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Name"));
		window.textBox("nameTextField").requireEnabled();
		window.label(JLabelMatcher.withText("Capacity"));
		window.textBox("capacityTextField").requireEnabled();
		window.label(JLabelMatcher.withText("Rate"));
		window.textBox("rateTextField").requireEnabled();
		window.checkBox("isAvailableCheckBox").requireEnabled();
		window.button("parkingZoneSaveButton").requireEnabled();
		window.table("parkingZoneTable").requireVisible();

		window.tabbedPane().selectTab("Ticket Management");
		window.label(JLabelMatcher.withText("Vehicle Plate"));
		window.textBox("vehiclePlateTextField").requireEnabled();
		window.label(JLabelMatcher.withText("Zone"));
		window.comboBox("parkingZoneComboBox").requireEnabled();
		window.textBox("entryTimeTextField").requireEnabled();
		window.textBox("exitTimeTextField").requireEnabled();
		window.checkBox("isPaidCheckBox").requireEnabled();
		window.button("ticketSaveButton").requireEnabled();
		window.table("parkingTicketTable").requireVisible();

		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testShowAllParkingZonesShouldAddZonesToTable() {
		ParkingZone zone1 = zone("1", "Parking A");
		ParkingZone zone2 = zone("2", "Parking B");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone1, zone2)));

		String[][] contents = window.table("parkingZoneTable").contents();
		assertThat(contents.length).isEqualTo(2);
		assertThat(contents[0][0]).isEqualTo("1");
		assertThat(contents[0][1]).isEqualTo("Parking A");
		assertThat(contents[1][0]).isEqualTo("2");
		assertThat(contents[1][1]).isEqualTo("Parking B");
	}

	@Test
	@GUITest
	public void testShowErrorForZoneShouldShowMessageInErrorLabel() {
		GuiActionRunner.execute(() -> parkingSwingView.showError("error message", zone("1", "Parking A")));

		window.label("errorMessageLabel").requireText("error message");
	}

	@Test
	@GUITest
	public void testShowAllParkingTicketsShouldAddTicketsToTable() {
		ParkingZone zone = zone("z1", "Parking A");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone)));

		ParkingTicket ticket1 = ticket("t1", "AB123", "z1");
		ParkingTicket ticket2 = ticket("t2", "CD456", "z1");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingTickets(Arrays.asList(ticket1, ticket2)));

		window.tabbedPane().selectTab("Ticket Management");
		String[][] contents = window.table("parkingTicketTable").contents();
		assertThat(contents.length).isEqualTo(2);
		assertThat(contents[0][0]).isEqualTo("t1");
		assertThat(contents[0][1]).isEqualTo("AB123");
		assertThat(contents[1][0]).isEqualTo("t2");
		assertThat(contents[1][1]).isEqualTo("CD456");
	}

	@Test
	@GUITest
	public void testDeleteZoneButtonShouldDelegateToControllerDeleteParkingZone() {
		ParkingZone zone1 = zone("1", "Parking A");
		ParkingZone zone2 = zone("2", "Parking B");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone1, zone2)));

		window.table("parkingZoneTable").cell(org.assertj.swing.data.TableCell.row(1).column(5)).click();

		verify(parkingController).deleteParkingZone(zone2);
	}

	@Test
	@GUITest
	public void testDeleteZoneButtonShouldShowErrorWhenZoneNotFoundInMap() {
		ParkingZone zone = zone("1", "Parking A");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone)));

		GuiActionRunner.execute(() -> parkingSwingView.getParkingZoneByIdMap().remove("1"));

		window.table("parkingZoneTable").cell(org.assertj.swing.data.TableCell.row(0).column(5)).click();

		window.label("errorMessageLabel").requireText("No existing parking zone with id 1");
	}

	@Test
	@GUITest
	public void testDeleteTicketButtonShouldDelegateToControllerDeleteParkingTicket() {
		ParkingTicket ticket1 = ticket("t1", "AB123", "z1");
		ParkingTicket ticket2 = ticket("t2", "CD456", "z1");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingTickets(Arrays.asList(ticket1, ticket2)));

		window.tabbedPane().selectTab("Ticket Management");
		window.table("parkingTicketTable").cell(org.assertj.swing.data.TableCell.row(1).column(7)).click();

		verify(parkingController).deleteParkingTicket(ticket2);
	}

	@Test
	@GUITest
	public void testDeleteTicketButtonShouldShowErrorWhenTicketNotFoundInMap() {
		ParkingTicket ticket = ticket("t1", "AB123", "z1");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingTickets(Arrays.asList(ticket)));

		GuiActionRunner.execute(() -> parkingSwingView.setParkingController(parkingController));
		GuiActionRunner.execute(() -> parkingSwingView.getParkingTicketByIdMap().remove("t1"));

		window.tabbedPane().selectTab("Ticket Management");
		window.table("parkingTicketTable").cell(org.assertj.swing.data.TableCell.row(0).column(7)).click();

		robot().waitForIdle();

		window.label("errorMessageLabel").requireText("No existing parking ticket with id t1");
	}
}
