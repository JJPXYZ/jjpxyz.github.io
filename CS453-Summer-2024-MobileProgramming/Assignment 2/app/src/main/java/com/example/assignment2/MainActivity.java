package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list_view);

        ListView listView = findViewById(R.id.custom_list_view);

        List<Animal> animals = new ArrayList<>();
        animals.add(new Animal("Lion", "lion.jpg", "The lion is a species in the family Felidae and a member of the genus Panthera."));
        animals.add(new Animal("Tiger", "tiger.jpg", "The tiger is the largest living cat species and a member of the genus Panthera."));
        animals.add(new Animal("Elephant", "elephant.jpg", "Elephants are the largest existing land animals. Three species are currently recognized."));
        animals.add(new Animal("Giraffe", "giraffe.jpg", "The giraffe is an African artiodactyl mammal, the tallest living terrestrial animal."));
        animals.add(new Animal("Zebra", "zebra.jpg", "Zebras are African equines with distinctive black-and-white striped coats."));

        CustomAdapter adapter = new CustomAdapter(this, R.layout.custom_row, animals);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Animal selectedAnimal = animals.get(position);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Scary Animal Warning")
                        .setMessage("This animal is scary. Would you like to continue?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue to the detail activity
                                Intent intent = new Intent(MainActivity.this, AnimalDetails.class);
                                intent.putExtra("name", selectedAnimal.getName());
                                intent.putExtra("image", selectedAnimal.getImage());
                                intent.putExtra("description", selectedAnimal.getDescription());
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_uninstall:
                showUninstallConfirmation();
                return true;
            case R.id.action_call_zoo:
                callZoo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showUninstallConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Uninstall")
                .setMessage("Are you sure you want to uninstall this app?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        uninstallApp();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void uninstallApp() {
        Uri packageURI = Uri.parse("package:" + getPackageName());
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        startActivity(uninstallIntent);
    }

    private void callZoo() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:+1234567890")); // Replace with the actual zoo phone number
        startActivity(callIntent);
    }
}
