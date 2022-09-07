package cn.itcast.chatserver.test;

import cn.itcast.chatserver.protocol.Serializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestGson {
    public static void main(String[] args) {
        final Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
        System.out.println(gson.toJson(String.class));
    }
}
