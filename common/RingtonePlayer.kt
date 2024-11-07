package com.example.bamboogarden.common

import android.content.Context
import android.media.MediaPlayer
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.R

class RingtonePlayer(private val context: Context = BambooGardenApplication.instance) {
  private var mediaPlayer : MediaPlayer? = null

  fun playRingtone() {
    if (mediaPlayer?.isPlaying == true) {
      mediaPlayer?.reset()
    } else {
      mediaPlayer = MediaPlayer.create(context, R.raw.ringtone)
    }

    mediaPlayer?.apply {
      setOnCompletionListener {
        release()
        mediaPlayer = null
      }
      start()
    }
  }

  fun release() {
    mediaPlayer?.release()
    mediaPlayer = null
  }
}