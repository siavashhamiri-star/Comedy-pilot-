package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class SoundEffectsEngine(private val context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var activeAudioTrack: AudioTrack? = null

    // Audio recording state
    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _recordedAudioBuffer = MutableStateFlow<ByteArray?>(null)
    val recordedAudioBuffer = _recordedAudioBuffer.asStateFlow()

    private val _audioWaveformLevels = MutableStateFlow<List<Float>>(emptyList())
    val audioWaveformLevels = _audioWaveformLevels.asStateFlow()

    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun triggerHapticFeedback() {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(40)
                }
            }
        } catch (e: Exception) {
            Log.e("SoundEffectsEngine", "Haptic error", e)
        }
    }

    fun playSoundEffect(effectId: String) {
        triggerHapticFeedback()
        scope.launch {
            try {
                activeAudioTrack?.stop()
                activeAudioTrack?.release()
                
                val sampleRate = 22050
                val pcmData = generatePcmForEffect(effectId, sampleRate)
                
                playPcmData(pcmData, sampleRate)
            } catch (e: Exception) {
                Log.e("SoundEffectsEngine", "Playback error", e)
            }
        }
    }

    fun playRecordedAudioWithEffect(pitchMultiplier: Float, speedMultiplier: Float, isRobot: Boolean) {
        triggerHapticFeedback()
        val rawBuffer = recordedAudioBuffer.value ?: return
        scope.launch {
            try {
                activeAudioTrack?.stop()
                activeAudioTrack?.release()

                val processedPcm = processPcmAudio(rawBuffer, pitchMultiplier, speedMultiplier, isRobot)
                val sampleRate = (22050 * speedMultiplier).toInt().coerceIn(8000, 48000)
                
                playPcmData(processedPcm, sampleRate)
            } catch (e: Exception) {
                Log.e("SoundEffectsEngine", "Voice change error", e)
            }
        }
    }

    fun startRecordingMicrophone() {
        if (_isRecording.value) return
        _isRecording.value = true
        _recordedAudioBuffer.value = null
        
        recordingJob = scope.launch {
            val sampleRate = 22050
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            
            val bufferSize = minBufferSize.coerceAtLeast(4096)
            var audioRecord: AudioRecord? = null

            try {
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    bufferSize
                )

                if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                    _isRecording.value = false
                    return@launch
                }

                audioRecord.startRecording()
                val baos = ByteArrayOutputStream()
                val tempBuffer = ByteArray(bufferSize)
                val levels = mutableListOf<Float>()

                while (_isRecording.value) {
                    val readBytes = audioRecord.read(tempBuffer, 0, tempBuffer.size)
                    if (readBytes > 0) {
                        baos.write(tempBuffer, 0, readBytes)
                        
                        // Calculate amplitude for waveform display
                        var maxAmp = 0
                        for (i in 0 until readBytes - 1 step 2) {
                            val sample = (tempBuffer[i].toInt() and 0xFF) or (tempBuffer[i + 1].toInt() shl 8)
                            val absSample = Math.abs(sample.toShort().toInt())
                            if (absSample > maxAmp) maxAmp = absSample
                        }
                        val normLevel = (maxAmp / 32768f).coerceIn(0.1f, 1.0f)
                        levels.add(normLevel)
                        if (levels.size > 30) levels.removeAt(0)
                        _audioWaveformLevels.value = levels.toList()
                    }
                    delay(30)
                }

                audioRecord.stop()
                _recordedAudioBuffer.value = baos.toByteArray()
            } catch (e: Exception) {
                Log.e("SoundEffectsEngine", "Mic record error", e)
            } finally {
                audioRecord?.release()
                _isRecording.value = false
            }
        }
    }

    fun stopRecordingMicrophone() {
        _isRecording.value = false
    }

    private fun playPcmData(pcmData: ByteArray, sampleRate: Int) {
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(Math.max(pcmData.size, minBufferSize))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(pcmData, 0, pcmData.size)
        activeAudioTrack = track
        track.play()
    }

    private fun processPcmAudio(
        rawPcm: ByteArray,
        pitchMult: Float,
        speedMult: Float,
        isRobot: Boolean
    ): ByteArray {
        val samples = ShortArray(rawPcm.size / 2)
        for (i in samples.indices) {
            val low = rawPcm[i * 2].toInt() and 0xFF
            val high = rawPcm[i * 2 + 1].toInt() shl 8
            samples[i] = (low or high).toShort()
        }

        val outputSize = (samples.size / pitchMult).toInt().coerceAtLeast(100)
        val processedSamples = ShortArray(outputSize)

        for (i in processedSamples.indices) {
            val sourceIdx = (i * pitchMult).toInt().coerceIn(0, samples.size - 1)
            var valSample = samples[sourceIdx].toInt()

            if (isRobot) {
                val modulation = sin(2.0 * PI * i * 150.0 / 22050.0)
                valSample = (valSample * (0.6 + 0.4 * modulation)).toInt()
            }

            processedSamples[i] = valSample.coerceIn(-32768, 32767).toShort()
        }

        val result = ByteArray(processedSamples.size * 2)
        for (i in processedSamples.indices) {
            val s = processedSamples[i].toInt()
            result[i * 2] = (s and 0xFF).toByte()
            result[i * 2 + 1] = ((s shr 8) and 0xFF).toByte()
        }
        return result
    }

    private fun generatePcmForEffect(effectId: String, sampleRate: Int): ByteArray {
        val durationSec = when (effectId) {
            "rimshot" -> 0.8
            "laugh_track" -> 2.0
            "sad_violin" -> 2.5
            "dramatic_horn" -> 1.5
            "boing" -> 1.0
            "fart" -> 0.9
            "wrong_buzzer" -> 1.2
            "win_chime" -> 1.8
            "cheetori" -> 1.2
            "ey_jan" -> 1.0
            "kheili_ali" -> 1.5
            "chehra_dada" -> 1.3
            else -> 1.2
        }

        val totalSamples = (sampleRate * durationSec).toInt()
        val pcm = ShortArray(totalSamples)

        when (effectId) {
            "rimshot" -> { // Snare & cymbal hit
                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val drumFreq = 200.0 * Math.exp(-t * 20)
                    val noise = (Random.nextDouble() * 2 - 1) * Math.exp(-t * 15)
                    val tone = sin(2 * PI * drumFreq * t) * Math.exp(-t * 10)
                    pcm[i] = ((tone + noise * 0.7) * 20000).toInt().coerceIn(-32768, 32767).toShort()
                }
            }
            "laugh_track" -> { // Staccato laughing sound wave
                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val laughPulse = sin(2 * PI * 6.0 * t) // 6 laughs per second
                    val freq = 450.0 + sin(2 * PI * 12.0 * t) * 100.0
                    val amp = Math.max(0.0, laughPulse) * Math.exp(- (t % 0.3) * 5)
                    pcm[i] = (sin(2 * PI * freq * t) * amp * 22000).toInt().coerceIn(-32768, 32767).toShort()
                }
            }
            "sad_violin" -> { // Descending minor tremolo tone
                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val baseFreq = 440.0 - t * 60.0
                    val tremolo = 1.0 + 0.2 * sin(2 * PI * 8.0 * t)
                    val tone = sin(2 * PI * baseFreq * t) + 0.5 * sin(2 * PI * baseFreq * 2 * t)
                    pcm[i] = (tone * tremolo * 15000).toInt().coerceIn(-32768, 32767).toShort()
                }
            }
            "dramatic_horn" -> { // Low brass horn
                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val freq = 130.0 + (if (t > 0.4) 30.0 else 0.0)
                    val brass = sin(2 * PI * freq * t) + 0.6 * sin(2 * PI * freq * 3 * t)
                    pcm[i] = (brass * 22000).toInt().coerceIn(-32768, 32767).toShort()
                }
            }
            "boing" -> { // Pitch up bouncy spring
                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val freq = 150.0 + t * 600.0
                    pcm[i] = (sin(2 * PI * freq * t) * Math.exp(-t * 2) * 22000).toInt().coerceIn(-32768, 32767).toShort()
                }
            }
            "fart" -> { // Low pitch noisy vibration
                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val freq = 60.0 + Random.nextDouble() * 40.0
                    val noise = (Random.nextDouble() * 2 - 1) * 0.4
                    pcm[i] = ((sin(2 * PI * freq * t) + noise) * Math.exp(-t * 2.5) * 24000).toInt().coerceIn(-32768, 32767).toShort()
                }
            }
            "wrong_buzzer" -> { // Harsh dual tone buzzer
                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val square = if (sin(2 * PI * 150.0 * t) > 0) 1.0 else -1.0
                    pcm[i] = (square * 18000).toInt().coerceIn(-32768, 32767).toShort()
                }
            }
            "win_chime" -> { // Major arpeggio chime
                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val noteIdx = (t * 5).toInt().coerceIn(0, 4)
                    val freqs = arrayOf(523.25, 659.25, 783.99, 1046.50, 1318.51)
                    val freq = freqs[noteIdx]
                    pcm[i] = (sin(2 * PI * freq * t) * 20000).toInt().coerceIn(-32768, 32767).toShort()
                }
            }
            "cheetori" -> { // High joyful double whistle
                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val freq = 700.0 + sin(2 * PI * 8.0 * t) * 200.0
                    pcm[i] = (sin(2 * PI * freq * t) * 22000).toInt().coerceIn(-32768, 32767).toShort()
                }
            }
            else -> { // Default cheer chime
                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val freq = 440.0 + t * 200.0
                    pcm[i] = (sin(2 * PI * freq * t) * 18000).toInt().coerceIn(-32768, 32767).toShort()
                }
            }
        }

        val result = ByteArray(pcm.size * 2)
        for (i in pcm.indices) {
            val s = pcm[i].toInt()
            result[i * 2] = (s and 0xFF).toByte()
            result[i * 2 + 1] = ((s shr 8) and 0xFF).toByte()
        }
        return result
    }
}
