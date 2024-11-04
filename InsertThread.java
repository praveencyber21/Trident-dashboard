
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


class InsertThread implements Runnable{


    @Override
    public void run() {
        
        // Database Connection class
        DB db = new DB();
        

        try (Connection connection = db.createConnection()) {

            String insertQuery = "INSERT INTO controller_tag_data (cid_fk, cmd_code) VALUES (?, ?)";

            PreparedStatement statement = connection.prepareStatement(insertQuery);

            statement.setInt(1, 511);
            statement.setInt(2, 103);

            for(int i=1; i>0; i++) {
                int insertedRows = statement.executeUpdate();


                if(insertedRows > 0) {
                    System.out.println(i + " Data inserted");
                    Thread.sleep(1000);
                }
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
