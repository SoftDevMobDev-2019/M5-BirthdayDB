package au.edu.swin.sdmd.birthdayDB;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * author various
 * amended by nronald August 2019 to work with API 28
 *
 * This is an old example from pre-2017 showing how to use sqlite directly in an Android app.
 * It was not used for SDMD in 2018.
 */

public class MainActivity extends AppCompatActivity {

    private DBHelper db = null;
    private Cursor personCursor = null;
    private String selectAllQuery = "SELECT _ID, name, dob " + "FROM person ORDER BY name";
    ListView lv = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHelper(this);
        lv = findViewById(R.id.birthday_list);
        showAllPeople();
        registerForContextMenu(lv);
    }

    /** Query database -- and populate results into the list adapter */
    private void showAllPeople() {
        if (personCursor != null) personCursor.close(); // close old cursor

        personCursor = db.getReadableDatabase()
                .rawQuery(selectAllQuery, null);

        final ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.row,
                personCursor, new String[] { DBHelper.NAME, DBHelper.DOB },
                new int[] { R.id.name, R.id.dob }, 0);

        lv.setAdapter(adapter);

    }

    /** Add a new person to the database */
    private void processAdd(DialogWrapper wrapper) {
        ContentValues values = new ContentValues(2);
        values.put(DBHelper.NAME, wrapper.getName());
        values.put(DBHelper.DOB, wrapper.getDOB());
        db.getWritableDatabase()
                .insert(DBHelper.PERSON_TBL, DBHelper.NAME, values);
        showAllPeople();
    }

    /** Remove an existing person from the database */
    private void processDelete(long rowId) {
        String[] args = { String.valueOf(rowId) };
        db.getWritableDatabase()
                .delete(DBHelper.PERSON_TBL, "_ID=?", args);
        showAllPeople();
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo)
    {
        // add(groupId, itemID, order, title)
        menu.add(Menu.NONE, 0, Menu.NONE, "Delete");
    }

    public boolean onContextItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case 0:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                delete(info.id);
                return (true);
        }

        return (super.onOptionsItemSelected(item));
    }


    public void onDestroy() {
        super.onDestroy();

        personCursor.close();
        db.close();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bd, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_person:
                add();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void add() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View addView = inflater.inflate(R.layout.add_new, null);
        final DialogWrapper wrapper = new DialogWrapper(addView);

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_person)
                .setView(addView)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,
                                                int whichButton)
                            {
                                processAdd(wrapper);
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,
                                                int whichButton)
                            {
                                // ignore, just dismiss
                            }
                        }).show();
    }

    private void delete(final long rowId) {
        if (rowId >= 0)
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_person)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton)
                                {
                                    processDelete(rowId);
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton)
                                {
                                    // ignore, just dismiss
                                }
                            }).show();
        }
    }


    class DialogWrapper {
        EditText nameField = null;

        EditText dobField = null;

        View base = null;

        DialogWrapper(View base) {
            this.base = base;
            dobField = (EditText) base.findViewById(R.id.dob);
        }

        String getName() {
            return (getNameEditText().getText().toString());
        }

        String getDOB() {
            return (getDOBEditText().getText().toString());
        }

        private EditText getNameEditText() {
            if (nameField == null)
            {
                nameField = (EditText) base.findViewById(R.id.name);
            }

            return (nameField);
        }

        private EditText getDOBEditText() {
            if (dobField == null)
            {
                dobField = (EditText) base.findViewById(R.id.name);
            }

            return (dobField);
        }
    }
}
