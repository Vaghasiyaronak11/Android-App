package com.example.caretrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorDetailsActivity extends AppCompatActivity {
    private String[][] doctor_details1 = {
            {"Doctor Name: Rohan Mehta", "Hospital Address: Wakad", "Exp: 10yrs", "Mobile No: 9876543210", "700"},
            {"Doctor Name: Sanjay Sharma", "Hospital Address: Hinjewadi", "Exp: 12yrs", "Mobile No: 9988776655", "1200"},
            {"Doctor Name: Neha Gupta", "Hospital Address: Baner", "Exp: 8yrs", "Mobile No: 9876123456", "950"},
            {"Doctor Name: Kiran Rao", "Hospital Address: Aundh", "Exp: 9yrs", "Mobile No: 9123456780", "650"},
            {"Doctor Name: Anil Patil", "Hospital Address: Kothrud", "Exp: 14yrs", "Mobile No: 9871234567", "850"}
    };

    private String[][] doctor_details2 = {
            {"Doctor Name: Priya Desai", "Hospital Address: Shivajinagar", "Exp: 7yrs", "Mobile No: 9966554433", "500"},
            {"Doctor Name: Amit Joshi", "Hospital Address: Viman Nagar", "Exp: 16yrs", "Mobile No: 9875432101", "1100"},
            {"Doctor Name: Snehal Kulkarni", "Hospital Address: Hadapsar", "Exp: 11yrs", "Mobile No: 9988771122", "600"},
            {"Doctor Name: Rajesh Nair", "Hospital Address: Kharadi", "Exp: 5yrs", "Mobile No: 9876543212", "400"},
            {"Doctor Name: Madhavi Dixit", "Hospital Address: Magarpatta", "Exp: 13yrs", "Mobile No: 9911223344", "950"}
    };

    private String[][] doctor_details3 = {
            {"Doctor Name: Ajay Verma", "Hospital Address: Swargate", "Exp: 9yrs", "Mobile No: 9123456789", "850"},
            {"Doctor Name: Shruti Sharma", "Hospital Address: Bibwewadi", "Exp: 8yrs", "Mobile No: 9876501234", "700"},
            {"Doctor Name: Manish Gupta", "Hospital Address: Camp", "Exp: 12yrs", "Mobile No: 9876543200", "1050"},
            {"Doctor Name: Ramesh Khanna", "Hospital Address: Katraj", "Exp: 6yrs", "Mobile No: 9876541234", "600"},
            {"Doctor Name: Sunita Patil", "Hospital Address: Pashan", "Exp: 14yrs", "Mobile No: 9876567890", "1200"}
    };

    private String[][] doctor_details4 = {
            {"Doctor Name: Pooja Chawla", "Hospital Address: Deccan", "Exp: 10yrs", "Mobile No: 9887766554", "900"},
            {"Doctor Name: Varun Khurana", "Hospital Address: FC Road", "Exp: 15yrs", "Mobile No: 9876512345", "1100"},
            {"Doctor Name: Ankit Jain", "Hospital Address: Kalyani Nagar", "Exp: 5yrs", "Mobile No: 9876451234", "750"},
            {"Doctor Name: Meera Iyer", "Hospital Address: Lohegaon", "Exp: 7yrs", "Mobile No: 9871236540", "500"},
            {"Doctor Name: Sameer Deshmukh", "Hospital Address: Koregaon Park", "Exp: 12yrs", "Mobile No: 9887612345", "1300"}
    };

    private String[][] doctor_details5 = {
            {"Doctor Name: Kavita Rao", "Hospital Address: Bhosari", "Exp: 9yrs", "Mobile No: 9898989890", "950"},
            {"Doctor Name: Suresh Pandey", "Hospital Address: Moshi", "Exp: 11yrs", "Mobile No: 9876512311", "1050"},
            {"Doctor Name: Akash Malhotra", "Hospital Address: Alandi", "Exp: 6yrs", "Mobile No: 9876543123", "700"},
            {"Doctor Name: Nidhi Sinha", "Hospital Address: Chakan", "Exp: 13yrs", "Mobile No: 9876123489", "1250"},
            {"Doctor Name: Raghavendra Kulkarni", "Hospital Address: Talegaon", "Exp: 10yrs", "Mobile No: 9877654321", "850"}
    };



    TextView tv;
    Button btn;
    String[][] doctor_details = {};
    HashMap<String,String> item;
    ArrayList list;
    SimpleAdapter sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

        tv = findViewById(R.id.textViewDDTitle);
        btn = findViewById(R.id.buttonDDBack);
        Intent it = getIntent();
        String title = it.getStringExtra("title");
        tv.setText(title);

        if(title.compareTo("Family Physicians")==0)
            doctor_details = doctor_details1;
            else
                if(title.compareTo("Dietician")==0)
                    doctor_details = doctor_details2;
                else
                    if(title.compareTo("Dentist")==0)
                        doctor_details = doctor_details3;
                    else
                        if(title.compareTo("Surgeon")==0)
                            doctor_details = doctor_details4;
                        else
                            doctor_details = doctor_details5;

        btn.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick (View view){
            startActivity(new Intent(DoctorDetailsActivity.this, FindDoctorActivity.class));
        }
    });

        list = new ArrayList();
        for(int i=0;i<doctor_details.length;i++){
            item = new HashMap<String,String>();
            item.put("line1", doctor_details[i][0]);
            item.put("line2", doctor_details[i][1]);
            item.put("line3", doctor_details[i][2]);
            item.put("line4", doctor_details[i][3]);
            item.put("line5", "Cons Fees: "+doctor_details[i][4]+"/-");
            list.add(item);
        }
        sa = new SimpleAdapter (this,list,
                R.layout.multi_lines,
                new String[]{"line1","line2","line3","line4","line5"},
                new int[]{R.id.line_a,R.id.line_b,R.id.line_c,R.id.line_d,R.id.line_e}
                     );

        ListView lst=findViewById(R.id.listViewBM);
        lst.setAdapter(sa);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent it = new Intent(DoctorDetailsActivity.this, BookAppointmentActivity.class);
                it.putExtra("text1", title);
                it.putExtra("text2", doctor_details[i][0]);
                it.putExtra("text3", doctor_details[i][1]);
                it.putExtra("text4", doctor_details[i][3]);
                it.putExtra("text5", doctor_details[i][4]);
                startActivity(it);
            }
        });

}
}