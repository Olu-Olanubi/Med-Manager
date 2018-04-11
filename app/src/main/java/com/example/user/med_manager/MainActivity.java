package com.example.user.med_manager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseHelper;
import database.Drugs;
import utils.MyDividerItemDecoration;
import utils.RecyclerTouchListener;

public class MainActivity extends AppCompatActivity {

    private DrugAdapter mAdapter;
    private List<Drugs> drugsList = new ArrayList<>();
    //private RelativeLayout relativeLayout;
    private RecyclerView recyclerView;
    private TextView noMedsView;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //relativeLayout =  findViewById(R.id.relative_layout);
        recyclerView =  findViewById(R.id.recycler_view);
        noMedsView =  findViewById(R.id.empty_med_view);

        db = new DatabaseHelper(this);

        drugsList.addAll(db.getAllDrugs());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDrugDialog(false, null, -1);
            }
        });


        mAdapter = new DrugAdapter(this, drugsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotes();

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

    /**
     * Inserting new medication in db
     * and refreshing the list
     */
    private void createDrug(String drugName, String description, String interval, String startDate, String endDate) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertDrug(drugName, description, interval, startDate, endDate);

        // get the newly inserted note from db
        Drugs d = db.getDrugs(id);

        if (d != null) {
            // adding new note to array list at 0 position
            drugsList.add(0, d);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }


    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateDrug(String drugName, String description, String interval, String startDate, String endDate, int position) {
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
    }

    /**
     * Deleting a medication from SQLite and removing the
     * item from the list by its position
     */
    private void deleteDrug(int position) {
        // deleting the note from db
        db.deleteDrugs(drugsList.get(position));

        // removing the note from the list
        drugsList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showDrugDialog(true, drugsList.get(position), position);
                } else {
                    deleteDrug(position);
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

        final EditText inputDrugName = view.findViewById(R.id.drug_name);
        final EditText inputDescription = view.findViewById(R.id.drug_description);
        final EditText inputInterval = view.findViewById(R.id.drug_interval);
        final EditText inputStartDate = view.findViewById(R.id.drug_start_date);
        final EditText inputEndDate = view.findViewById(R.id.drug_end_date);
        //TextView dialogTitle = view.findViewById(R.id.dialog_title);
        //dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

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
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (db.getDrugsCount() > 0) {
            noMedsView.setVisibility(View.GONE);
        } else {
            noMedsView.setVisibility(View.VISIBLE);
        }
    }

}
