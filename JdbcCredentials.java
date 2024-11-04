import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Properties;
import java.io.FileOutputStream;

public class JdbcCredentials {

    
    // Generate new AES secret key
    private static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();

        return secretKey;
    }

    // Convert secret key to base64
    private static String keyToBase64(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    // Encrypt 
    private static String encrypt(String data, SecretKey secretKey) throws Exception {
        
        Cipher cipher =  Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    
    private static void setCredentials(String url, String user, String password) {
        Properties properties = new Properties();

        try {
            SecretKey key = generateKey();
            String secretkey = keyToBase64(key);
            

            // Set key value pairs
            properties.setProperty("db.url", encrypt(url, key));
            properties.setProperty("db.user", encrypt(user, key));
            properties.setProperty("db.passwd", encrypt(password, key));
            properties.setProperty("secret.key", secretkey);
            
           

            // Write to properties file
            FileOutputStream out = new FileOutputStream("config.properties");
            properties.store(out, "Database credentials");
            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
   
    public static void main(String[] args) throws Exception{
        
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Enter your database url: ");
        String url = sc.next();

        System.out.println("Enter database username: ");
        String user = sc.next();

        System.out.println("Enter database password; ");
        String password = sc.next();

        setCredentials(url, user, password);
        sc.close();

        
        
    }
}
