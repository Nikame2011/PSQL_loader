package com.for_work.test;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private final List<String[]> rows;
    private final OnRowClickListener onClickListener;

    TableAdapter(Context context, List<String[]> rows, OnRowClickListener onClickListener) {
        this.rows = rows;
        this.inflater = LayoutInflater.from(context);
        this.onClickListener=onClickListener;
    }
    @NonNull
    @Override
    public TableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TableAdapter.ViewHolder holder, int position) {

        String[] row = rows.get(holder.getAdapterPosition());
        if (row.length>0)
        holder.firstView.setText(row[0]);
        if (row.length>1){
            holder.secondView.setText(row[1]);
            holder.secondView.setVisibility(View.VISIBLE);
        }
        if (row.length>2){
            holder.thirdView.setText(row[2]);
            holder.thirdView.setVisibility(View.VISIBLE);
        }
        if (row.length>3){
            holder.fourthView.setText(row[3]);
            holder.fourthView.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(v -> {
            // вызываем метод слушателя, передавая ему данные
            onClickListener.onRowClick(row, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView firstView, secondView, thirdView, fourthView;
        ViewHolder(View view){
            super(view);
            firstView = view.findViewById(R.id.first);
            secondView = view.findViewById(R.id.second);
            thirdView = view.findViewById(R.id.third);
            fourthView = view.findViewById(R.id.fourth);
        }
    }

    interface OnRowClickListener{
        void onRowClick(String[] row, int position);
    }
}
