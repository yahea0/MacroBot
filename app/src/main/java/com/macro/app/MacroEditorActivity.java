package com.macro.app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MacroEditorActivity extends AppCompatActivity {

    private List<String> actionDisplayList = new ArrayList<>();
    private List<MacroAction> actionsList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macro_editor);

        ListView listView = findViewById(R.id.listViewActions);
        Button btnAdd = findViewById(R.id.btnAddAction);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, actionDisplayList);
        listView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> showAddActionDialog());
    }

    private void showAddActionDialog() {
        String[] options = {"Click (x, y)", "Click Image", "Swipe (x, y)", "Wait", "Press Back", "Press Home", "Toast Message"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إضافة إجراء (Action)");
        builder.setItems(options, (dialog, which) -> {
            MacroAction action;
            switch (which) {
                case 0:
                    action = new MacroAction(MacroAction.Type.CLICK_XY);
                    action.setX(500); action.setY(1000);
                    actionDisplayList.add("1. Click [500, 1000]");
                    break;
                case 1:
                    action = new MacroAction(MacroAction.Type.CLICK_IMAGE);
                    actionDisplayList.add("2. Click Image [Match]");
                    break;
                case 2:
                    action = new MacroAction(MacroAction.Type.SWIPE);
                    action.setX(500); action.setY(1500); action.setEndX(500); action.setEndY(500);
                    actionDisplayList.add("3. Swipe [500,1500 -> 500,500]");
                    break;
                case 3:
                    action = new MacroAction(MacroAction.Type.WAIT);
                    action.setDuration(1000);
                    actionDisplayList.add("4. Wait [1000ms]");
                    break;
                case 4:
                    action = new MacroAction(MacroAction.Type.PRESS_BACK);
                    actionDisplayList.add("5. Press Back");
                    break;
                case 5:
                    action = new MacroAction(MacroAction.Type.PRESS_HOME);
                    actionDisplayList.add("6. Press Home");
                    break;
                default:
                    action = new MacroAction(MacroAction.Type.TOAST);
                    action.setText("Hello Macro");
                    actionDisplayList.add("7. Toast [Hello Macro]");
                    break;
            }
            actionsList.add(action);
            adapter.notifyDataSetChanged();
        });
        builder.show();
    }
}
