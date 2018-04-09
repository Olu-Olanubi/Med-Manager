package com.example.user.med_manager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import database.Drugs;

public class DrugAdapter extends RecyclerView.Adapter<DrugAdapter.MyViewHolder> {

    private Context context;
    private List<Drugs> drugsList;

    public DrugAdapter(Context context, List<Drugs> drugsList) {
        this.context = context;
        this.drugsList = drugsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description;/** interval; startDate, endDate;*/

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            /**
            interval = itemView.findViewById(R.id.interval);

             startDate = itemView.findViewById(R.id.start_date);
             endDate = itemView.findViewById(R.id.end_date);

             */
        }
    }

    @Override
    public DrugAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drugs_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DrugAdapter.MyViewHolder holder, int position) {
        Drugs drug = drugsList.get(position);

        holder.name.setText(drug.getDrugName());
        holder.description.setText(drug.getDescription());
        //holder.interval.setText(String.valueOf(drug.getInterval()));
       // holder.startDate.setText(formatDate(drug.getStartDate()));
       // holder.endDate.setText(formatDate(drug.getEndDate()));
    }

    @Override
    public int getItemCount() {
        return drugsList.size();
    }


    /**
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
     */
}
