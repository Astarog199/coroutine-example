package com.example.seminar_4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.seminar_4.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val scope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            job?.cancel()

            job = scope.launch {
                createSpeedometerValues().combine(createSpeedometerValues2()) {f1, f2->
                    (f1+f2)/2
                }
                    .map {
                        if (binding.mode.isChecked) {
                            (it / 1.6).toInt()
                        } else {
                            it
                        }
                    }
                    .filter {
                        if (binding.mode2.isChecked) it%5== 0 else true
                    }
                    .map {
                        it.toString() + if (binding.mode.isChecked){
                            "миль/час"
                        }else{
                            "км/ч"
                        }
                    }
                    .collect {
                        withContext(Dispatchers.Main) {
                            binding.speedometer.text = it
                        }
                    }
            }

        }
    }

    fun createSpeedometerValues() = flow {
        var isAccelerate = true
        var speed = 0
        while (true) {
            Thread.sleep(Random.nextLong(500))
            if (isAccelerate) {
                speed++
                if (speed == 100) isAccelerate = false
            } else {
                speed--
                if (speed == 0) isAccelerate = true
            }
            emit(speed)
        }
    }

    fun createSpeedometerValues2() = flow {
        var isAccelerate = true
        var speed = 0
        while (true) {
            Thread.sleep(Random.nextLong(500))
            if (isAccelerate) {
                speed++
                if (speed == 100) isAccelerate = false
            } else {
                speed--
                if (speed == 0) isAccelerate = true
            }
            emit(speed)
        }
    }
}