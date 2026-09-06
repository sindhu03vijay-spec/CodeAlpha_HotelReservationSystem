import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Java Swing GUI for the Aurelia Grand Hotel Reservation System.
 * A navy + gold "boutique hotel" theme, built entirely with custom-painted
 * Swing components (no external UI libraries) — gradient header, room cards,
 * KPI dashboard with a hand-drawn occupancy chart, and a badge-styled table.
 */
public class HotelReservationGUI extends JFrame {

    private final Hotel hotel;

    // ---- Dashboard tab ----
    private StatCard totalCard, availableCard, bookedCard, revenueCard;
    private OccupancyChartPanel occupancyChart;
    private DonutChart occupancyDonut;
    private JPanel recentActivityList;
    private JLabel clockLabel;

    // ---- Book a Room tab ----
    private JPanel roomGridPanel;
    private RoomCard selectedCard;
    private JLabel selectedRoomLabel;
    private JTextField nameField, phoneField;
    private JSpinner checkInSpinner, checkOutSpinner;
    private String currentCategoryFilter = "All";
    private final Map<String, Theme.Chip> chips = new LinkedHashMap<>();

    // ---- Manage Reservations tab ----
    private DefaultTableModel reservationsModel;
    private JTable reservationsTable;

    // ---- Room Overview tab ----
    private JPanel allRoomsGridPanel;

