package com.example.caretrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class LabTestActivity extends AppCompatActivity {

    private String[][] packages = {
            {"Package 1: Full Body Checkup", "", "  ", " ", "2999"},
            {"Package 2: Blood Glucose Fasting", "   ", "   ", "   ", "199"},
            {"Package 3: COVID-19 Antibody-IgG", " ", " ", "  ", "499"},
            {"Package 4: Thyroid Check", "  ", "  ", "  ", "399"},
            {"Package 5: Immunity Check", "   ", "    ", "  ", "999"}
    };

    private String[] package_details = {
            "Complete Blood Count (CBC)\n" +
                    "Blood Glucose Fasting\n" +
                    "Liver Function Test (LFT)\n" +
                    "Kidney Function Test (KFT)\n" +
                    "Lipid Profile\n" +
                    "Thyroid Profile (T3, T4, TSH)\n" +
                    "Vitamin D Total\n" +
                    "Calcium\n" +
                    "Urine Routine Examination",

            "Blood Glucose Fasting: Helps in monitoring blood sugar levels for diabetes diagnosis and management.",

            "COVID-19 Antibody IgG: Detects antibodies to determine immunity after exposure to COVID-19 or vaccination.",

            "Thyroid Profile (T3, T4, TSH): Measures thyroid hormones to evaluate thyroid gland function and detect thyroid disorders.",

            "Complete Blood Count (CBC)\n" +
                    "CRP (C-Reactive Protein): Marker for inflammation\n" +
                    "Vitamin D Total\n" +
                    "Liver Function Test (LFT)\n" +
                    "Lipid Profile\n" +
                    "Kidney Function Test (KFT)"
    };


    private ArrayList<HashMap<String, String>> list;
    private SimpleAdapter sa;
    private Button btnGoToCart,btnBack;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_test);

        btnGoToCart = findViewById(R.id.buttonBMDAddToCart);
        btnBack = findViewById(R.id.buttonBMDBack);
        listView = findViewById(R.id.editTextBMDMultiLine);

        list = new ArrayList<>();
        for (int i = 0; i < packages.length; i++) {
            HashMap<String, String> item = new HashMap<>();
            item.put("line1", packages[i][0]);
            item.put("line2", packages[i][1]);
            item.put("line3", packages[i][2]);
            item.put("line4", packages[i][3]);
            item.put("line5", "Total Cost: " + packages[i][4] + "/-");
            list.add(item);
        }

        sa = new SimpleAdapter(this, list,
                R.layout.multi_lines,
                new String[]{"line1", "line2", "line3", "line4", "line5"},
                new int[]{R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e});
        listView.setAdapter(sa);

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            Intent it = new Intent(LabTestActivity.this, LabTestDetailsActivity.class);
            it.putExtra("text1", packages[position][0]); // Package name
            it.putExtra("text2", package_details[position]); // Package details
            it.putExtra("text3", packages[position][4]); // Price
            startActivity(it);
        });

        btnGoToCart.setOnClickListener(view -> {
            Intent intent = new Intent(LabTestActivity.this, CartLabActivity.class);
            ArrayList<String> selectedItems = getSelectedItems(); // Implement this method to get selected items
            intent.putStringArrayListExtra("cart_items", selectedItems);
            startActivity(intent);
        });


        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(LabTestActivity.this, HomeActivity.class));
          ;
        });
    }

    private ArrayList<String> getSelectedItems() {
        // Implement this method to return selected items in format "name$price"
        ArrayList<String> selectedItems = new ArrayList<>();
        // Example data; replace with actual selected items
        selectedItems.add("Package 1: Full Body Checkup$999");
        return selectedItems;
    }
}
