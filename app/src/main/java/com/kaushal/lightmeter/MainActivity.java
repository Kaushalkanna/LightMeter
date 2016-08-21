package com.kaushal.lightmeter;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    TextView tv1;
    private final Handler handler = new Handler();
    GraphView graph;
    int graphXMin = 0;
    int graphXMax = 250;
    int graphYMin = 0;
    int graphYMax = 120;
    private LineGraphSeries<DataPoint> graphData;
    private double graphLastXValue = -1d;
    private double luxValue = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1 = (TextView) findViewById(R.id.textView);
        graph = (GraphView) findViewById(R.id.graph);
        graphData = new LineGraphSeries<DataPoint>();
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(graphXMin);
        graph.getViewport().setMaxX(graphXMax);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(graphYMin);
        graph.getViewport().setMaxY(graphYMax);
        graph.getViewport().setScrollable(true);



        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        Runnable runTimer = new Runnable() {
            @Override
            public void run() {
                graphLastXValue += 1d;
                graphData.appendData(new DataPoint(graphLastXValue, luxValue), true, graphXMax);
                graph.addSeries(graphData);
            }
        };
        handler.postDelayed(runTimer, 100);

    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_LIGHT) {
            Log.i("Sensor Changed", "Accuracy :" + accuracy);
        }

    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            Log.i("Sensor Changed", "onSensor Change :" + event.values[0]);
            luxValue = event.values[0];
            tv1.setText(luxValue + " lux");
        }

    }
}
