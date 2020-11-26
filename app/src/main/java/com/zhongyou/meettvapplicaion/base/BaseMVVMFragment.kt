package com.zhongyou.meettvapplicaion.base

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhongyou.meettvapplicaion.network.ApiService
import com.zhongyou.meettvapplicaion.network.HttpsRequest

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/1 2:44 PM.
 * @
 */
abstract class BaseMVVMFragment<V : ViewDataBinding, VM : BaseViewModel> : Fragment() {
    protected lateinit var mBinding: V
    protected lateinit var mViewModel: VM
    private var fragmentIsVisibleToUser = false
    private var isPrepared = false
    private var isFirstLoad = true

    protected val mApiService: ApiService by lazy {
        HttpsRequest.provideClientApi()
    }

    /**
     * 要想fragment依附于activity，请重写此方法，并返回true
     * （fragment xml根布局为，merge时）
     *
     * @return
     */
    protected open fun getAttachToParent(): Boolean {
        return false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, initContentView(), container, getAttachToParent())
        mBinding.setVariable(initVariableId(), initViewModel())
        mBinding.executePendingBindings()
        mBinding.lifecycleOwner = this
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isPrepared = true
        initViewObservable()
        lazyLoad()
        commonLoad()
    }

    open fun lazyLoad() {
        if (!isPrepared || !fragmentIsVisibleToUser || !isFirstLoad) { //if (!isAdded() || !isVisible || !isFirstLoad) {
            return
        }
        isFirstLoad = false
        initData()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint) {
            fragmentIsVisibleToUser = true
            onVisible()
        } else {
            fragmentIsVisibleToUser = false
            onInvisible()
        }
    }

    open fun onInvisible() {}

    open fun onVisible() {
        lazyLoad()
    }

    open fun commonLoad() {

    }

    /**
     * fragment可见时才加载数据data
     */
    open fun initData() {

    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    abstract fun initContentView(): Int

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    abstract fun initVariableId(): Int

    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    abstract fun initViewModel(): VM

    open fun initViewObservable() {

    }

    /**
     * 创建ViewModel
     *
     * @param cls
     * @param <T>
     * @return
    </T> */
    open fun <T : AndroidViewModel?> createViewModel(fragment: Fragment?, cls: Class<T>?): T {
        return ViewModelProviders.of(fragment!!)[cls!!]
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding?.unbind()
        mViewModel?.onCleared()
    }
}