    public HotelReservationGUI() {
        hotel = new Hotel();
        hotel.loadData();

        setTitle("Aurelia Grand Hotel — Reservation System");
        setSize(1150, 720);
        setMinimumSize(new Dimension(950, 620));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.CREAM);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTabs(), BorderLayout.CENTER);
        setContentPane(root);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hotel.saveData();
                dispose();
                System.exit(0);
            }
        });

        refreshEverything();

        // live clock on the dashboard banner — ticks every second
        javax.swing.Timer clockTimer = new javax.swing.Timer(1000, e -> updateClock());
        clockTimer.setInitialDelay(0);
        clockTimer.start();
    }

    private void updateClock() {
        if (clockLabel != null) {
            clockLabel.setText(java.time.format.DateTimeFormatter
                    .ofPattern("hh:mm:ss a").format(java.time.LocalTime.now()));
        }
    }

    // ============================================================
    // HEADER
    // ============================================================
    private JComponent buildHeader() {
        Theme.GradientPanel header = new Theme.GradientPanel(Theme.NAVY_DARK, Theme.NAVY);
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(22, 30, 22, 30));
        header.setPreferredSize(new Dimension(100, 92));

        JLabel title = new JLabel("\u2726 Aurelia Grand Hotel");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.GOLD_LIGHT);

        JLabel subtitle = new JLabel("Where every stay feels like an upgrade");
        subtitle.setFont(Theme.FONT_SUB);
        subtitle.setForeground(new Color(210, 218, 232));

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        textStack.add(title);
        textStack.add(Box.createVerticalStrut(4));
        textStack.add(subtitle);

        header.add(textStack, BorderLayout.WEST);
        return header;
    }

    // ============================================================
    // TABS
    // ============================================================
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.FONT_BODY_B);
        tabs.setBackground(Theme.CREAM);
        tabs.addTab("  \u2605 Dashboard  ", buildDashboardPanel());
        tabs.addTab("  \u2302 Book a Room  ", buildBookingPanel());
        tabs.addTab("  \u270E My Reservations  ", buildReservationsPanel());
        tabs.addTab("  \u25A6 Room Overview  ", buildRoomOverviewPanel());
        return tabs;
    }

    private void refreshEverything() {
        refreshDashboard();
        refreshRoomGrid();
        refreshReservationsTable();
        refreshAllRoomsGrid();
    }

    // ============================================================
    // TAB 1: DASHBOARD
    // ============================================================
    private JPanel buildDashboardPanel() {
        Theme.GradientPanel panel = new Theme.GradientPanel(new Color(255, 253, 246), Theme.CREAM);
        panel.setLayout(new BorderLayout(16, 16));
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // ---- greeting banner ----
        Theme.GradientPanel banner = new Theme.GradientPanel(Theme.NAVY, Theme.NAVY_LIGHT);
        banner.setLayout(new BorderLayout());
        banner.setBorder(new EmptyBorder(18, 22, 18, 22));
        banner.setPreferredSize(new Dimension(100, 78));

        String greeting = greetingForNow();
        JLabel greetLabel = new JLabel(greeting + " \u2014 here's how the hotel looks today");
        greetLabel.setFont(Theme.FONT_HEADER);
        greetLabel.setForeground(Color.WHITE);

        JLabel dateLabel = new JLabel(java.time.format.DateTimeFormatter
                .ofPattern("EEEE, d MMMM yyyy").format(LocalDate.now()));
        dateLabel.setFont(Theme.FONT_SMALL);
        dateLabel.setForeground(new Color(210, 218, 232));

        JPanel bannerText = new JPanel();
        bannerText.setOpaque(false);
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        greetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bannerText.add(greetLabel);
        bannerText.add(Box.createVerticalStrut(3));
        bannerText.add(dateLabel);
        banner.add(bannerText, BorderLayout.WEST);

        clockLabel = new JLabel("--:--:-- --");
        clockLabel.setFont(Theme.FONT_BIG_NUM.deriveFont(22f));
        clockLabel.setForeground(Theme.GOLD_LIGHT);
        JPanel clockWrap = new JPanel(new GridBagLayout());
        clockWrap.setOpaque(false);
        clockWrap.add(clockLabel);
        banner.add(clockWrap, BorderLayout.EAST);

        // ---- KPI stat row ----
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        totalCard = new StatCard("\u2302", "Total Rooms", "0", Theme.NAVY);
        availableCard = new StatCard("\u2713", "Available Now", "0", Theme.SUCCESS);
        bookedCard = new StatCard("\u2605", "Currently Booked", "0", Theme.DANGER);
        revenueCard = new StatCard("\u20B9", "Revenue Collected", "\u20B90", Theme.GOLD);
        statsRow.add(totalCard);
        statsRow.add(availableCard);
        statsRow.add(bookedCard);
        statsRow.add(revenueCard);

        JPanel topStack = new JPanel();
        topStack.setOpaque(false);
        topStack.setLayout(new BoxLayout(topStack, BoxLayout.Y_AXIS));
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        topStack.add(banner);
        topStack.add(Box.createVerticalStrut(16));
        topStack.add(statsRow);

        // ---- middle row: donut + bar chart + recent activity ----
        JPanel middleRow = new JPanel(new GridLayout(1, 3, 16, 0));
        middleRow.setOpaque(false);

        occupancyDonut = new DonutChart();
        occupancyChart = new OccupancyChartPanel();

        Theme.RoundedCard activityCard = new Theme.RoundedCard(18);
        activityCard.setLayout(new BorderLayout());
        activityCard.setBorder(new EmptyBorder(18, 22, 18, 22));
        JLabel activityTitle = new JLabel("Recent Bookings");
        activityTitle.setFont(Theme.FONT_HEADER);
        activityTitle.setForeground(Theme.NAVY_DARK);
        activityCard.add(activityTitle, BorderLayout.NORTH);

        recentActivityList = new JPanel();
        recentActivityList.setOpaque(false);
        recentActivityList.setLayout(new BoxLayout(recentActivityList, BoxLayout.Y_AXIS));
        JScrollPane activityScroll = new JScrollPane(recentActivityList);
        activityScroll.setOpaque(false);
        activityScroll.getViewport().setOpaque(false);
        activityScroll.setBorder(new EmptyBorder(10, 0, 0, 0));
        activityCard.add(activityScroll, BorderLayout.CENTER);

        middleRow.add(occupancyDonut);
        middleRow.add(occupancyChart);
        middleRow.add(activityCard);

        panel.add(topStack, BorderLayout.NORTH);
        panel.add(middleRow, BorderLayout.CENTER);

        return panel;
    }

    private String greetingForNow() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) return "Good morning";
        if (hour < 17) return "Good afternoon";
        return "Good evening";
    }

    private void refreshDashboard() {
        totalCard.setValue(String.valueOf(hotel.getTotalRoomCount()));
        availableCard.setValue(String.valueOf(hotel.getAvailableRoomCount()));
        bookedCard.setValue(String.valueOf(hotel.getBookedRoomCount()));
        revenueCard.setValue("\u20B9" + (int) hotel.getTotalRevenueCollected());

        occupancyChart.setData(hotel.getOccupancyByCategory());

        int total = hotel.getTotalRoomCount();
        int booked = hotel.getBookedRoomCount();
        int pct = total == 0 ? 0 : (int) Math.round(100.0 * booked / total);
        occupancyDonut.setPercent(pct);

        recentActivityList.removeAll();
        List<Reservation> all = hotel.getAllReservations();
        int shown = 0;
        for (int i = all.size() - 1; i >= 0 && shown < 8; i--, shown++) {
            recentActivityList.add(buildActivityRow(all.get(i)));
            recentActivityList.add(Box.createVerticalStrut(8));
        }
        if (shown == 0) {
            JLabel empty = new JLabel("No bookings yet — make one from the Book a Room tab.");
            empty.setFont(Theme.FONT_BODY);
            empty.setForeground(Theme.MUTED_TEXT);
            recentActivityList.add(empty);
        }
        recentActivityList.revalidate();
        recentActivityList.repaint();
    }

    private JPanel buildActivityRow(Reservation r) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel info = new JLabel(r.getGuestName() + "  \u2022  Room " + r.getRoomNumber());
        info.setFont(Theme.FONT_BODY);
        info.setForeground(Theme.NAVY_DARK);

        Color badgeColor = r.getStatus().equals("CANCELLED") ? Theme.DANGER
                : (r.isPaymentDone() ? Theme.SUCCESS : Theme.WARNING);
        String badgeText = r.getStatus().equals("CANCELLED") ? "CANCELLED"
                : (r.isPaymentDone() ? "PAID" : "PENDING");
        Theme.Badge badge = new Theme.Badge(badgeText, badgeColor);
        badge.setPreferredSize(new Dimension(90, 22));

        row.add(info, BorderLayout.WEST);
        row.add(badge, BorderLayout.EAST);
        return row;
    }

    // ============================================================
    // TAB 2: BOOK A ROOM
    // ============================================================
    private JPanel buildBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBackground(Theme.CREAM);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // ---- filter chips ----
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        String[] categories = {"All", "Standard", "Deluxe", "Suite"};
        for (String c : categories) {
            Color accent = c.equals("All") ? Theme.NAVY : Theme.categoryColor(c);
            Theme.Chip chip = new Theme.Chip(c, accent);
            chip.setSelected(c.equals("All"));
            chip.addActionListener(e -> {
                currentCategoryFilter = c;
                for (Map.Entry<String, Theme.Chip> entry : chips.entrySet()) {
                    entry.getValue().setSelected(entry.getKey().equals(c));
                }
                refreshRoomGrid();
            });
            chips.put(c, chip);
            filterRow.add(chip);
        }

        // ---- room grid ----
        roomGridPanel = new JPanel(new GridLayout(0, 4, 16, 16));
        roomGridPanel.setOpaque(false);
        JScrollPane gridScroll = new JScrollPane(roomGridPanel);
        gridScroll.setBorder(null);
        gridScroll.getViewport().setOpaque(false);
        gridScroll.setOpaque(false);
        gridScroll.getVerticalScrollBar().setUnitIncrement(14);

        JPanel gridWrap = new JPanel(new BorderLayout());
        gridWrap.setOpaque(false);
        gridWrap.add(filterRow, BorderLayout.NORTH);
        JPanel gridSpacer = new JPanel(new BorderLayout());
        gridSpacer.setOpaque(false);
        gridSpacer.setBorder(new EmptyBorder(12, 0, 0, 0));
        gridSpacer.add(gridScroll, BorderLayout.CENTER);
        gridWrap.add(gridSpacer, BorderLayout.CENTER);

        panel.add(gridWrap, BorderLayout.CENTER);
        panel.add(buildBookingForm(), BorderLayout.SOUTH);

        return panel;
    }

    private JComponent buildBookingForm() {
        Theme.RoundedCard form = new Theme.RoundedCard(18);
        form.setBorder(new EmptyBorder(18, 22, 18, 22));
        form.setLayout(new BorderLayout(12, 10));

        JLabel formTitle = new JLabel("Book Selected Room");
        formTitle.setFont(Theme.FONT_HEADER);
        formTitle.setForeground(Theme.NAVY_DARK);

        selectedRoomLabel = new JLabel("No room selected — click a card above");
        selectedRoomLabel.setFont(Theme.FONT_BODY);
        selectedRoomLabel.setForeground(Theme.MUTED_TEXT);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.add(formTitle, BorderLayout.WEST);
        titleRow.add(selectedRoomLabel, BorderLayout.EAST);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(14);
        phoneField = new JTextField(14);

        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date defaultCheckout = Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant());
        checkInSpinner = new JSpinner(new SpinnerDateModel(today, null, null, java.util.Calendar.DAY_OF_MONTH));
        checkOutSpinner = new JSpinner(new SpinnerDateModel(defaultCheckout, null, null, java.util.Calendar.DAY_OF_MONTH));
        checkInSpinner.setEditor(new JSpinner.DateEditor(checkInSpinner, "yyyy-MM-dd"));
        checkOutSpinner.setEditor(new JSpinner.DateEditor(checkOutSpinner, "yyyy-MM-dd"));

        addFormField(fields, gbc, 0, 0, "Guest Name", nameField);
        addFormField(fields, gbc, 2, 0, "Phone", phoneField);
        addFormField(fields, gbc, 0, 1, "Check-in", checkInSpinner);
        addFormField(fields, gbc, 2, 1, "Check-out", checkOutSpinner);

        Theme.PillButton bookButton = new Theme.PillButton("Confirm Booking", Theme.GOLD, Theme.GOLD_LIGHT);
        bookButton.addActionListener(e -> handleBooking());

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonRow.setOpaque(false);
        buttonRow.add(bookButton);

        form.add(titleRow, BorderLayout.NORTH);
        form.add(fields, BorderLayout.CENTER);
        form.add(buttonRow, BorderLayout.SOUTH);
        return form;
    }

    private void addFormField(JPanel parent, GridBagConstraints gbc, int col, int row, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.MUTED_TEXT);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(220, 30));
        stack.add(l);
        stack.add(Box.createVerticalStrut(2));
        stack.add(field);

        gbc.gridx = col;
        gbc.gridy = row;
        parent.add(stack, gbc);
    }

    private void refreshRoomGrid() {
        roomGridPanel.removeAll();
        selectedCard = null;
        selectedRoomLabel.setText("No room selected — click a card above");

        List<Room> rooms = "All".equals(currentCategoryFilter)
                ? hotel.getAllRooms()
                : hotel.getAvailableRoomsByCategory(currentCategoryFilter);

        // when filtering by category, still only show that category's rooms (any status),
        // so guests can see what's booked too — but only available ones are clickable.
        if (!"All".equals(currentCategoryFilter)) {
            rooms = hotel.getAllRooms().stream()
                    .filter(r -> r.getCategory().equalsIgnoreCase(currentCategoryFilter))
                    .collect(java.util.stream.Collectors.toList());
        }

        for (Room r : rooms) {
            RoomCard card = new RoomCard(r, this::onRoomCardSelected);
            roomGridPanel.add(card);
        }
        roomGridPanel.revalidate();
        roomGridPanel.repaint();
    }

    private void onRoomCardSelected(RoomCard card) {
        if (selectedCard != null) selectedCard.setSelected(false);
        selectedCard = card;
        card.setSelected(true);
        Room r = card.getRoom();
        selectedRoomLabel.setText("Selected: Room " + r.getRoomNumber() + " (" + r.getCategory()
                + ") \u2014 \u20B9" + (int) r.getPricePerNight() + "/night");
    }

    private void handleBooking() {
        if (selectedCard == null) {
            JOptionPane.showMessageDialog(this, "Please click a room card to select it first.",
                    "No Room Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter guest name and phone number.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate checkIn = ((Date) checkInSpinner.getValue()).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate checkOut = ((Date) checkOutSpinner.getValue()).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        if (!checkOut.isAfter(checkIn)) {
            JOptionPane.showMessageDialog(this, "Check-out date must be after check-in date.",
                    "Invalid Date Range", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Room room = selectedCard.getRoom();
        try {
            Reservation reservation = hotel.bookRoom(name, phone, room, checkIn, checkOut);
            hotel.saveData();

            int choice = JOptionPane.showConfirmDialog(this,
                    "Reservation #" + reservation.getReservationId() + " confirmed for " + name
                            + ".\nTotal Amount: \u20B9" + (int) reservation.getTotalAmount()
                            + "\n\nProceed to payment now (simulated)?",
                    "Booking Confirmed", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                simulatePayment(reservation);
            }

            nameField.setText("");
            phoneField.setText("");
            refreshEverything();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simulatePayment(Reservation reservation) {
        String[] options = {"Credit/Debit Card", "UPI", "Cash at Hotel"};
        int method = JOptionPane.showOptionDialog(this,
                "Select payment method for \u20B9" + (int) reservation.getTotalAmount(),
                "Simulated Payment", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (method != JOptionPane.CLOSED_OPTION) {
            hotel.processPayment(reservation.getReservationId());
            hotel.saveData();
            JOptionPane.showMessageDialog(this,
                    "Payment of \u20B9" + (int) reservation.getTotalAmount() + " via " + options[method]
                            + " successful (simulated).\nReservation #" + reservation.getReservationId()
                            + " is now PAID.",
                    "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
            refreshEverything();
        }
    }

    // ============================================================
    // TAB 3: MANAGE RESERVATIONS
    // ============================================================
    private JPanel buildReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Theme.CREAM);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        reservationsModel = new DefaultTableModel(
                new Object[]{"Res ID", "Guest", "Phone", "Room", "Category", "Check-in", "Check-out",
                        "Amount", "Payment", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        reservationsTable = new JTable(reservationsModel);
        reservationsTable.setRowHeight(34);
        reservationsTable.setFont(Theme.FONT_BODY);
        reservationsTable.setShowGrid(false);
        reservationsTable.setIntercellSpacing(new Dimension(0, 0));
        reservationsTable.setSelectionBackground(new Color(212, 175, 55, 60));
        reservationsTable.setSelectionForeground(Theme.NAVY_DARK);
        reservationsTable.setFillsViewportHeight(true);

        JTableHeader header = reservationsTable.getTableHeader();
        header.setFont(Theme.FONT_BODY_B);
        header.setBackground(Theme.NAVY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 36));
        header.setOpaque(true);

        reservationsTable.setDefaultRenderer(Object.class, new StripedCellRenderer());
        reservationsTable.getColumnModel().getColumn(8).setCellRenderer(new BadgeCellRenderer(true));
        reservationsTable.getColumnModel().getColumn(9).setCellRenderer(new BadgeCellRenderer(false));

        JScrollPane scroll = new JScrollPane(reservationsTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(225, 228, 235)));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        Theme.PillButton payButton = new Theme.PillButton("Process Payment", Theme.SUCCESS, new Color(46, 204, 113));
        Theme.PillButton cancelButton = new Theme.PillButton("Cancel Reservation", Theme.DANGER, new Color(231, 100, 90));
        Theme.PillButton refreshButton = new Theme.PillButton("Refresh", Theme.NAVY, Theme.NAVY_LIGHT, false);

        payButton.addActionListener(e -> {
            Reservation res = getSelectedReservation();
            if (res == null) return;
            if (res.isPaymentDone()) {
                JOptionPane.showMessageDialog(this, "This reservation is already paid.",
                        "Already Paid", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (!res.getStatus().equals("CONFIRMED")) {
                JOptionPane.showMessageDialog(this, "Cannot pay for a cancelled reservation.",
                        "Invalid Operation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            simulatePayment(res);
        });

        cancelButton.addActionListener(e -> {
            Reservation res = getSelectedReservation();
            if (res == null) return;
            if (!res.getStatus().equals("CONFIRMED")) {
                JOptionPane.showMessageDialog(this, "Reservation is already cancelled.",
                        "Invalid Operation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Cancel reservation #" + res.getReservationId() + " for " + res.getGuestName() + "?",
                    "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                hotel.cancelReservation(res.getReservationId());
                hotel.saveData();
                refreshEverything();
                JOptionPane.showMessageDialog(this, "Reservation cancelled. Room is now available.");
            }
        });

        refreshButton.addActionListener(e -> refreshReservationsTable());

        buttonPanel.add(payButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private Reservation getSelectedReservation() {
        int row = reservationsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int resId = (int) reservationsModel.getValueAt(row, 0);
        return hotel.findReservationById(resId);
    }

    private void refreshReservationsTable() {
        reservationsModel.setRowCount(0);
        for (Reservation r : hotel.getAllReservations()) {
            reservationsModel.addRow(new Object[]{
                    r.getReservationId(), r.getGuestName(), r.getGuestPhone(),
                    r.getRoomNumber(), r.getRoomCategory(),
                    r.getCheckIn(), r.getCheckOut(), "\u20B9" + (int) r.getTotalAmount(),
                    r.isPaymentDone() ? "PAID" : "PENDING", r.getStatus()
            });
        }
    }

    /** Renders normal cells with soft alternating row stripes for readability. */
    private static class StripedCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Theme.PAPER : new Color(247, 248, 251));
            }
            setBorder(new EmptyBorder(0, 12, 0, 0));
            return c;
        }
    }

    /** Renders the Payment / Status columns as colored pill badges instead of plain text. */
    private static class BadgeCellRenderer implements javax.swing.table.TableCellRenderer {
        private final boolean isPaymentColumn;

        BadgeCellRenderer(boolean isPaymentColumn) {
            this.isPaymentColumn = isPaymentColumn;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            String text = String.valueOf(value);
            Color color;
            if (isPaymentColumn) {
                color = "PAID".equals(text) ? Theme.SUCCESS : Theme.WARNING;
            } else {
                color = "CANCELLED".equals(text) ? Theme.DANGER : Theme.SUCCESS;
            }
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            wrap.setBackground(row % 2 == 0 ? Theme.PAPER : new Color(247, 248, 251));
            Theme.Badge badge = new Theme.Badge(text, color);
            badge.setPreferredSize(new Dimension(text.length() * 8 + 34, 22));
            wrap.add(badge);
            return wrap;
        }
    }

    // ============================================================
    // TAB 4: ROOM OVERVIEW
    // ============================================================
    private JPanel buildRoomOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.CREAM);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel caption = new JLabel("All rooms at a glance \u2014 blue = Standard, purple = Deluxe, gold = Suite");
        caption.setFont(Theme.FONT_BODY);
        caption.setForeground(Theme.MUTED_TEXT);
        caption.setBorder(new EmptyBorder(0, 4, 14, 0));

        allRoomsGridPanel = new JPanel(new GridLayout(0, 5, 14, 14));
        allRoomsGridPanel.setOpaque(false);
        JScrollPane scroll = new JScrollPane(allRoomsGridPanel);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        panel.add(caption, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void refreshAllRoomsGrid() {
        allRoomsGridPanel.removeAll();
        for (Room r : hotel.getAllRooms()) {
            RoomCard card = new RoomCard(r, c -> { /* informational only, no selection here */ });
            allRoomsGridPanel.add(card);
        }
        allRoomsGridPanel.revalidate();
        allRoomsGridPanel.repaint();
    }

    // ============================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new HotelReservationGUI().setVisible(true);
        });
    }
}
