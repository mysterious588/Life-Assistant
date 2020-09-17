package com.lifeassistance.Database;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BooleanArrayListConverter {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static ArrayList<Boolean> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Boolean>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Boolean> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
