package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class NutrientsDao {
    public static void saveNutrients(NutrientsBean nutrients, int reciepeId) {
        Connection connection;
        Statement statement;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/dietary",
                            "postgres", "postgres");

            statement = connection.createStatement();

            String sql = "INSERT INTO public.nutrients( " +
                    "reciepeId, fats, carbones, proteine, kcal)" +
            "VALUES ( "
                    + reciepeId + " , "
                    + nutrients.getFats() + ", "
                    + nutrients.getCarbonate() + " , "
                    + nutrients.getProteine() + " , "
                    + nutrients.getKkal() + ");";

            statement.execute(sql);

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());

        }

    }
}
