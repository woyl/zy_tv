package com.zhongyou.meettvapplicaion.maker

import android.app.Service
import android.arch.lifecycle.Observer
import android.content.*
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.bingoogolapple.bgabanner.BGABanner
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.zhongyou.meet.mobile.ameeting.BaseMVVMActivity
import com.zhongyou.meettvapplicaion.BR
import com.zhongyou.meettvapplicaion.Constant
import com.zhongyou.meettvapplicaion.R
import com.zhongyou.meettvapplicaion.business.adapter.BaseRecyclerAdapter
import com.zhongyou.meettvapplicaion.business.adapter.BaseRecyclerHolder
import com.zhongyou.meettvapplicaion.core.CommonUtils
import com.zhongyou.meettvapplicaion.core.GlobalConsts
import com.zhongyou.meettvapplicaion.core.PlayService
import com.zhongyou.meettvapplicaion.core.PlayService.MusicBinder
import com.zhongyou.meettvapplicaion.databinding.ActivityMakerIndexActivtyBinding
import com.zhongyou.meettvapplicaion.entity.AudioItem
import com.zhongyou.meettvapplicaion.entity.Lable
import com.zhongyou.meettvapplicaion.entity.MakerCourse
import com.zhongyou.meettvapplicaion.entity.RecomandData
import com.zhongyou.meettvapplicaion.network.RxSchedulersHelper
import com.zhongyou.meettvapplicaion.network.RxSubscriber
import com.zhongyou.meettvapplicaion.view.SpaceItemDecoration
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_maker_index_activty.*
import kotlinx.android.synthetic.main.item_notify.*
import me.jessyan.autosize.utils.AutoSizeUtils
import org.jetbrains.anko.error

class MakerIndexActivity : BaseMVVMActivity<ActivityMakerIndexActivtyBinding, MakerIndexViewModel>() {

    var mCurrentPage = 1
    var mTotalPage = 100
    override fun getLayoutId() = R.layout.activity_maker_index_activty

    private var mAudioAdapter: BaseRecyclerAdapter<AudioItem>? = null
    private var mLableAdapter: BaseRecyclerAdapter<Lable>? = null
    private var mMakerCourseAdapter: BaseRecyclerAdapter<MakerCourse>? = null

    private val mOperatingAnim by lazy {
        AnimationUtils.loadAnimation(this, R.anim.tip)
    }
    private val receiver by lazy {
        MusicInfoReceiver()
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
        addImageButton.setOnClickListener {
            DialogUtils.getInstance(this).showQuickJoinDialog(this)
        }
        searchEditText.setOnClickListener {
            DialogUtils.getInstance(this).showSearchDialog(this)
        }
        banner.setOnClickListener {
            val currentItem: Int = banner.currentItem
            val meetingList: RecomandData? = viewModel.dataLists.value
            val model = meetingList?.chuangkeList?.get(currentItem)

            error {
                JSON.toJSONString(model)
            }

            if (model?.isDefaultImg == 1) {
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(model!!.urlId)) {
                return@setOnClickListener
            }
            if (model.linkType == 1) {
                var isToken = 1
                if (!TextUtils.isEmpty(model.isToken)) {
                    if (model.isToken == "0") {
                        isToken = 0
                    }
                }
                DialogUtils.getInstance(this)
                        .showNormalJoinMeetingDialog(this, model.urlId, isToken)
            } else if (model.linkType == 2) {
                val intent = Intent(this, MakerDetailActivity::class.java)
                intent.putExtra("pageId", model.urlId)
                intent.putExtra("seriesId", model.seriesId)
                startActivity(intent)
            }
        }
    }

    companion object {
        private var musicBinder: PlayService.MusicBinder? = null

        @JvmStatic
        fun setMusicBinder(binder: PlayService.MusicBinder) {
            musicBinder = binder
        }

    }


    var audioLists = ArrayList<AudioItem>()

