public class Dog extends Animal {
    public Dog(
            String id, String name, String owner,
            String breed, int months, double kg) {
        super(id, name, owner, breed, months, kg);
    }

    @Override public String getSpecies() {
        return "Dog";
    }

    @Override public String getDietaryNeeds() {
        return "Dog diet, fresh water and daily exercise.";
    }

    @Override public double getFoodGrams() {
        return getWeight() * 20;
    }
}