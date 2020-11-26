package com.zhongyou.meettvapplicaion.business;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.zhongyou.meettvapplicaion.BaseApplication;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.im.NoPluginConversationFragment;
import com.zhongyou.meettvapplicaion.utils.DeviceUtil;

import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

public class IMChatActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imchat);
		try {
			FragmentManager fragmentManage = getSupportFragmentManager();
			NoPluginConversationFragment fragement = (NoPluginConversationFragment) fragmentManage.findFragmentById(R.id.conversation);
			Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
					.appendPath("conversation").appendPath("group")
					.appendQueryParameter("targetId", getIntent().getStringExtra("groupId")).build();

			if (fragement != null) {
				fragement.setUri(uri);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Constant.getIsPadApplication()){
			findViewById(R.id.close).setVisibility(View.VISIBLE);
		}else {
			findViewById(R.id.close).setVisibility(View.VISIBLE);
		}

		findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}


}
