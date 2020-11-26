package com.zhongyou.meettvapplicaion.im;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.zhongyou.meettvapplicaion.R;

import io.agora.openlive.ui.BaseActivity;
import io.rong.imkit.fragment.SubConversationListFragment;

public class SubConversationListActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.conversation_activity_subconversation_list);
		FragmentManager fragmentManage = getSupportFragmentManager();
		SubConversationListFragment fragement = (SubConversationListFragment) fragmentManage.findFragmentById(R.id.subconversationlist);
		Uri uri = Uri.parse("rong://" + getApplication().getApplicationInfo().packageName).buildUpon()
				.appendPath("subconversationlist")
				.appendQueryParameter("type", "121212")
				.build();
		fragement.setUri(uri);

		Intent intent = getIntent();
		if (intent.getData() == null) {
			return;
		}
		//聚合会话参数
		String type = intent.getData().getQueryParameter("type");

		if (type == null)
			return;
	}

	@Override
	protected void initUIandEvent() {

	}

	@Override
	protected void deInitUIandEvent() {

	}


}
