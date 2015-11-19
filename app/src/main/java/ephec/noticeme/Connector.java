package ephec.noticeme;


import android.net.Uri;
import android.util.Base64;

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
            System.out.println("Une Exception dans login : "+e);
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
        String key = "IAmKey!";
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

    //try {
    //----------------------------------------------------------------------------------
    //                  OLD HASHED METHOD
    //----------------------------------------------------------------------------------
                /*MessageDigest digest = MessageDigest.getInstance("SHA-512");
                byte[] hash = digest.digest(mPassword.getBytes("UTF-8"));
                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < hash.length; i++) {
                    String hex = Integer.toHexString(0xff & hash[i]);
                    if(hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                String hashed = hexString.toString();*/
    //----------------------------------------------------------------------------------
    //                  END OLD HASHED METHOD
    //----------------------------------------------------------------------------------

    //openssl rsa -pubin -in pubkey.pem -modulus -noout
    //openssl rsa -pubin -in pubkey.pem -text -noout
                /*BigInteger modulus = new BigInteger("D927FD65F4F1218349B0A198401997CFF12E27AE0EF02666C91603BF2BAE7E5EBC9191927AEC909C0A10BD925289E5451C758FF32CC38F82DAFC38230DC9436712026BE3C789AFAC5AF32214B5110D0A7AF81D4D375C2D18BE6644817171585DCC35B5EEBF1312682B1CDB040974D75F3385DEEB5AED3B12115A5D2E19A7F36F", 16);
                BigInteger pubExp = new BigInteger("010001", 16);

                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, pubExp);
                RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);

                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, key);

                //encryption
                byte[] cipherData = cipher.doFinal(mPassword.getBytes());
                //translation to string
                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < cipherData.length; i++) {
                    String hex = Integer.toHexString(0xff & cipherData[i]);
                    if(hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                String hashed = hexString.toString();*/
    //int i = Integer.parseInt(mPassword) ^ 1101101101;
                /*String key = "css is awesome !";

                String hashed="";

                for (int i = 0; i < mPassword.length(); i++){
                    for (int j = 0; j < key.length() && i < mPassword.length();j++){
                        hashed += mPassword.charAt(i) ^ key.charAt(j);
                        i++;
                    }
                }
                System.out.println(hashed);
                System.out.println(mPassword);*/

                /*URL url= new URL("http://superpie.ddns.net:8035/app_dev.php/android/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(100000);
                conn.setConnectTimeout(150000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("username",mEmail)
                                                .appendQueryParameter("password",mPassword);
                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                response = "";
                String line = "";
                while ((line = bufferedReader.readLine())!=null){
                    response+= line;
                }
                bufferedReader.close();
                inputStream.close();
                conn.disconnect();
                System.out.println("la fin de connexion");*/


            /*}catch (Exception e){
                System.out.println("Une exeption s'est produite : "+e);
                return false;
            }*/

}
