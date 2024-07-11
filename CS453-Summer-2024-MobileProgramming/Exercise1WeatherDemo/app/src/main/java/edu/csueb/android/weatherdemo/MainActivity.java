package edu.csueb.android.weatherdemo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class MainActivity extends Activity {

    private SensorManager sensorManager;
    private TextView temperatureTextView;
    private TextView pressureTextView;
    private TextView lightTextView;

    private float currentTemperature = Float.NaN;
    private float currentPressure = Float.NaN;
    private float currentLight = Float.NaN;

    private final SensorEventListener tempSensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }

        @Override
        public void onSensorChanged(SensorEvent event) {
            currentTemperature = event.values[0];
        }
    };

    private final SensorEventListener pressureSensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }

        @Override
        public void onSensorChanged(SensorEvent event) {
            currentPressure = event.values[0];
        }
    };

    private final SensorEventListener lightSensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }

        @Override
        public void onSensorChanged(SensorEvent event) {
            currentLight = event.values[0];
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureTextView = findViewById(R.id.temperature);
        pressureTextView = findViewById(R.id.pressure);
        lightTextView = findViewById(R.id.light);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Use Handler for periodic updates
        final Handler handler = new Handler();
        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                updateGUI();
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(updateTask);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            sensorManager.registerListener(lightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            lightTextView.setText(R.string.light_sensor_unavailable);
        }

        Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (pressureSensor != null) {
            sensorManager.registerListener(pressureSensorEventListener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            pressureTextView.setText(R.string.barometer_unavailable);
        }

        Sensor temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if (temperatureSensor != null) {
            sensorManager.registerListener(tempSensorEventListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            temperatureTextView.setText(R.string.thermometer_unavailable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorEventListener);
        sensorManager.unregisterListener(pressureSensorEventListener);
        sensorManager.unregisterListener(tempSensorEventListener);
    }

    private void updateGUI() {
        temperatureTextView.setText(String.format("Temperature: %.2f°C", currentTemperature));
        pressureTextView.setText(String.format("Pressure: %.2f hPa", currentPressure));
        lightTextView.setText(String.format("Light: %.2f lx", currentLight));
    }
}