    override fun initData() {
        registerAudioReceiver()
        viewModel.getRecomandData()

        viewModel.getBannerData().observe(this, Observer<RecomandData> {
            it?.let {
                val textLists: MutableList<String> = ArrayList()
                if (it.chuangkeList == null) {
                    return@Observer
                }
                for (meetingListBean in it.chuangkeList) {
                    textLists.add(meetingListBean.name)
                }

                mBinding.banner.setData(R.layout.item_localimage, it.chuangkeList, textLists)
                mBinding.banner.setAdapter(object : BGABanner.Adapter<AppCompatImageView?, RecomandData.ChuangkeListBean> {
                    override fun fillBannerItem(banner: BGABanner?, itemView: AppCompatImageView?, model: RecomandData.ChuangkeListBean?, position: Int) {
                        if (itemView != null) {
                            Glide.with(applicationContext)
                                    .load(model?.pictureURL)
                                    .dontAnimate()
                                    .error(R.drawable.defaule_banner)
                                    .placeholder(R.drawable.defaule_banner)
                                    .into(itemView)
                        }
                    }
                })
            }
        })
        getAudioData()//获取日更课程
        getLableData()//获取栏目

        notifyImageButton.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    val itemView = (labelRecyclerView.layoutManager as LinearLayoutManager).findViewByPosition(mLastfocusedLabePosition)
                    val labelView = itemView?.findViewById<LinearLayout>(R.id.item)
                    labelView?.requestFocus()
                    true
                } else {
                    false
                }
            } else {
                false
            }

        }
        addImageButton.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    val itemView = (labelRecyclerView.layoutManager as LinearLayoutManager).findViewByPosition(mLastfocusedLabePosition)
                    val labelView = itemView?.findViewById<LinearLayout>(R.id.item)
                    labelView?.requestFocus()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        notifyImageButton.setOnClickListener {
            startActivity(Intent(this, NotifyActivity::class.java))
        }

    }

    fun getAudioData(currentPage: Int = 1) {
        mApiService.getMoreAudioCourse(currentPage).compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<JSONObject>() {
                    override fun onSubscribe(d: Disposable) {
//                        super.onSubscribe(d)

                    }

                    override fun _onNext(t: JSONObject?) {
                        t?.let {
                            if (t.getInteger("errcode") == 0) {
                                if (mCurrentPage == 1) {
                                    audioLists.clear()
                                }

                                var jsonObject = t.getJSONObject("data").getJSONObject("changeLessonByDayList")
                                mTotalPage = jsonObject.getInteger("totalPage")
                                var jsonArray = jsonObject.getJSONArray("list")
                                for (any in jsonArray) {
                                    var audioArray = JSON.parseObject(JSON.toJSONString(any)).getJSONArray("list")
                                    var list = JSON.parseArray(JSON.toJSONString(audioArray), AudioItem::class.java)
                                    error(
                                            JSON.toJSONString(list)
                                    )
                                    audioLists.addAll(list)
                                }
                                setAudioAdapter()
                            } else {
                                showMessage(t.getString("errmsg"))
                            }
                        }
                    }

                })
    }

    private fun getLableData() {
        mApiService.makerColumn.compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<JSONObject>() {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun _onNext(t: JSONObject?) {
                        t?.let {
                            if (t.getInteger("errcode") == 0) {
                                var jsonArray = t.getJSONObject("data").getJSONArray("columns")
                                var lableLists = JSON.parseArray(JSON.toJSONString(jsonArray), Lable::class.java)
                                setLableAdapter(lableLists);
                            }
                        }

                    }

                })
    }

    private var mSeriesPage = 1
    lateinit var mFocusLabel: Lable
    private var mLastfocusedLabePosition = 0

    private fun setLableAdapter(lableLists: MutableList<Lable>) {
        if (mLableAdapter == null) {
            mLableAdapter = object : BaseRecyclerAdapter<Lable>(this, lableLists, R.layout.item_status_type_label) {
                override fun convert(holder: BaseRecyclerHolder, item: Lable, position: Int, isScrolling: Boolean) {

                    holder.setText(R.id.label, item.typeName)
                    val textViewSide =  holder.getView<TextView>(R.id.sideView)
                    val labelTv = holder.getView<TextView>(R.id.label)
                    if (Constant.isPadApplicaion) {
                        if (position == mLastfocusedLabePosition) {
                            mLastfocusedLabePosition = position
                            mFocusLabel = item
                            mSeriesPage = 1
                            mCourseLastPosition = 0
                            getSeriesPageData(item, mSeriesPage)
                            textViewSide.visibility = View.VISIBLE
                            labelTv.setTextColor(resources.getColor(R.color.blue))
                        } else {
                            textViewSide.visibility = View.INVISIBLE
                            labelTv.setTextColor(resources.getColor(R.color.white))
                        }
                        holder.itemView.setOnClickListener {
                            mFocusLabel = item
                            mSeriesPage = 1
                            mCourseLastPosition = 0
                            getSeriesPageData(item, mSeriesPage)
                            textViewSide.visibility = View.VISIBLE
                            labelTv.setTextColor(resources.getColor(R.color.blue))
                            notifyItemChanged(mLastfocusedLabePosition)
                            mLastfocusedLabePosition = position
                        }
                    } else {
                        holder.itemView.setOnFocusChangeListener { _: View, b: Boolean ->
                            if (b) {
                                val itemView = (labelRecyclerView.layoutManager as LinearLayoutManager).findViewByPosition(mLastfocusedLabePosition)
                                val itemLinearLayout = itemView?.findViewById<LinearLayout>(R.id.item)
                                itemLinearLayout?.findViewById<TextView>(R.id.label)?.setTextColor(resources.getColor(R.color.white))
                                itemLinearLayout?.findViewById<TextView>(R.id.sideView)?.visibility = View.INVISIBLE
                                mLastfocusedLabePosition = position
                                mFocusLabel = item
                                mSeriesPage = 1
                                mCourseLastPosition = 0
                                getSeriesPageData(item, mSeriesPage)
                                textViewSide.visibility = View.VISIBLE
                                labelTv.setTextColor(resources.getColor(R.color.blue))
                            } else {
                                if (mLastfocusedLabePosition != position) {
                                    labelTv.setTextColor(resources.getColor(R.color.white))
                                    textViewSide.visibility = View.INVISIBLE
                                } else {
                                    labelTv.setTextColor(resources.getColor(R.color.white))
                                    textViewSide.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }

            }
            labelRecyclerView.adapter = mLableAdapter
            labelRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        } else {
            mLableAdapter?.notifyDataSetChanged()
        }
        Handler().postDelayed({
            val itemView = (labelRecyclerView.layoutManager as LinearLayoutManager).findViewByPosition(0)
            val item = itemView?.findViewById<LinearLayout>(R.id.item)
            item?.requestFocus()
        }, 300)


    }

    var mCourseLists = ArrayList<MakerCourse>()
    private fun getSeriesPageData(item: Lable, currentPage: Int = 1) {
        mApiService.getSeriesPageByType(item.id, currentPage, 10).compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<JSONObject>() {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun _onNext(t: JSONObject?) {
                        t?.let {
                            if (t.getInteger("errcode") == 0) {

                                var jsonArray = t.getJSONObject("data").getJSONArray("list")
                                mCourseTotalPag = t.getJSONObject("data").getInteger("totalPage")
                                if (currentPage == 1) {
                                    mCourseLists.clear()
                                }
                                var dataList = JSON.parseArray(JSON.toJSONString(jsonArray), MakerCourse::class.java)
                                mCourseLists.addAll(dataList)
                                setMakerCourseAdapter()
                            }
                        }
                    }
                })
    }

    var canRemoveCourse = false
    private var mCourseLastPosition = 0
    private var mCourseTotalPag = 100
    private fun setMakerCourseAdapter() {
        if (mMakerCourseAdapter == null) {
            mMakerCourseAdapter = object : BaseRecyclerAdapter<MakerCourse>(this, mCourseLists, R.layout.item_maker) {
                override fun convert(holder: BaseRecyclerHolder?, item: MakerCourse?, position: Int, isScrolling: Boolean) {

                    holder?.setText(R.id.title, item?.name)
                    holder?.setImageByUrl(R.id.image, item?.pictureURL)

                    holder?.itemView?.setOnFocusChangeListener { v, hasFocus ->

                        //<= (mCourseLists.size - mCourseLists.size % 3) && position >= mCourseLists.size / 3
                        canRemoveCourse = if (mCourseLists.size % 3 != 0) {
                            (position / 3) + 1 >= (mCourseLists.size / 3) + 1
                        } else {
                            (position / 3) + 1 >= (mCourseLists.size) / 3
                        }



                        if (hasFocus && position >= mSeriesPage * 9) {

                            mCourseLastPosition = position
                            if (mSeriesPage < mCourseTotalPag) {
                                mSeriesPage++
                                getSeriesPageData(mFocusLabel, mSeriesPage)
                            } else {
                                showMessage("没有更多了")
                            }
                        }
                    }
                    holder?.itemView?.setOnKeyListener { _, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                            if (position <= 2) {
                                val itemView = (labelRecyclerView.layoutManager as LinearLayoutManager).findViewByPosition(mLastfocusedLabePosition)
                                val labelView = itemView?.findViewById<LinearLayout>(R.id.item)
                                labelView?.requestFocus()
                                true
                            } else {
                                false
                            }
                        } else {
                            keyCode == KeyEvent.KEYCODE_DPAD_DOWN && canRemoveCourse
                        }
                    }
                    holder?.itemView?.setOnClickListener {
                        startActivity(Intent(applicationContext, MakerDetailActivity::class.java)
                                .putExtra("pageId", item?.pageId)
                                .putExtra("seriesId", item?.id)
                        )
                    }

                }

            }
            courseRecyclerView.layoutManager = GridLayoutManager(this, 3)
            courseRecyclerView.adapter = mMakerCourseAdapter
            courseRecyclerView.addItemDecoration(SpaceItemDecoration(0, AutoSizeUtils.pt2px(applicationContext, 26f), 0, 0))

        } else {
            if (mSeriesPage != 1) {
                mMakerCourseAdapter?.notifyItemRangeInserted(mCourseLastPosition + 1, 1)
            } else {
                mMakerCourseAdapter?.notifyDataSetChanged()
            }

        }
    }

    var nextItem: Int = 0
    override fun initListener() {
        banner.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                nextItem = if (banner.currentItem - 1 < 0) 0 else banner.currentItem - 1
                error(
                        nextItem
                )
                banner.currentItem = nextItem
                true
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                nextItem = if (banner.currentItem + 1 > banner.itemCount - 1) banner.itemCount - 1 else banner.currentItem + 1
                error(
                        nextItem
                )
                banner.currentItem = nextItem
                true
            } else {
                false
            }
        }

    }

    var lastPosition = 0
    var lastClickPosition = -1
    private fun setAudioAdapter() {
        if (mAudioAdapter == null) {
            mAudioAdapter = object : BaseRecyclerAdapter<AudioItem>(this, audioLists, R.layout.item_audio) {
                override fun convert(holder: BaseRecyclerHolder?, item: AudioItem, position: Int, isScrolling: Boolean) {
                    var titleView = holder?.getView<TextView>(R.id.title)

                    holder?.setText(R.id.name, item?.name)
                    holder?.setText(R.id.teacherName, "主讲人:${item?.anchorName}")
                    holder?.setText(R.id.time, "${CommonUtils.getFormattedTime(item.currentPlayTime)}/${item?.totalDate}")
                    holder?.setText(R.id.date, "${item?.belongTime}")
                    holder?.setImageResource(R.id.audioState, R.drawable.maker_pause)
                    when (item.currentType) {
                        // 0 默认状态 1播放状态   2 暂停状态 3 加载中,
                        0 -> {
                            holder?.setImageResource(R.id.audioState, R.drawable.maker_pause)
                            mOperatingAnim.cancel()
                            holder?.getView<ImageView>(R.id.audioState)?.clearAnimation()
                        }
                        1 -> {
                            holder?.setImageResource(R.id.audioState, R.drawable.maker_play)
                            mOperatingAnim.cancel()
                            holder?.getView<ImageView>(R.id.audioState)?.clearAnimation()
                        }
                        2 -> {
                            holder?.setImageResource(R.id.audioState, R.drawable.maker_pause)
                            mOperatingAnim.cancel()
                            holder?.getView<ImageView>(R.id.audioState)?.clearAnimation()
                        }
                        3 -> {
                            holder?.setImageResource(R.id.audioState, R.drawable.audio_loading_small)
                            holder?.getView<ImageView>(R.id.audioState)?.startAnimation(mOperatingAnim)

                        }

                        //                    holder?.itemView?.setOnKeyListener()
                    }

                    holder?.itemView?.setOnFocusChangeListener { _, hasFocus ->
                        if (hasFocus && position == audioLists.size - 1) {
                            lastPosition = position
                            if (mCurrentPage < mTotalPage) {
                                mCurrentPage++
                                getAudioData(mCurrentPage)
                            }
                        }
                        holder.getView<TextView>(R.id.teacherName).isSelected = hasFocus
                    }



                    holder?.itemView?.setOnClickListener {
                        if (lastClickPosition == -1) {
                            lastClickPosition = position
                            musicBinder?.playMusic(item.vioceURL)
                            item.currentType = 3
                            notifyItemChanged(lastClickPosition)

                        } else if (lastClickPosition == position) {
                            if (musicBinder != null) {
                                if (musicBinder!!.isPlaying) {
                                    musicBinder?.pause()
                                    item.currentType = 2
                                } else {
                                    musicBinder?.start()
                                    item.currentType = 1
                                }
                            }

                            notifyItemChanged(lastClickPosition)
                        } else {
                            audioLists[lastClickPosition].currentType = 0
                            notifyItemChanged(lastClickPosition)

                            lastClickPosition = position
                            musicBinder?.playMusic(item.vioceURL)
                            item.currentType = 3
                            notifyItemChanged(lastClickPosition)
                        }

                    }

                    holder?.itemView?.setOnKeyListener { _, keyCode, event ->
                        if (event.action == KeyEvent.ACTION_DOWN) {
                            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                                val visiblePosition = (courseRecyclerView.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
                                val visiblePositionView = (courseRecyclerView.layoutManager as GridLayoutManager).findViewByPosition(visiblePosition)
                                val itemView = visiblePositionView?.findViewById<FrameLayout>(R.id.itemView)
                                itemView?.requestFocus()
                                true
                            } else {
                                keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                            }

                        } else {
                            false
                        }

                    }

//                    holder?.itemView?.setOnKeyListener()
                }
            }
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = mAudioAdapter
            recyclerView.itemAnimator = null
            recyclerView.setHasFixedSize(true)

        } else {
//            mAudioAdapter?.notifyDataSetChanged()
            mAudioAdapter?.notifyItemRangeInserted(lastPosition + 1, 1)

        }


    }

    override fun initViewModel() = createViewModel(this, MakerIndexViewModel::class.java)

    override fun initVariableId() = BR.viewModel

    private inner class MusicInfoReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (lastClickPosition == -1) {
                return
            }
            if (action == GlobalConsts.ACTION_UPDATE_PROGRESS) {
                val current = intent.getIntExtra("current", 0)
                val total = intent.getIntExtra("total", 0)

                audioLists[lastClickPosition].currentPlayTime = current
                mAudioAdapter?.notifyItemChanged(lastClickPosition)


            } else if (GlobalConsts.ACTION_LOCAL_MUSIC.equals(action)) {
            } else if (GlobalConsts.ACTION_PAUSE_MUSIC.equals(action)) {
                //音乐暂停播放
                audioLists[lastClickPosition].currentType = 2
                mAudioAdapter?.notifyItemChanged(lastClickPosition)

            } else if (GlobalConsts.ACTION_STATR_MUSIC.equals(action)) {
                //只有点暂停和播放按钮时
            } else if (GlobalConsts.ACTION_ONLINE_MUSIC.equals(action)) {
            } else if (GlobalConsts.ACTION_NEXT_MUSIC.equals(action)) {
            } else if (GlobalConsts.ACTION_MUSIC_STARTED.equals(action)) {

                audioLists[lastClickPosition].currentType = 1
                mAudioAdapter?.notifyItemChanged(lastClickPosition)

            } else if (GlobalConsts.ACTION_COMPLETE_MUSIC.equals(action)) {
                //播放完成
                // 设置当前的为完成状态
                audioLists[lastClickPosition].currentType = 0
                mAudioAdapter?.notifyItemChanged(lastClickPosition)
                //  如果不是最后一条 就继续播放下一个音频
                if (lastClickPosition + 1 < audioLists.size) {
                    lastClickPosition += 1
                    audioLists[lastClickPosition].currentType = 3
                    musicBinder?.playMusic(audioLists[lastClickPosition].vioceURL)
                    mAudioAdapter?.notifyItemChanged(lastClickPosition)

                    //将下一个音频设置为获取焦点

                    recyclerView.smoothScrollToPosition(lastClickPosition)
                    Handler().postDelayed({
                        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val findViewByPosition = linearLayoutManager.findViewByPosition(lastClickPosition)
                        findViewByPosition?.findViewById<LinearLayout>(R.id.audioContainer)?.requestFocus()
                    }, 200)

                }


            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (lastClickPosition != -1) {
            audioLists[lastClickPosition].currentType = 2
            mAudioAdapter?.notifyItemChanged(lastClickPosition)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        musicBinder?.stop()
    }
}