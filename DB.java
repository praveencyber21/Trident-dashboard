
// import io.github.cdimascio.dotenv.Dotenv;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Base64;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;



public class DB {
    
    // private Dotenv dotenv = Dotenv.load();

    // private String url = dotenv.get("DB_URL");
    // private String user = dotenv.get("DB_USER");
    // private String password = dotenv.get("DB_PASSWD");

    Connection myConnection = null;

    private String url;
    private String user;
    private String password;
    private SecretKey key;
    
   

    public DB() {
        try {
            loadProperties();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void loadProperties() throws Exception{
        Properties properties = new Properties();

        try (FileInputStream file = new FileInputStream("config.properties")) {
            properties.load(file);
            

            // Convert base64 to key
            key = base64ToKey(properties.getProperty("secret.key"));
            //key = base64ToKey(System.getProperty("DB_SECRET_KEY"));
            url = properties.getProperty("db.url");
            user = properties.getProperty("db.user");
            password = properties.getProperty("db.passwd");

        } catch (Exception e) {
           System.out.println(e.getMessage());
        }
    }
    
    // Convert method for base64 to key
    private SecretKey base64ToKey(String key) {
       
        byte[] decodedKey = null;
        try {
            decodedKey = Base64.getDecoder().decode(key);
        } catch (IllegalArgumentException e) {
            System.out.println("Key error: " + e.getMessage());
            System.exit(1);
        }
        
        if(decodedKey != null) {
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        }
        return null;

    }

    // Data decryption method
    private String decrypt(String encryptedData) throws Exception {
        
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }
    
   

    public Connection createConnection() {
        
        try { 

            myConnection = DriverManager.getConnection(decrypt(url), decrypt(user), decrypt(password)); 
            
            return myConnection;
            
        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }

        return null;
       
    }   
}

