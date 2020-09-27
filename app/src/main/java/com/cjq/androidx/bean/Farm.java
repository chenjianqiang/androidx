package com.cjq.androidx.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.android.gms.maps.model.LatLng;

public class Farm implements Parcelable {
    public String flag;
    public String farmerType;
    public Integer id;
    public String name;
    public Double lng;
    public Double lat;
    public String address;
    public String province;
    public String city;
    public String district;
    public Object created;
    public String creator;
    public Integer creatorId;
    public String isDefault = "0";
    public String regionCode;
    public int count;
    public double area;

    public Farm() {
    }

    protected Farm(Parcel in) {
        flag = in.readString();
        farmerType = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        if (in.readByte() == 0) {
            lng = null;
        } else {
            lng = in.readDouble();
        }
        if (in.readByte() == 0) {
            lat = null;
        } else {
            lat = in.readDouble();
        }
        address = in.readString();
        province = in.readString();
        city = in.readString();
        district = in.readString();
        creator = in.readString();
        if (in.readByte() == 0) {
            creatorId = null;
        } else {
            creatorId = in.readInt();
        }
        isDefault = in.readString();
        regionCode = in.readString();
        count = in.readInt();
        area = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(flag);
        dest.writeString(farmerType);
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        if (lng == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lng);
        }
        if (lat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lat);
        }
        dest.writeString(address);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(district);
        dest.writeString(creator);
        if (creatorId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(creatorId);
        }
        dest.writeString(isDefault);
        dest.writeString(regionCode);
        dest.writeInt(count);
        dest.writeDouble(area);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Farm> CREATOR = new Creator<Farm>() {
        @Override
        public Farm createFromParcel(Parcel in) {
            return new Farm(in);
        }

        @Override
        public Farm[] newArray(int size) {
            return new Farm[size];
        }
    };

    @JSONField(serialize = false, deserialize = false)
    public LatLng getLatLng() {
        if (lat == null || lng == null) {
            return null;
        }
        return new LatLng(lat, lng);
    }

    public boolean isDefaultFarm() {
        return "1".equals(isDefault);
    }
}
