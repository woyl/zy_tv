package com.zhongyou.meettvapplicaion.maker

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.zhongyou.meet.mobile.ameeting.BaseMVVMActivity
import com.zhongyou.meettvapplicaion.BR
import com.zhongyou.meettvapplicaion.R
import com.zhongyou.meettvapplicaion.business.adapter.BaseRecyclerAdapter
import com.zhongyou.meettvapplicaion.business.adapter.BaseRecyclerHolder
import com.zhongyou.meettvapplicaion.databinding.ActivityNotifyDetailBinding
import com.zhongyou.meettvapplicaion.entity.NotifyDetail
import com.zhongyou.meettvapplicaion.network.RxSchedulersHelper
import com.zhongyou.meettvapplicaion.network.RxSubscriber
import com.zhongyou.meettvapplicaion.persistence.Preferences
import com.zhongyou.meettvapplicaion.utils.MyDialog
import com.zhongyou.meettvapplicaion.utils.ToastUtils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_notify_detail.*

class NotifyDetailActivity : BaseMVVMActivity<ActivityNotifyDetailBinding, NotifyDetailViewModel>() {

    var totalPage: Int = 0
    var notifyDetailList = ArrayList<NotifyDetail>()
    var mCurrentPage = 1

    var mAdapter: BaseRecyclerAdapter<NotifyDetail>? = null
    override fun initData() {
        viewModel.title.value = intent.getStringExtra("title")
        viewModel.type.value = intent.getStringExtra("type")
        getData(Preferences.getUserId(), viewModel.type.value!!, 1)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_notify_detail
    }

    override fun initViewModel(): NotifyDetailViewModel {
        viewModel = createViewModel(this, NotifyDetailViewModel::class.java)
        return viewModel
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }


    fun getData(userID: String, type: String, pageNo: Int = 1) {
        mApiService.getMessageDetailByUserId(userID, type, pageNo)
                .compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<JSONObject>() {

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                    }

                    override fun _onNext(t: JSONObject?) {
                        t?.let {
                            if (t.getInteger("errcode") == 0) {
                                totalPage = t.getJSONObject("data").getInteger("totalPage")
                                var jsonArray = t.getJSONObject("data").getJSONArray("list")
                                var parseArray = JSON.parseArray(jsonArray.toJSONString(), NotifyDetail::class.java)
                                if (pageNo == 1) {
                                    notifyDetailList.clear()
                                }
                                notifyDetailList.addAll(parseArray)
                                setAdapter();
                            } else {
                                ToastUtils.showToast(t.getString("errmsg"))
                            }
                        }
                    }

                })
    }

    fun getActivity(): Activity {
        return this
    }

    private fun setAdapter() {
        if (mAdapter == null) {
            mAdapter = object : BaseRecyclerAdapter<NotifyDetail>(this, notifyDetailList, R.layout.item_notify) {
                override fun convert(holder: BaseRecyclerHolder, item: NotifyDetail, position: Int, isScrolling: Boolean) {
                    holder.setText(R.id.content, item.notice)
                    holder.setText(R.id.time, item.time)
                    if (item.type == 1) {
                        holder.setImageResource(R.id.logo, R.drawable.class_notify)
                    } else if (item.type == 2) {
                        holder.setImageResource(R.id.logo, R.drawable.maker_notify_msg)
                    } else if (item.type == 3) {
                        holder.setImageResource(R.id.logo, R.drawable.system_notify)
                    }

                    if (item.type == 1) {
                        holder.itemView.setOnClickListener {
                            DialogUtils.getInstance(applicationContext).showNormalJoinMeetingDialog(getActivity(), item.meetingId, item.isToken)
                        }
                    }

                    holder.itemView.setOnFocusChangeListener { _, hasFocus ->
                        if (hasFocus && position == notifyDetailList.size - 1) {
                            if (mCurrentPage < totalPage) {
                                mCurrentPage++
                                getData(Preferences.getUserId(), viewModel.type.value!!, mCurrentPage)
                            }
                        }
                    }
                }
            }
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = mAdapter
        } else {
            mAdapter?.notifyItemRangeInserted(notifyDetailList.size, 1)
        }
    }
}