package com.zhongyou.meettvapplicaion.ui

import com.alibaba.fastjson.JSON
import com.android.databinding.library.baseAdapters.BR
import com.zhongyou.meet.mobile.ameeting.BaseMVVMActivity
import com.zhongyou.meettvapplicaion.R
import com.zhongyou.meettvapplicaion.databinding.ActivityDemoBinding
import com.zhongyou.meettvapplicaion.entity.PPT
import org.jetbrains.anko.info


class DemoActivity : BaseMVVMActivity<ActivityDemoBinding, DemoViewModel>() {
    lateinit var demoViewModel: DemoViewModel
    override fun initData() {
        demoViewModel.getData()

    }

    override fun initViewObservable() {
        super.initViewObservable()
        /* initViewModel().getListMutableLiveData().observe(this, Observer<List<PPT>>() {
             info {
                 JSON.toJSONString(it)
             }
             adapter?.datas = it
         })*/

    }


    override fun getLayoutId(): Int {
        return R.layout.activity_demo
    }

    override fun initViewModel(): DemoViewModel {
        demoViewModel = createViewModel(this, DemoViewModel::class.java)

        return demoViewModel
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }
}
