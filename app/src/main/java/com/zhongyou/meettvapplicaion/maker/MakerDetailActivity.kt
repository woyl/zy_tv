package com.zhongyou.meettvapplicaion.maker

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.TextView
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.zhongyou.meet.mobile.ameeting.BaseMVVMActivity
import com.zhongyou.meettvapplicaion.BR
import com.zhongyou.meettvapplicaion.R
import com.zhongyou.meettvapplicaion.business.adapter.BaseRecyclerAdapter
import com.zhongyou.meettvapplicaion.business.adapter.BaseRecyclerHolder
import com.zhongyou.meettvapplicaion.core.CommonUtils
import com.zhongyou.meettvapplicaion.core.GlobalConsts
import com.zhongyou.meettvapplicaion.core.PlayService
import com.zhongyou.meettvapplicaion.databinding.ActivityMakerDetailBinding
import com.zhongyou.meettvapplicaion.entity.MakerDetailCourse
import com.zhongyou.meettvapplicaion.entity.MakerSubPageData
import com.zhongyou.meettvapplicaion.network.RxSchedulersHelper
import com.zhongyou.meettvapplicaion.network.RxSubscriber
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_maker_detail.*
import kotlinx.android.synthetic.main.layout_video_player.view.*
import kotlinx.android.synthetic.main.maker_detail_audio.*
import kotlinx.android.synthetic.main.maker_detail_live.*
import kotlinx.android.synthetic.main.maker_detail_video.*
import org.jetbrains.anko.error

class MakerDetailActivity : BaseMVVMActivity<ActivityMakerDetailBinding, MakerDetailModel>() {


    private lateinit var mMakerAdapter: BaseRecyclerAdapter<MakerDetailCourse>
    private val mOperatingAnim by lazy {
        AnimationUtils.loadAnimation(this, R.anim.tip)
    }

    override fun getLayoutId(): Int = R.layout.activity_maker_detail


    private val receiver by lazy {
        MusicInfoReceiver()
    }

    companion object {
        private var musicBinder: PlayService.MusicBinder? = null

        @JvmStatic
        fun setMusicBinder(binder: PlayService.MusicBinder) {
            musicBinder = binder
        }


    }


    override fun initData() {
        registerAudioReceiver()
        val pageId = intent.getStringExtra("pageId")
        val seriesId = intent.getStringExtra("seriesId")
        getSubCourseData(pageId, seriesId)

        val lin = LinearInterpolator()
        mOperatingAnim.interpolator = lin
    }

    private fun registerAudioReceiver() {
        val filter = IntentFilter()
        filter.addAction(GlobalConsts.ACTION_MUSIC_STARTED)
        filter.addAction(GlobalConsts.ACTION_UPDATE_PROGRESS)
        filter.addAction(GlobalConsts.ACTION_STATR_MUSIC)
        filter.addAction(GlobalConsts.ACTION_PAUSE_MUSIC)
        filter.addAction(GlobalConsts.ACTION_LOCAL_MUSIC)
        filter.addAction(GlobalConsts.ACTION_ONLINE_MUSIC)
        filter.addAction(GlobalConsts.ACTION_NEXT_MUSIC)
        filter.addAction(GlobalConsts.ACTION_COMPLETE_MUSIC)
        registerReceiver(receiver, filter)
        //播放服务¬
    }


