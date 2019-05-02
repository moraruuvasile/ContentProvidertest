package com.example.contentprovider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";
	private ListView contactNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		contactNames = findViewById(R.id.contact_names);

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "onClick: starts");
				String[] projection = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
				ContentResolver contentResolver = getContentResolver();
				Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
						projection,
						null,
						null,
						ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
				if (cursor != null) {
					List<String> contacts = new ArrayList<>();
					while (cursor.moveToNext()) {
						contacts.add(cursor.getString(cursor.getColumnIndex
								(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
					}
					cursor.close();
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
							R.layout.contact_detail, R.id.name, contacts);
					contactNames.setAdapter(adapter);
				}
				Log.d(TAG, "onClick: ends");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
