package com.rizki.projectskripsi.api;

import android.os.Parcel;
import android.os.Parcelable;

public class Rs implements Parcelable {

    private String id;
    private String nama_rs;
    private String deskripsi;
    private String telepon;
    private String alamat;
    private String website;
    private String gambar;
    private String jml_kamar;
    private String latitude;
    private String longitude;
    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama_rs() {
        return nama_rs;
    }

    public void setNama_rs(String nama_rs) {
        this.nama_rs = nama_rs;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getJml_kamar() {
        return jml_kamar;
    }

    public void setJml_kamar(String jml_kamar) {
        this.jml_kamar = jml_kamar;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    protected Rs(Parcel in) {
        id = in.readString();
        nama_rs = in.readString();
        deskripsi = in.readString();
        telepon = in.readString();
        alamat = in.readString();
        website = in.readString();
        gambar = in.readString();
        jml_kamar = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    public static final Creator<Rs> CREATOR = new Creator<Rs>() {
        @Override
        public Rs createFromParcel(Parcel in) {
            return new Rs(in);
        }

        @Override
        public Rs[] newArray(int size) {
            return new Rs[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nama_rs);
        dest.writeString(deskripsi);
        dest.writeString(telepon);
        dest.writeString(alamat);
        dest.writeString(website);
        dest.writeString(gambar);
        dest.writeString(jml_kamar);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }
}
