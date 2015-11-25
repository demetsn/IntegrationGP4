package ephec.noticeme;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
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
    private User current;
    private User modifUser;

    private FloatingActionButton fab;

    private String actualName = "";
    private String actualFisrtname = "";
    private String actualEmail = "";

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
        current = db.getCurrentUSer();
        db.close();

        actualName = current.getNom();
        actualFisrtname = current.getPrenom();
        actualEmail = current.getMail();

        EditProfileTask task = new EditProfileTask(this,false);
        task.execute((Void)null);
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
                modifUser = new User();
                modifUser.setMail(email.getText().toString());
                modifUser.setNom(name.getText().toString());
                modifUser.setPrenom(firstname.getText().toString());
                //TODO ASYNCTASK
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
                User usr = db1.getCurrentUSer();
                db1.setCurrentToFalse(usr);
                db1.close();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private class EditProfileTask extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private boolean isEdit;

        EditProfileTask(Context context, boolean isEdit) {
            this.context = context;
            this.isEdit = isEdit;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Connector connect = new Connector();
            DBHelper db = new DBHelper(context);
            db.getWritableDatabase();
            if(isEdit){
                connect.connect("http://ephecnoticeme.me/app.php/android/edituser");
                String response = connect.editUser(actualEmail,Connector.decrypt(Connector.decrypt(current.getPassword())),modifUser);
                if(response.equals("0")){
                    connect.disconnect();
                    db.setCurrentToFalse(current);
                    db.close();
                    return false;
                }
                return true;

            }else{
                if (!connect.connect("http://ephecnoticeme.me/app.php/android/getuser")) {
                    connect.disconnect();
                } else {
                    String answer = connect.login(actualEmail, Connector.decrypt(Connector.decrypt(current.getPassword())));
                    if (answer.equals("0")) {
                        connect.disconnect();
                        db.setCurrentToFalse(current);
                        db.close();
                        return false;
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
                            db.setCurrentToFalse(current);
                            db.close();
                            connect.disconnect();
                            return false;
                        }
                        db.close();
                    }
                }
            }
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                if(isEdit){
                    Intent intent= new Intent(context,MainActivity.class);
                    startActivity(intent);
                }else{
                    name.setText(actualName);
                    firstname.setText(actualFisrtname);
                    email.setText(actualEmail);
                }

            } else {
                Intent disconnect = new Intent(context, LoginActivity.class);
                startActivity(disconnect);
            }
        }
    }
}

