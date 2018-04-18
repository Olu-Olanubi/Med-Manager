package database;

import android.net.Uri;

public class Drugs {
    public static final String TABLE_NAME = "DRUGS";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "DrugName";
    public static final String COLUMN_DESC = "Description";
    public static final String COLUMN_INTERVAL = "Interval";
    public static final String COLUMN_START_DATE = "StartDate";
    public static final String COLUMN_END_DATE = "EndDate";


    //define URI constants
    public static final String AUTHORITY = "com.example.user.med_manager";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TABLE_NAME = "DRUGS";

    //public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TABLE_NAME).build();
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TABLE_NAME);
    private int id;
    //private int interval;
    private String drugName, description, interval,startDate, endDate;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_DESC + " TEXT,"
                    + COLUMN_INTERVAL + " TEXT,"
                    + COLUMN_START_DATE + " DATE,"
                    + COLUMN_END_DATE + " DATE"
                    + ")";


    //Constructors to initialize variables
    public Drugs(){

    }
    public Drugs(int id, String drugName, String description, String interval, String startDate, String endDate){
        this.id = id;
        this.drugName = drugName;
        this.description = description;
        this.interval = interval;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //getter and setter methods

    public int getId(){
        return id;
    }
    public String getDrugName(){
        return drugName;
    }
    public String getDescription(){
        return description;
    }
    public String getInterval(){
        return interval;
    }

    public String getStartDate(){
        return startDate;
    }
    public String getEndDate(){
        return endDate;
    }


    public void setId(int id){
        this.id = id;
    }
    public void setDrugName(String drugName){
        this.drugName = drugName;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public void setInterval(String interval){
        this.interval = interval;
    }

    public void setStartDate(String startDate){
        this.startDate = startDate;
    }
    public void setEndDate(String endDate){
        this.endDate = endDate;
    }


}
