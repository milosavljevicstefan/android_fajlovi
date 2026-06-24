Naravno. Evo ti fajl po fajl za ovaj zadatak. Package sam stavio kao:

package com.example.template;

Ako ti je drugačiji package, samo promeni com.example.template svuda.


---

1. activity_main.xml

Putanja:

app/src/main/res/layout/activity_main.xml

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/mainRoot"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp">

    <CheckBox
        android:id="@+id/stopRecordingCheckBox"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Zaustavi snimanje" />

    <CheckBox
        android:id="@+id/countriesCheckBox"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Države" />

    <Button
        android:id="@+id/recordButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Snimi" />

    <TextView
        android:id="@+id/locationTextView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Lokacija"
        android:textSize="18sp"
        android:layout_marginTop="16dp" />

    <TextView
        android:id="@+id/proximityTextView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Proximity"
        android:textSize="18sp"
        android:layout_marginTop="16dp" />

</LinearLayout>


---

2. AndroidManifest.xml

Putanja:

app/src/main/AndroidManifest.xml

Permisije moraju biti iznad <application>.

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Template"
        tools:targetApi="31">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:windowSoftInputMode="adjustResize">

            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>

        </activity>

    </application>

</manifest>

Ako ti se tema ne zove Theme.Template, ostavi onu koju ti je Android Studio generisao.


---

3. build.gradle dependencies

Putanja:

app/build.gradle

U dependencies dodaj:

implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.google.android.gms:play-services-location:21.0.1'

Posle toga klikni Sync Now.


---

4. Country.java

Putanja:

app/src/main/java/com/example/template/model/Country.java

Ako nemaš model package, napravi ga.

package com.example.template.model;

public class Country {
    private String name;
    private String code;

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}


---

5. AppDatabaseHelper.java

Putanja:

app/src/main/java/com/example/template/db/AppDatabaseHelper.java

Ako nemaš db package, napravi ga.

package com.example.template.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.template.model.Country;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_database.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_COUNTRIES = "countries";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_CODE = "code";

    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_COUNTRIES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_CODE + " TEXT" +
                ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRIES);
        onCreate(db);
    }

    public boolean insertCountry(Country country) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, country.getName());
        values.put(COLUMN_CODE, country.getCode());

        long result = db.insert(TABLE_COUNTRIES, null, values);

        return result != -1;
    }

    public int getCountryCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_COUNTRIES,
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    public boolean deleteLastCountry() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(
                TABLE_COUNTRIES,
                new String[]{COLUMN_ID},
                null,
                null,
                null,
                null,
                COLUMN_ID + " DESC",
                "1"
        );

        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        cursor.close();

        int rows = db.delete(
                TABLE_COUNTRIES,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        return rows > 0;
    }
}


---

6. MainActivity.java

Putanja:

app/src/main/java/com/example/template/MainActivity.java

package com.example.template;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.template.db.AppDatabaseHelper;
import com.example.template.model.Country;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int REQUEST_PERMISSIONS = 100;

    private CheckBox stopRecordingCheckBox;
    private CheckBox countriesCheckBox;
    private Button recordButton;
    private TextView locationTextView;
    private TextView proximityTextView;

    private AppDatabaseHelper databaseHelper;

    private FusedLocationProviderClient locationClient;

    private SensorManager sensorManager;
    private Sensor proximitySensor;

    private MediaRecorder recorder;
    private String audioPath;

    interface ApiService {
        @GET("countries")
        Call<ArrayList<Country>> getCountries();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stopRecordingCheckBox = findViewById(R.id.stopRecordingCheckBox);
        countriesCheckBox = findViewById(R.id.countriesCheckBox);
        recordButton = findViewById(R.id.recordButton);
        locationTextView = findViewById(R.id.locationTextView);
        proximityTextView = findViewById(R.id.proximityTextView);

        databaseHelper = new AppDatabaseHelper(this);

        requestNeededPermissions();

        locationClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        recordButton.setOnClickListener(v -> {
            startRecording();
        });

        stopRecordingCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                stopRecording();
                stopRecordingCheckBox.setChecked(false);
            }
        });

        countriesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                handleCountriesCheckBox();
                countriesCheckBox.setChecked(false);
            }
        });
    }

    private void requestNeededPermissions() {
        ArrayList<String> permissions = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissions.toArray(new String[0]),
                    REQUEST_PERMISSIONS
            );
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();

                locationTextView.setText("Latitude: " + lat + "\nLongitude: " + lon);
            } else {
                locationTextView.setText("Lokacija nije dostupna");
            }
        });
    }

    private void startRecording() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Nema dozvole za mikrofon", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File audioFile = new File(getFilesDir(), "snimak.3gp");
            audioPath = audioFile.getAbsolutePath();

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(audioPath);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            recorder.prepare();
            recorder.start();

            recordButton.setEnabled(false);

            Toast.makeText(this, "Snimanje počelo", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Greška snimanja: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void stopRecording() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;

                recordButton.setEnabled(true);

                Toast.makeText(this, "Snimak sačuvan u files: " + audioPath, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Snimanje nije pokrenuto", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            recorder = null;
            recordButton.setEnabled(true);
            Toast.makeText(this, "Greška stop: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void handleCountriesCheckBox() {
        if (databaseHelper.getCountryCount() == 0) {
            fetchCountriesAndSave();
        } else {
            boolean deleted = databaseHelper.deleteLastCountry();

            int count = databaseHelper.getCountryCount();

            if (deleted) {
                Toast.makeText(this, "Ostalo država u bazi: " + count, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Nema država za brisanje", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchCountriesAndSave() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dummy-json.mock.beeceptor.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getCountries().enqueue(new Callback<ArrayList<Country>>() {
            @Override
            public void onResponse(Call<ArrayList<Country>> call, Response<ArrayList<Country>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Country> countries = response.body();

                    for (Country country : countries) {
                        databaseHelper.insertCountry(country);
                    }

                    Toast.makeText(
                            MainActivity.this,
                            "Države sačuvane u bazi: " + databaseHelper.getCountryCount(),
                            Toast.LENGTH_SHORT
                    ).show();

                } else {
                    Toast.makeText(MainActivity.this, "Response nije uspešan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Country>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Greška: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float value = event.values[0];
            proximityTextView.setText("Proximity: " + value);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sensorManager == null) {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        }

        if (proximitySensor == null && sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        if (proximitySensor != null) {
            sensorManager.registerListener(
                    this,
                    proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        if (recorder != null) {
            try {
                recorder.stop();
            } catch (Exception ignored) {
            }

            recorder.release();
            recorder = null;
            recordButton.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            getCurrentLocation();
        }
    }
}


---

Najbitnije ako nešto pukne

Ako Retrofit ne radi, proveri:

.baseUrl("https://dummy-json.mock.beeceptor.com/")

i:

@GET("countries")

Ako baza ne menja strukturu posle izmene, obriši aplikaciju sa telefona pa ponovo Run.

Ako lokacija piše Lokacija nije dostupna, nije nužno greška — getLastLocation() nekad vrati null.

Ako audio pukne na stop, uglavnom si čekirao stop pre nego što je snimanje stvarno počelo.
