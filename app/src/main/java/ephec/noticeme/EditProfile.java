package ephec.noticeme;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

public class EditProfile extends AppCompatActivity {

    private EditText name;
    private EditText firstname;
    private EditText email;

    private FloatingActionButton fab;

    private String actualName = "Dekeyser";
    private String actualFisrtname = "Olivier";
    private String actualEmail = "Dksrolivier@gmail.com";

    private String newName;
    private String newFirstname;
    private String newEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        name = (EditText) findViewById(R.id.editProfileName);
        firstname = (EditText) findViewById(R.id.editProfileFirstname);
        email = (EditText) findViewById(R.id.editProfileEmail);
        name.setEnabled(false);
        firstname.setEnabled(false);
        email.setEnabled(false);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setEnabled(true);
                firstname.setEnabled(true);
                email.setEnabled(true);
                fab.setVisibility(View.INVISIBLE);
            }
        });

        DBHelper db = new DBHelper(this.getApplicationContext());
        db.getReadableDatabase();

        User current = db.getCurrentUSer();

        actualName = current.getNom();
        actualFisrtname = current.getPrenom();
        actualEmail = current.getMail();

        Connector connect = new Connector();
        if (!connect.connect("http://superpie.ddns.net:8035/app_dev.php/android/getuser")) {
            connect.disconnect();
        } else {
            String answer = connect.login(actualEmail, Connector.decrypt(Connector.decrypt(current.getPassword())));
            if (answer.equals('0')) {
                connect.disconnect();
                db.setCurrentToFalse();
                db.close();
                Intent disconnect = new Intent(this, LoginActivity.class);
                startActivity(disconnect);
                return;
            } else {
                answer = android.text.Html.fromHtml(answer).toString();
                //System.out.println("responce : "+response);
                //DBHelper db = new DBHelper(context);
                //db.getWritableDatabase();
                try {
                    JSONObject obj = new JSONObject(answer);
                    JSONArray jArray = obj.getJSONArray("json");
                    for (int i = 0; i < jArray.length(); i++) {
                        try {
                            JSONObject oneObject = jArray.getJSONObject(i);

                            actualName = oneObject.getString("lastname");
                            actualFisrtname = oneObject.getString("firstname");
                            int id = current.getId();
                            User usr = new User();
                            usr.setId(id);
                            usr.setNom(actualName);
                            usr.setPrenom(actualFisrtname);
                            usr.setMail(actualEmail);

                            db.modifyUser(usr);

                        } catch (SQLiteConstraintException e) {

                        }
                    }
                } catch (Exception e) {
                    //TODO AFFICHER TOAST ERREUR CO SERVER
                    db.close();
                    connect.disconnect();
                    return;
                }
                db.close();
            }
            db.close();

            name.setText(actualName);
            firstname.setText(actualFisrtname);
            email.setText(actualEmail);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_memo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_done:
                /*TODO Valide le changement de profil il faut faire une connexion au serveur pour qu'il enregistre les modifications.
                *Il faudra faire un if else en fonction de la réponse du serveur. True, on retourne sur mainactivity
                *False on reste et on remet les champs non éditables avec les anciennes valeurs.
                *Enregistrer les nouvelles valeurs dans newName,Firstname et Email.*/
                boolean newProfileValidated = false;

                if (newProfileValidated) {
                    Intent returnToMain = new Intent(this, MainActivity.class);
                    startActivity(returnToMain);
                } else {
                    name.setError("Enable to connect. Please try again.");
                    name.requestFocus();
                }
                return true;

            case R.id.action_cancel:

                name.setText(actualName);
                firstname.setText(actualFisrtname);
                email.setText(actualEmail);
                name.setEnabled(false);
                firstname.setEnabled(false);
                email.setEnabled(false);
                fab.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_deco:
                DBHelper db1 = new DBHelper(this);
                db1.getReadableDatabase();
                db1.setCurrentToFalse();
                db1.close();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

