package by.training.cafe.entity;

/**
 * The class {@code DishCategory} is an enumeration
 * of the supported dish categories.
 *
 * @author Nikita Romanov
 */
public enum DishCategory {

    PIZZA, SNACKS, SAUCES, DRINKS, DESSERTS, SALADS;

    public static boolean contains(String test) {
        for (DishCategory category : DishCategory.values()) {
            if (category.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
