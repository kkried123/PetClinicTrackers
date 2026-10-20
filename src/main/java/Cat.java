public class Cat extends Animal {
    public Cat(
            String id, String name, String owner,
            String breed, int months, double kg) {
        super(id, name, owner, breed, months, kg);
    }

    @Override public String getSpecies() {
        return "Cat";
    }

    @Override public String getDietaryNeeds() {
        return "Cat diet, fresh water, play and clean litter.";
    }

    @Override public double getFoodGrams() {
        return getWeight() * 15;
    }
}