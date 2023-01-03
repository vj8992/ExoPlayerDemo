package com.vijay.exoplayeradsdemo.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.ExoPlayer.Builder
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.vijay.exoplayeradsdemo.data.AdsModel
import com.vijay.exoplayeradsdemo.databinding.ActivityExoPlayerBinding
import com.vijay.exoplayeradsdemo.ui.AdsAdapter.OnItemClicked

@androidx.annotation.OptIn(
  androidx.media3.common.util.UnstableApi::class)
class ExoPlayerActivity : AppCompatActivity(), OnItemClicked{

  private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
    ActivityExoPlayerBinding.inflate(layoutInflater)
  }
  private lateinit var adsViewModel: AdsViewModel
  private lateinit var adsAdapter: AdsAdapter
  private val videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
  private var player: ExoPlayer? = null

  private var playWhenReady = true
  private var currentItem = 0
  private var playbackPosition = 0L
  private val playbackStateListener: Player.Listener = object : Player.Listener {
    override fun  onIsPlayingChanged(isPlaying: Boolean) {
      if (isPlaying){
        viewBinding.container.visibility = View.GONE
      }else{
        viewBinding.container.visibility = View.VISIBLE
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(viewBinding.root)
    adsViewModel = ViewModelProvider(this)[AdsViewModel::class.java]
    supportActionBar?.hide()
    adsViewModel.getAds()
    adsAdapter = AdsAdapter(this, this@ExoPlayerActivity)
    setUpViews()
  }

  private fun setUpViews(){
    viewBinding.adsRv.apply {
      layoutManager = LinearLayoutManager(this@ExoPlayerActivity)
      adapter = adsAdapter
    }
    adsViewModel.getAdsList().observe(this) { list ->
      adsAdapter.differ.submitList(list)
    }
    viewBinding.backIv.setOnClickListener{
      viewBinding.fillContainer.visibility = View.GONE
    }

    viewBinding.seeAllBTN.setOnClickListener{
      viewBinding.adsGridRv.apply {
        layoutManager = GridLayoutManager(this@ExoPlayerActivity, 4)
        adapter = adsAdapter
      }
      viewBinding.container.visibility = View.GONE
      viewBinding.fillContainer.visibility = View.VISIBLE
    }
  }

  public override fun onStart() {
    super.onStart()
    initializePlayer()
  }

  public override fun onResume() {
    super.onResume()
    hideSystemUi()
    if (player == null) {
      initializePlayer()
    }
  }

  public override fun onStop() {
    super.onStop()
    releasePlayer()
  }

  private fun  initializePlayer() {
    val trackSelector = DefaultTrackSelector(this).apply {
      setParameters(buildUponParameters().setMaxVideoSizeSd())
    }
    player = Builder(this)
      .setTrackSelector(trackSelector)
      .build()
      .also { exoPlayer ->
        viewBinding.videoView.player = exoPlayer

        val mediaItem = MediaItem.Builder()
          .setUri(Uri.parse(videoUrl))
          .setMimeType(MimeTypes.APPLICATION_MP4)
          .build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.addListener(playbackStateListener)
        exoPlayer.seekTo(currentItem, playbackPosition)
        exoPlayer.prepare()
      }
  }

  private fun hideSystemUi() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, viewBinding.videoView).let { controller ->
      controller.hide(WindowInsetsCompat.Type.systemBars())
      controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
  }

  private fun releasePlayer() {
    player?.let { exoPlayer ->
      playbackPosition = exoPlayer.currentPosition
      currentItem = exoPlayer.currentMediaItemIndex
      playWhenReady = exoPlayer.playWhenReady
      exoPlayer.removeListener(playbackStateListener)
      exoPlayer.release()
    }
    player = null
  }

  override fun onItemClicked(ad: AdsModel?) {
    openWebPage(ad?.webHook)
  }

  private fun openWebPage(url: String?) {
    val webpage = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, webpage)
    startActivity(intent)
  }
}