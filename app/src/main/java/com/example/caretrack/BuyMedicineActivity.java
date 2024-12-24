package com.example.caretrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

public class BuyMedicineActivity extends AppCompatActivity {

    // Package details with price at the end
    private String[][] packages = {
            {"Uprise-D3 1000IU Capsule", "", "", "", "50"},
            {"HealthVit Chromium Picolinate 200mcg Capsule", "", "", "", "305"},
            {"Vitamin B Complex Capsules", "", "", "", "448"},
            {"Inlife Vitamin E Wheat Germ Oil Capsule", "", "", "", "539"},
            {"Dolo 650 Tablet", "", "", "", "30"},
            {"Crocin 650 Advance Tablet", "", "", "", "50"},
            {"Strepsils Medicated Lozenges for Sore Throat", "", "", "", "40"},
            {"Tata 1mg Calcium + Vitamin D3", "", "", "", "30"},
            {"Feronia -XT Tablet", "", "", "", "130"}
    };

    // Detailed descriptions for each package
    private String[] package_details = {
            "Uprise-D3 1000IU Capsule: Supports bone health and immune function. Helps in calcium absorption and maintains overall skeletal health.",

            "HealthVit Chromium Picolinate 200mcg Capsule: Aids in glucose metabolism by enhancing insulin sensitivity. Supports balanced blood sugar levels and weight management.",

            "Vitamin B Complex Capsules: Combines essential B vitamins to support energy production, brain function, and red blood cell formation. Helps maintain overall vitality.",

            "Inlife Vitamin E Wheat Germ Oil Capsule: Provides antioxidant support to protect cells from damage. Promotes healthy skin and improves overall skin appearance.",

            "Dolo 650 Tablet: Effective for relieving mild to moderate pain and reducing fever. Commonly used for headaches, muscle aches, and fever management.",

            "Crocin 650 Advance Tablet: Used to alleviate fever and mild to moderate pain. Provides quick relief from headaches, muscle pain, and body aches.",

            "Strepsils Medicated Lozenges for Sore Throat: Soothes sore throat and reduces discomfort caused by throat infections. Contains antiseptic agents to help fight infection.",

            "Tata 1mg Calcium + Vitamin D3: Supports bone strength and density. Combines calcium and vitamin D3 to improve bone health and reduce the risk of osteoporosis.",

            "Feronia -XT Tablet: Helps to combat iron deficiency anemia. Provides a combination of iron and vitamins to support healthy blood and overall energy levels."
    };

    ListView lst;
     SimpleAdapter sa;
     HashMap<String, String> item;
     Button btnBack, btnGoTOCart;
    ArrayList list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_medicine); // Link to your XML layout file


        ListView lst = findViewById(R.id.listViewBM); // Use the correct ID for ListView
        Button btnBack = findViewById(R.id.buttonBMBack);
        Button btnGoToCart = findViewById(R.id.buttonBMGoToCart);

        // Set OnClickListener for "Go To Cart" button
        btnGoToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to CartBuyMedicineActivity
              startActivity(new Intent(BuyMedicineActivity.this, CartBuyMedicineActivity.class));
            }
        });

        // Set OnClickListener for "Back" button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate back to HomeActivity
                startActivity(new Intent(BuyMedicineActivity.this, HomeActivity.class));
            }
        });



            ArrayList<HashMap<String, String>> list = new ArrayList<>();

            for (int i = 0; i < packages.length; i++) {
                HashMap<String, String> item = new HashMap<>();
                item.put("line1", packages[i][0]);
                item.put("line2", packages[i][1]);
                item.put("line3", packages[i][2]);
                item.put("line4", packages[i][3]);
                item.put("line5", "Total Cost: " + packages[i][4] + "/-");

                list.add(item);
            }

            SimpleAdapter sa = new SimpleAdapter(
                    this,
                    list,
                    R.layout.multi_lines,
                    new String[]{"line1", "line2", "line3", "line4", "line5"},
                    new int[]{R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e}
            );
            lst.setAdapter(sa);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create an Intent to start BuyMedicineDetailsActivity
                Intent it = new Intent(BuyMedicineActivity.this, BuyMedicineDetailsActivity.class);

                // Put the selected item's details as extras in the Intent
                it.putExtra("text1", packages[position][0]); // Name of the package
                it.putExtra("text2", package_details[position]); // Details of the package
                it.putExtra("text3", packages[position][4]); // Cost of the package

                // Start the BuyMedicineDetailsActivity
                startActivity(it);
            }
        });
    }
}

