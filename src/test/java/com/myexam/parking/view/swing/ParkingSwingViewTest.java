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

import java.time.LocalDateTime;
import java.time.Month;

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
		return new ParkingTicket(id, plate, zoneId, LocalDateTime.of(2026, Month.JUNE, 26, 9, 0),
				LocalDateTime.of(2026, Month.JUNE, 26, 11, 0), false, 5.0);
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
	public void testShowAllParkingZonesShouldClearExistingRowsBeforeAdding() {
		ParkingZone zone1 = zone("1", "Parking A");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone1)));
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone1)));

		assertThat(window.table("parkingZoneTable").contents().length).isEqualTo(1);
	}

	@Test
	@GUITest
	public void testShowAllParkingZonesShouldPopulateZoneComboBox() {
		ParkingZone zone1 = zone("1", "Parking A");
		ParkingZone zone2 = zone("2", "Parking B");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone1, zone2)));

		window.tabbedPane().selectTab("Ticket Management");
		assertThat(window.comboBox("parkingZoneComboBox").contents()).containsExactly("1", "2");
	}

	@Test
	@GUITest
	public void testShowAllParkingZonesShouldClearComboBoxBeforeRepopulating() {
		ParkingZone zone1 = zone("1", "Parking A");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone1)));
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone1)));

		window.tabbedPane().selectTab("Ticket Management");
		assertThat(window.comboBox("parkingZoneComboBox").contents()).hasSize(1);
	}

	@Test
	@GUITest
	public void testParkingZoneAddedShouldAddRowToTable() {
		ParkingZone zone = zone("1", "Parking A");
		GuiActionRunner.execute(() -> parkingSwingView.parkingZoneAdded(zone));

		String[][] contents = window.table("parkingZoneTable").contents();
		assertThat(contents.length).isEqualTo(1);
		assertThat(contents[0][0]).isEqualTo("1");
		assertThat(contents[0][1]).isEqualTo("Parking A");
	}

	@Test
	@GUITest
	public void testParkingZoneAddedShouldClearZoneForm() {
		window.textBox("nameTextField").enterText("Parking A");
		window.textBox("capacityTextField").enterText("10");
		window.textBox("rateTextField").enterText("2.5");
		window.checkBox("isAvailableCheckBox").check();

		GuiActionRunner.execute(() -> parkingSwingView.parkingZoneAdded(zone("1", "Parking A")));

		window.textBox("nameTextField").requireText("");
		window.textBox("capacityTextField").requireText("");
		window.textBox("rateTextField").requireText("");
		window.checkBox("isAvailableCheckBox").requireNotSelected();
	}

	@Test
	@GUITest
	public void testParkingZoneAddedShouldAddZoneToComboBox() {
		ParkingZone zone = zone("1", "Parking A");
		GuiActionRunner.execute(() -> parkingSwingView.parkingZoneAdded(zone));

		window.tabbedPane().selectTab("Ticket Management");
		assertThat(window.comboBox("parkingZoneComboBox").contents()).containsExactly("1");
	}

	@Test
	@GUITest
	public void testParkingZoneAddedShouldResetErrorLabel() {
		GuiActionRunner.execute(() -> parkingSwingView.showError("some error", new ParkingZone()));
		GuiActionRunner.execute(() -> parkingSwingView.parkingZoneAdded(zone("1", "Parking A")));

		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testParkingZoneRemovedShouldRemoveRowFromTable() {
		ParkingZone zone1 = zone("1", "Parking A");
		ParkingZone zone2 = zone("2", "Parking B");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone1, zone2)));
		GuiActionRunner.execute(() -> parkingSwingView.parkingZoneRemoved(zone1));

		String[][] contents = window.table("parkingZoneTable").contents();
		assertThat(contents.length).isEqualTo(1);
		assertThat(contents[0][0]).isEqualTo("2");
	}

	@Test
	@GUITest
	public void testParkingZoneRemovedShouldRemoveZoneFromComboBox() {
		ParkingZone zone1 = zone("1", "Parking A");
		ParkingZone zone2 = zone("2", "Parking B");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone1, zone2)));
		GuiActionRunner.execute(() -> parkingSwingView.parkingZoneRemoved(zone1));

		window.tabbedPane().selectTab("Ticket Management");
		assertThat(window.comboBox("parkingZoneComboBox").contents()).containsExactly("2");
	}

	@Test
	@GUITest
	public void testParkingZoneRemovedShouldResetErrorLabel() {
		ParkingZone zone = zone("1", "Parking A");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone)));
		GuiActionRunner.execute(() -> parkingSwingView.showError("some error", new ParkingZone()));
		GuiActionRunner.execute(() -> parkingSwingView.parkingZoneRemoved(zone));

		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testShowErrorForZoneShouldShowMessageInErrorLabel() {
		GuiActionRunner.execute(() -> parkingSwingView.showError("error message", zone("1", "Parking A")));

		window.label("errorMessageLabel").requireText("error message");
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

	@Test
	@GUITest
	public void testShowAllParkingTicketsShouldClearExistingRowsBeforeAdding() {
		ParkingTicket ticket = ticket("t1", "AB123", "z1");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingTickets(Arrays.asList(ticket)));
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingTickets(Arrays.asList(ticket)));

		window.tabbedPane().selectTab("Ticket Management");
		assertThat(window.table("parkingTicketTable").contents().length).isEqualTo(1);
	}

	@Test
	@GUITest
	public void testShowAllParkingTicketsShouldShowZoneNameInsteadOfId() {
		ParkingZone zone = zone("z1", "Parking A");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone)));

		ParkingTicket ticket = ticket("t1", "AB123", "z1");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingTickets(Arrays.asList(ticket)));

		window.tabbedPane().selectTab("Ticket Management");
		String[][] contents = window.table("parkingTicketTable").contents();
		assertThat(contents[0][2]).isEqualTo("Parking A");
	}

	@Test
	@GUITest
	public void testShowAllParkingTicketsShouldFallbackToZoneIdWhenZoneNotFound() {
		ParkingTicket ticket = ticket("t1", "AB123", "unknown-zone");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingTickets(Arrays.asList(ticket)));

		window.tabbedPane().selectTab("Ticket Management");
		String[][] contents = window.table("parkingTicketTable").contents();
		assertThat(contents[0][2]).isEqualTo("unknown-zone");
	}

	@Test
	@GUITest
	public void testParkingTicketAddedShouldAddRowToTable() {
		ParkingTicket ticket = ticket("t1", "AB123", "z1");
		GuiActionRunner.execute(() -> parkingSwingView.parkingTicketAdded(ticket));

		window.tabbedPane().selectTab("Ticket Management");
		String[][] contents = window.table("parkingTicketTable").contents();
		assertThat(contents.length).isEqualTo(1);
		assertThat(contents[0][0]).isEqualTo("t1");
		assertThat(contents[0][1]).isEqualTo("AB123");
	}

	@Test
	@GUITest
	public void testParkingTicketAddedShouldResetErrorLabel() {
		GuiActionRunner.execute(() -> parkingSwingView.showError("some error", new ParkingTicket()));
		GuiActionRunner.execute(() -> parkingSwingView.parkingTicketAdded(ticket("t1", "AB123", "z1")));

		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testParkingTicketAddedShouldClearTicketForm() {
		window.tabbedPane().selectTab("Ticket Management");
		window.textBox("vehiclePlateTextField").enterText("AB123");
		window.textBox("entryTimeTextField").enterText("2026-06-26 09:00");
		window.textBox("exitTimeTextField").enterText("2026-06-26 11:00");
		window.checkBox("isPaidCheckBox").check();

		GuiActionRunner.execute(() -> parkingSwingView.parkingTicketAdded(ticket("t1", "AB123", "z1")));

		window.textBox("vehiclePlateTextField").requireText("");
		window.textBox("entryTimeTextField").requireText("");
		window.textBox("exitTimeTextField").requireText("");
		window.checkBox("isPaidCheckBox").requireNotSelected();
	}

	@Test
	@GUITest
	public void testParkingTicketRemovedShouldRemoveRowFromTable() {
		ParkingTicket ticket1 = ticket("t1", "AB123", "z1");
		ParkingTicket ticket2 = ticket("t2", "CD456", "z1");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingTickets(Arrays.asList(ticket1, ticket2)));
		GuiActionRunner.execute(() -> parkingSwingView.parkingTicketRemoved(ticket1));

		window.tabbedPane().selectTab("Ticket Management");
		String[][] contents = window.table("parkingTicketTable").contents();
		assertThat(contents.length).isEqualTo(1);
		assertThat(contents[0][0]).isEqualTo("t2");
	}

	@Test
	@GUITest
	public void testParkingTicketRemovedShouldResetErrorLabel() {
		ParkingTicket ticket = ticket("t1", "AB123", "z1");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingTickets(Arrays.asList(ticket)));
		GuiActionRunner.execute(() -> parkingSwingView.showError("some error", new ParkingTicket()));
		GuiActionRunner.execute(() -> parkingSwingView.parkingTicketRemoved(ticket));

		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testShowErrorForTicketShouldShowMessageInErrorLabel() {
		GuiActionRunner.execute(() -> parkingSwingView.showError("ticket error", ticket("t1", "AB123", "z1")));

		window.label("errorMessageLabel").requireText("ticket error");
	}

	@Test
	@GUITest
	public void testZoneSaveButtonShouldDelegateToControllerNewParkingZone() {
		window.textBox("nameTextField").enterText("Parking A");
		window.textBox("capacityTextField").enterText("10");
		window.textBox("rateTextField").enterText("2.5");
		window.checkBox("isAvailableCheckBox").check();
		window.button("parkingZoneSaveButton").click();

		ArgumentCaptor<ParkingZone> captor = ArgumentCaptor.forClass(ParkingZone.class);
		verify(parkingController).newParkingZone(captor.capture());

		ParkingZone captured = captor.getValue();
		assertThat(captured.getName()).isEqualTo("Parking A");
		assertThat(captured.getCapacity()).isEqualTo(10);
		assertThat(captured.getHourlyRate()).isEqualTo(2.5);
		assertThat(captured.isAvailable()).isTrue();
		assertThat(captured.getId()).isNotNull();
	}

	@Test
	@GUITest
	public void testZoneSaveButtonShouldShowErrorWhenCapacityIsNotANumber() {
		window.textBox("nameTextField").enterText("Parking A");
		window.textBox("capacityTextField").enterText("abc");
		window.textBox("rateTextField").enterText("2.5");
		window.button("parkingZoneSaveButton").click();

		window.label("errorMessageLabel").requireText("Capacity and Rate must be valid numbers.");
	}

	@Test
	@GUITest
	public void testZoneSaveButtonShouldShowErrorWhenRateIsNotANumber() {
		window.textBox("nameTextField").enterText("Parking A");
		window.textBox("capacityTextField").enterText("10");
		window.textBox("rateTextField").enterText("abc");
		window.button("parkingZoneSaveButton").click();

		window.label("errorMessageLabel").requireText("Capacity and Rate must be valid numbers.");
	}

	@Test
	@GUITest
	public void testTicketSaveButtonShouldDelegateToControllerNewParkingTicket() {
		ParkingZone zone = zone("z1", "Parking A");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone)));

		window.tabbedPane().selectTab("Ticket Management");
		window.textBox("vehiclePlateTextField").enterText("AB123");
		window.comboBox("parkingZoneComboBox").selectItem("z1");
		window.textBox("entryTimeTextField").enterText("2026-06-26 09:00");
		window.textBox("exitTimeTextField").enterText("2026-06-26 11:00");
		window.checkBox("isPaidCheckBox").check();
		window.button("ticketSaveButton").click();

		ArgumentCaptor<ParkingTicket> captor = ArgumentCaptor.forClass(ParkingTicket.class);
		verify(parkingController).newParkingTicket(captor.capture());

		ParkingTicket captured = captor.getValue();
		assertThat(captured.getVehiclePlate()).isEqualTo("AB123");
		assertThat(captured.getParkingZoneId()).isEqualTo("z1");
		assertThat(captured.getEntryTime()).isEqualTo(LocalDateTime.of(2026, 6, 26, 9, 0));
		assertThat(captured.getExitTime()).isEqualTo(LocalDateTime.of(2026, 6, 26, 11, 0));
		assertThat(captured.isPaid()).isTrue();
		assertThat(captured.getTotalCost()).isEqualTo(0.0);
		assertThat(captured.getId()).isNotNull();
	}

	@Test
	@GUITest
	public void testTicketSaveButtonShouldShowErrorWhenEntryTimeFormatIsInvalid() {
		ParkingZone zone = zone("z1", "Parking A");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone)));

		window.tabbedPane().selectTab("Ticket Management");
		window.textBox("vehiclePlateTextField").enterText("AB123");
		window.comboBox("parkingZoneComboBox").selectItem("z1");
		window.textBox("entryTimeTextField").enterText("invalid-date");
		window.textBox("exitTimeTextField").enterText("2026-06-26 11:00");
		window.button("ticketSaveButton").click();

		window.label("errorMessageLabel").requireText("Entry/Exit Time must be in format: 2026-06-26 09:00");
	}

	@Test
	@GUITest
	public void testTicketSaveButtonShouldShowErrorWhenVehiclePlateIsEmpty() {
		ParkingZone zone = zone("z1", "Parking A");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone)));

		window.tabbedPane().selectTab("Ticket Management");
		window.textBox("vehiclePlateTextField").enterText(" ");
		window.comboBox("parkingZoneComboBox").selectItem("z1");
		window.textBox("entryTimeTextField").enterText("2026-06-26 09:00");
		window.textBox("exitTimeTextField").enterText("2026-06-26 11:00");
		window.button("ticketSaveButton").click();

		window.label("errorMessageLabel").requireText("Vehicle plate cannot be empty.");
	}

	@Test
	@GUITest
	public void testTicketSaveButtonShouldShowErrorWhenNoZoneSelected() {
		window.tabbedPane().selectTab("Ticket Management");
		window.textBox("vehiclePlateTextField").enterText("AB123");
		window.textBox("entryTimeTextField").enterText("2026-06-26 09:00");
		window.textBox("exitTimeTextField").enterText("2026-06-26 11:00");
		window.button("ticketSaveButton").click();

		window.label("errorMessageLabel").requireText("Please select a parking zone.");
	}

	@Test
	@GUITest
	public void testDeleteRowByIdShouldDoNothingWhenControllerIsNull() {
		GuiActionRunner.execute(() -> parkingSwingView.setParkingController(null));

		ParkingZone zone = zone("1", "Parking A");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone)));

		window.table("parkingZoneTable").cell(org.assertj.swing.data.TableCell.row(0).column(5)).click();

		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testParkingTicketAddedShouldClearTicketFormWhenComboBoxIsEmpty() {
		window.tabbedPane().selectTab("Ticket Management");
		window.textBox("vehiclePlateTextField").enterText("AB123");
		window.textBox("entryTimeTextField").enterText("2026-06-26 09:00");
		window.textBox("exitTimeTextField").enterText("2026-06-26 11:00");
		window.checkBox("isPaidCheckBox").check();

		GuiActionRunner.execute(() -> parkingSwingView.parkingTicketAdded(ticket("t1", "AB123", "z1")));

		window.textBox("vehiclePlateTextField").requireText("");
		window.textBox("entryTimeTextField").requireText("");
		window.textBox("exitTimeTextField").requireText("");
		window.checkBox("isPaidCheckBox").requireNotSelected();
		assertThat(window.comboBox("parkingZoneComboBox").contents().length).isZero();
	}

	@Test
	@GUITest
	public void testParkingTicketRemovedShouldDoNothingWhenTicketNotInTable() {
		ParkingTicket ticket1 = ticket("t1", "AB123", "z1");
		ParkingTicket ticket2 = ticket("t2", "CD456", "z1");

		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingTickets(Arrays.asList(ticket1)));

		GuiActionRunner.execute(() -> parkingSwingView.parkingTicketRemoved(ticket2));

		window.tabbedPane().selectTab("Ticket Management");
		assertThat(window.table("parkingTicketTable").contents().length).isEqualTo(1);
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testParkingTicketAddedShouldResetComboBoxSelectionWhenZonesArePresent() {
		ParkingZone zone1 = zone("z1", "Parking A");
		ParkingZone zone2 = zone("z2", "Parking B");
		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone1, zone2)));

		window.tabbedPane().selectTab("Ticket Management");
		window.comboBox("parkingZoneComboBox").selectItem("z2");

		GuiActionRunner.execute(() -> parkingSwingView.parkingTicketAdded(ticket("t1", "AB123", "z1")));

		assertThat(window.comboBox("parkingZoneComboBox").selectedItem()).isEqualTo("z1");
	}

	@Test
	@GUITest
	public void testParkingZoneRemovedShouldDoNothingWhenZoneNotInTable() {
		ParkingZone zone1 = zone("1", "Parking A");
		ParkingZone zone2 = zone("2", "Parking B");

		GuiActionRunner.execute(() -> parkingSwingView.showAllParkingZones(Arrays.asList(zone1)));

		GuiActionRunner.execute(() -> parkingSwingView.parkingZoneRemoved(zone2));

		assertThat(window.table("parkingZoneTable").contents().length).isEqualTo(1);
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testDeleteRowByIdShouldDoNothingWhenIdIsNull() {
		GuiActionRunner.execute(() -> {
			javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) parkingSwingView
					.getTableParkingZones().getModel();
			parkingSwingView.showAllParkingZones(java.util.Collections.emptyList());
			model.addRow(new Object[] { null, "Parking A", 5, 1.0, true, "Delete" });
		});

		window.table("parkingZoneTable").cell(org.assertj.swing.data.TableCell.row(0).column(5)).click();

		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testDeleteRowByIdShouldDoNothingWhenControllerAndIdAreBothNull() {
		GuiActionRunner.execute(() -> parkingSwingView.setParkingController(null));
		GuiActionRunner.execute(() -> {
			javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) parkingSwingView
					.getTableParkingZones().getModel();
			parkingSwingView.showAllParkingZones(java.util.Collections.emptyList());
			model.addRow(new Object[] { null, "Parking A", 5, 1.0, true, "Delete" });
		});

		window.table("parkingZoneTable").cell(org.assertj.swing.data.TableCell.row(0).column(5)).click();

		window.label("errorMessageLabel").requireText(" ");
	}
}
