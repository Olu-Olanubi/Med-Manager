package com.example.user.med_manager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import database.DrugContentProvider;
import database.Drugs;
import utils.MyDividerItemDecoration;
import utils.RecyclerTouchListener;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class MainActivity extends AppCompatActivity {

    private DrugAdapter mAdapter;
    private List<Drugs> drugsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView noMedsView;
    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView =  findViewById(R.id.recycler_view);
        noMedsView =  findViewById(R.id.empty_med_view);

        //Declare floating action button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDrugDialog(false, null, -1);
            }
        });
        //cursor = getContentResolver().query(Drugs.CONTENT_URI, null, null, null, null);

        //drugsList.addAll(getAllDrugs());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 0));
        //Method loads all drugs from the database to screen
        refreshMedList();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_list:
                refreshMedList();
                return true;
            case R.id.search_medications:
                searchDbByName();
                return true;
            case R.id.delete_all:
                deleteAll();
                return true;
            case R.id.exit:
                    exitApp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //Method implementation to refresh database
    //If no item exists in the database, it displays an empty screen
    private void refreshMedList(){
        drugsList.clear();
        //cursor object gets database query for the first time
        cursor = getContentResolver().query(Drugs.CONTENT_URI, null, null, null, null);
        drugsList.addAll(getAllDrugs());
        mAdapter = new DrugAdapter(this, drugsList);
        recyclerView.setAdapter(mAdapter);
        toggleEmptyNotes();
    }

    private void showToast(String search){
        Uri SEARCH_URI = Uri.withAppendedPath(Drugs.CONTENT_URI, search);
        //Toast.makeText(MainActivity.this, SEARCH_URI.toString(), Toast.LENGTH_LONG).show();
        cursor = getContentResolver().query(SEARCH_URI, null, null, null, null);
        //Transfer cursor object return into drugsList ArrayList
        if(cursor != null && cursor.moveToFirst()) {
            drugsList.clear();

             do{
            Drugs d = new Drugs(
                    cursor.getInt(cursor.getColumnIndex(Drugs.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_DESC)),
                    cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_INTERVAL)),
                    cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_START_DATE)),
                    cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_END_DATE)));

            drugsList.add(d);
            }while (cursor.moveToNext());

            cursor.close();
            // refreshing the list
            mAdapter.notifyDataSetChanged();
            toggleEmptyNotes();
        }
        else{
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
        }
    }
    private void searchDbByName(){
        //inflate search layout in alert dialog
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.search_by_name, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(getString(R.string.alert_search));
        alert.setView(view);
        final EditText searchText = view.findViewById(R.id.edit_search);

        alert.setCancelable(false)
                .setPositiveButton("search",
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {
                            if (TextUtils.isEmpty(searchText.getText().toString())) {
                                Toast.makeText(MainActivity.this, "Enter Data!", Toast.LENGTH_SHORT).show();
                                return;
                            }else{

                                showToast(searchText.getText().toString());
                            }
                            dialogBox.dismiss();
                        }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alert.create();
        // show it
        alertDialog.show();
    }

    //Method to delete all records in the database
    private void deleteAll(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Warning!");
        dialogBuilder.setMessage("Are you sure you want to delete all medication records?");
        dialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getContentResolver().delete(Drugs.CONTENT_URI, null, null);
                drugsList.clear();
                mAdapter.notifyDataSetChanged();
                toggleEmptyNotes();
            }
        });
        dialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    //Method to exit the application
    private void exitApp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Warning!");
        dialogBuilder.setMessage("Exit Application?");
        dialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        dialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Set Reminder", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /** changed to switch statement
                if (which == 0) {
                    showDrugDialog(true, drugsList.get(position), position);
                } else {
                    deleteDrug(position);
                }
                */
                switch (which){
                    case 0:
                        showDrugDialog(true, drugsList.get(position), position);
                        break;
                    case 1:
                        setReminder( drugsList.get(position), position);
                        break;
                    case 2:
                        deleteDrug(position);
                        break;
                    default:
                        return;

                }
            }
        });
        builder.show();
    }
    /**
     * Shows alert dialog with EditText options to enter / edit
     * a medication.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showDrugDialog(final boolean shouldUpdate, final Drugs drug, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.add_medication, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final Calendar myCalendar = Calendar.getInstance();
        final EditText inputDrugName = view.findViewById(R.id.drug_name);
        final EditText inputDescription = view.findViewById(R.id.drug_description);
        final EditText inputInterval = view.findViewById(R.id.drug_interval);
        final EditText inputStartDate = view.findViewById(R.id.drug_start_date);
        final EditText inputEndDate = view.findViewById(R.id.drug_end_date);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_medication_dialog) : getString(R.string.lbl_edit_medication_dialog));

        final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(year, monthOfYear, dayOfMonth);
                String myFormat = "MMM d, yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

                inputStartDate.setText(sdf.format(myCalendar.getTime()));

            }
        };

        inputStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MainActivity.this, startDate, myCalendar
                        .get(YEAR), myCalendar.get(MONTH),
                        myCalendar.get(DAY_OF_MONTH)).show();
            }
        });

        //DatePicker for endDate
        final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(year, monthOfYear, dayOfMonth);

                String myFormat = "MMM d, yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

                inputEndDate.setText(sdf.format(myCalendar.getTime()));

            }

        };
        //onClickListener for endDate.
        inputEndDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MainActivity.this, endDate, myCalendar
                        .get(YEAR), myCalendar.get(MONTH),
                        myCalendar.get(DAY_OF_MONTH)).show();
            }
        });
        if (shouldUpdate && drug != null) {
            inputDrugName.setText(drug.getDrugName());
            inputDescription.setText(drug.getDescription());
            inputInterval.setText(drug.getInterval());
            inputStartDate.setText(drug.getStartDate());
            inputEndDate.setText(drug.getEndDate());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });
        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputDrugName.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Data!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }
                // check if user updating med list
                if (shouldUpdate && drug != null) {
                    // update med list by it's id
                    updateDrug(inputDrugName.getText().toString(), inputDescription.getText().toString(), inputInterval.getText().toString(), inputStartDate.getText().toString(), inputEndDate.getText().toString(), position);
                } else {
                    // create new med list
                    createDrug(inputDrugName.getText().toString(), inputDescription.getText().toString(), inputInterval.getText().toString(), inputStartDate.getText().toString(), inputEndDate.getText().toString()); /**,Integer.parseInt(inputInterval.getText().toString()));*/
                }

            }
        });
    }
    /**
     * Inserting new medication in db
     * and refreshing the list
     */
    private void createDrug(String drugName, String description, String interval, String startDate, String endDate) {

        ContentValues values = new ContentValues();
        values.put(Drugs.COLUMN_NAME, drugName);
        values.put(Drugs.COLUMN_DESC, description);
        values.put(Drugs.COLUMN_INTERVAL, interval);
        values.put(Drugs.COLUMN_START_DATE, startDate);
        values.put(Drugs.COLUMN_END_DATE, endDate);

        //Insert the content values to the db using the content resolver, nd get a URI in return
        Uri uri = getContentResolver().insert(Drugs.CONTENT_URI, values);
        //query the URI for the data just inserted, and get a cursor in return
        cursor = getContentResolver().query(uri, null, null, null, null);
        //Transfer cursor object return into drugsList ArrayList
        if(cursor != null && cursor.moveToFirst()){
            Drugs d = new Drugs(
                    cursor.getInt(cursor.getColumnIndex(Drugs.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_DESC)),
                    cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_INTERVAL)),
                    cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_START_DATE)),
                    cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_END_DATE)));
            drugsList.add(d);

            // refreshing the list
            mAdapter.notifyDataSetChanged();
            toggleEmptyNotes();
        }else{
            Toast.makeText(this, "No value in Cursor", Toast.LENGTH_LONG).show();
        }
        /**
        String _id = String.valueOf(cursor.getInt(cursor.getColumnIndex(Drugs.COLUMN_ID)));
        String _name = cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_NAME));
        String _description = cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_DESC));
        String _interval = cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_INTERVAL));
        String _startDate = cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_START_DATE));
        String _endDate = cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_END_DATE));
        cursor.close();

        Toast.makeText(this, _id+" "+ _name+" "+_description+" "+_interval+" "+_startDate+" "
                +_endDate, Toast.LENGTH_LONG).show();
        */
    }
    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateDrug(String drugName, String description, String interval, String startDate, String endDate, int position) {
        /**
        Drugs d = drugsList.get(position);
        // updating drug data text
        d.setDrugName(drugName);
        d.setDescription(description);
        d.setInterval(interval);
        d.setStartDate(startDate);
        d.setEndDate(endDate);
        // updating a medication in db
        db.updateDrug(d);
        // refreshing the list
        drugsList.set(position, d);
        mAdapter.notifyItemChanged(position);
        toggleEmptyNotes();
        **/

        Drugs d = drugsList.get(position);
        // updating drug data text
        d.setDrugName(drugName);
        d.setDescription(description);
        d.setInterval(interval);
        d.setStartDate(startDate);
        d.setEndDate(endDate);
        ContentValues values = new ContentValues();
        values.put(Drugs.COLUMN_NAME, d.getDrugName());
        values.put(Drugs.COLUMN_DESC, d.getDescription());
        values.put(Drugs.COLUMN_INTERVAL, d.getInterval());
        values.put(Drugs.COLUMN_START_DATE, d.getStartDate());
        values.put(Drugs.COLUMN_END_DATE, d.getEndDate());

        Uri updateUri = ContentUris.withAppendedId(Drugs.CONTENT_URI, d.getId());
         int drugsUpdated = getContentResolver().update(updateUri, values, null, null);

        if(drugsUpdated != 0) {
            //set notifications if a task was updated
            mAdapter.notifyItemChanged(position);
        }
    }
    /**
     * Deleting a medication from SQLite and removing the
     * item from the list by its position
     */
    private void deleteDrug(int position) {
        // deleting the note from db

       Uri deleteUri = ContentUris.withAppendedId(Drugs.CONTENT_URI, drugsList.get(position).getId());
        getContentResolver().delete(deleteUri, null,null);
        // removing the note from the list
        drugsList.remove(position);
        mAdapter.notifyItemRemoved(position);
        toggleEmptyNotes();
    }

    public List<Drugs> getAllDrugs() {
        List<Drugs> drugs = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Drugs drug = new Drugs();
                drug.setId(cursor.getInt(cursor.getColumnIndex(Drugs.COLUMN_ID)));
                drug.setDrugName(cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_NAME)));
                drug.setDescription(cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_DESC)));
                drug.setInterval(cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_INTERVAL)));
                drug.setStartDate(cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_START_DATE)));
                drug.setEndDate(cursor.getString(cursor.getColumnIndex(Drugs.COLUMN_END_DATE)));
                drugs.add(drug);
            } while (cursor.moveToNext());
        }
        // close db connection
        //cursor.close();
        // return notes list
        return drugs;
    }
    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        //check the size of the cursor
        int count = drugsList.size();
        if (count > 0) {
            noMedsView.setVisibility(View.GONE);
        } else {
            noMedsView.setVisibility(View.VISIBLE);
        }
    }

    private void setReminder(Drugs drugs, int position){
        //check the number stored as interval
        List<EditText> editList = new ArrayList<EditText>();
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));


        int interval = Integer.parseInt(drugs.getInterval());
        String startDate = drugs.getStartDate();
        String endDate = drugs.getEndDate();
        Date sDate = new Date();
        Date eDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
        Calendar currDate = Calendar.getInstance();
        Date cDate = currDate.getTime();

        try {
            sDate = sdf.parse(startDate);
            eDate = sdf.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(sDate.getTime() <= cDate.getTime() & cDate.getTime() <= eDate.getTime()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Set Reminder");
            builder.setMessage("Set Reminder for Medications");

            for(int i = 1;i <= interval & i < 4; i++){
               final EditText timeView = new EditText(this);
                ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                timeView.setLayoutParams(lparams);
                //timeView.setTextAppearance(this, android.R.attr.textAppearanceLarge);
                timeView.setFocusable(false);

                timeView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                timeView.setText( selectedHour + ":" + selectedMinute);
                            }
                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                    }
                });

                editList.add(timeView);
            }
            for (int i=0; i < editList.size(); i++){
                layout.addView(editList.get(i));
            }
            builder.setView(layout);
            builder.setPositiveButton("SET", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            AlertDialog b = builder.create();
            b.show();

        }
        else{
            Toast.makeText(this, "Medication duration elapsed!.\n Edit date or delete", Toast.LENGTH_LONG).show();
        }
    }
}


