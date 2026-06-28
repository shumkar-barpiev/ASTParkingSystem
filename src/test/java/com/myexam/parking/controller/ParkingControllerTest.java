package com.myexam.parking.controller;

import static org.mockito.Mockito.*;
import static java.util.Arrays.asList;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.myexam.parking.model.ParkingTicket;
import com.myexam.parking.model.ParkingZone;
import com.myexam.parking.repository.*;
import com.myexam.parking.view.ParkingView;

public class ParkingControllerTest {

	@Mock
	private ParkingZoneRepository parkingZoneRepository;

	@Mock
	private ParkingTicketRepository parkingTicketRepository;

	@Mock
	private ParkingView parkingView;

	@InjectMocks
	private ParkingController parkingController;

	private AutoCloseable closeable;

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testAllParkingZones() {
		List<ParkingZone> zones = asList(new ParkingZone("1", "ParkingA", 100, 2.50, true));

		when(parkingZoneRepository.findAll()).thenReturn(zones);

		parkingController.allParkingZones();

		verify(parkingView).showAllParkingZones(zones);
	}

	@Test
	public void testNewParkingZoneWhenZoneDoesNotAlreadyExist() {
		ParkingZone zone = new ParkingZone("1", "ParkingA", 100, 2.50, true);

		when(parkingZoneRepository.findById("1")).thenReturn(null);

		parkingController.newParkingZone(zone);

		InOrder inOrder = inOrder(parkingZoneRepository, parkingView);
		inOrder.verify(parkingZoneRepository).save(zone);
		inOrder.verify(parkingView).parkingZoneAdded(zone);
	}

	@Test
	public void testNewParkingZoneWhenZoneAlreadyExists() {
		ParkingZone zoneToAdd = new ParkingZone("1", "ParkingNew", 50, 3.0, true);
		ParkingZone existingZone = new ParkingZone("1", "ParkingExisting", 100, 2.50, true);

		when(parkingZoneRepository.findById("1")).thenReturn(existingZone);

		parkingController.newParkingZone(zoneToAdd);

		verify(parkingView).showError("Already existing parking zone with id 1", existingZone);
		verifyNoMoreInteractions(ignoreStubs(parkingZoneRepository));
	}

	@Test
	public void testNewParkingZoneShouldShowErrorWhenRepositoryThrowsIllegalArgument() {
		ParkingZone zone = new ParkingZone("1", "ParkingA", 10, 2.5, true);
		when(parkingZoneRepository.findById("1")).thenReturn(null);

		doThrow(new IllegalArgumentException("Name cannot be null or blank")).when(parkingZoneRepository).save(zone);

		parkingController.newParkingZone(zone);

		verify(parkingView).showError("Name cannot be null or blank", zone);
		verify(parkingView, never()).parkingZoneAdded(any());
	}

	@Test
	public void testDeleteParkingZoneWhenZoneExists() {
		ParkingZone zoneToDelete = new ParkingZone("1", "ParkingA", 100, 2.50, true);

		when(parkingZoneRepository.findById("1")).thenReturn(zoneToDelete);

		parkingController.deleteParkingZone(zoneToDelete);

		InOrder inOrder = inOrder(parkingZoneRepository, parkingView);
		inOrder.verify(parkingZoneRepository).delete("1");
		inOrder.verify(parkingView).parkingZoneRemoved(zoneToDelete);
	}

	@Test
	public void testDeleteParkingZoneWhenZoneDoesNotExist() {
		ParkingZone zone = new ParkingZone("1", "ParkingA", 100, 2.50, true);

		when(parkingZoneRepository.findById("1")).thenReturn(null);

		parkingController.deleteParkingZone(zone);

		verify(parkingView).showError("No existing parking zone with id 1", zone);
		verifyNoMoreInteractions(ignoreStubs(parkingZoneRepository));
	}

