package com.macro.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MacroEditorActivity extends AppCompatActivity {

    private List<MacroAction> actionsList = new ArrayList<>();
    private ActionAdapter adapter;
    private String currentMacroName = "Default_Macro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macro_editor);

        ListView listView = findViewById(R.id.listViewActions);
        Button btnAdd = findViewById(R.id.btnAddAction);
        Button btnSave = findViewById(R.id.btnSaveMacro);

        // تحميل السكربت المحفوظ سابقاً
        List<MacroAction> loaded = MacroStorage.loadMacro(this, currentMacroName);
        if (!loaded.isEmpty()) {
            actionsList.addAll(loaded);
        }

        adapter = new ActionAdapter();
        listView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> showAddActionDialog());

        // تعديل الخطوة عند النقر عليها
        listView.setOnItemClickListener((parent, view, position, id) -> {
            showEditActionDialog(actionsList.get(position), position);
        });

        // حفظ السكربت كاملاً
        btnSave.setOnClickListener(v -> {
            MacroStorage.saveMacro(this, currentMacroName, actionsList);
            Toast.makeText(this, "تم حفظ الماكرو بنجاح!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showAddActionDialog() {
        String[] options = {
            "🎯 Click (x, y)", 
            "🖼 Click Image (مطابقة صورة)", 
            "↔️ Swipe (سحب)", 
            "⏱️ Wait (انتظار)", 
            "◀️ Press Back", 
            "🏠 Press Home"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر نوع الإجراء (Action)");
        builder.setItems(options, (dialog, which) -> {
            MacroAction action;
            switch (which) {
                case 0:
                    action = new MacroAction(MacroAction.Type.CLICK_XY, "Click Point");
                    break;
                case 1:
                    action = new MacroAction(MacroAction.Type.CLICK_IMAGE, "Click Image Match");
                    break;
                case 2:
                    action = new MacroAction(MacroAction.Type.SWIPE, "Swipe Screen");
                    break;
                case 3:
                    action = new MacroAction(MacroAction.Type.WAIT, "Wait Delay");
                    break;
                case 4:
                    action = new MacroAction(MacroAction.Type.PRESS_BACK, "Press Back");
                    break;
                default:
                    action = new MacroAction(MacroAction.Type.PRESS_HOME, "Press Home");
                    break;
            }
            actionsList.add(action);
            adapter.notifyDataSetChanged();
            showEditActionDialog(action, actionsList.size() - 1);
        });
        builder.show();
    }

    // نافذة تعديل التفاصيل الدقيقة (السرعة، التأخير، نسبة المطابقة)
    private void showEditActionDialog(MacroAction action, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تعديل تفاصيل الخطوة #" + (position + 1));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etX = new EditText(this);
        etX.setHint("X Coordinate (مثلاً 500)");
        etX.setText(String.valueOf(action.getX()));
        layout.addView(etX);

        final EditText etY = new EditText(this);
        etY.setHint("Y Coordinate (مثلاً 1000)");
        etY.setText(String.valueOf(action.getY()));
        layout.addView(etY);

        final EditText etDelayBefore = new EditText(this);
        etDelayBefore.setHint("الانتظار قبل النقر (ms)");
        etDelayBefore.setText(String.valueOf(action.getDelayBefore()));
        layout.addView(etDelayBefore);

        final EditText etDelayAfter = new EditText(this);
        etDelayAfter.setHint("الانتظار بعد النقر (ms)");
        etDelayAfter.setText(String.valueOf(action.getDelayAfter()));
        layout.addView(etDelayAfter);

        final EditText etThreshold = new EditText(this);
        etThreshold.setHint("نسبة مطابقة الصورة (0.50 إلى 0.99)");
        etThreshold.setText(String.valueOf(action.getMatchThreshold()));
        if (action.getType() == MacroAction.Type.CLICK_IMAGE) {
            layout.addView(etThreshold);
        }

        builder.setView(layout);

        builder.setPositiveButton("حفظ التعديل", (dialog, which) -> {
            try {
                action.setX(Integer.parseInt(etX.getText().toString()));
                action.setY(Integer.parseInt(etY.getText().toString()));
                action.setDelayBefore(Long.parseLong(etDelayBefore.getText().toString()));
                action.setDelayAfter(Long.parseLong(etDelayAfter.getText().toString()));
                if (action.getType() == MacroAction.Type.CLICK_IMAGE) {
                    action.setMatchThreshold(Double.parseDouble(etThreshold.getText().toString()));
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                Toast.makeText(this, "يرجى إدخال قيم صحيحة", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("حذف الخطوة", (dialog, which) -> {
            actionsList.remove(position);
            adapter.notifyDataSetChanged();
        });

        builder.show();
    }

    // Adapter لملء القائمة ببطاقات أنيقة
    private class ActionAdapter extends BaseAdapter {
        @Override
        public int getCount() { return actionsList.size(); }
        @Override
        public Object getItem(int position) { return actionsList.get(position); }
        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MacroEditorActivity.this)
                        .inflate(R.layout.item_macro_action, parent, false);
            }

            MacroAction action = actionsList.get(position);

            TextView tvTitle = convertView.findViewById(R.id.tvActionTitle);
            TextView tvDetails = convertView.findViewById(R.id.tvActionDetails);

            tvTitle.setText((position + 1) + ". " + action.getName() + " [" + action.getType() + "]");
            
            String details = "X: " + action.getX() + " | Y: " + action.getY() +
                    "\nDelay Before: " + action.getDelayBefore() + "ms | Delay After: " + action.getDelayAfter() + "ms";

            if (action.getType() == MacroAction.Type.CLICK_IMAGE) {
                details += "\nMatch Threshold: " + (int)(action.getMatchThreshold() * 100) + "%";
            }

            tvDetails.setText(details);

            return convertView;
        }
    }
}
