package ephec.noticeme;

import android.content.Intent;
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

        //TODO Set les champs avec les valeurs enregistrées sur le serveur et aussi actualName,FirstName et Email.
        name = (EditText) findViewById(R.id.editProfileName);
        name.setText(actualName);
        name.setEnabled(false);
        firstname = (EditText) findViewById(R.id.editProfileFirstname);
        firstname.setText(actualFisrtname);
        firstname.setEnabled(false);
        email = (EditText) findViewById(R.id.editProfileEmail);
        email.setText(actualEmail);
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
