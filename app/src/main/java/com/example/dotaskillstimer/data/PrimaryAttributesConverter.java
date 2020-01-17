package com.example.dotaskillstimer.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.room.TypeConverter;

public class PrimaryAttributesConverter {
    @TypeConverter
    public String fromPrimaryAttributes(PrimaryAttributes attribute){
        return new Gson().toJson(attribute);
    }
    @TypeConverter
    public PrimaryAttributes toPrimaryAttributes (String attribute){
        Type enumType = new TypeToken<PrimaryAttributes>() {}.getType();
        return new Gson().fromJson(attribute,enumType);
    }
}
