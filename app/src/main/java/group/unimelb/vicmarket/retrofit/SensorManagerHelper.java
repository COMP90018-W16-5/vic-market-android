package group.unimelb.vicmarket.retrofit;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static io.reactivex.internal.schedulers.SchedulerPoolFactory.start;

public class SensorManagerHelper implements SensorEventListener {

    private final int SPEED_SHRESHOLD = 5000;
    private final int UPDATE_INTERVAL_TIME = 50;
    private SensorManager sensorManager;
    private Sensor sensor;
    private OnShakeListener onShakeListener;
    private Context context;
    private float lastX;
    private float lastY;
    private float lastZ;
    private long lastUpdataTime;

    public SensorManagerHelper(Context context)
    {
        this.context = context;
        start();
    }

    public void start()
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager != null)
        {
            sensor = sensorManager.getDefaultSensor(sensor.TYPE_ACCELEROMETER);
        }
        if(sensor != null)
        {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void stop()
    {
        sensorManager.unregisterListener(this);
    }

    public interface OnShakeListener
    {
        void onShake();
    }

    public void setOnShakeListener(OnShakeListener listener)
    {
        onShakeListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentUpdateTime = System.currentTimeMillis();
        long timeInterval = currentUpdateTime - lastUpdataTime;
        if(timeInterval < UPDATE_INTERVAL_TIME)
        {
            return;
        }
        else
        {
            lastUpdataTime = currentUpdateTime;
        }
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;
        lastX = x;
        lastY = y;
        lastZ = z;
        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval * 10000;
        if(speed >= SPEED_SHRESHOLD)
        {
            onShakeListener.onShake();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
