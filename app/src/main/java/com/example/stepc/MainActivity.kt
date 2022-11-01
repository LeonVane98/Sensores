package com.example.stepc

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var running = false //esta variable indica que esta en ejecucion
    private var totalSteps = 0f //variable que cuenta el total de pasos

    val ACTIVITY_RECOGNITION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Verifica si el permiso no ha sido concedido y lo solicita
        if (isPermissionGranted()) {
            requestPermission()
        }
        //Inicializa la instancia del sensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    //Esta funcion indica que la ejecucion ha sido retomada
    override fun onResume() {
        super.onResume()
        running = true

        //TYPE_STEP_COUNTER es una constante que describe un sensor de contador de pasos
        //Regresa el numero de pasos tomados por el usuario despues del ultimo reinicio
        //Este sensor requiere el permiso android.permission.ACTIVITY_RECOGNITION.
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            //Si el dispositivo no cuenta con este sensor se mostrara este mensaje
            Toast.makeText(
                this,
                "No se ha detectado ningun sensor en este dispositivo",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            //Registra el listener con el sensorManager
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause(){
        super.onPause()
        running = false
        //Quita el registro del listener
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //Obtiene el textview por medio del ID
        var tvConteoPasos = findViewById<TextView>(R.id.tvConteoPasos)

        if (running){
            //Obtiene el numero de pasos tomados por el usuario
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt()
            //Establece los pasos actuales para mostrarse en el textview
            tvConteoPasos.text = ("$currentSteps")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        println("onAccuracyChanged: Sensor: $sensor; accuracy: $accuracy")
    }

    //Esta funcion solicita el permiso del usuario
    private fun requestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_RECOGNITION_CODE
            )
        }
    }
    //En caso que el permiso sea concedido
    private fun isPermissionGranted(): Boolean{
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        )!= PackageManager.PERMISSION_GRANTED
    }

    //Maneja el resultado de la solicitud de permiso(permitir o negar)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            ACTIVITY_RECOGNITION_CODE ->{
                if((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)){

                }
            }
        }
    }



}