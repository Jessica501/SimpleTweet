package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Entities {
    public String mediaUrl;

    // empty constructor for Parceler
    public Entities() {
    }

    public static Entities fromJson(JSONObject jsonObject) throws JSONException {
        Entities entities = new Entities();

        if (!jsonObject.isNull("media")) {
            JSONArray media = jsonObject.getJSONArray("media");
            entities.mediaUrl = ((JSONObject)media.get(0)).getString("media_url_https");

        }
        return entities;
    }
}
