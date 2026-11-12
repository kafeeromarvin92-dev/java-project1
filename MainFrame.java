/* 
 MainFrame.java
 Builds the Swing dashboard and connects its controls to
  RentalManager. Rental rules and stored data stay in their
  existing model classes.

 */

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {

    // Shared palette keeps the whole application visually consistent.
    private static final Color NAVY = new Color(15, 30, 58);
    private static final Color ACCENT = new Color(37, 99, 235);
    private static final Color PAGE_BACKGROUND = new Color(244, 246, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT = new Color(31, 41, 55);
    private static final Color SECONDARY_TEXT = new Color(75, 85, 99);
    private static final Color AVAILABLE = new Color(22, 101, 52);
    private static final Color ACTIVE = new Color(146, 64, 14);
    private static final Color RETURNED = new Color(55, 65, 81);
    private static final Color FIELD_BORDER = new Color(216, 222, 231);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);

    // One manager is shared by every page of the interface.
    private final RentalManager manager = new RentalManager();

    // The CardLayout shows one page at a time while the sidebar stays visible.
    private final CardLayout pageLayout = new CardLayout();
    private final JPanel pageContainer = new JPanel(pageLayout);
    private final JLabel pageTitle = new JLabel("Dashboard");
    private final JLabel statusLabel = new JLabel("Ready");
    private final java.util.List<JButton> navigationButtons = new java.util.ArrayList<>();

    // Vehicle form fields preserve the existing add-vehicle features.
    private final JTextField makeField = new JTextField();
    private final JTextField modelField = new JTextField();
    private final JTextField rateField = new JTextField();
    private final JTextField luxuryFeeField = new JTextField();
    private final JComboBox<String> typeBox =
            new JComboBox<>(new String[]{"Car", "Van", "Bike", "Luxury Car"});
    private final JLabel pictureNameLabel = new JLabel("No picture selected");
    private String chosenImagePath = "";
    private JPanel vehicleCardsPanel;

    // Dashboard cards show live totals from RentalManager.
    private final JLabel totalVehiclesValue = new JLabel("0");
    private final JLabel availableVehiclesValue = new JLabel("0");
    private final JLabel activeRentalsValue = new JLabel("0");
    private final JLabel customersValue = new JLabel("0");
    private DefaultTableModel recentRentalsModel;

    // Customer registration and table components.
    private final JTextField nameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField licenseField = new JTextField();
    private DefaultTableModel customerModel;

    // These fields let a customer register and book directly on this computer.
    private final JTextField bookingNameField = new JTextField();
    private final JTextField bookingPhoneField = new JTextField();
    private final JTextField bookingLicenseField = new JTextField();
    private final JComboBox<Vehicle> bookingVehicleBox = new JComboBox<>();
    private final JTextField bookingDaysField = new JTextField("1");
    private final JLabel bookingCostLabel = new JLabel("UGX 0");

    // Rental form components keep live price and image updates.
    private final JComboBox<Customer> customerBox = new JComboBox<>();
    private final JComboBox<Vehicle> vehicleBox = new JComboBox<>();
    private final JTextField daysField = new JTextField("1");
    private final JLabel costLabel = new JLabel("UGX 0");
    private final JLabel previewLabel = new JLabel("Select a vehicle", SwingConstants.CENTER);

    // Rental history and fleet summary components.
    private DefaultTableModel rentalModel;
    private JTable rentalTable;
    private final JLabel activeLabel = new JLabel("Active rentals: 0");
    private final JTextArea summaryArea = new JTextArea();

    // Creates the window, all pages, and the initial sample data.
    public MainFrame() {
        setTitle("Vehicle Rental | Fleet Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setSize(1200, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(PAGE_BACKGROUND);
        setLayout(new BorderLayout());

        manager.addSampleData();
        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainContent(), BorderLayout.CENTER);
        refreshAll();
        showPage("Dashboard", "Dashboard");
    }

    // Builds the left navigation area and its seven page buttons.
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 16, 20, 16));

        JLabel brand = new JLabel("NORTHSTAR");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 19));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(4));

        JLabel brandSubtitle = new JLabel("VEHICLE RENTAL");
        brandSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brandSubtitle.setForeground(new Color(174, 188, 211));
        brandSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brandSubtitle);
        sidebar.add(Box.createVerticalStrut(34));

        addNavigationButton(sidebar, "Dashboard");
        addNavigationButton(sidebar, "Vehicles");
        addNavigationButton(sidebar, "Customers");
        addNavigationButton(sidebar, "Book a vehicle");
        addNavigationButton(sidebar, "Staff rental");
        addNavigationButton(sidebar, "Rentals");
        addNavigationButton(sidebar, "Fleet Summary");
        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("FLEET MANAGEMENT");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setForeground(new Color(174, 188, 211));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(footer);
        return sidebar;
    }

    // Adds one navigation button and connects it to the matching page.
    private void addNavigationButton(JPanel sidebar, String name) {
        JButton button = new JButton(name);
        button.setFont(BODY_FONT);
        button.setForeground(new Color(224, 231, 242));
        button.setBackground(NAVY);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 10));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(event -> showPage(name, name));
        navigationButtons.add(button);
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(5));
    }

    // Creates the header, page cards, and footer on the right of the sidebar.
    private JPanel buildMainContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(PAGE_BACKGROUND);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(18, 26, 18, 26));
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 23));
        pageTitle.setForeground(TEXT);
        header.add(pageTitle, BorderLayout.WEST);
        content.add(header, BorderLayout.NORTH);

        pageContainer.setBackground(PAGE_BACKGROUND);
        pageContainer.add(buildDashboardPage(), "Dashboard");
        pageContainer.add(buildVehiclesPage(), "Vehicles");
        pageContainer.add(buildCustomersPage(), "Customers");
        pageContainer.add(buildCustomerBookingPage(), "Book a vehicle");
        pageContainer.add(buildRentPage(), "Staff rental");
        pageContainer.add(buildRentalsPage(), "Rentals");
        pageContainer.add(buildSummaryPage(), "Fleet Summary");
        content.add(pageContainer, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(CARD_BACKGROUND);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, FIELD_BORDER),
                BorderFactory.createEmptyBorder(9, 24, 9, 24)));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(SECONDARY_TEXT);
        footer.add(statusLabel, BorderLayout.WEST);
        content.add(footer, BorderLayout.SOUTH);
        return content;
    }

    // Builds the first page with four summary cards and recent rentals.
    private JPanel buildDashboardPage() {
        JPanel page = createPage();
        page.setLayout(new BorderLayout(0, 20));

        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);
        stats.add(createStatCard("Total vehicles", totalVehiclesValue));
        stats.add(createStatCard("Available", availableVehiclesValue));
        stats.add(createStatCard("Active rentals", activeRentalsValue));
        stats.add(createStatCard("Customers", customersValue));
        page.add(stats, BorderLayout.NORTH);

        JPanel recentCard = createCard();
        recentCard.setLayout(new BorderLayout(0, 14));
        recentCard.add(sectionHeading("Recent rentals"), BorderLayout.NORTH);
        String[] columns = {"Rental", "Customer", "Vehicle", "Days", "Total (UGX)", "Status"};
        recentRentalsModel = readOnlyModel(columns);
        JTable recentTable = new JTable(recentRentalsModel);
        styleTable(recentTable, 5);
        recentCard.add(new JScrollPane(recentTable), BorderLayout.CENTER);
        page.add(recentCard, BorderLayout.CENTER);
        return page;
    }

    // Creates one white dashboard statistic card with a number and label.
    private JPanel createStatCard(String title, JLabel value) {
        JPanel card = createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        value.setFont(new Font("Segoe UI", Font.BOLD, 28));
        value.setForeground(TEXT);
        JLabel caption = new JLabel(title);
        caption.setFont(BODY_FONT);
        caption.setForeground(SECONDARY_TEXT);
        card.add(value);
        card.add(Box.createVerticalStrut(5));
        card.add(caption);
        return card;
    }

    // Builds the vehicle form and the scrollable grid of vehicle cards.
    private JPanel buildVehiclesPage() {
        JPanel page = createPage();
        page.setLayout(new BorderLayout(0, 16));

        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(0, 12));
        formCard.add(sectionHeading("Add a vehicle"), BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(2, 3, 14, 10));
        fields.setOpaque(false);
        fields.add(fieldGroup("Make", makeField));
        fields.add(fieldGroup("Model", modelField));
        fields.add(fieldGroup("Vehicle type", typeBox));
        fields.add(fieldGroup("Rate per day (UGX)", rateField));
        fields.add(fieldGroup("Luxury service fee (UGX)", luxuryFeeField));
        fields.add(fieldGroup("Vehicle photo", buildPictureChooser()));
        formCard.add(fields, BorderLayout.CENTER);

        luxuryFeeField.setEnabled(false);
        typeBox.addActionListener(event ->
                luxuryFeeField.setEnabled("Luxury Car".equals(typeBox.getSelectedItem())));

        JButton addButton = createStyledButton("Add vehicle", true);
        addButton.addActionListener(event -> addVehicle());
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionRow.setOpaque(false);
        actionRow.add(addButton);
        formCard.add(actionRow, BorderLayout.SOUTH);
        page.add(formCard, BorderLayout.NORTH);

        vehicleCardsPanel = new JPanel(new GridLayout(0, 3, 16, 16));
        vehicleCardsPanel.setBackground(PAGE_BACKGROUND);
        vehicleCardsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JScrollPane vehicleScroll = new JScrollPane(vehicleCardsPanel);
        vehicleScroll.setBorder(BorderFactory.createEmptyBorder());
        vehicleScroll.getViewport().setBackground(PAGE_BACKGROUND);
        page.add(vehicleScroll, BorderLayout.CENTER);
        return page;
    }

    // Combines the picture name and file picker in the vehicle form.
    private JComponent buildPictureChooser() {
        JPanel picturePanel = new JPanel(new BorderLayout(8, 0));
        picturePanel.setOpaque(false);
        pictureNameLabel.setFont(BODY_FONT);
        pictureNameLabel.setForeground(SECONDARY_TEXT);
        picturePanel.add(pictureNameLabel, BorderLayout.CENTER);
        JButton chooseButton = createStyledButton("Choose picture", false);
        chooseButton.addActionListener(event -> chooseImage());
        picturePanel.add(chooseButton, BorderLayout.EAST);
        return picturePanel;
    }

    // Builds the customer form and its styled customer table.
    private JPanel buildCustomersPage() {
        JPanel page = createPage();
        page.setLayout(new BorderLayout(0, 16));

        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(0, 12));
        formCard.add(sectionHeading("Register a customer"), BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridLayout(1, 3, 14, 0));
        fields.setOpaque(false);
        fields.add(fieldGroup("Full name", nameField));
        fields.add(fieldGroup("Phone number", phoneField));
        fields.add(fieldGroup("Driving licence", licenseField));
        formCard.add(fields, BorderLayout.CENTER);
        JButton registerButton = createStyledButton("Register customer", true);
        registerButton.addActionListener(event -> registerCustomer());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(registerButton);
        formCard.add(actions, BorderLayout.SOUTH);
        page.add(formCard, BorderLayout.NORTH);

        JPanel tableCard = createCard();
        tableCard.setLayout(new BorderLayout(0, 12));
        tableCard.add(sectionHeading("Customer directory"), BorderLayout.NORTH);
        customerModel = readOnlyModel(new String[]{"ID", "Name", "Phone", "Driving licence"});
        JTable table = new JTable(customerModel);
        styleTable(table, -1);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        page.add(tableCard, BorderLayout.CENTER);
        return page;
    }

    // Builds a simple self-service page where a customer enters details and books.
    private JPanel buildCustomerBookingPage() {
        JPanel page = createPage();
        page.setLayout(new GridBagLayout());

        JPanel bookingCard = createCard();
        bookingCard.setLayout(new BorderLayout(0, 20));
        bookingCard.setPreferredSize(new Dimension(850, 470));

        JPanel introduction = new JPanel();
        introduction.setOpaque(false);
        introduction.setLayout(new BoxLayout(introduction, BoxLayout.Y_AXIS));
        introduction.add(sectionHeading("Book a vehicle"));
        JLabel instructions = new JLabel("Enter your details, choose a vehicle, and confirm your booking.");
        instructions.setFont(BODY_FONT);
        instructions.setForeground(SECONDARY_TEXT);
        introduction.add(Box.createVerticalStrut(5));
        introduction.add(instructions);
        JLabel localNotice = new JLabel("Demo note: bookings are kept only while this program is open.");
        localNotice.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        localNotice.setForeground(SECONDARY_TEXT);
        introduction.add(Box.createVerticalStrut(5));
        introduction.add(localNotice);
        bookingCard.add(introduction, BorderLayout.NORTH);

        JPanel columns = new JPanel(new GridLayout(1, 2, 22, 0));
        columns.setOpaque(false);
        JPanel customerDetails = new JPanel(new GridLayout(3, 1, 0, 14));
        customerDetails.setOpaque(false);
        customerDetails.add(fieldGroup("Your name", bookingNameField));
        customerDetails.add(fieldGroup("Phone number", bookingPhoneField));
        customerDetails.add(fieldGroup("Driving licence number", bookingLicenseField));
        columns.add(customerDetails);

        JPanel bookingDetails = new JPanel(new GridLayout(3, 1, 0, 14));
        bookingDetails.setOpaque(false);
        bookingDetails.add(fieldGroup("Choose an available vehicle", bookingVehicleBox));
        bookingDetails.add(fieldGroup("Number of days", bookingDaysField));
        JPanel totalField = new JPanel(new BorderLayout(0, 7));
        totalField.setOpaque(false);
        JLabel totalHeading = new JLabel("Estimated total");
        totalHeading.setFont(LABEL_FONT);
        totalHeading.setForeground(SECONDARY_TEXT);
        bookingCostLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        bookingCostLabel.setForeground(ACCENT);
        totalField.add(totalHeading, BorderLayout.NORTH);
        totalField.add(bookingCostLabel, BorderLayout.CENTER);
        bookingDetails.add(totalField);
        columns.add(bookingDetails);
        bookingCard.add(columns, BorderLayout.CENTER);

        JButton confirmButton = createStyledButton("Confirm booking", true);
        confirmButton.addActionListener(event -> createCustomerBooking());
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionRow.setOpaque(false);
        actionRow.add(confirmButton);
        bookingCard.add(actionRow, BorderLayout.SOUTH);
        page.add(bookingCard);

        bookingVehicleBox.addActionListener(event -> updateCustomerBookingCost());
        bookingDaysField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                updateCustomerBookingCost();
            }
        });
        return page;
    }

    // Builds a centered rental form with a prominent cost and vehicle preview.
    private JPanel buildRentPage() {
        JPanel page = createPage();
        page.setLayout(new GridBagLayout());

        JPanel rentalCard = createCard();
        rentalCard.setLayout(new BorderLayout(24, 18));
        rentalCard.setPreferredSize(new Dimension(850, 430));
        rentalCard.add(sectionHeading("Create a rental"), BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(fieldGroup("Customer", customerBox));
        form.add(Box.createVerticalStrut(18));
        form.add(fieldGroup("Available vehicle", vehicleBox));
        form.add(Box.createVerticalStrut(18));
        form.add(fieldGroup("Rental duration (days)", daysField));
        form.add(Box.createVerticalStrut(24));

        JPanel costPanel = new JPanel();
        costPanel.setBackground(new Color(244, 247, 252));
        costPanel.setLayout(new BoxLayout(costPanel, BoxLayout.Y_AXIS));
        costPanel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        JLabel costCaption = new JLabel("TOTAL ESTIMATE");
        costCaption.setFont(new Font("Segoe UI", Font.BOLD, 11));
        costCaption.setForeground(SECONDARY_TEXT);
        costLabel.setFont(new Font("Segoe UI", Font.BOLD, 23));
        costLabel.setForeground(ACCENT);
        costPanel.add(costCaption);
        costPanel.add(Box.createVerticalStrut(5));
        costPanel.add(costLabel);
        form.add(costPanel);
        form.add(Box.createVerticalStrut(20));

        JButton rentButton = createStyledButton("Confirm rental", true);
        rentButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        rentButton.addActionListener(event -> rentVehicle());
        form.add(rentButton);
        rentalCard.add(form, BorderLayout.CENTER);

        JPanel previewCard = new JPanel(new BorderLayout());
        previewCard.setBackground(new Color(244, 246, 250));
        previewCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        previewLabel.setPreferredSize(new Dimension(330, 230));
        previewLabel.setForeground(SECONDARY_TEXT);
        previewLabel.setFont(BODY_FONT);
        previewCard.add(previewLabel, BorderLayout.CENTER);
        rentalCard.add(previewCard, BorderLayout.EAST);
        page.add(rentalCard);

        vehicleBox.addActionListener(event -> {
            updatePreview();
            updateCost();
        });
        daysField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                updateCost();
            }
        });
        return page;
    }

    // Builds the rental history table with its return action.
    private JPanel buildRentalsPage() {
        JPanel page = createPage();
        page.setLayout(new BorderLayout(0, 16));
        JPanel rentalCard = createCard();
        rentalCard.setLayout(new BorderLayout(0, 12));
        rentalCard.add(sectionHeading("Rental history"), BorderLayout.NORTH);

        rentalModel = readOnlyModel(
                new String[]{"Rental ID", "Customer", "Vehicle", "Days", "Total (UGX)", "Status"});
        rentalTable = new JTable(rentalModel);
        styleTable(rentalTable, 5);
        rentalCard.add(new JScrollPane(rentalTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        activeLabel.setFont(BODY_FONT);
        activeLabel.setForeground(SECONDARY_TEXT);
        actions.add(activeLabel, BorderLayout.WEST);
        JButton returnButton = createStyledButton("Return selected vehicle", false);
        returnButton.addActionListener(event -> returnVehicle());
        actions.add(returnButton, BorderLayout.EAST);
        rentalCard.add(actions, BorderLayout.SOUTH);
        page.add(rentalCard, BorderLayout.CENTER);
        return page;
    }

    // Builds the text-based fleet summary, including LuxuryCar details.
    private JPanel buildSummaryPage() {
        JPanel page = createPage();
        page.setLayout(new BorderLayout());
        JPanel summaryCard = createCard();
        summaryCard.setLayout(new BorderLayout(0, 12));
        summaryCard.add(sectionHeading("Fleet summary"), BorderLayout.NORTH);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        summaryArea.setForeground(TEXT);
        summaryArea.setBackground(CARD_BACKGROUND);
        summaryArea.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        summaryCard.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        page.add(summaryCard, BorderLayout.CENTER);
        return page;
    }

    // Returns a padded page surface over the shared light background.
    private JPanel createPage() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(PAGE_BACKGROUND);
        page.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        return page;
    }

    // Returns a white panel with even padding for forms and tables.
    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 236, 243)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        return card;
    }

    // Creates a section title using the dashboard font scale.
    private JLabel sectionHeading(String text) {
        JLabel heading = new JLabel(text);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        heading.setForeground(TEXT);
        return heading;
    }

    // Places a caption above a consistently styled input component.
    private JPanel fieldGroup(String title, JComponent input) {
        JPanel group = new JPanel(new BorderLayout(0, 7));
        group.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(LABEL_FONT);
        label.setForeground(SECONDARY_TEXT);
        styleInput(input);
        group.add(label, BorderLayout.NORTH);
        group.add(input, BorderLayout.CENTER);
        return group;
    }

    // Gives text fields and combo boxes the same height, padding, and focus border.
    private void styleInput(JComponent input) {
        input.setFont(BODY_FONT);
        input.setForeground(TEXT);
        input.setBackground(Color.WHITE);
        input.setOpaque(true);
        input.setPreferredSize(new Dimension(160, 38));
        Border normalBorder = BorderFactory.createLineBorder(FIELD_BORDER);
        Border padding = BorderFactory.createEmptyBorder(6, 9, 6, 9);
        input.setBorder(BorderFactory.createCompoundBorder(normalBorder, padding));
        if (input instanceof JComboBox<?>) {
            JComboBox<?> comboBox = (JComboBox<?>) input;
            comboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                        int index, boolean selected, boolean focused) {
                    JLabel item = (JLabel) super.getListCellRendererComponent(
                            list, value, index, selected, focused);
                    item.setOpaque(true);
                    item.setFont(BODY_FONT);
                    item.setBackground(selected ? ACCENT : Color.WHITE);
                    item.setForeground(selected ? Color.WHITE : TEXT);
                    item.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                    return item;
                }
            });
        }
        input.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent event) {
                input.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT), padding));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent event) {
                input.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_BORDER), padding));
            }
        });
    }

    // Creates a flat button with clear text and a visible hover colour.
    private JButton createStyledButton(String text, boolean primary) {
        JButton button = new JButton(text);
        Color baseColor = primary ? ACCENT : Color.WHITE;
        Color hoverColor = primary ? new Color(29, 78, 216) : new Color(226, 235, 250);
        button.setFont(LABEL_FONT);
        button.setForeground(primary ? Color.WHITE : ACCENT);
        button.setBackground(baseColor);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(!primary);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (primary) {
            button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        } else {
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT),
                    BorderFactory.createEmptyBorder(9, 15, 9, 15)));
        }
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                button.setBackground(baseColor);
            }
        });
        return button;
    }

    // Makes a table read-only and applies the shared professional table styling.
    private DefaultTableModel readOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // Applies a navy header, alternating rows, light lines, and status badges.
    private void styleTable(JTable table, int statusColumn) {
        table.setFont(BODY_FONT);
        table.setForeground(TEXT);
        table.setRowHeight(32);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(232, 236, 243));
        table.setSelectionBackground(new Color(219, 231, 255));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable source, Object value,
                    boolean selected, boolean focused, int row, int column) {
                JLabel heading = (JLabel) super.getTableCellRendererComponent(
                        source, value, selected, focused, row, column);
                heading.setOpaque(true);
                heading.setBackground(NAVY);
                heading.setForeground(Color.WHITE);
                heading.setFont(new Font("Segoe UI", Font.BOLD, 14));
                heading.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return heading;
            }
        });
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable source, Object value,
                    boolean selected, boolean focused, int row, int column) {
                Component cell = super.getTableCellRendererComponent(
                        source, value, selected, focused, row, column);
                if (!selected) {
                    cell.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 253));
                    cell.setForeground(TEXT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return cell;
            }
        });
        if (statusColumn >= 0) {
            table.getColumnModel().getColumn(statusColumn).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable source, Object value,
                        boolean selected, boolean focused, int row, int column) {
                    JLabel badge = (JLabel) super.getTableCellRendererComponent(
                            source, value, selected, focused, row, column);
                    badge.setHorizontalAlignment(SwingConstants.CENTER);
                    badge.setOpaque(true);
                    badge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                    if ("Available".equals(value)) {
                        badge.setBackground(new Color(220, 252, 231));
                        badge.setForeground(AVAILABLE);
                    } else if ("Active".equals(value) || "Rented".equals(value)) {
                        badge.setBackground(new Color(255, 237, 213));
                        badge.setForeground(ACTIVE);
                    } else {
                        badge.setBackground(new Color(243, 244, 246));
                        badge.setForeground(RETURNED);
                    }
                    if (selected) {
                        badge.setBackground(new Color(219, 231, 255));
                    }
                    return badge;
                }
            });
        }
    }

    // Selects a page and updates the header and active sidebar item.
    private void showPage(String name, String title) {
        pageLayout.show(pageContainer, name);
        pageTitle.setText(title);
        for (JButton button : navigationButtons) {
            boolean selected = button.getText().equals(name);
            button.setBackground(selected ? ACCENT : NAVY);
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setBorderPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 10));
        }
    }

    // Opens a file picker rooted at the project's images folder.
    private void chooseImage() {
        JFileChooser chooser = new JFileChooser("images");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Vehicle pictures (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            chosenImagePath = file.getPath();
            pictureNameLabel.setText(file.getName());
        }
    }

    // Validates the vehicle form and asks RentalManager to add the new vehicle.
    private void addVehicle() {
        String make = makeField.getText().trim();
        String model = modelField.getText().trim();
        String type = (String) typeBox.getSelectedItem();
        String rateText = rateField.getText().trim();
        if (make.isEmpty() || model.isEmpty() || rateText.isEmpty()) {
            showError("Enter the make, model, and daily rate.");
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateText);
        } catch (NumberFormatException exception) {
            showError("Enter a valid number for the daily rate.");
            return;
        }
        if (rate <= 0) {
            showError("The daily rate must be greater than zero.");
            return;
        }

        double luxuryFee = 0;
        if ("Luxury Car".equals(type)) {
            try {
                luxuryFee = Double.parseDouble(luxuryFeeField.getText().trim());
            } catch (NumberFormatException exception) {
                showError("Enter a valid number for the luxury service fee.");
                return;
            }
            if (luxuryFee < 0) {
                showError("The luxury service fee cannot be negative.");
                return;
            }
        }

        if (manager.addVehicle(make, model, type, rate, chosenImagePath, luxuryFee)) {
            setStatus("Vehicle added: " + make + " " + model);
            makeField.setText("");
            modelField.setText("");
            rateField.setText("");
            luxuryFeeField.setText("");
            chosenImagePath = "";
            pictureNameLabel.setText("No picture selected");
        } else {
            showError("The vehicle list is full.");
        }
        refreshAll();
    }

    // Validates customer details, including a phone number containing digits only.
    private void registerCustomer() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String license = licenseField.getText().trim();
        if (name.isEmpty() || phone.isEmpty() || license.isEmpty()) {
            showError("Enter the customer's name, phone number, and driving licence.");
            return;
        }
        if (!isDigitsOnly(phone) || phone.length() < 10) {
            showError("Enter a phone number with at least 10 digits.");
            return;
        }

        String result = manager.addCustomer(name, phone, license);
        if ("OK".equals(result)) {
            setStatus("Customer registered: " + name);
            nameField.setText("");
            phoneField.setText("");
            licenseField.setText("");
        } else {
            showError(result);
        }
        refreshAll();
    }

    // Checks the registration details, saves a new customer, and creates their booking.
    private void createCustomerBooking() {
        String name = bookingNameField.getText().trim();
        String phone = bookingPhoneField.getText().trim();
        String license = bookingLicenseField.getText().trim();
        Vehicle vehicle = (Vehicle) bookingVehicleBox.getSelectedItem();

        if (name.isEmpty() || phone.isEmpty() || license.isEmpty()) {
            showError("Enter your name, phone number, and driving licence number.");
            return;
        }
        if (!isDigitsOnly(phone) || phone.length() < 10) {
            showError("Enter a phone number with at least 10 digits.");
            return;
        }
        if (vehicle == null) {
            showError("There are no available vehicles to book.");
            return;
        }

        int days;
        try {
            days = Integer.parseInt(bookingDaysField.getText().trim());
        } catch (NumberFormatException exception) {
            showError("Enter a whole number for the number of days.");
            return;
        }
        if (days < 1) {
            showError("Choose at least one rental day.");
            return;
        }

        Customer customer = findCustomerByLicense(license);
        if (customer != null) {
            boolean sameName = customer.getName().equalsIgnoreCase(name);
            boolean samePhone = customer.getPhone().equals(phone);
            if (!sameName || !samePhone) {
                showError("That driving licence is already registered with different details.");
                return;
            }
        } else {
            String result = manager.addCustomer(name, phone, license);
            if (!"OK".equals(result)) {
                showError(result);
                return;
            }
            customer = manager.getCustomer(manager.getCustomerCount() - 1);
        }

        try {
            String result = manager.rentVehicle(vehicle, customer, days);
            if (!"OK".equals(result)) {
                showError(result);
                refreshAll();
                return;
            }
        } catch (VehicleAlreadyRentedException exception) {
            showError(exception.getMessage());
            refreshAll();
            return;
        }

        Rental rental = manager.getRental(manager.getRentalCount() - 1);
        String total = "UGX " + String.format("%,.0f", rental.getTotalCost());
        setStatus("Booking confirmed for " + customer.getName() + ".");
        String receipt = "BOOKING CONFIRMED\n\n"
                + "Customer: " + customer.getName() + "\n"
                + "Vehicle: " + vehicle.getMake() + " " + vehicle.getModel() + "\n"
                + "Number of days: " + days + "\n"
                + "Total: " + total;
        JOptionPane.showMessageDialog(this, receipt, "Booking confirmation",
                JOptionPane.INFORMATION_MESSAGE);

        bookingNameField.setText("");
        bookingPhoneField.setText("");
        bookingLicenseField.setText("");
        bookingDaysField.setText("1");
        refreshAll();
    }

    // Finds an already registered customer so their details are not stored twice.
    private Customer findCustomerByLicense(String license) {
        for (int i = 0; i < manager.getCustomerCount(); i++) {
            Customer customer = manager.getCustomer(i);
            if (customer.getLicenseNumber().equalsIgnoreCase(license)) {
                return customer;
            }
        }
        return null;
    }

    // Checks every character without using advanced library features.
    private boolean isDigitsOnly(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // Loads the selected vehicle's image or clears the preview when none is selected.
    private void updatePreview() {
        Vehicle vehicle = (Vehicle) vehicleBox.getSelectedItem();
        if (vehicle == null) {
            previewLabel.setIcon(null);
            previewLabel.setText("No available vehicle");
        } else {
            previewLabel.setText("");
            previewLabel.setIcon(ImageHelper.loadIcon(
                    vehicle.getImagePath(), vehicle.getType(), 330, 230));
        }
    }

    // Recalculates the live estimated price, including the existing discount rules.
    private void updateCost() {
        Vehicle vehicle = (Vehicle) vehicleBox.getSelectedItem();
        if (vehicle == null) {
            costLabel.setText("UGX 0");
            return;
        }
        try {
            int days = Integer.parseInt(daysField.getText().trim());
            if (days < 1) {
                costLabel.setText("Enter at least 1 day");
                return;
            }
            double cost = Rental.computeCost(vehicle, days);
            costLabel.setText("UGX " + String.format("%,.0f", cost));
        } catch (NumberFormatException exception) {
            costLabel.setText("Enter a whole number of days");
        }
    }

    // Shows the customer's estimated total before they confirm the booking.
    private void updateCustomerBookingCost() {
        Vehicle vehicle = (Vehicle) bookingVehicleBox.getSelectedItem();
        if (vehicle == null) {
            bookingCostLabel.setText("No vehicle available");
            return;
        }
        try {
            int days = Integer.parseInt(bookingDaysField.getText().trim());
            if (days < 1) {
                bookingCostLabel.setText("Enter at least 1 day");
                return;
            }
            double cost = Rental.computeCost(vehicle, days);
            bookingCostLabel.setText("UGX " + String.format("%,.0f", cost));
        } catch (NumberFormatException exception) {
            bookingCostLabel.setText("Enter a whole number of days");
        }
    }

    // Completes a rental, handles the existing custom exception, and shows its receipt.
    private void rentVehicle() {
        Customer customer = (Customer) customerBox.getSelectedItem();
        Vehicle vehicle = (Vehicle) vehicleBox.getSelectedItem();
        int days;
        try {
            days = Integer.parseInt(daysField.getText().trim());
        } catch (NumberFormatException exception) {
            showError("Enter a whole number for the rental duration.");
            return;
        }

        String result;
        try {
            result = manager.rentVehicle(vehicle, customer, days);
        } catch (VehicleAlreadyRentedException exception) {
            showError(exception.getMessage());
            refreshAll();
            return;
        }
        if ("OK".equals(result)) {
            Rental newest = manager.getRental(manager.getRentalCount() - 1);
            String total = "UGX " + String.format("%,.0f", newest.getTotalCost());
            setStatus("Rental confirmed. Total due: " + total);
            String receipt = "RENTAL CONFIRMATION\n\n"
                    + "Customer: " + newest.getCustomer().getName() + "\n"
                    + "Vehicle: " + newest.getVehicle().getMake() + " "
                    + newest.getVehicle().getModel() + "\n"
                    + "Duration: " + newest.getDays() + " day(s)\n"
                    + "Total due: " + total;
            JOptionPane.showMessageDialog(this, receipt, "Rental receipt",
                    JOptionPane.INFORMATION_MESSAGE);
            daysField.setText("1");
        } else {
            showError(result);
        }
        refreshAll();
    }

    // Confirms a return before asking the manager to mark that rental as complete.
    private void returnVehicle() {
        int row = rentalTable.getSelectedRow();
        if (row < 0) {
            showError("Select a rental from the table first.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Confirm that the selected vehicle has been returned.",
                "Confirm vehicle return", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            String result = manager.returnRental(row);
            if ("OK".equals(result)) {
                setStatus("Vehicle returned and is available for rent.");
            } else {
                setStatus(result);
            }
            refreshAll();
        }
    }

    // Refreshes all screens after any change to the stored data.
    private void refreshAll() {
        refreshVehicleCards();
        refreshCustomerTable();
        refreshRentTab();
        refreshRentalTable();
        refreshDashboard();
        refreshSummary();
    }

    // Rebuilds the vehicle grid so each item appears as an image-led card.
    private void refreshVehicleCards() {
        vehicleCardsPanel.removeAll();
        if (manager.getVehicleCount() == 0) {
            vehicleCardsPanel.add(emptyMessage("No vehicles have been added."));
        }
        for (int i = 0; i < manager.getVehicleCount(); i++) {
            vehicleCardsPanel.add(createVehicleCard(manager.getVehicle(i)));
        }
        vehicleCardsPanel.revalidate();
        vehicleCardsPanel.repaint();
    }

    // Creates a single vehicle card with photo, details, price, and availability badge.
    private JPanel createVehicleCard(Vehicle vehicle) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 10));
        JLabel photo = new JLabel(ImageHelper.loadIcon(
                vehicle.getImagePath(), vehicle.getType(), 300, 145));
        photo.setHorizontalAlignment(SwingConstants.CENTER);
        photo.setPreferredSize(new Dimension(300, 145));
        card.add(photo, BorderLayout.NORTH);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        JPanel titleRow = new JPanel(new BorderLayout(8, 0));
        titleRow.setOpaque(false);
        JLabel name = new JLabel(vehicle.getMake() + " " + vehicle.getModel());
        name.setFont(new Font("Segoe UI", Font.BOLD, 16));
        name.setForeground(TEXT);
        titleRow.add(name, BorderLayout.CENTER);
        if (vehicle instanceof LuxuryCar) {
            JLabel premium = new JLabel("PREMIUM");
            premium.setFont(new Font("Segoe UI", Font.BOLD, 9));
            premium.setForeground(ACCENT);
            titleRow.add(premium, BorderLayout.EAST);
        }
        details.add(titleRow);
        details.add(Box.createVerticalStrut(5));
        JLabel type = new JLabel(vehicle.getType());
        type.setFont(BODY_FONT);
        type.setForeground(SECONDARY_TEXT);
        details.add(type);
        details.add(Box.createVerticalStrut(10));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JLabel price = new JLabel("UGX " + String.format("%,.0f", vehicle.getDailyRate()) + " / day");
        price.setFont(new Font("Segoe UI", Font.BOLD, 14));
        price.setForeground(TEXT);
        bottom.add(price, BorderLayout.WEST);
        bottom.add(createStatusBadge(vehicle.isAvailable() ? "Available" : "Rented"),
                BorderLayout.EAST);
        details.add(bottom);
        card.add(details, BorderLayout.CENTER);
        return card;
    }

    // Creates a compact status badge using only the defined status palette.
    private JLabel createStatusBadge(String text) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(5, 9, 5, 9));
        if ("Available".equals(text)) {
            badge.setBackground(new Color(220, 252, 231));
            badge.setForeground(AVAILABLE);
        } else {
            badge.setBackground(new Color(255, 237, 213));
            badge.setForeground(ACTIVE);
        }
        return badge;
    }

    // Rebuilds the customer directory from the customer's array.
    private void refreshCustomerTable() {
        customerModel.setRowCount(0);
        for (int i = 0; i < manager.getCustomerCount(); i++) {
            Customer customer = manager.getCustomer(i);
            customerModel.addRow(new Object[]{customer.getId(), customer.getName(),
                    customer.getPhone(), customer.getLicenseNumber()});
        }
    }

    // Refills the rental dropdowns from current customers and available vehicles.
    private void refreshRentTab() {
        customerBox.removeAllItems();
        for (int i = 0; i < manager.getCustomerCount(); i++) {
            customerBox.addItem(manager.getCustomer(i));
        }
        vehicleBox.removeAllItems();
        bookingVehicleBox.removeAllItems();
        Vehicle[] availableVehicles = manager.getAvailableVehicles();
        for (Vehicle vehicle : availableVehicles) {
            vehicleBox.addItem(vehicle);
            bookingVehicleBox.addItem(vehicle);
        }
        updatePreview();
        updateCost();
        updateCustomerBookingCost();
    }

    // Rebuilds rental history and updates the active count.
    private void refreshRentalTable() {
        rentalModel.setRowCount(0);
        for (int i = 0; i < manager.getRentalCount(); i++) {
            Rental rental = manager.getRental(i);
            String status = rental.isActive() ? "Active" : "Returned";
            rentalModel.addRow(new Object[]{
                    rental.getId(),
                    rental.getCustomer().getName(),
                    rental.getVehicle().getMake() + " " + rental.getVehicle().getModel(),
                    rental.getDays(),
                    String.format("%,.0f", rental.getTotalCost()),
                    status
            });
        }
        activeLabel.setText("Active rentals: " + manager.getActiveRentalCount());
    }

    // Updates the four dashboard totals and lists up to five newest rentals.
    private void refreshDashboard() {
        totalVehiclesValue.setText(String.valueOf(manager.getVehicleCount()));
        availableVehiclesValue.setText(String.valueOf(manager.getAvailableVehicles().length));
        activeRentalsValue.setText(String.valueOf(manager.getActiveRentalCount()));
        customersValue.setText(String.valueOf(manager.getCustomerCount()));
        recentRentalsModel.setRowCount(0);
        int recentCount = Math.min(5, manager.getRentalCount());
        for (int i = 0; i < recentCount; i++) {
            Rental rental = manager.getRental(manager.getRentalCount() - 1 - i);
            recentRentalsModel.addRow(new Object[]{
                    rental.getId(),
                    rental.getCustomer().getName(),
                    rental.getVehicle().getMake() + " " + rental.getVehicle().getModel(),
                    rental.getDays(),
                    String.format("%,.0f", rental.getTotalCost()),
                    rental.isActive() ? "Active" : "Returned"
            });
        }
    }

    // Writes polymorphic vehicle details into the fleet summary.
    private void refreshSummary() {
        summaryArea.setText("");
        for (int i = 0; i < manager.getVehicleCount(); i++) {
            Vehicle vehicle = manager.getVehicle(i);
            summaryArea.append(vehicle.getDetails() + "\n");
        }
    }

    // Shows a message in the footer and a clear warning dialog.
    private void showError(String message) {
        setStatus(message);
        JOptionPane.showMessageDialog(this, message, "Unable to complete request",
                JOptionPane.WARNING_MESSAGE);
    }

    // Updates the slim footer status message.
    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    // Creates an explanatory message for an empty list.
    private JLabel emptyMessage(String message) {
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setForeground(SECONDARY_TEXT);
        label.setFont(BODY_FONT);
        return label;
    }
}

