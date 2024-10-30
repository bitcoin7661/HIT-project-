import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var tiltTextView: TextView
    private lateinit var sensorManager: SensorManager
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tiltTextView = findViewById(R.id.tiltTextView)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Bluetooth 권한 요청
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION),
            1)

        // 가속도계 센서 등록
        val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // 소리 재생을 위한 MediaPlayer 설정
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound) // raw 폴더에 알람 소리 추가
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Roll 계산 (X축 기울기)
            val roll = Math.toDegrees(Math.atan2(y.toDouble(), z.toDouble())).toFloat()

            tiltTextView.text = "Tilt Angle: ${roll.toInt()} degrees"

            // 30도 이상 벗어날 경우 소리 재생
            if (Math.abs(roll) > 30) {
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.start() // 소리 재생
                }
            } else {
                mediaPlayer.pause() // 소리 멈춤
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this) // 센서 리스너 해제
        mediaPlayer.release() // MediaPlayer 자원 해제
    }
}
