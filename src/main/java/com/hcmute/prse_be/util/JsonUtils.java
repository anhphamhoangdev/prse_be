package com.hcmute.prse_be.util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.lang.reflect.Type;

public class JsonUtils
{
  public static final Gson json = new Gson();
  
  public static String Serialize(Object value)
  {
    return json.toJson(value);
  }
  
  public static <T> T DeSerialize(String value, Type typeOfT)
  {
    T result = json.fromJson(value, typeOfT);
    
    return result;
  }

  public static <T> T DeSerializeReader(JsonReader value, Type typeOfT)
  {
    T result = json.fromJson(value, typeOfT);

    return result;
  }

  public static JsonReader CreateJsonReader(String value)
  {
    JsonReader reader = new JsonReader(new StringReader(value));
    reader.setLenient(true);
    return reader;
//        return new JsonReader(new java.io.StringReader(value));
  }
}
