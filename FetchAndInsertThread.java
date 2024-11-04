
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class FetchAndInsertThread implements Runnable {
    
    DB db = new DB();

    @Override
    public void run() {
        
        try (Connection connection = db.createConnection()) {

            // Local time
            LocalDateTime now = LocalDateTime.now();

            // Date and time pattern 2023-08-20 14:19:20 "yyyy-MM-dd HH:mm:ss" (Data time stamp pattern)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Past 1 Minute Date and time 
            LocalDateTime pastDateTime = now.minusMinutes(1);

            // Format a dateandtime 
            String formatedDate = pastDateTime.format(formatter);
            System.out.println("Thread 2 started...");

            String fetchQuery = "SELECT * FROM controller_tag_data ORDER BY data_timestamp DESC LIMIT 10";
            String insertQuery = "INSERT INTO controller_tag_data_archive (id_pk, cid_fk, cmd_code, updt) VALUES (?, ?, ?, ?)";
            String deleteQuery = "DELETE FROM controller_tag_data WHERE data_timestamp < ?";

            PreparedStatement fetchStatement = connection.prepareStatement(fetchQuery);
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);

            //fetchStatement.setString(1, formatedDate);

            //Result based on date and time condition
            ResultSet fetchResult = fetchStatement.executeQuery();

            while(fetchResult.next()) {
                
                insertStatement.setInt(1, fetchResult.getInt(1));
                insertStatement.setInt(2, fetchResult.getInt(2));
                insertStatement.setInt(3, fetchResult.getInt(3));
                insertStatement.setTimestamp(4, fetchResult.getTimestamp(132));
                // insertStatement.setTimestamp(5, fetchResult.getTimestamp(133));

                insertStatement.addBatch();
                
                // if(insertedRows > 0 ){
                //     System.out.println(fetchResult.getInt(1) + " Data inserted into archive table");
                // }
                System.out.println(fetchResult.getTimestamp("data_timestamp"));
                deleteStatement.setTimestamp(1, fetchResult.getTimestamp("data_timestamp"));

                deleteStatement.addBatch();

                // if(deletedRows > 0) {
                //     System.out.println("Deleted from data table");
                // }

            } 

            int[] insertedRows = insertStatement.executeBatch();
            int[] deletedRows = deleteStatement.executeBatch();

            if(insertedRows.length != 0) {
                System.out.println("Data inserted into controller_tag_data_archive");
            }
            if(deletedRows.length != 0) {
                System.out.println("Data has been deleted from controller_tag_data.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
