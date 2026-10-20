import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Clinic {
    private final Map<String, Animal> patients =
            new LinkedHashMap<>();

    private final Map<String, String> care =
            new LinkedHashMap<>();

    private final CareGraph graph = new CareGraph();

    private final List<Veterinarian> veterinarians = List.of(
            new Veterinarian(
                    "V1", "Dr. Ana Santos", "General veterinary care"
            ),
            new Veterinarian(
                    "V2", "Dr. Luis Reyes", "Feline wellness"
            ),
            new Veterinarian(
                    "V3", "Dr. Mia Cruz", "Avian wellness"
            )
    );

    public Clinic() {
        for (Veterinarian vet : veterinarians) {
            care.put(vet.getId(), vet.getName());
        }

        care.put("T1", "Wellness check");
        care.put("T2", "Vaccination");
        care.put("T3", "Nutrition review");

        for (String id : care.keySet()) {
            graph.addVertex(id);
        }
    }

    public List<Animal> getPatients() {
        return new ArrayList<>(patients.values());
    }

    public Map<String, String> getCareOptions() {
        return new LinkedHashMap<>(care);
    }

    public List<String> getConnections(String patient) {
        return graph.neighbors(patient);
    }

    public int connectionCount() {
        return graph.edgeCount();
    }

    public List<Veterinarian> getVeterinarians() {
        return new ArrayList<>(veterinarians);
    }

    // Veterinarian connections come directly from the graph.
    public List<Animal> getVeterinarianPatients(String vetId) {
        boolean exists = false;

        for (Veterinarian vet : veterinarians) {
            if (vet.getId().equals(vetId)) {
                exists = true;
            }
        }

        if (!exists) {
            throw new IllegalArgumentException(
                    "Unknown veterinarian."
            );
        }

        List<Animal> result = new ArrayList<>();

        for (String id : graph.neighbors(vetId)) {
            result.add(patients.get(id));
        }

        return result;
    }

    public boolean removePatient(String id) {
        if (!patients.containsKey(id)) {
            return false;
        }

        graph.removeVertex(id);
        patients.remove(id);
        return true;
    }

    public Animal register(
            String species, String name, String owner,
            String breed, int months, double kg) {
        if (months < 0) {
            throw new IllegalArgumentException(
                    "Age cannot be negative."
            );
        }

        int number = 1;

        while (patients.containsKey("P" + number)) {
            number++;
        }

        return add(
                "P" + number, species, name,
                owner, breed, months, kg
        );
    }

    private Animal add(
            String id, String species, String name,
            String owner, String breed, int months, double kg) {
        Animal animal;

        if (species.equals("Dog")) {
            animal = new Dog(
                    id, name, owner, breed, months, kg
            );
        } else if (species.equals("Cat")) {
            animal = new Cat(
                    id, name, owner, breed, months, kg
            );
        } else if (species.equals("Bird")) {
            animal = new Bird(
                    id, name, owner, breed, months, kg
            );
        } else {
            throw new IllegalArgumentException(
                    "Choose Dog, Cat or Bird."
            );
        }

        graph.addVertex(id);
        patients.put(id, animal);
        return animal;
    }

    public void assignCare(String patient, List<String> selected) {
        if (!patients.containsKey(patient)) {
            throw new IllegalArgumentException("Unknown patient.");
        }

        for (String id : selected) {
            if (!care.containsKey(id)) {
                throw new IllegalArgumentException(
                        "Unknown care record."
                );
            }
        }

        for (String old : graph.neighbors(patient)) {
            graph.disconnect(patient, old);
        }

        for (String id : selected) {
            graph.connect(patient, id);
        }
    }

    // Graph traversal: patient -> care record -> another patient.
    public List<Animal> relatedPatients(String patient) {
        Set<String> found = new LinkedHashSet<>();

        for (String careId : graph.neighbors(patient)) {
            for (String other : graph.neighbors(careId)) {
                if (!other.equals(patient)
                        && patients.containsKey(other)) {
                    found.add(other);
                }
            }
        }

        List<Animal> result = new ArrayList<>();

        for (String id : found) {
            result.add(patients.get(id));
        }

        return result;
    }

    public void save(Path file) throws IOException {
        Properties data = new Properties();

        data.setProperty("format", "pet-clinic-simple-1");
        data.setProperty(
                "patients", String.join(",", patients.keySet())
        );

        for (Animal animal : patients.values()) {
            String key = animal.getId() + ".";

            data.setProperty(key + "species", animal.getSpecies());
            data.setProperty(key + "name", animal.getName());
            data.setProperty(key + "owner", animal.getOwner());
            data.setProperty(key + "breed", animal.getBreed());
            data.setProperty(key + "weight", "" + animal.getWeight());
            data.setProperty(key + "months", "" + animal.getAgeMonths());
            data.setProperty(
                    key + "care",
                    String.join(",", graph.neighbors(animal.getId()))
            );

            List<String> notes = animal.getNotes();
            data.setProperty(key + "notes", "" + notes.size());

            for (int i = 0; i < notes.size(); i++) {
                data.setProperty(key + "note" + i, notes.get(i));
            }
        }

        Path target = file.toAbsolutePath();
        Files.createDirectories(target.getParent());

        Path temp = Files.createTempFile(
                target.getParent(), "clinic-", ".tmp"
        );

        try {
            try (Writer writer = Files.newBufferedWriter(
                    temp, StandardCharsets.UTF_8)) {
                data.store(writer, "Pet Clinic Tracker");
            }

            try {
                Files.move(
                        temp, target,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(
                        temp, target,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    public static Clinic load(Path file) throws IOException {
        if (!Files.exists(file)) {
            return demo();
        }

        Properties data = new Properties();

        try (Reader reader = Files.newBufferedReader(
                file, StandardCharsets.UTF_8)) {
            data.load(reader);
        }

        try {
            if (!"pet-clinic-simple-1".equals(
                    data.getProperty("format"))) {
                throw new IllegalArgumentException(
                        "Unsupported data format."
                );
            }

            Clinic clinic = new Clinic();

            for (String id : split(data.getProperty("patients"))) {
                String key = id + ".";

                Animal animal = clinic.add(
                        id,
                        data.getProperty(key + "species"),
                        data.getProperty(key + "name"),
                        data.getProperty(key + "owner"),
                        data.getProperty(key + "breed"),
                        Integer.parseInt(
                                data.getProperty(key + "months", "-1")
                        ),
                        Double.parseDouble(
                                data.getProperty(key + "weight")
                        )
                );

                int count = Integer.parseInt(
                        data.getProperty(key + "notes")
                );

                if (count < 0 || count > 100000) {
                    throw new IllegalArgumentException(
                            "Invalid note count."
                    );
                }

                for (int i = 0; i < count; i++) {
                    animal.restoreNote(
                            data.getProperty(key + "note" + i)
                    );
                }

                clinic.assignCare(
                        id,
                        split(data.getProperty(key + "care"))
                );
            }

            return clinic;
        } catch (RuntimeException ex) {
            throw new IOException(
                    "Invalid clinic data. " + ex.getMessage(), ex
            );
        }
    }

    private static List<String> split(String text) {
        if (text == null) {
            throw new IllegalArgumentException(
                    "Missing saved field."
            );
        }

        return text.isEmpty()
                ? List.of()
                : Arrays.asList(text.split(","));
    }

    public static Clinic demo() {
        Clinic clinic = new Clinic();

        clinic.register(
                "Dog", "Buddy", "Alex Reyes",
                "Golden retriever", 36, 24.5
        );
        clinic.register(
                "Cat", "Luna", "Sam Cruz",
                "British shorthair", 24, 4.2
        );
        clinic.register(
                "Bird", "Kiwi", "Jamie Santos",
                "Lovebird", 14, 0.05
        );
        clinic.register(
                "Dog", "Milo", "Robin Lim",
                "Beagle", 18, 9.4
        );
        clinic.register(
                "Cat", "Cleo", "Taylor Lee",
                "Domestic shorthair", 48, 3.8
        );
        clinic.register(
                "Dog", "Coco", "Drew Garcia",
                "Shih tzu", 30, 5.6
        );

        clinic.assignCare("P1", List.of("V1", "T1", "T2"));
        clinic.assignCare("P2", List.of("V2", "T1"));
        clinic.assignCare("P3", List.of("V3", "T3"));
        clinic.assignCare("P4", List.of("V1", "T1"));
        clinic.assignCare("P5", List.of("V2", "T2"));
        clinic.assignCare("P6", List.of("V1", "T3"));

        clinic.patients.get("P1").addNote(
                "Routine wellness examination completed."
        );

        return clinic;
    }
}