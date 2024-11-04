import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TableMonitor implements Runnable{
    
    private Connection connection;
    private int targetRecordCount  = 100;
    
    
    DB db = new DB();

    @Override
    public void run() {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            
            connection = db.createConnection();
            
            executor.submit(() -> {
            while (true) {
                
                try {
                    int recordCount = getRecordCount();
                    if (recordCount >= targetRecordCount) {
                        triggerSecondThread();
                    }
                    Thread.sleep(500);
                } catch (SQLException | InterruptedException e) {
                    System.err.println("Error monitoring table: " + e.getMessage());
                    System.exit(0);
                }
            }
        });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }

    private int getRecordCount() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM controller_tag_data");
        resultSet.next();
        return resultSet.getInt(1);
    }

    private void triggerSecondThread() {
        
        FetchAndInsertThread fetchInsert = new FetchAndInsertThread();

        Thread fiThread = new Thread(fetchInsert);;
        fiThread.start();
    }
}

