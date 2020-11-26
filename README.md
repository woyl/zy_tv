
GuideSale
===============





### Application下的方法介绍
1. isPadApplication()

```
根据多渠道打包 判断是否是平板(触摸屏)
通过build variants来配置默认debug包的渠道

```

2. 屏幕适配

```
 implementation 'me.jessyan:autosize:1.1.2'
 //在AndroidManifest.xml中的applicaion节点下写设计尺寸 使用px测量(平板、电视 注意横向竖向与手机不同)
   <meta-data
             android:name="design_width_in_dp"
             android:value="1920"/>
   <meta-data
            android:name="design_height_in_dp"
            android:value="1080"/>

 //在applicaion.java文件中忽略对dp的修改 并开启副单位

 AutoSizeConfig.getInstance().getUnitsManager()
 				.setSupportDP(false)
 				.setSupportSP(false)
 				.setSupportSubunits(Subunits.PT);

// 使用的时候 在px模式下测量 例如测量出来的是20px
//在布局文件中就使用20px
 android:layout_width="20px"

```

3. 首页中焦点控制


```
tvOpen.setFocusable(true);
tvInvited.setFocusable(true);
	if (Constant.isPadApplicaion) {
		    tvInvited.setOnClickListener(v -> {
			tvInvited.setFocusable(true);
			tvInvited.setFocusableInTouchMode(true);
			tvInvited.requestFocus();
		});

		    tvOpen.setOnClickListener(v -> {
		    tvOpen.setFocusable(true);
			tvOpen.setFocusableInTouchMode(true);
			tvOpen.requestFocus();
		});
    }
```


### 商学院中方法

1. 给adapter设置点击事件

```
adapter..setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(RecyclerView parent, View view, int position) {
        labelRecyclerView.setItemSelected(position);
    }
});
```

1. 在点击事件中设置某一个item被选中

```
labelRecyclerView.setItemSelected(position);

```