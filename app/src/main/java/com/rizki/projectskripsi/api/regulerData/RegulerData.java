package com.rizki.projectskripsi.api.regulerData;

import com.google.gson.annotations.SerializedName;

public class RegulerData {

    @SerializedName("update")
    private UpdatedData updatedData;

    public UpdatedData getUpdatedData() {
        return updatedData;
    }

    public static class UpdatedData {

        @SerializedName("penambahan")
        private NewCases newCases;

        @SerializedName("total")
        private TotalCases totalCases;

        public TotalCases getTotalCases() {
            return totalCases;
        }

        public NewCases getNewCases() {
            return newCases;
        }

        public static class TotalCases {
            @SerializedName("jumlah_positif")
            private int mPositif;

            @SerializedName("jumlah_meninggal")
            private int mMeninggal;

            @SerializedName("jumlah_sembuh")
            private int mSembuh;

            public int getmPositif() {
                return mPositif;
            }

            public int getmMeninggal() {
                return mMeninggal;
            }

            public int getmSembuh() {
                return mSembuh;
            }
        }

        public static class NewCases {

            @SerializedName("created")
            private String mWaktuUpdate;

            public String getmWaktuUpdate() {
                return mWaktuUpdate;
            }
        }

        public static class TheValue {
            @SerializedName("value")
            private int mValue;

            public int getmValue() {
                return mValue;
            }
        }
    }
}
