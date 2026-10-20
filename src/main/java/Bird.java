public class Bird extends Animal {
    public Bird(
            String id, String name, String owner,
            String breed, int months, double kg) {
        super(id, name, owner, breed, months, kg);
    }

    @Override public String getSpecies() {
        return "Bird";
    }

    @Override public String getDietaryNeeds() {
        return "Bird-specific diet, fresh water and a clean enclosure.";
    }

    @Override public double getFoodGrams() {
        return getWeight() * 100;
    }
}