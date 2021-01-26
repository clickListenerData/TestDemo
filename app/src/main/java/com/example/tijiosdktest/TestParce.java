package com.example.tijiosdktest;

import android.os.Parcel;
import android.os.Parcelable;

public class TestParce implements Parcelable {

    private boolean flag;
    private int code;
    private String name;

    private Test test;

    protected TestParce(Parcel in) {
        flag = in.readByte() != 0;
        code = in.readInt();
        name = in.readString();
//        test = in.readParcelable(Test.class.getClassLoader());
        in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (flag ? 1 : 0));
        dest.writeInt(code);
        dest.writeString(name);
//        dest.writeParcelable(test, flags);
        dest.writeSerializable("");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TestParce> CREATOR = new Creator<TestParce>() {
        @Override
        public TestParce createFromParcel(Parcel in) {
            return new TestParce(in);
        }

        @Override
        public TestParce[] newArray(int size) {
            return new TestParce[size];
        }
    };
}
