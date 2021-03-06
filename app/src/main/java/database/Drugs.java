package database;

public class Drugs {
    public static final String TABLE_NAME = "DRUGS";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "DrugName";
    public static final String COLUMN_DESC = "Description";
    //public static final String COLUMN_INTERVAL = "Interval";
   // public static final String COLUMN_START_DATE = "StartDate";
   // public static final String COLUMN_END_DATE = "EndDate";

    private int id;
    private int interval;
    private String drugName, description;
    //startDate, endDate;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_DESC + " TEXT"
                    //+ COLUMN_INTERVAL + " INTEGER"
                    //+ COLUMN_START_DATE + " DATE"
                    //+ COLUMN_END_DATE + " DATE"
                    + ")";

    //Constructors to initialize variables
    public Drugs(){

    }
    public Drugs(int id, String drugName, String description /**, int interval, String startDate, String endDate*/){
        this.id = id;
        this.drugName = drugName;
        this.description = description;
        //this.interval = interval;
        //this.startDate = startDate;
        //this.endDate = endDate;
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
    /**
    public int getInterval(){
        return interval;
    }

    public String getStartDate(){
        return startDate;
    }
    public String getEndDate(){
        return endDate;
    }
     */

    public void setId(int id){
        this.id = id;
    }
    public void setDrugName(String drugName){
        this.drugName = drugName;
    }
    public void setDescription(String description){
        this.description = description;
    }
    /**
    public void setInterval(int interval){
        this.interval = interval;
    }

    public void setStartDate(String startDate){
        this.startDate = startDate;
    }
    public void setEndDate(String endDate){
        this.endDate = endDate;
    }
     */

}
