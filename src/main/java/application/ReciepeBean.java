package application;


import java.util.List;

public class ReciepeBean {

    private String title;
    private String recipe;
    private List<Ingredient>  ingredients;

    public ReciepeBean(String title, String recipe, List<Ingredient> ingredients, NutrientsBean nutrientsBean) {
        this.title = title;
        this.recipe = recipe;
        this.ingredients = ingredients;
        this.nutrientsBean = nutrientsBean;
    }

    public ReciepeBean(){

    }

    private NutrientsBean nutrientsBean;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public NutrientsBean getNutrientsBean() {
        return nutrientsBean;
    }

    public void setNutrientsBean(NutrientsBean nutrientsBean) {
        this.nutrientsBean = nutrientsBean;
    }
}
