package com.macro.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MacroStorage {

    private static final String PREF_NAME = "MacrorifyBotPrefs";
    private static final String KEY_MACROS = "saved_macros_list";

    public static void saveMacro(Context context, String macroName, List<MacroAction> actions) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        try {
            JSONArray jsonArray = new JSONArray();
            for (MacroAction action : actions) {
                jsonArray.put(action.toJSONObject());
            }

            JSONObject allMacros = new JSONObject(prefs.getString(KEY_MACROS, "{}"));
            allMacros.put(macroName, jsonArray);

            prefs.edit().putString(KEY_MACROS, allMacros.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<MacroAction> loadMacro(Context context, String macroName) {
        List<MacroAction> actions = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        try {
            JSONObject allMacros = new JSONObject(prefs.getString(KEY_MACROS, "{}"));
            if (allMacros.has(macroName)) {
                JSONArray jsonArray = allMacros.getJSONArray(macroName);
                for (int i = 0; i < jsonArray.length(); i++) {
                    actions.add(MacroAction.fromJSONObject(jsonArray.getJSONObject(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return actions;
    }
}