    private fun getSubCourseData(pageId: String?, seriesId: String) {
        mApiService.getMoreCourse(pageId, seriesId).compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<JSONObject>() {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun _onNext(t: JSONObject?) {
                        t?.let {
                            if (t.getInteger("errcode") == 0) {
                                val jsonArray = t.getJSONObject("data").getJSONArray("subPages")
                                introduction.text = t.getJSONObject("data").getString("introduction")
                                makerTitle.text = t.getJSONObject("data").getString("name")
                                val makerDetailCourseList = JSON.parseArray(JSON.toJSONString(jsonArray), MakerDetailCourse::class.java)
                                setAdapter(makerDetailCourseList)
                            }
                        }
                    }

                })

    }

    private fun setAdapter(makerDetailCourseList: List<MakerDetailCourse>) {
        mMakerAdapter = object : BaseRecyclerAdapter<MakerDetailCourse>(this, makerDetailCourseList, R.layout.item_maker_detail) {
            override fun convert(holder: BaseRecyclerHolder?, item: MakerDetailCourse?, position: Int, isScrolling: Boolean) {
                holder?.setText(R.id.title, item?.name)
                holder?.setImageByUrl(R.id.image, item?.subPageURL)

                holder?.itemView?.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        item?.let {
                            getSubPageDatail(item.subPageId, item.subPageURL)
                        }
                    }
                }

                /* holder?.itemView?.setOnClickListener {
                     item?.let {
                         getSubPageDatail(item.subPageId, item.subPageURL)

                     }
                 }*/
                holder?.itemView?.setOnKeyListener { v, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.action == KeyEvent.ACTION_DOWN && makerDetailCourseList.size <= 2) {
                        error {
                            "position--->${position}----(makerDetailCourseList.size - 1)-->${makerDetailCourseList.size - 1}"
                        }
                        if (position + 1 <= makerDetailCourseList.size - 1) {
                            false
                        } else {
                            introductionContainer.requestFocus()
                            true
                        }
                    } else {
                        false
                    }

                }
            }

        }


        recyclerView.adapter = mMakerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.addItemDecoration(SpaceItemDecoration(0, AutoSizeUtils.pt2px(this, 20f), 0, 0))


        getSubPageDatail(makerDetailCourseList[0].subPageId, makerDetailCourseList[0].subPageURL)
        when (makerDetailCourseList[0].type) {
            2 -> {
                liveParent.visibility = View.VISIBLE
                videoParent.visibility = View.GONE
                audioParent.visibility = View.GONE
                liveParent.requestFocus()
            }
            3 -> {
                liveParent.visibility = View.GONE
                videoParent.visibility = View.VISIBLE
                audioParent.visibility = View.GONE
                videoParent.requestFocus()

            }
            4 -> {
                liveParent.visibility = View.GONE
                videoParent.visibility = View.GONE
                audioParent.visibility = View.VISIBLE
                audioParent.requestFocus()
            }
        }


    }

    var resourceURL: String? = null
    var meetingData: MakerSubPageData? = null
    private fun getSubPageDatail(subPageId: String, imageUrl: String) {
        mApiService.getMoreCourse(subPageId, "")
                .compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<JSONObject>() {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun _onNext(t: JSONObject?) {
                        t?.let {
                            if (t.getInteger("errcode") == 0) {
                                val makerSubPageData = JSON.parseObject(t.getJSONObject("data").toJSONString(), MakerSubPageData::class.java)

                                //type 1：详情页面 2：直播；3 视频；4：音频
                                resourceURL = makerSubPageData.resourceURL
                                when (makerSubPageData.type) {
                                    2 -> {//直播
                                        liveParent.visibility = View.VISIBLE
                                        videoParent.visibility = View.GONE
                                        audioParent.visibility = View.GONE
                                        musicBinder?.stop()
                                        Jzvd.releaseAllVideos()
                                        Glide.with(liveImage).asBitmap().load(imageUrl).into(liveImage)
                                        meetingData = makerSubPageData

                                    }
                                    3 -> {//视频
                                        liveParent.visibility = View.GONE
                                        videoParent.visibility = View.VISIBLE
                                        audioParent.visibility = View.GONE
                                        musicBinder?.stop()
                                        makerVideoPlayer.setUp(resourceURL, "", JzvdStd.SCREEN_NORMAL)
                                        makerVideoPlayer.bottom_seek_progress.progress = 0
                                        Glide.with(makerVideoPlayer.thumbImageView).asBitmap()
                                                .error(R.drawable.rc_image_error)
                                                .load(imageUrl)
                                                .into(makerVideoPlayer.thumbImageView)
                                        makerVideoPlayer.startButton.callOnClick()
                                    }
                                    4 -> {//音频
                                        audioState.visibility = View.VISIBLE
                                        audioState.setImageDrawable(resources.getDrawable(R.drawable.maker_audio_play))
                                        liveParent.visibility = View.GONE
                                        videoParent.visibility = View.GONE
                                        audioParent.visibility = View.VISIBLE
                                        musicBinder?.stop()
                                        Jzvd.releaseAllVideos()
                                        time.text = "00:00/${makerSubPageData.resourceTime}"
                                        seekBar.configBuilder.progress(0f).build()


                                    }
                                }
                                hideStatusBar()//隐藏状态栏
                            }
                        }
                    }

                })
    }


    override fun initViewModel(): MakerDetailModel {
        viewModel = createViewModel(this, MakerDetailModel::class.java)
        return viewModel
    }

    override fun initListener() {
        /*视频设置*/
        videoParent.setOnClickListener {

            makerVideoPlayer.fullscreenButton.performClick()
            makerVideoPlayer.surface_container.setFocusable(true)
            makerVideoPlayer.surface_container.requestFocus()
            makerVideoPlayer.backButton.visibility = View.INVISIBLE
            makerVideoPlayer.batteryTimeLayout.visibility = View.INVISIBLE
            makerVideoPlayer.fullscreenButton.visibility = View.INVISIBLE
//            val position=JCMediaManager.instance().mediaPlayer.currentPosition
//
//            makerVideoPlayer.setCurrentPlayPosition(position)


            /* if (makerVideoPlayer.currentState == JCVideoPlayer.CURRENT_STATE_AUTO_COMPLETE) return@setOnClickListener
             if (makerVideoPlayer.currentScreen == JCVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {

             } else {
                 Log.d(JCVideoPlayer.TAG, "toFullscreenActivity [" + this.hashCode() + "] ")
                 makerVideoPlayer.onEvent(JCUserAction.ON_ENTER_FULLSCREEN)
                 makerVideoPlayer.currentScreen = JCVideoPlayer.SCREEN_WINDOW_FULLSCREEN
                 makerVideoPlayer.startWindowFullscreen()




             }*/
        }



        makerVideoPlayer.surface_container.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN && makerVideoPlayer.screen == Jzvd.SCREEN_FULLSCREEN) {
                if (makerVideoPlayer.mediaInterface.currentPosition + 10 * 1000 <= makerVideoPlayer.mediaInterface.duration) {
                    makerVideoPlayer.mediaInterface.seekTo((makerVideoPlayer.mediaInterface.currentPosition + 10 * 1000))
                    makerVideoPlayer.setAllControlsVisiblity(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                            View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE)
                    makerVideoPlayer.updateStartImage()
                    makerVideoPlayer.postDelayed({
                        makerVideoPlayer.setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE)
                    }, 3000)
                }

                true
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN && makerVideoPlayer.screen == Jzvd.SCREEN_FULLSCREEN) {
                if (makerVideoPlayer.mediaInterface.currentPosition - 10 * 1000 > 0) {
                    makerVideoPlayer.mediaInterface.seekTo((makerVideoPlayer.mediaInterface.currentPosition - 10 * 1000))
                    makerVideoPlayer.setAllControlsVisiblity(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                            View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE)
                    makerVideoPlayer.updateStartImage()
                    makerVideoPlayer.postDelayed({
                        makerVideoPlayer.setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE)
                    }, 3000)
                }
                true
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                var linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                var itemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                linearLayoutManager.findViewByPosition(itemPosition)?.requestFocus()
                true
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.action == KeyEvent.ACTION_DOWN) {
                makerVideoPlayer.startButton.performClick()
                true
            } else if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                //quit fullscreen
                /* makerVideoPlayer.screen = Jzvd.SCREEN_NORMAL
                 makerVideoPlayer.surface_container.setFocusable(false)
                 JCVideoPlayer.backPress()*/
                makerVideoPlayer.fullscreenButton.performClick()
                videoParent.requestFocus()
                makerVideoPlayer.backButton.visibility = View.INVISIBLE
                makerVideoPlayer.batteryTimeLayout.visibility = View.INVISIBLE
                makerVideoPlayer.fullscreenButton.visibility = View.INVISIBLE
                true
            } else keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
        }

        audioParent.setOnClickListener {
            if (musicBinder != null && musicBinder!!.isPlaying) {
                musicBinder?.pause()
            } else if (!TextUtils.isEmpty(musicBinder?.currentUrl)) {
                musicBinder?.start()
            } else {
                musicBinder?.playMusic(resourceURL)
                audioState.visibility = View.VISIBLE
                audioState.setImageDrawable(resources.getDrawable(R.drawable.audio_loading))
                audioState.startAnimation(mOperatingAnim)
            }
        }

        audioParent.setOnKeyListener { _, keyCode, event ->

            if (musicBinder != null && musicBinder!!.isPlaying) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                    if ((musicBinder!!.currentPosition) - 10 * 1000 > 0) {
                        musicBinder!!.seekTo((musicBinder!!.currentPosition - 10 * 1000).toInt())
                    } else if (musicBinder!!.currentPosition - 10 * 1000 <= 0) {
                        musicBinder!!.seekTo(0)
                    }
                    true
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                    if (musicBinder != null && musicBinder!!.currentPosition + 10 * 1000 > musicBinder!!.currentDutation) {
                        true
                    } else if (musicBinder != null) {
                        musicBinder!!.seekTo((musicBinder!!.currentPosition + 10 * 1000).toInt())
                        true
                    } else {
                        false
                    }

                } else {
                    false
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                var linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                var itemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                linearLayoutManager.findViewByPosition(itemPosition)?.requestFocus()
                true
            } else {
                false
            }
        }

        liveParent.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                var linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                var itemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                linearLayoutManager.findViewByPosition(itemPosition)?.requestFocus()
                true
            } else {
                false
            }
        }

        liveParent.setOnClickListener {
            meetingData?.let {
                DialogUtils.getInstance(this).showNormalJoinMeetingDialog(this, meetingData!!.liveId, meetingData!!.isToken!!)
            }
        }

        introductionContainer.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                if ((recyclerView.layoutManager as LinearLayoutManager).itemCount <= 3) {
                    var linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                    var itemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                    linearLayoutManager.findViewByPosition(itemPosition)?.requestFocus()
                    true
                } else {
                    false
                }
            } else {
                false
            }

        }

        introductionContainer.setOnClickListener {
            val view = View.inflate(this, R.layout.dialog_maker_introduce, null)
            val dialog = Dialog(this, R.style.CustomDialog)
            view.findViewById<TextView>(R.id.text).text = introduction.text
            dialog.setContentView(view)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.show()
        }

    }

    override fun initVariableId(): Int = BR.viewModel

    private inner class MusicInfoReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (action == GlobalConsts.ACTION_UPDATE_PROGRESS) {
                val current = intent.getIntExtra("current", 0)
                val total = intent.getIntExtra("total", 0)
                seekBar.configBuilder.max(total.toFloat()).progress(current.toFloat()).build()
                time.text = "${CommonUtils.getFormattedTime(current)}/${CommonUtils.getFormattedTime(total)}"
                audioState.visibility = View.GONE
            } else if (GlobalConsts.ACTION_LOCAL_MUSIC.equals(action)) {
            } else if (GlobalConsts.ACTION_PAUSE_MUSIC.equals(action)) {
                //音乐暂停播放
                audioState.visibility = View.VISIBLE
                audioState.setImageDrawable(resources.getDrawable(R.drawable.maker_audio_pause))
            } else if (GlobalConsts.ACTION_STATR_MUSIC.equals(action)) {
                //只有点暂停和播放按钮时
            } else if (GlobalConsts.ACTION_ONLINE_MUSIC.equals(action)) {
            } else if (GlobalConsts.ACTION_NEXT_MUSIC.equals(action)) {
            } else if (GlobalConsts.ACTION_MUSIC_STARTED.equals(action)) {

                audioState.visibility = View.GONE

                audioState.setImageDrawable(resources.getDrawable(R.drawable.maker_audio_play))
                audioState.clearAnimation()
                mOperatingAnim.cancel()

            } else if (GlobalConsts.ACTION_COMPLETE_MUSIC.equals(action)) {
                audioState.visibility = View.VISIBLE
                audioState.setImageDrawable(resources.getDrawable(R.drawable.maker_audio_play))
                //播放完成
                /*mAudioList.get(clickPosition).setType(0);
				mAudioList.get(clickPosition).setCurrentDuration(0);
				mAdapter.notifyDataSetChanged();*/

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        musicBinder?.stop()
        Jzvd.releaseAllVideos()

    }

    fun hideStatusBar() {
        val decorView = window.decorView
        val option = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = option
    }
}