package androidumps.callblocker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;


public class CallBlockerActivity extends AppCompatActivity {   private ListView LIST_VIEW;
    private ArrayList<String> ARRAY_LIST_OF_NAMES = new ArrayList<String>();
    private ArrayAdapter<String> LIST_ADAPTER;
    private SharedPreferences SHARED_PREF;
    private SharedPreferences.Editor EDITOR;
    private Uri URI_CONTACT;
    private String CONTACT_ID;

    //private FloatingActionButton ADD_CONTACT_FAB;


    private static final String SHARED_PREF_NAME = "BLACKLIST";
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_blocker);


        SHARED_PREF = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        EDITOR = SHARED_PREF.edit();

        //LIST_VIEW = (ListView) findViewById(R.id.listView);

        /*ADD_CONTACT_FAB = (FloatingActionButton) findViewById(R.id.add_contact);

        ADD_CONTACT_FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
            }
        });*/

        //checking whether shared preference having any data or not
        if(SHARED_PREF.getAll().size()>=1)
        {
            setNames();
            LIST_ADAPTER = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,ARRAY_LIST_OF_NAMES);
            //LIST_VIEW.setAdapter(LIST_ADAPTER);
        }
        else
        {
            LIST_ADAPTER = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,ARRAY_LIST_OF_NAMES);
            //LIST_VIEW.setAdapter(LIST_ADAPTER);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_call_blocker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            URI_CONTACT = data.getData();
            //get the contact name and number , then store it in to shared preference (BLACKLIST)
            EDITOR.putString(retrieveContactName(),retrieveContactNumber());
            EDITOR.commit();

            LIST_ADAPTER.clear();
            ARRAY_LIST_OF_NAMES.clear();
            setNames();

            //notifyDataSetChanged() will say to adapter that, data for list has been updated so update the list with including newer values
            LIST_ADAPTER.notifyDataSetChanged();
        }
    }


    // function for get the names from the shared preference and set the names to an array
    public void setNames(){
        //Getting all the names from the shared preference and storing in to
        Map<String,?> entries = SHARED_PREF.getAll();
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            ARRAY_LIST_OF_NAMES.add(key.toUpperCase());
        }
        //sorting the list of names based on ALPHABETICAL ORDER
        Collections.sort(ARRAY_LIST_OF_NAMES);
    }

    // this function is used to retrive the contact number from the database
    private String retrieveContactNumber() {

        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(URI_CONTACT,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            CONTACT_ID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();


        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{CONTACT_ID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();
        return contactNumber;
    }

    // this function is used to retrive the contact name from the database
    private String retrieveContactName() {

        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(URI_CONTACT, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

        return contactName;
    }
}
