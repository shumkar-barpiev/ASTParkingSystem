package com.myexam.parking.view.swing;

import com.myexam.parking.controller.*;
import com.myexam.parking.model.*;
import com.myexam.parking.view.ParkingView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.WindowConstants;

public class ParkingSwingView extends JFrame implements ParkingView {
	private static final long serialVersionUID = 1L;
	private static final String DELETE_BTN = "Delete";

	private JPanel mainPanel;

	private JTextField nameTextField;
	private JTextField capacityTextField;
	private JTextField rateTextField;
	private JCheckBox isAvailableCheckBox;

	private JTextField vehiclePlateTextField;
	private JTextField entryTimeTextField;
	private JTextField exitTimeTextField;
	private JCheckBox isPaidCheckBox;

	private JTable parkingZoneTable;
	private JTable parkingTicketTable;

	private JLabel errorMessageLabel;
	private JComboBox<String> parkingZoneComboBox;

	private transient Map<String, ParkingZone> parkingZoneByIdMap = new java.util.HashMap<>();
	private transient Map<String, ParkingTicket> parkingTicketByIdMap = new java.util.HashMap<>();

	private transient ParkingController parkingController;

	public ParkingSwingView() {
		setResizable(false);
		setTitle("Parking Management System");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 820, 550);

		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainPanel);
		mainPanel.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(javax.swing.SwingConstants.TOP);
		tabbedPane.setForeground(new Color(0, 0, 0));
		mainPanel.add(tabbedPane, BorderLayout.CENTER);

		JPanel errorPanel = new JPanel();
		errorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));
		mainPanel.add(errorPanel, BorderLayout.SOUTH);

		errorMessageLabel = new JLabel(" ");
		errorMessageLabel.setName("errorMessageLabel");
		errorMessageLabel.setForeground(java.awt.Color.RED);
		errorMessageLabel.setFont(new java.awt.Font("Lucida Grande", java.awt.Font.BOLD, 12));
		errorPanel.add(errorMessageLabel);

		JPanel parkingPlacePanel = new JPanel(new BorderLayout(0, 0));
		parkingPlacePanel.setName("parkingPlacePanel");
		tabbedPane.addTab("Parking Places", null, parkingPlacePanel, null);

		JPanel parkingZoneFormPanel = new JPanel();
		parkingZoneFormPanel.setName("parkingZoneFormPanel");
		parkingZoneFormPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
		parkingPlacePanel.add(parkingZoneFormPanel, BorderLayout.NORTH);

		JLabel nameLabel = new JLabel("Name");
		nameLabel.setName("nameLabel");
		parkingZoneFormPanel.add(nameLabel);

		nameTextField = new JTextField(10);
		nameTextField.setName("nameTextField");
		parkingZoneFormPanel.add(nameTextField);

		JLabel capacityLabel = new JLabel("Capacity");
		capacityLabel.setName("capacityLabel");
		parkingZoneFormPanel.add(capacityLabel);

		capacityTextField = new JTextField(10);
		capacityTextField.setName("capacityTextField");
		parkingZoneFormPanel.add(capacityTextField);

		JLabel rateLabel = new JLabel("Rate");
		rateLabel.setName("rateLabel");
		parkingZoneFormPanel.add(rateLabel);

		rateTextField = new JTextField(10);
		rateTextField.setName("rateTextField");
		parkingZoneFormPanel.add(rateTextField);

		isAvailableCheckBox = new JCheckBox("Available");
		isAvailableCheckBox.setName("isAvailableCheckBox");
		parkingZoneFormPanel.add(isAvailableCheckBox);

		JButton parkingZoneSaveBtn = new JButton("Save");
		parkingZoneSaveBtn.setName("parkingZoneSaveButton");
		parkingZoneFormPanel.add(parkingZoneSaveBtn);

		parkingZoneSaveBtn.addActionListener(e -> {
			try {
				String id = java.util.UUID.randomUUID().toString();
				String name = nameTextField.getText().trim();
				int capacity = Integer.parseInt(capacityTextField.getText().trim());
				double rate = Double.parseDouble(rateTextField.getText().trim());
				boolean isAvailable = isAvailableCheckBox.isSelected();

				ParkingZone newZone = new ParkingZone(id, name, capacity, rate, isAvailable);

				parkingController.newParkingZone(newZone);
			} catch (NumberFormatException ex) {
				showError("Capacity and Rate must be valid numbers.", new ParkingZone());
			}
		});

		JScrollPane parkingZoneTableScrollPane = new JScrollPane();
		parkingZoneTableScrollPane.setName("parkingZoneTableScrollPane");
		parkingPlacePanel.add(parkingZoneTableScrollPane, BorderLayout.CENTER);

		parkingZoneTable = new JTable();
		parkingZoneTable.setName("parkingZoneTable");
		parkingZoneTable.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "ID", "Name", "Capacity", "Hourly Rate", "Available", "Actions" }));
		parkingZoneTableScrollPane.setViewportView(parkingZoneTable);

		JPanel parkingTicketPanel = new JPanel(new BorderLayout(0, 0));
		parkingTicketPanel.setName("parkingTicketPanel");
		tabbedPane.addTab("Ticket Management", null, parkingTicketPanel, null);

		JPanel parkingTicketFormPanel = new JPanel(new java.awt.GridBagLayout());
		parkingTicketFormPanel.setName("parkingTicketFormPanel");
		parkingTicketFormPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
		parkingTicketPanel.add(parkingTicketFormPanel, BorderLayout.NORTH);

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.insets = new java.awt.Insets(5, 8, 5, 8);
		gbc.anchor = java.awt.GridBagConstraints.WEST;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		JLabel vehiclePlateLabel = new JLabel("Vehicle Plate");
		vehiclePlateLabel.setName("vehiclePlateLabel");
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		parkingTicketFormPanel.add(vehiclePlateLabel, gbc);

		vehiclePlateTextField = new JTextField(12);
		vehiclePlateTextField.setName("vehiclePlateTextField");
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		parkingTicketFormPanel.add(vehiclePlateTextField, gbc);

		JLabel parkingZoneLabel = new JLabel("Zone");
		parkingZoneLabel.setName("parkingZoneLabel");
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 0;
		parkingTicketFormPanel.add(parkingZoneLabel, gbc);

		parkingZoneComboBox = new JComboBox<>();
		parkingZoneComboBox.setName("parkingZoneComboBox");
		parkingZoneComboBox.setPrototypeDisplayValue("XXXXXXXXXXXX");
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.weightx = 1;
		parkingTicketFormPanel.add(parkingZoneComboBox, gbc);

		JLabel entryTimeLabel = new JLabel("Entry Time");
		entryTimeLabel.setName("entryTimeLabel");
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		parkingTicketFormPanel.add(entryTimeLabel, gbc);

		entryTimeTextField = new JTextField(14);
		entryTimeTextField.setName("entryTimeTextField");
		entryTimeTextField.setToolTipText("e.g. 2026-06-26 09:00");
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		parkingTicketFormPanel.add(entryTimeTextField, gbc);

		JLabel exitTimeLabel = new JLabel("Exit Time");
		exitTimeLabel.setName("exitTimeLabel");
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.weightx = 0;
		parkingTicketFormPanel.add(exitTimeLabel, gbc);

		exitTimeTextField = new JTextField(14);
		exitTimeTextField.setName("exitTimeTextField");
		exitTimeTextField.setToolTipText("e.g. 2026-06-26 11:00");
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.weightx = 1;
		parkingTicketFormPanel.add(exitTimeTextField, gbc);

		isPaidCheckBox = new JCheckBox("Paid");
		isPaidCheckBox.setName("isPaidCheckBox");
		gbc.gridx = 4;
		gbc.gridy = 1;
		gbc.weightx = 0;
		parkingTicketFormPanel.add(isPaidCheckBox, gbc);

		JButton ticketSaveBtn = new JButton("Save");
		ticketSaveBtn.setName("ticketSaveButton");
		gbc.gridx = 5;
		gbc.gridy = 1;
		gbc.weightx = 0;
		parkingTicketFormPanel.add(ticketSaveBtn, gbc);

		ticketSaveBtn.addActionListener(e -> {
			try {
				String vehiclePlate = vehiclePlateTextField.getText().trim();
				String zoneId = (String) parkingZoneComboBox.getSelectedItem();

				if (vehiclePlate.isEmpty()) {
					showError("Vehicle plate cannot be empty.", new ParkingTicket());
					return;
				}
				if (zoneId == null) {
					showError("Please select a parking zone.", new ParkingTicket());
					return;
				}

				java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
						.ofPattern("yyyy-MM-dd HH:mm");
				java.time.LocalDateTime entryTime = java.time.LocalDateTime
						.parse(entryTimeTextField.getText().trim().replace("T", " "), formatter);
				java.time.LocalDateTime exitTime = java.time.LocalDateTime
						.parse(exitTimeTextField.getText().trim().replace("T", " "), formatter);

				boolean paid = isPaidCheckBox.isSelected();
				String id = java.util.UUID.randomUUID().toString();

				ParkingTicket newTicket = new ParkingTicket(id, vehiclePlate, zoneId, entryTime, exitTime, paid, 0.0);

				parkingController.newParkingTicket(newTicket);

			} catch (java.time.format.DateTimeParseException ex) {
				showError("Entry/Exit Time must be in format: 2026-06-26 09:00", new ParkingTicket());
			}
		});

		JScrollPane parkingTicketTableScrollPane = new JScrollPane();
		parkingTicketTableScrollPane.setName("parkingTicketTableScrollPane");
		parkingTicketPanel.add(parkingTicketTableScrollPane, BorderLayout.CENTER);

		parkingTicketTable = new JTable();
		parkingTicketTable.setName("parkingTicketTable");
		parkingTicketTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "ID", "Vehicle Plate",
				"Zone", "Entry Time", "Exit Time", "Paid", "Total Cost", "Actions" }));
		parkingTicketTableScrollPane.setViewportView(parkingTicketTable);

	}

	@Override
	public void showAllParkingZones(List<ParkingZone> zones) {
		DefaultTableModel model = (DefaultTableModel) parkingZoneTable.getModel();
		model.setRowCount(0);

		parkingZoneComboBox.removeAllItems();
		parkingZoneByIdMap.clear();

		setupDeleteColumn(parkingZoneTable, 5, false);

		for (ParkingZone zone : zones) {
			parkingZoneComboBox.addItem(zone.getId());
			parkingZoneByIdMap.put(zone.getId(), zone);

			model.addRow(new Object[] { zone.getId(), zone.getName(), zone.getCapacity(), zone.getHourlyRate(),
					zone.isAvailable(), DELETE_BTN });
		}
	}

	@Override
	public void parkingZoneAdded(ParkingZone zone) {
		DefaultTableModel model = (DefaultTableModel) parkingZoneTable.getModel();

		setupDeleteColumn(parkingZoneTable, 5, false);

		parkingZoneComboBox.addItem(zone.getId());
		parkingZoneByIdMap.put(zone.getId(), zone);

		model.addRow(new Object[] { zone.getId(), zone.getName(), zone.getCapacity(), zone.getHourlyRate(),
				zone.isAvailable(), DELETE_BTN });

		clearZoneForm();
		resetErrorLabel();
	}

	@Override
	public void parkingZoneRemoved(ParkingZone zone) {
		DefaultTableModel model = (DefaultTableModel) parkingZoneTable.getModel();
		for (int i = 0; i < model.getRowCount(); i++) {
			if (zone.getId().equals(model.getValueAt(i, 0))) {
				model.removeRow(i);
				break;
			}
		}
		parkingZoneComboBox.removeItem(zone.getId());
		parkingZoneByIdMap.remove(zone.getId());
		resetErrorLabel();
	}

	@Override
	public void showError(String message, ParkingZone zone) {
		errorMessageLabel.setText(message);
	}

	@Override
	public void showAllParkingTickets(List<ParkingTicket> tickets) {
		DefaultTableModel model = (DefaultTableModel) parkingTicketTable.getModel();
		model.setRowCount(0);

		parkingTicketByIdMap.clear();
		setupDeleteColumn(parkingTicketTable, 7, true);

		for (ParkingTicket ticket : tickets) {
			parkingTicketByIdMap.put(ticket.getId(), ticket);
			model.addRow(ticketToRow(ticket));
		}
	}

	@Override
	public void parkingTicketAdded(ParkingTicket ticket) {
		DefaultTableModel model = (DefaultTableModel) parkingTicketTable.getModel();

		setupDeleteColumn(parkingTicketTable, 7, true);

		parkingTicketByIdMap.put(ticket.getId(), ticket);
		model.addRow(ticketToRow(ticket));

		clearTicketForm();
		resetErrorLabel();
	}

	@Override
	public void parkingTicketRemoved(ParkingTicket ticket) {
		DefaultTableModel model = (DefaultTableModel) parkingTicketTable.getModel();
		for (int i = 0; i < model.getRowCount(); i++) {
			if (ticket.getId().equals(model.getValueAt(i, 0))) {
				model.removeRow(i);
				break;
			}
		}
		resetErrorLabel();
	}

	@Override
	public void showError(String message, ParkingTicket ticket) {
		errorMessageLabel.setText(message);
	}

	public void setParkingController(ParkingController parkingController) {
		this.parkingController = parkingController;
	}

	Map<String, ParkingZone> getParkingZoneByIdMap() {
		return parkingZoneByIdMap;
	}

	Map<String, ParkingTicket> getParkingTicketByIdMap() {
		return parkingTicketByIdMap;
	}

	public JTable getTableParkingZones() {
		return parkingZoneTable;
	}

	private void resetErrorLabel() {
		errorMessageLabel.setText(" ");
	}

	private void clearZoneForm() {
		nameTextField.setText("");
		capacityTextField.setText("");
		rateTextField.setText("");
		isAvailableCheckBox.setSelected(false);
	}

	private void clearTicketForm() {
		vehiclePlateTextField.setText("");
		entryTimeTextField.setText("");
		exitTimeTextField.setText("");
		isPaidCheckBox.setSelected(false);
		if (parkingZoneComboBox.getItemCount() > 0)
			parkingZoneComboBox.setSelectedIndex(0);
	}

	private void setupDeleteColumn(JTable table, int columnIndex, boolean isTicketTable) {
		table.setRowHeight(32);
		table.getColumnModel().getColumn(columnIndex).setCellRenderer(new DeleteButtonRenderer());
		table.getColumnModel().getColumn(columnIndex).setCellEditor(new DeleteButtonEditor(table, isTicketTable));
	}

	private Object[] ticketToRow(ParkingTicket ticket) {
		ParkingZone zone = parkingZoneByIdMap.get(ticket.getParkingZoneId());

		String zoneName = zone != null ? zone.getName() : ticket.getParkingZoneId();

		return new Object[] { ticket.getId(), ticket.getVehiclePlate(), zoneName, ticket.getEntryTime(),
				ticket.getExitTime(), ticket.isPaid(), ticket.getTotalCost(), DELETE_BTN };
	}

	private static class DeleteButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
		private static final long serialVersionUID = 1L;

		DeleteButtonRenderer() {
			setText(DELETE_BTN);
		}

		@Override
		public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			return this;
		}
	}

	private class DeleteButtonEditor extends javax.swing.AbstractCellEditor
			implements javax.swing.table.TableCellEditor {
		private static final long serialVersionUID = 1L;
		private final JButton button = new JButton(DELETE_BTN);
		private String idToDelete;

		DeleteButtonEditor(JTable table, boolean isTicketTable) {
			button.addActionListener(e -> {
				String id = idToDelete;
				fireEditingStopped();

				javax.swing.SwingUtilities.invokeLater(() -> deleteRowById(id, isTicketTable));
			});
		}

		@Override
		public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {

			int modelRow = table.convertRowIndexToModel(row);
			idToDelete = table.getModel().getValueAt(modelRow, 0).toString();

			return button;
		}

		@Override
		public Object getCellEditorValue() {
			return DELETE_BTN;
		}
	}

	private void deleteRowById(String id, boolean isTicketTable) {
		if (parkingController == null) {
			return;
		}

		if (isTicketTable) {
			ParkingTicket ticket = parkingTicketByIdMap.get(id);

			if (ticket != null) {
				parkingController.deleteParkingTicket(ticket);
			} else {
				showError("No existing parking ticket with id " + id, new ParkingTicket());
			}
		} else {
			ParkingZone zone = parkingZoneByIdMap.get(id);

			if (zone != null) {
				parkingController.deleteParkingZone(zone);
			} else {
				showError("No existing parking zone with id " + id, new ParkingZone());
			}
		}
	}

}
