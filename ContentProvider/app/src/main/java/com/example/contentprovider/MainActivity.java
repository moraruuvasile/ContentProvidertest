package com.example.contentprovider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
	private static final int REQUEST_CODE_READ_CONTACTS = 1;
	private static boolean READ_CONTACTS_GRANTED = false;
	private FloatingActionButton fab = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		contactNames = findViewById(R.id.contact_names);

		int hasReadContactPermission = ContextCompat.checkSelfPermission(
				this, Manifest.permission.READ_CONTACTS);
		Log.d(TAG, "onCreate: checkSelfPermision " + hasReadContactPermission);

		if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
			Log.d(TAG, "onCreate: Permision Granted");
			READ_CONTACTS_GRANTED = true;
		} else {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
					REQUEST_CODE_READ_CONTACTS);
			Log.d(TAG, "onCreate: requesting permisions");
		}

		fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "onClick: starts");
				if (READ_CONTACTS_GRANTED) {
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
				} else {
					Snackbar.make(view, "Please grante access to Contacts", Snackbar.LENGTH_LONG)
							.setAction("Action", new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											Log.d(TAG, "Snackbar onClick: starts");
											if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)){
												ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS},
														REQUEST_CODE_READ_CONTACTS);
											} else {
												//user has permanently denied
												Log.d(TAG, "Snackbar onClick: Lounching settings");
												Intent intent = new Intent();
												intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
												Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
												intent.setData(uri);
												MainActivity.this.startActivity(intent);
											}
										}
									}

							).show();

				}
				Log.d(TAG, "onClick: ends");
			}
		});
		Log.d(TAG, "onCreate: ends");
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										@NonNull String[] permissions,
										@NonNull int[] grantResults) {
		Log.d(TAG, "onRequestPermissionsResult: strats");
		switch (requestCode) {
			case REQUEST_CODE_READ_CONTACTS: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d(TAG, "onRequestPermissionsResult: permision granted");
					READ_CONTACTS_GRANTED = true;
				} else {
					Log.d(TAG, "onRequestPermissionsResult: permision refused");
				}
//				fab.setEnabled(READ_CONTACTS_GRANTED);
			}
		}
		Log.d(TAG, "onRequestPermissionsResult: ends");
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
