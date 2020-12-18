package com.rizki.projectskripsi.adapter;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rizki.projectskripsi.R;
import com.rizki.projectskripsi.api.Rs;

import java.util.ArrayList;
import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.ViewHolder> {

    private ArrayList<Rs> hospitalListData;
    private ListClickedListener listClickedListener;

    public HospitalAdapter(ArrayList<Rs> hospitalListData, ListClickedListener listClickedListener) {
        this.hospitalListData = hospitalListData;
        this.listClickedListener = listClickedListener;
    }

    public void setData(List<Rs> newData) {
        hospitalListData.clear();
        hospitalListData.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rs, parent, false);
        return new ViewHolder(view, listClickedListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(hospitalListData.get(position).getGambar()).into(holder.mHospitalImage);
        holder.mHospitalName.setText(hospitalListData.get(position).getNama_rs());
        holder.mHospitalAddress.setText(hospitalListData.get(position).getAlamat());
        holder.mHospitalRoom.setText(hospitalListData.get(position).getJml_kamar() + " Kamar tersedia");
        int distance = (int) hospitalListData.get(position).getDistance();
        holder.mHospitalDistance.setText(distance + " Km");

        holder.itemView.setOnClickListener(v -> holder.mListClickedListener.onListClicked(hospitalListData.get(position)));
    }

    @Override
    public int getItemCount() {
        return hospitalListData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mHospitalImage;
        TextView mHospitalName, mHospitalAddress, mHospitalRoom, mHospitalDistance;
        ListClickedListener mListClickedListener;

        public ViewHolder(@NonNull View itemView, ListClickedListener listClickedListener) {
            super(itemView);

            mHospitalImage = itemView.findViewById(R.id.iv_hospital);

            mHospitalName = itemView.findViewById(R.id.tv_hospital_name);
            mHospitalAddress = itemView.findViewById(R.id.tv_hospital_address);
            mHospitalRoom = itemView.findViewById(R.id.tv_hospital_room);
            mHospitalDistance = itemView.findViewById(R.id.tv_hospital_distance);

            mListClickedListener = listClickedListener;
        }
    }

    public interface ListClickedListener {
        void onListClicked(Rs rs);
    }
}