	@Test
	public void testAllParkingTickets() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 10, 0);
		List<ParkingTicket> tickets = asList(new ParkingTicket("1", "ABC1234", "ParkingA", entry, null, false, 0.0));

		when(parkingTicketRepository.findAll()).thenReturn(tickets);

		parkingController.allParkingTickets();

		verify(parkingView).showAllParkingTickets(tickets);
	}

	@Test
	public void testNewParkingTicketWhenTicketDoesNotAlreadyExist() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 10, 0);
		ParkingTicket ticket = new ParkingTicket("1", "ABC1234", "ParkingA", entry, null, false, 0.0);

		when(parkingTicketRepository.findById("1")).thenReturn(null);

		parkingController.newParkingTicket(ticket);

		InOrder inOrder = inOrder(parkingTicketRepository, parkingView);
		inOrder.verify(parkingTicketRepository).save(ticket);
		inOrder.verify(parkingView).parkingTicketAdded(ticket);
	}

	@Test
	public void testNewParkingTicketWhenTicketAlreadyExists() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 10, 0);
		ParkingTicket ticketToAdd = new ParkingTicket("1", "ABC1234", "ParkingA", entry, null, false, 0.0);
		ParkingTicket existingTicket = new ParkingTicket("1", "QWE123", "ParkingB", entry, null, false, 0.0);

		when(parkingTicketRepository.findById("1")).thenReturn(existingTicket);

		parkingController.newParkingTicket(ticketToAdd);

		verify(parkingView).showError("Already existing parking ticket with id 1", existingTicket);
		verifyNoMoreInteractions(ignoreStubs(parkingTicketRepository));
	}

	@Test
	public void testDeleteParkingTicketWhenTicketExists() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 10, 0);
		ParkingTicket ticketToDelete = new ParkingTicket("1", "ABC1234", "ParkingA", entry, null, false, 0.0);

		when(parkingTicketRepository.findById("1")).thenReturn(ticketToDelete);

		parkingController.deleteParkingTicket(ticketToDelete);

		InOrder inOrder = inOrder(parkingTicketRepository, parkingView);
		inOrder.verify(parkingTicketRepository).delete("1");
		inOrder.verify(parkingView).parkingTicketRemoved(ticketToDelete);
	}

	@Test
	public void testDeleteParkingTicketWhenTicketDoesNotExist() {
		LocalDateTime entry = LocalDateTime.of(2026, 6, 20, 10, 0);
		ParkingTicket ticket = new ParkingTicket("1", "ABC1234", "ParkingA", entry, null, false, 0.0);

		when(parkingTicketRepository.findById("1")).thenReturn(null);

		parkingController.deleteParkingTicket(ticket);

		verify(parkingView).showError("No existing parking ticket with id 1", ticket);
		verifyNoMoreInteractions(ignoreStubs(parkingTicketRepository));
	}

	@Test
	public void testNewParkingTicketShouldSaveAndNotifyViewWhenAllChecksPass() {
		ParkingZone zone = new ParkingZone("z1", "ParkingA", 2, 2.5, true);
		ParkingTicket ticket = new ParkingTicket("t1", "ABC123", "z1", LocalDateTime.of(2026, 6, 26, 9, 0), null, false,
				0.0);

		when(parkingTicketRepository.findById("t1")).thenReturn(null);
		when(parkingTicketRepository.findActiveTicketByVehiclePlate("AB123")).thenReturn(null);
		when(parkingZoneRepository.findById("z1")).thenReturn(zone);
		when(parkingTicketRepository.countActiveTicketsByZoneId("z1")).thenReturn(1L);
		parkingController.newParkingTicket(ticket);

		verify(parkingTicketRepository).save(ticket);
		verify(parkingView).parkingTicketAdded(ticket);
	}

	@Test
	public void testNewParkingTicketShouldNotCheckZoneCapacityWhenPlateAlreadyActive() {
		ParkingTicket ticket = new ParkingTicket("t1", "ABC123", "z1", LocalDateTime.of(2026, 6, 26, 11, 0), null,
				false, 0.0);

		ParkingTicket existingActiveTicket = new ParkingTicket("t1", "ABC123", "z1",
				LocalDateTime.of(2026, 6, 26, 9, 0), null, false, 0.0);

		when(parkingTicketRepository.findById("t1")).thenReturn(null);
		when(parkingTicketRepository.findActiveTicketByVehiclePlate("ABC123")).thenReturn(existingActiveTicket);

		parkingController.newParkingTicket(ticket);

		verify(parkingZoneRepository, never()).findById(any());
		verify(parkingTicketRepository, never()).countActiveTicketsByZoneId(any());
	}

	@Test
	public void testNewParkingTicketShouldShowErrorWhenZoneIsFull() {
		ParkingZone zone = new ParkingZone("z1", "ParkingA", 2, 2.5, true);
		ParkingTicket ticket = new ParkingTicket("t1", "ABC123", "z1", LocalDateTime.of(2026, 6, 26, 11, 0), null,
				false, 0.0);

		when(parkingTicketRepository.findById("t3")).thenReturn(null);
		when(parkingTicketRepository.findActiveTicketByVehiclePlate("CDF456")).thenReturn(null);
		when(parkingZoneRepository.findById("z1")).thenReturn(zone);
		when(parkingTicketRepository.countActiveTicketsByZoneId("z1")).thenReturn(2L);

		parkingController.newParkingTicket(ticket);

		verify(parkingView).showError("Parking zone ParkingA is full", ticket);
		verify(parkingTicketRepository, never()).save(any());
		verify(parkingView, never()).parkingTicketAdded(any());
	}

	@Test
	public void testNewParkingTicketShouldShowErrorWhenRepositoryThrowsIllegalArgument() {
		ParkingZone zone = new ParkingZone("z1", "ParkingA", 2, 2.5, true);
		ParkingTicket ticket = new ParkingTicket("t1", "ABC123", "z1", LocalDateTime.of(2026, 6, 26, 9, 0), null, false,
				0.0);

		when(parkingTicketRepository.findById("t1")).thenReturn(null);
		when(parkingTicketRepository.findActiveTicketByVehiclePlate("ABC123")).thenReturn(null);
		when(parkingZoneRepository.findById("z1")).thenReturn(zone);
		when(parkingTicketRepository.countActiveTicketsByZoneId("z1")).thenReturn(1L);
		doThrow(new IllegalArgumentException("Vehicle plate cannot be null or blank")).when(parkingTicketRepository)
				.save(ticket);

		parkingController.newParkingTicket(ticket);

		verify(parkingView).showError("Vehicle plate cannot be null or blank", ticket);
		verify(parkingView, never()).parkingTicketAdded(any());
	}

	@Test
	public void testNewParkingTicketShouldShowErrorWithCorrectMessageWhenVehicleAlreadyHasActiveTicket() {
		ParkingTicket newTicket = new ParkingTicket("t2", "AB123", "z1", LocalDateTime.of(2026, 6, 26, 10, 0), null,
				false, 0.0);
		ParkingTicket activeTicket = new ParkingTicket("t1", "AB123", "z1", LocalDateTime.of(2026, 6, 26, 9, 0), null,
				false, 0.0);

		when(parkingTicketRepository.findById("t2")).thenReturn(null);
		when(parkingTicketRepository.findActiveTicketByVehiclePlate("AB123")).thenReturn(activeTicket);

		parkingController.newParkingTicket(newTicket);

		verify(parkingView).showError("Vehicle AB123 already has an active ticket", newTicket);
		verify(parkingView, never()).parkingTicketAdded(any());
		verify(parkingTicketRepository, never()).save(any());
	}

}