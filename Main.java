import java.sql.Connection;
// import java.util.concurrent.*;


public class Main {
    public static void main(String[] args) throws Exception {
        
        System.out.println("\nTrident_dashboard_beta database program");
        
        //Database connection class
        DB db = new DB();

        // InsertThread class
        InsertThread insert = new InsertThread();

        // FetchAndInsertThread class
        FetchAndInsertThread fetchInsert = new FetchAndInsertThread();

        Connection myConnection = db.createConnection();

        // Table Monitor class
        TableMonitor tableMonitor = new TableMonitor();

        

        // Scheduler
        //ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        

        if(myConnection != null) {
            Thread insertThread = new Thread(insert);
            Thread fiThread = new Thread(fetchInsert);
            Thread monitorThread = new Thread(tableMonitor);
            

            insertThread.start();
            monitorThread.start();
            fiThread.start();
            //scheduler.scheduleAtFixedRate(fetchInsert, 0, 1, TimeUnit.MINUTES);
            

            try {
                insertThread.join();
                fiThread.join();

            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            } finally {
                if(myConnection != null) {
                    //scheduler.shutdown();
                    myConnection.close();
                    
                }
            }
        } 

       
    }    
}
