package application;


import java.sql.*;

public class ReciepeDao {

    public static int saveReciepe(ReciepeBean reciepeBean) {
        Connection connection = null;
        Statement statement = null;
        int id = 0;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/dietary",
                            "postgres", "postgres");

            statement = connection.createStatement();

            String sql = "INSERT INTO reciepe( " +
                    "title, receipe) " +
                    "VALUES ( '" + reciepeBean.getTitle() + "' , '" + reciepeBean.getRecipe() + "' );";

            statement.execute(sql);

            id = getLastIndex(connection);

            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return id;
    }

    private static int getLastIndex(Connection connection) throws SQLException {
        Statement statement= null;

        statement = connection.createStatement();

        String sql = "SELECT id FROM public.reciepe where id = (SELECT max(id) FROM public.reciepe);";

        ResultSet set = statement.executeQuery(sql);
        int id = 0;

        while (set.next()){
            id = set.getInt("id");
        }

        set.close();

        return id;
    }

}
