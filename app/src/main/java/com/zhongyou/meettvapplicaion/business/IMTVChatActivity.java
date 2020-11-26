package com.zhongyou.meettvapplicaion.business;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.im.NoPluginConversationFragment;
import com.zhongyou.meettvapplicaion.im.NoPluginTVConversationFragment;

import io.rong.imkit.widget.adapter.MessageListAdapter;

public class IMTVChatActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imtvchat);
		try {
			FragmentManager fragmentManage = getSupportFragmentManager();
			NoPluginTVConversationFragment fragement = (NoPluginTVConversationFragment) fragmentManage.findFragmentById(R.id.conversation);

			fragement.setGroupID(getIntent().getStringExtra("groupId"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Constant.getIsPadApplication()){
			findViewById(R.id.close).setVisibility(View.VISIBLE);
		}else {
			findViewById(R.id.close).setVisibility(View.GONE);
		}

		findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}


}
