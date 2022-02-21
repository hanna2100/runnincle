package com.example.runnincle.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.runnincle.R

object SoundPlayer {

    const val COUNT = R.raw.count_sound
    const val START = R.raw.start_sound
    const val END = R.raw.end_sound

    private lateinit var soundPool: SoundPool
    private lateinit var soundPoolMap: HashMap<Int, Int>

    fun initSounds(context: Context) {
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(attribute)
            .build()

        soundPoolMap = HashMap(2)
        soundPoolMap[COUNT] = soundPool.load(context, COUNT, 1)
        soundPoolMap[START] = soundPool.load(context, START, 2)
        soundPoolMap[END] = soundPool.load(context, END, 2)
    }

    fun play(rawId: Int) {
        if(soundPoolMap.containsKey(rawId)) {
            soundPool.play(soundPoolMap[rawId]!!, 1f, 1f, 1, 0, 1f)
        }
    }

}