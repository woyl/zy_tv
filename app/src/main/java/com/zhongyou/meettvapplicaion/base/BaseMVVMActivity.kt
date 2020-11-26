package com.zhongyou.meet.mobile.ameeting

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zhongyou.meettvapplicaion.base.ActivityCollector
import com.zhongyou.meettvapplicaion.base.BaseViewModel
import com.zhongyou.meettvapplicaion.base.MyObserver
import com.zhongyou.meettvapplicaion.network.ApiService
import com.zhongyou.meettvapplicaion.network.HttpsRequest
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/1 1:09 PM.
 * @
 */
abstract class BaseMVVMActivity<V : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity(), AnkoLogger {


    protected lateinit var mBinding: V

    protected var viewModelId = 0

    lateinit var viewModel: VM

    protected val mApiService: ApiService by lazy {
        HttpsRequest.provideClientApi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMVVM()
        initViewObservable()
        initData()
        ActivityCollector.addActivity(this)
        initListener()
    }

    open fun initListener() {

    }

    abstract fun initData()

    private fun initMVVM() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())

        viewModelId = initVariableId()
        viewModel = initViewModel()
        //关联ViewModel
        mBinding.setVariable(viewModelId, viewModel)
        //支持LiveData绑定xml，数据改变，UI自动会更新
        mBinding.executePendingBindings()
        mBinding.lifecycleOwner = this
        //让ViewModel拥有View的生命周期感应
//        lifecycle.addObserver(MyObserver())
    }

    abstract fun getLayoutId(): Int

    abstract fun initViewModel(): VM

    abstract fun initVariableId(): Int

    open fun initViewObservable() {

    }

    open fun showMessage(msg: String) {
        toast(msg)
    }


    open fun <T : ViewModel> createViewModel(activity: AppCompatActivity, cls: Class<T>): T {
        return ViewModelProviders.of(activity)[cls]
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
        viewModel.onCleared()
//        ActivityCollector.removeActivity(this)

    }
}