package com.zhongyou.meettvapplicaion.maker

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.zhongyou.meet.mobile.ameeting.BaseMVVMActivity
import com.zhongyou.meettvapplicaion.BR
import com.zhongyou.meettvapplicaion.BaseApplication
import com.zhongyou.meettvapplicaion.R
import com.zhongyou.meettvapplicaion.databinding.ActivityNotifyBinding
import com.zhongyou.meettvapplicaion.entity.Notify
import kotlinx.android.synthetic.main.activity_notify.*

class NotifyActivity : BaseMVVMActivity<ActivityNotifyBinding, NotifyViewModel>() {

    override fun initVariableId(): Int = BR.viewModel


    override fun getLayoutId(): Int = R.layout.activity_notify

    override fun initViewModel(): NotifyViewModel {
        viewModel = createViewModel(this, NotifyViewModel::class.java)
        return viewModel
    }

    override fun initData() {
        classInformation.requestFocus()
        viewModel.getNotifyData()

    }

    override fun initViewObservable() {
        viewModel.getNotifyList().observe(this, Observer<List<Notify>>() {
            it?.let {
                for (notify in it) {
                    when (notify.type) {
                        1 -> {//教室通知
//                            viewModel.classNotifyCount.value = if (notify.number > 99) "99+" else notify.number.toString()
                            viewModel.classNotifyCount.value = "0"

                        }
                        2 -> {//创客通知
//                            viewModel.makerNotifyCount.value = if (notify.number > 99) "99+" else notify.number.toString()
                            viewModel.makerNotifyCount.value = "0"
                        }
                        3 -> {//系统通知
//                            viewModel.systemNotifyCount.value = if (notify.number > 99) "99+" else notify.number.toString()
                            viewModel.systemNotifyCount.value = "0"
                        }
                    }
                }
            }
        })
    }

    override fun initListener() {
        classInformation.setOnClickListener {
            startActivity(Intent(this, NotifyDetailActivity::class.java).putExtra("title", "教室通知").putExtra("type", "1"))
        }
        makerInformation.setOnClickListener {
            startActivity(Intent(this, NotifyDetailActivity::class.java).putExtra("title", "创客通知").putExtra("type", "2"))
        }
        systemInformation.setOnClickListener {
            startActivity(Intent(this, NotifyDetailActivity::class.java).putExtra("title", "系统通知").putExtra("type", "3"))
        }
    }



}