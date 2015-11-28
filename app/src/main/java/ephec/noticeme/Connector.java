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
            System.out.println("PASS : "+crypted);
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
    public String delMemo(String mEmail, String mPassword, int id){
        String response= "";
        try{
            String crypted = encrypt(mPassword);
            System.out.println("PASS : "+crypted);
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("username",mEmail)
                    .appendQueryParameter("password",crypted)
                    .appendQueryParameter("id",""+id);
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
        return  response;
    }
    public String addMemo(String mEmail, String mPassword, Alarm memo){
        String response = "";
        try{
            System.out.println("ID IDI IDDIIDIDIDI : "+memo.getAlarmDate());
            String crypted = encrypt(mPassword);
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("username",mEmail)
                    .appendQueryParameter("password", crypted)
                    .appendQueryParameter("title",memo.getTitle())
                    .appendQueryParameter("description",memo.getDescription())
                    .appendQueryParameter("date",""+memo.getAlarmDate().replace('&',' '))
                    .appendQueryParameter("latitude",""+memo.getLatitude())
                    .appendQueryParameter("longitude",""+memo.getLongitude())
                    .appendQueryParameter("idmemo", "" + memo.getId());

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
    public String editUser(String mEmail, String mPassword, User usr){
        String response = "";
        try{
            String crypted = encrypt(mPassword);
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("username",mEmail)
                    .appendQueryParameter("password", crypted)
                    .appendQueryParameter("mail",usr.getMail())
                    .appendQueryParameter("firstname",usr.getPrenom())
                    .appendQueryParameter("lastname",""+usr.getNom());
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
