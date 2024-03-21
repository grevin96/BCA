package com.bca.music.view

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bca.music.R
import com.bca.music.databinding.ActivityMainBinding
import com.bca.music.listener.OnSingleClickListener
import com.bca.music.model.Item
import com.bca.music.util.SharedPreferences
import com.bumptech.glide.Glide
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

class MainActivity: AppCompatActivity(), OnSingleClickListener, View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var mainAdapter: MainAdapter
    private lateinit var thread: Thread

    private var mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        AppCenter.start(application, "a46de656-ee00-49a3-9e16-b2fa2878986e", Analytics::class.java, Crashes::class.java)
        setContentView(binding.root)
        SharedPreferences.clear(this)
        search()
        mainAdapter()
        viewModel()
        listener()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancel()
    }

    override fun onClick(view: View?) {
        with(binding) {
            if (view?.id == recycler[0].id) bottom(mainAdapter.item())
            if (view == imageEvent) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()

                    imageEvent(false)
                } else {
                    thread()
                    mediaPlayer.start()

                    imageEvent(true)
                }
            }
        }
    }

    private fun search() {
        with(binding) {
            val handler     = Handler(Looper.getMainLooper())
            val runnable    = Runnable { request() }

            search.addTextChangedListener {
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 1200)
            }
        }
    }

    private fun mainAdapter() {
        mainAdapter = MainAdapter(this)

        mainAdapter.listener(this)
        binding.recycler.apply {
            layoutManager   = LinearLayoutManager(context)
            adapter         = mainAdapter
        }
    }

    private fun viewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        request()
        viewModel.observerData().observe(this) { data ->
            run {
                with(binding) {
                    if (data.size == 0 && search.text.isNotEmpty()) notFound.visibility = View.VISIBLE
                    mainAdapter.data(data)
                    recycler.scrollToPosition(0)
                }
            }
        }
        viewModel.observerProgress().observe(this) { flag -> progress(flag) }
        viewModel.observerFailure().observe(this) { flag -> failure(flag) }
    }

    private fun progress(flag: Boolean) {
        with(binding) {
            loading.visibility  = if (flag) View.VISIBLE else View.GONE
            recycler.visibility = if (!flag) View.VISIBLE else View.GONE
            notFound.visibility = View.GONE
        }
    }

    private fun failure(flag: Boolean) {
        if (flag) request()
    }

    private fun bottom(item: Item) {
        with(binding) {
            track.text          = item.trackName
            artist.text         = item.artistName
            bottom.visibility   = View.VISIBLE

            Glide.with(this@MainActivity).load(item.artworkUrl100).into(image)
            item.previewUrl?.let { start(it) }
        }
    }

    private fun start(track: String) {
        with(binding) {
            stop()
            imageEvent(true)
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.setDataSource(track)

            mediaPlayer.isLooping   = false

            mediaPlayer.setVolume(1f, 1f)
            mediaPlayer.prepare()
            thread()

            seekbar.max = mediaPlayer.duration

            mediaPlayer.start()
        }
    }

    private fun stop() {
        mediaPlayer.stop()
        mediaPlayer.release()

        mediaPlayer = MediaPlayer()

        mediaPlayer.reset()
        mediaPlayer.setOnCompletionListener {
            imageEvent(false)
        }
    }

    private fun imageEvent(isPlaying: Boolean) {
        binding.imageEvent.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun listener() {
        with(binding) {
            imageEvent.setOnClickListener(this@MainActivity)
            seekbar.setOnTouchListener { _, _ -> true }
        }
    }

    private fun thread() {
        thread = object: Thread() {
            override fun run() {
                val totalDuration   = mediaPlayer.duration
                var currentPosition = 0
                while(currentPosition < totalDuration) {
                    try {
                        sleep(500)
                        currentPosition = mediaPlayer.currentPosition
                        binding.seekbar.progress = currentPosition
                    }
                    catch (e: IllegalStateException) { e.printStackTrace(); }
                }
                binding.seekbar.progress = 0
            }
        }

        thread.start()
    }

    private fun request() {
        viewModel.cancel()
        viewModel.data(binding.search.text.toString())
    }
}