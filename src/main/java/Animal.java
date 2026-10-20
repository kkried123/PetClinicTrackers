import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Animal {
    private final String id, name, owner, breed;
    private double weight;
    private int ageMonths;
    private final List<String> notes = new ArrayList<>();

    public Animal(
            String id, String name, String owner,
            String breed, int ageMonths, double weight) {
        this.id = required(id);
        this.name = required(name);
        this.owner = required(owner);
        this.breed = required(breed);

        // -1 is used when an older saved record has no age.
        if (ageMonths < -1 || ageMonths > 1200) {
            throw new IllegalArgumentException(
                    "Age must be between 0 and 1200 months."
            );
        }

        this.ageMonths = ageMonths;
        setWeight(weight);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getOwner() { return owner; }
    public String getBreed() { return breed; }
    public double getWeight() { return weight; }
    public int getAgeMonths() { return ageMonths; }

    public String getAgeText() {
        if (ageMonths < 0) return "Not recorded";
        if (ageMonths < 12) return ageMonths + " mo";

        return ageMonths / 12 + " yr"
                + (ageMonths % 12 == 0
                ? ""
                : " " + ageMonths % 12 + " mo");
    }

    public void setAgeMonths(int months) {
        if (months < 0 || months > 1200) {
            throw new IllegalArgumentException(
                    "Age must be between 0 and 1200 months."
            );
        }
        ageMonths = months;
    }

    public List<String> getNotes() {
        return new ArrayList<>(notes);
    }

    public final void setWeight(double value) {
        if (!Double.isFinite(value) || value <= 0 || value > 1000) {
            throw new IllegalArgumentException(
                    "Weight must be above 0 and at most 1000 kg."
            );
        }
        weight = value;
    }

    public void addNote(String text) {
        restoreNote(LocalDate.now() + " | " + required(text));
    }

    void restoreNote(String text) {
        notes.add(required(text));
    }

    public abstract String getSpecies();
    public abstract String getDietaryNeeds();
    public abstract double getFoodGrams();

    static String required(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Please complete all fields."
            );
        }
        return text.trim();
    }
}