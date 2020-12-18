package com.rizki.projectskripsi.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rizki.projectskripsi.R;
import com.rizki.projectskripsi.api.ProvData;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.internal.ListenerClass;

public class ProvAdapter extends RecyclerView.Adapter<ProvAdapter.ViewHolder> {

    private ArrayList<ProvData.ProvListData> provListData = new ArrayList<>();
    private ListClickedListener listClickedListener;

    public ProvAdapter(ArrayList<ProvData.ProvListData> provListData, ListClickedListener listClickedListener) {
        this.provListData = provListData;
        this.listClickedListener = listClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_layout, parent, false);
        return new ViewHolder(view, listClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mProvName.setText(provListData.get(position).getProvName());
        holder.mProvCase.setText(numberSeparator(provListData.get(position).getCaseAmount()));
        holder.mProvDeath.setText(numberSeparator(provListData.get(position).getDeathAmount()));
        holder.mProvHealed.setText(numberSeparator(provListData.get(position).getHealedAmount()));
        holder.mProvTreated.setText(numberSeparator(provListData.get(position).getTreatedAmount()));

        holder.mProvListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mListClickedListener.onListClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return provListData.size();
    }

    public void filterList(ArrayList<ProvData.ProvListData> filteredList) {
        provListData = filteredList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mProvListLayout;
        TextView mProvName, mProvCase, mProvDeath, mProvHealed, mProvTreated;
        ListClickedListener mListClickedListener;

        public ViewHolder(@NonNull View itemView, ListClickedListener listClickedListener) {
            super(itemView);

            mProvListLayout = itemView.findViewById(R.id.prov_list_layout);

            mProvName = itemView.findViewById(R.id.prov_name);
            mProvCase = itemView.findViewById(R.id.prov_case);
            mProvDeath = itemView.findViewById(R.id.prov_death);
            mProvHealed = itemView.findViewById(R.id.prov_cured);
            mProvTreated = itemView.findViewById(R.id.prov_treated);

            mListClickedListener = listClickedListener;
        }
    }

    public interface ListClickedListener {
        void onListClicked(int position);
    }

    private String numberSeparator(int value) {
        return String.valueOf(NumberFormat.getNumberInstance(Locale.ITALY).format(value));
    }
}
