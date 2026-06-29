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
	}}
