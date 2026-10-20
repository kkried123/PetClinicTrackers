import java.awt.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

public final class ClinicPanel extends JPanel {
    private final Clinic clinic;
    private final Path file;

    private Animal selected;
    private Veterinarian selectedVet;
    private String filter = "All patients";

    private final JPanel sidebar =
            new JPanel(new BorderLayout());

    private final JPanel cards =
            new JPanel(new GridLayout(0, 3, 12, 12));

    private final JPanel profile =
            new JPanel(new BorderLayout());

    private final JTextField search = new JTextField(20);

    private final JLabel totals =
            Style.label("", 12, false);

    private final JLabel status =
            Style.label("Local clinic workspace", 10, false);

    private final JTabbedPane tabs = Style.tabs();

    private final JPanel vetCards =
            new JPanel(new GridLayout(1, 3, 12, 0));

    private final JPanel vetProfile =
            new JPanel(new BorderLayout());

    public ClinicPanel(Clinic clinic, Path file) {
        super(new BorderLayout());

        this.clinic = clinic;
        this.file = file;

        if (!clinic.getPatients().isEmpty()) {
            selected = clinic.getPatients().get(0);
        }

        selectedVet = clinic.getVeterinarians().get(0);

        setBackground(Style.BG);
        add(sidebar, BorderLayout.WEST);

        JPanel page = new JPanel(new BorderLayout(18, 18));
        page.setBorder(new EmptyBorder(26, 24, 24, 24));
        page.add(header(), BorderLayout.NORTH);

        cards.setOpaque(false);

        JPanel cardHolder = new JPanel(new BorderLayout());
        cardHolder.add(cards, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(20, 0));
        center.add(
                Style.scroll(cardHolder),
                BorderLayout.CENTER
        );

        JScrollPane right = Style.scroll(profile);
        right.setPreferredSize(new Dimension(290, 0));
        center.add(right, BorderLayout.EAST);

        tabs.addTab("Patients", center);
        tabs.addTab("Veterinarians", veterinarianPage());
        tabs.addChangeListener(event -> refresh());

        page.add(tabs, BorderLayout.CENTER);
        add(page, BorderLayout.CENTER);

        search.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void insertUpdate(DocumentEvent e) {
                        refreshCards();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        refreshCards();
                    }

                    public void changedUpdate(DocumentEvent e) {
                        refreshCards();
                    }
                }
        );

        refresh();
    }

    private JPanel header() {
        JPanel header = new JPanel(new BorderLayout(0, 18));
        JPanel title = new JPanel(new BorderLayout());

        title.add(
                Style.label("Good care starts here.", 28, true),
                BorderLayout.WEST
        );

        title.add(
                Style.button(
                        "+ Register patient", true, this::register
                ),
                BorderLayout.EAST
        );

        header.add(title, BorderLayout.NORTH);

        JPanel hero = Style.card(Style.SAND, 20);
        hero.setLayout(new BorderLayout());

        JPanel words = Style.column();
        words.add(
                Style.label("HAPPY PETS, HEALTHIER DAYS", 10, false)
        );
        words.add(Box.createVerticalStrut(12));
        words.add(
                Style.label(
                        "Little paws. A whole lot of love.", 24, true
                )
        );
        words.add(Box.createVerticalStrut(12));
        words.add(
                Style.label(
                        "Every profile tells a story. Keep their care together.",
                        12, false
                )
        );

        hero.add(words, BorderLayout.CENTER);
        hero.add(
                new PetArt("Dog", 112, new Color(0xE9D7BF)),
                BorderLayout.EAST
        );

        header.add(hero, BorderLayout.CENTER);

        JPanel tools = new JPanel(new BorderLayout());
        tools.add(totals, BorderLayout.WEST);

        JPanel find = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        find.add(Style.label("Search patients", 11, false));
        find.add(search);
        tools.add(find, BorderLayout.EAST);

        header.add(tools, BorderLayout.SOUTH);
        return header;
    }

    private void refresh() {
        sidebar.removeAll();
        sidebar.setBackground(new Color(0xF1EDE5));
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBorder(new EmptyBorder(30, 16, 24, 16));

        JPanel menu = Style.column();
        menu.add(Style.label("Pet Clinic", 24, true));
        menu.add(Style.label("Tracker", 20, true));
        menu.add(Box.createVerticalStrut(42));

        boolean patientTab = tabs.getSelectedIndex() == 0;

        menu.add(
                Style.label(
                        patientTab ? "YOUR PATIENTS" : "YOUR CARE TEAM",
                        10, false
                )
        );
        menu.add(Box.createVerticalStrut(14));

        if (patientTab) {
            for (String option : new String[]{
                    "All patients", "Dog", "Cat", "Bird"}) {
                JButton button = Style.button(
                        option,
                        filter.equals(option),
                        () -> {
                            filter = option;
                            refresh();
                        }
                );

                button.setMaximumSize(new Dimension(148, 42));
                menu.add(button);
                menu.add(Box.createVerticalStrut(10));
            }
        } else {
            for (Veterinarian vet : clinic.getVeterinarians()) {
                JButton button = Style.button(
                        vet.getName(),
                        vet == selectedVet,
                        () -> {
                            selectedVet = vet;
                            refresh();
                        }
                );

                button.setMaximumSize(new Dimension(148, 42));
                menu.add(button);
                menu.add(Box.createVerticalStrut(10));
            }
        }

        sidebar.add(menu, BorderLayout.NORTH);

        JPanel footer = Style.column();
        footer.add(Style.label("GRAPH1 · GRP1", 11, true));
        footer.add(Box.createVerticalStrut(8));
        footer.add(status);
        sidebar.add(footer, BorderLayout.SOUTH);

        totals.setText(
                clinic.getPatients().size() + " patients  ·  "
                        + clinic.getVeterinarians().size()
                        + " veterinarians  ·  "
                        + clinic.connectionCount() + " care links"
        );

        search.setEnabled(patientTab);

        refreshCards();
        refreshProfile();
        refreshVeterinarians();

        revalidate();
        repaint();
    }

    private void refreshCards() {
        cards.removeAll();

        String query = search.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        for (Animal animal : clinic.getPatients()) {
            String text = animal.getName() + " "
                    + animal.getOwner() + " "
                    + animal.getBreed() + " "
                    + animal.getSpecies();

            if (!filter.equals("All patients")
                    && !filter.equals(animal.getSpecies())) {
                continue;
            }

            if (!text.toLowerCase(Locale.ROOT).contains(query)) {
                continue;
            }

            cards.add(patientCard(animal));
        }

        if (cards.getComponentCount() == 0) {
            cards.add(
                    Style.label("No patients found.", 13, false)
            );
        }

        while (cards.getComponentCount() % 3 != 0) {
            JPanel blank = new JPanel();
            blank.setOpaque(false);
            cards.add(blank);
        }

        cards.revalidate();
        cards.repaint();
    }

    private JPanel patientCard(Animal animal) {
        JPanel card = Style.card(Color.WHITE, 14);
        card.setLayout(new BorderLayout(0, 10));

        Color color = animal instanceof Cat
                ? new Color(0xEEEAF5)
                : animal instanceof Bird
                ? new Color(0xEDF3ED)
                : Style.SAND;

        JPanel picture = new JPanel(new BorderLayout());
        picture.setOpaque(false);

        picture.add(
                new PetArt(animal.getSpecies(), 62, color),
                BorderLayout.WEST
        );
        picture.add(
                Style.label(animal.getSpecies(), 10, false),
                BorderLayout.EAST
        );

        card.add(picture, BorderLayout.NORTH);

        JPanel name = Style.column();

        name.add(
                Style.label(animal.getName(), 18, true)
        );
        name.add(
                Style.label(animal.getBreed(), 11, false)
        );
        name.add(
                Style.label(
                        animal.getWeight() + " kg · "
                                + animal.getAgeText(),
                        11, false
                )
        );

        card.add(name, BorderLayout.CENTER);

        boolean active =
                tabs.getSelectedIndex() == 0 && animal == selected;

        card.add(
                Style.button(
                        active ? "Selected ✓" : "View profile →",
                        false,
                        () -> {
                            selected = animal;
                            filter = "All patients";
                            search.setText("");
                            tabs.setSelectedIndex(0);
                            refresh();
                        }
                ),
                BorderLayout.SOUTH
        );

        card.setPreferredSize(new Dimension(200, 230));
        return card;
    }

    private JPanel veterinarianPage() {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        vetCards.setOpaque(false);

        page.add(vetCards, BorderLayout.NORTH);
        page.add(
                Style.scroll(vetProfile),
                BorderLayout.CENTER
        );

        return page;
    }

    private void refreshVeterinarians() {
        vetCards.removeAll();

        for (Veterinarian vet : clinic.getVeterinarians()) {
            JPanel card = Style.card(Color.WHITE, 16);
            card.setLayout(new BorderLayout(0, 10));

            JPanel words = Style.column();

            words.add(
                    Style.label("VETERINARIAN", 10, false)
            );
            words.add(Box.createVerticalStrut(6));
            words.add(
                    Style.label(vet.getName(), 18, true)
            );
            words.add(
                    Style.label(vet.getSpecialty(), 12, false)
            );
            words.add(Box.createVerticalStrut(6));
            words.add(
                    Style.label(
                            clinic.getVeterinarianPatients(
                                    vet.getId()
                            ).size() + " connected patients",
                            12, false
                    )
            );

            card.add(words, BorderLayout.CENTER);

            card.add(
                    Style.button(
                            vet == selectedVet
                                    ? "Selected ✓"
                                    : "View veterinarian →",
                            vet == selectedVet,
                            () -> {
                                selectedVet = vet;
                                refresh();
                            }
                    ),
                    BorderLayout.SOUTH
            );

            vetCards.add(card);
        }

        vetProfile.removeAll();

        JPanel content = new JPanel(
                new BorderLayout(0, 14)
        );

        JPanel heading = Style.column();

        heading.add(
                Style.label(selectedVet.getName(), 24, true)
        );
        heading.add(
                Style.label(
                        selectedVet.getSpecialty()
                                + " · " + selectedVet.getId(),
                        12, false
                )
        );
        heading.add(Box.createVerticalStrut(8));
        heading.add(
                Style.label("CONNECTED PATIENTS", 11, true)
        );
        heading.add(
                Style.label(
                        "These profiles are linked to this veterinarian in the graph.",
                        12, false
                )
        );

        content.add(heading, BorderLayout.NORTH);

        List<Animal> connected =
                clinic.getVeterinarianPatients(selectedVet.getId());

        JPanel patients = new JPanel(
                new GridLayout(0, 3, 12, 12)
        );
        patients.setOpaque(false);

        for (Animal animal : connected) {
            patients.add(patientCard(animal));
        }

        if (connected.isEmpty()) {
            patients.add(
                    Style.label(
                            "No patients assigned yet.", 13, false
                    )
            );
        }

        while (patients.getComponentCount() % 3 != 0) {
            JPanel blank = new JPanel();
            blank.setOpaque(false);
            patients.add(blank);
        }

        content.add(patients, BorderLayout.CENTER);
        vetProfile.add(content, BorderLayout.NORTH);

        vetCards.revalidate();
        vetCards.repaint();
        vetProfile.revalidate();
        vetProfile.repaint();
    }

    private void refreshProfile() {
        profile.removeAll();

        JPanel card = Style.card(Color.WHITE, 18);
        card.setLayout(new BorderLayout());

        JPanel content = Style.column();

        JPanel profileHeading = new JPanel(
                new BorderLayout(6, 0)
        );
        profileHeading.setOpaque(false);

        profileHeading.add(
                Style.label("PATIENT SPOTLIGHT", 10, false),
                BorderLayout.WEST
        );

        if (selected != null) {
            JButton remove = Style.button(
                    "Remove patient", false, this::removePatient
            );
            remove.setForeground(new Color(0xA3443D));
            remove.setBorder(new EmptyBorder(6, 8, 6, 8));

            profileHeading.add(remove, BorderLayout.EAST);
        }

        content.add(profileHeading);
        content.add(Box.createVerticalStrut(12));

        if (selected == null) {
            content.add(
                    Style.label(
                            "Register a pet to begin.", 12, false
                    )
            );
        } else {
            content.add(
                    new PetArt(
                            selected.getSpecies(), 88, Style.SAND
                    )
            );
            content.add(
                    Style.label(selected.getName(), 24, true)
            );
            content.add(
                    Style.label(selected.getBreed(), 12, false)
            );
            content.add(
                    Style.label(
                            "Pet parent: " + selected.getOwner(),
                            12, false
                    )
            );
            content.add(
                    Style.label(
                            "Age: " + selected.getAgeText(),
                            12, false
                    )
            );
            content.add(
                    Style.label(
                            "Weight: " + selected.getWeight() + " kg",
                            12, false
                    )
            );

            content.add(Box.createVerticalStrut(10));

            JPanel updates = new JPanel(
                    new GridLayout(1, 2, 6, 0)
            );
            updates.setOpaque(false);

            updates.add(
                    Style.button(
                            "Update weight", false, this::updateWeight
                    )
            );
            updates.add(
                    Style.button(
                            "Update age", false, this::updateAge
                    )
            );

            content.add(updates);
            content.add(Box.createVerticalStrut(16));
            content.add(
                    Style.label("EVERYDAY CARE", 10, true)
            );
            content.add(
                    Style.text(selected.getDietaryNeeds(), 2)
            );
            content.add(
                    Style.label(
                            String.format(
                                    Locale.US,
                                    "%.2f g / day",
                                    selected.getFoodGrams()
                            ),
                            20, true
                    )
            );
            content.add(
                    Style.text(
                            "Fictional classroom feeding example. "
                                    + "Follow your veterinarian's plan.",
                            2
                    )
            );

            content.add(Box.createVerticalStrut(14));
            content.add(
                    Style.label("ASSIGNED CARE", 10, true)
            );

            String care = "";

            for (String id : clinic.getConnections(
                    selected.getId())) {
                care += clinic.getCareOptions().get(id) + "\n";
            }

            content.add(
                    Style.text(
                            care.isEmpty()
                                    ? "No care assigned yet."
                                    : care,
                            3
                    )
            );

            JPanel actions = new JPanel(
                    new GridLayout(1, 2, 6, 0)
            );
            actions.setOpaque(false);

            actions.add(
                    Style.button(
                            "Assign care", false, this::assignCare
                    )
            );
            actions.add(
                    Style.button(
                            "Related patients", false, this::related
                    )
            );

            content.add(actions);
            content.add(Box.createVerticalStrut(14));
            content.add(
                    Style.label("MEDICAL NOTES", 10, true)
            );

            JScrollPane notes = Style.scroll(
                    Style.text(
                            String.join("\n\n", selected.getNotes()),
                            5
                    )
            );

            notes.setPreferredSize(new Dimension(235, 110));
            content.add(notes);
            content.add(Box.createVerticalStrut(8));
            content.add(
                    Style.button(
                            "+ Add medical note", true, this::addNote
                    )
            );
        }

        for (Component child : content.getComponents()) {
            if (child instanceof JComponent component) {
                component.setAlignmentX(
                        Component.LEFT_ALIGNMENT
                );
            }
        }

        card.add(content, BorderLayout.NORTH);
        profile.add(card, BorderLayout.NORTH);

        profile.revalidate();
        profile.repaint();
    }

    private void register() {
        JTextField name = new JTextField();
        JTextField owner = new JTextField();
        JTextField breed = new JTextField();
        JTextField kg = new JTextField();
        JTextField months = new JTextField("12");

        JComboBox<String> species = new JComboBox<>(
                new String[]{"Dog", "Cat", "Bird"}
        );

        Object[] fields = {
                "Pet name", name,
                "Pet parent", owner,
                "Species", species,
                "Breed / type", breed,
                "Age in months (1 year = 12 months)", months,
                "Weight in kilograms (30 g = 0.03 kg)", kg
        };

        edit("Register patient", fields, () -> {
            selected = clinic.register(
                    (String) species.getSelectedItem(),
                    name.getText(),
                    owner.getText(),
                    breed.getText(),
                    Integer.parseInt(months.getText().trim()),
                    Double.parseDouble(kg.getText().trim())
            );

            search.setText("");
            filter = "All patients";
            tabs.setSelectedIndex(0);
        });
    }

    private void removePatient() {
        if (selected == null) return;

        boolean confirmed = ClinicDialog.confirm(
                this,
                "Remove patient",
                "Remove " + selected.getName()
                        + " and their medical notes and care connections?\n"
                        + "This cannot be undone.",
                "Remove patient"
        );

        if (!confirmed) return;

        clinic.removePatient(selected.getId());

        selected = clinic.getPatients().isEmpty()
                ? null
                : clinic.getPatients().get(0);

        filter = "All patients";
        search.setText("");

        save();
        refresh();
    }

    private void updateWeight() {
        JTextField weight = new JTextField(
                "" + selected.getWeight()
        );

        edit(
                "Update weight",
                new Object[]{
                        "New weight in kilograms", weight
                },
                () -> selected.setWeight(
                        Double.parseDouble(weight.getText().trim())
                )
        );
    }

    private void updateAge() {
        JTextField months = new JTextField(
                selected.getAgeMonths() < 0
                        ? ""
                        : "" + selected.getAgeMonths()
        );

        edit(
                "Update age",
                new Object[]{
                        "Age in months (1 year = 12 months)", months
                },
                () -> selected.setAgeMonths(
                        Integer.parseInt(months.getText().trim())
                )
        );
    }

    private void addNote() {
        JTextArea note = new JTextArea(6, 30);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);

        edit(
                "Medical note",
                new Object[]{Style.scroll(note)},
                () -> selected.addNote(note.getText())
        );
    }

    private void assignCare() {
        Map<String, JCheckBox> boxes = new LinkedHashMap<>();
        JPanel options = Style.column();

        List<String> current =
                clinic.getConnections(selected.getId());

        for (Map.Entry<String, String> option
                : clinic.getCareOptions().entrySet()) {

            if (option.getKey().equals("V1")
                    || option.getKey().equals("T1")) {
                options.add(Box.createVerticalStrut(8));

                options.add(
                        Style.label(
                                option.getKey().equals("V1")
                                        ? "CARE TEAM"
                                        : "TREATMENTS",
                                10, false
                        )
                );

                options.add(Box.createVerticalStrut(8));
            }

            JCheckBox box = ClinicDialog.choice(
                    option.getValue(),
                    current.contains(option.getKey())
            );

            boxes.put(option.getKey(), box);
            options.add(box);
        }

        edit(
                "Assign care to " + selected.getName(),
                new Object[]{options},
                () -> {
                    List<String> chosen = new ArrayList<>();

                    for (String id : boxes.keySet()) {
                        if (boxes.get(id).isSelected()) {
                            chosen.add(id);
                        }
                    }

                    clinic.assignCare(selected.getId(), chosen);
                }
        );
    }

    private void related() {
        JPanel rows = Style.column();

        List<Animal> matches =
                clinic.relatedPatients(selected.getId());

        if (matches.isEmpty()) {
            rows.add(
                    Style.text("No shared care records yet.", 2)
            );
        }

        for (Animal animal : matches) {
            JPanel row = Style.card(Style.SOFT, 12);
            row.setLayout(new BorderLayout(12, 0));

            row.add(
                    new PetArt(
                            animal.getSpecies(), 44, Style.SAND
                    ),
                    BorderLayout.WEST
            );

            JPanel words = Style.column();

            words.add(
                    Style.label(animal.getName(), 15, true)
            );
            words.add(
                    Style.label(
                            animal.getSpecies()
                                    + " · " + animal.getBreed(),
                            11, false
                    )
            );
            words.add(
                    Style.label(
                            animal.getAgeText()
                                    + " · " + animal.getWeight() + " kg",
                            11, false
                    )
            );

            row.add(words, BorderLayout.CENTER);

            row.setPreferredSize(
                    new Dimension(350, 90)
            );
            row.setMinimumSize(
                    new Dimension(0, 90)
            );
            row.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 90)
            );

            rows.add(row);
            rows.add(Box.createVerticalStrut(8));
        }

        ClinicDialog.show(
                this,
                "Related patients",
                "Sharing a veterinarian or treatment with "
                        + selected.getName() + ".",
                rows
        );
    }

    private void edit(
            String title, Object[] fields, Runnable change) {

        if (ClinicDialog.form(this, title, fields, change)) {
            save();
            refresh();
        }
    }

    public boolean save() {
        if (file == null) return true;

        try {
            clinic.save(file);
            status.setText("Saved locally ✓");
            return true;

        } catch (java.io.IOException ex) {
            status.setText("Unsaved changes");

            ClinicDialog.message(
                    this,
                    "Could not save",
                    "Could not save changes: " + ex.getMessage()
            );

            return false;
        }
    }
}