// Loads vehicle photos and creates a neutral placeholder if a photo is missing.
class ImageHelper {

    // Load and crop a photo to fit its display area without stretching it.
    public static ImageIcon loadIcon(String path, String type, int width, int height) {
        int safeWidth = Math.max(1, width);
        int safeHeight = Math.max(1, height);
        File file = findFile(path);
        if (file != null) {
            try {
                BufferedImage original = ImageIO.read(file);
                if (original != null) {
                    return new ImageIcon(fitImage(original, safeWidth, safeHeight));
                }
            } catch (IOException exception) {
                System.err.println("Could not read vehicle photo '" + file.getPath()
                        + "': " + exception.getMessage());
            }
        }
        return new ImageIcon(createPlaceholder(type, safeWidth, safeHeight));
    }

    // Look in the saved path first and then in the images folder.
    private static File findFile(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        File directFile = new File(path);
        if (directFile.isFile()) {
            return directFile;
        }
        File imagesFile = new File("images", directFile.getName());
        if (imagesFile.isFile()) {
            return imagesFile;
        }
        return null;
    }

    // Scale the image proportionally and crop its center to fill the area.
    private static BufferedImage fitImage(BufferedImage source, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = result.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        double scale = Math.max((double) width / source.getWidth(),
                (double) height / source.getHeight());
        int scaledWidth = (int) Math.ceil(source.getWidth() * scale);
        int scaledHeight = (int) Math.ceil(source.getHeight() * scale);
        int x = (width - scaledWidth) / 2;
        int y = (height - scaledHeight) / 2;
        graphics.drawImage(source, x, y, scaledWidth, scaledHeight, null);
        graphics.dispose();
        return result;
    }

    // Draw a simple label when the requested photo is unavailable.
    private static BufferedImage createPlaceholder(String type, int width, int height) {
        BufferedImage placeholder = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = placeholder.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(new Color(238, 241, 245));
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(new Color(75, 85, 99));
        graphics.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        String label = (type == null || type.trim().isEmpty()) ? "Vehicle" : type;
        int textWidth = graphics.getFontMetrics().stringWidth(label);
        int textX = Math.max(8, (width - textWidth) / 2);
        int textY = height / 2 + graphics.getFontMetrics().getAscent() / 2;
        graphics.drawString(label, textX, textY);
        graphics.dispose();
        return placeholder;
    }
}
