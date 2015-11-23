package ephec.noticeme;


import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
//import org.apache.commons.codec.binary.Base64;

public class Connector {
    private HttpURLConnection conn;
    private URL url;

    public Connector(){
        super();
    }

    public boolean connect(String adresse){
        try{
            url = new URL(adresse);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(150000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
        }catch(Exception e){
            System.out.println("Une exception s'est produite : "+e);
            return false;
        }
        return true;
    }

    public String login(String mEmail, String mPassword){
        String response = "";
        try{
            String crypted = encrypt(mPassword);
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("username",mEmail)
                    .appendQueryParameter("password",crypted);
            String query = builder.build().getEncodedQuery();
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            InputStream inputStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));

            String line = "";
            while ((line = bufferedReader.readLine())!=null){
                response+= line;
            }
            bufferedReader.close();
            inputStream.close();
        }catch (Exception e){

            e.printStackTrace();
            return "ERROR";
        }
        return response;
    }

    public boolean disconnect(){
        try{
            conn.disconnect();
        }catch(Exception e){
            System.out.println("Erreur lors de deconnection : "+e);
            return false;
        }
        return true;
    }
    public ArrayList<User> getUser(String mail){
        //TODO
        return null;
    }
    public boolean setUser(User user){
        //TODO
        return false;
    }
    public boolean addMemo(Alarm memo){
        //TODO recuperer l'id du server
        return false;
    }
    public boolean removeMemo(Alarm memo){
        //TODO
        return false;
    }
    public ArrayList<Alarm> getMemo(){
        //TODO
        return null;
    }

    public static String encrypt(String input){
        String key = "Notice Me Sempai";
        byte[] crypted = null;
        try{
            System.out.println(key.getBytes());
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
        }catch(Exception e){
            System.out.println(e.toString());
        }
        return Base64.encodeToString(crypted,Base64.DEFAULT);
    }
    public static String decrypt(String input){
        String key = "Notice Me Sempai";
        byte[] output = null;
        try{
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.decode(input,Base64.DEFAULT));
        }catch(Exception e){
            System.out.println(e.toString());
        }
        return new String(output);
    }
}
