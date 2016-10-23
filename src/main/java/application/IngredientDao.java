package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

public class IngredientDao {

    public static void saveIngredient(List<Ingredient> ingredients, int reciepeId) {
        Connection connection;
        Statement statement;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/dietary",
                            "postgres", "postgres");

            for (Ingredient ingredient : ingredients) {
                statement = connection.createStatement();
                String sql = "INSERT INTO public.ingredients( " +
                        "receipeId, ingredientName, ingredientWeight) " +
                        "VALUES ( "
                        + reciepeId + " , '"
                        + ingredient.getIngredient() + "' , '"
                        + ingredient.getWeight() + "' );";

                statement.execute(sql);
                statement.close();
            }

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());

        }
    }
}
