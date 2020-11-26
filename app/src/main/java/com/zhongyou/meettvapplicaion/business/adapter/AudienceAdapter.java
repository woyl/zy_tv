package com.zhongyou.meettvapplicaion.business.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.AudienceVideo;
import com.zhongyou.meettvapplicaion.interfaces.CallbackListener;
import com.zhongyou.meettvapplicaion.interfaces.CallbackTwoListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by whatisjava on 17-11-22.
 */

public class AudienceAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<AudienceVideo> audiences;
	private CallbackTwoListener<Integer,Integer> respontListences;
	private CallbackTwoListener<Integer,Integer> respontListencesStopSpeak;
	private CallbackTwoListener<Integer,Integer> respontListencesRemovePerson;
	private CallbackListener<Boolean> callbackListener;

	private int uid = -1;
	private int uidState = -1;

	public AudienceAdapter(Context context, ArrayList<AudienceVideo> audiences) {
		this.context = context;
		this.audiences = audiences;
		inflater = LayoutInflater.from(context);
	}

	public void setRespontListences(CallbackTwoListener<Integer,Integer> respontListences) {
		this.respontListences = respontListences;
	}

	public void setRespontListencesStopSpeak(CallbackTwoListener<Integer,Integer> respontListences) {
		this.respontListencesStopSpeak = respontListences;
	}
	public void setRespontListencesRemovePerson(CallbackTwoListener<Integer,Integer> respontListences) {
		this.respontListencesRemovePerson = respontListences;
	}
	public void setCallbackListener(CallbackListener<Boolean> callbackListener) {
		this.callbackListener = callbackListener;
	}



	@Override
	public int getCount() {
		return audiences != null ? audiences.size() : 0;
	}

	@Override
	public Object getItem(int i) {
		return audiences != null ? audiences.get(i) : null;
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			if (Constant.isPadApplicaion) {
				convertView = inflater.inflate(R.layout.item_audience_pad, null);
			} else {
				convertView = inflater.inflate(R.layout.item_audience, null);
			}
			viewHolder = new ViewHolder();
			viewHolder.audienceLayout = convertView.findViewById(R.id.stop_audience);
			viewHolder.nameText = convertView.findViewById(R.id.audience_name);
			viewHolder.talkButton = convertView.findViewById(R.id.talk);
			viewHolder.checkButton = convertView.findViewById(R.id.check);
			viewHolder.handsupImage = convertView.findViewById(R.id.handsup);
			viewHolder.clientTypeImage = convertView.findViewById(R.id.clientType);
			viewHolder.callingText = convertView.findViewById(R.id.calling);
			viewHolder.auditStatusImage = convertView.findViewById(R.id.auditStatus);
			viewHolder.postTypeNameText = convertView.findViewById(R.id.postTypeName);
			viewHolder.remove_person = convertView.findViewById(R.id.remove_person);
			viewHolder.stop_speak = convertView.findViewById(R.id.stop_speak);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		AudienceVideo audience = audiences.get(position);


		if (TextUtils.isEmpty(audience.getName())) {
			if (!TextUtils.isEmpty(audience.getUname())) {
				viewHolder.nameText.setText((position + 1) + ". " + audience.getUname());
			} else {
				viewHolder.nameText.setText((position + 1) + ". " + "");
			}

		} else {
			if (!TextUtils.isEmpty(audience.getName())) {
				viewHolder.nameText.setText((position + 1) + ". " + audience.getName());
			} else {
				viewHolder.nameText.setText((position + 1) + ". " + "");
			}
		}
		if (audience.getAuditStatus() == 1) {
			viewHolder.auditStatusImage.setVisibility(View.VISIBLE);
		} else {
			viewHolder.auditStatusImage.setVisibility(View.GONE);
		}

		if (String.valueOf(audience.getUid()).startsWith("1")) {
			viewHolder.clientTypeImage.setVisibility(View.VISIBLE);
			viewHolder.clientTypeImage.setImageResource(R.drawable.ic_lable_tv);
		} else if (String.valueOf(audience.getUid()).startsWith("2")) {
			viewHolder.clientTypeImage.setVisibility(View.VISIBLE);
			viewHolder.clientTypeImage.setImageResource(R.drawable.ic_lable_mobile);
		} else if (String.valueOf(audience.getUid()).startsWith("3")) {
			viewHolder.clientTypeImage.setVisibility(View.VISIBLE);
			viewHolder.clientTypeImage.setImageResource(R.drawable.ic_lable_tv);
		} else if (String.valueOf(audience.getUid()).startsWith("4")) {
			viewHolder.clientTypeImage.setVisibility(View.VISIBLE);
			viewHolder.clientTypeImage.setImageResource(R.drawable.ic_lable_mobile);
		} else {
			viewHolder.clientTypeImage.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(audience.getPostTypeName())) {
			viewHolder.postTypeNameText.setText(audience.getPostTypeName());
			viewHolder.postTypeNameText.setVisibility(View.VISIBLE);
		} else {
			viewHolder.postTypeNameText.setVisibility(View.GONE);
		}

		if (audience.isHandsUp()) {
			viewHolder.handsupImage.setVisibility(View.VISIBLE);
		} else {
			viewHolder.handsupImage.setVisibility(View.INVISIBLE);
		}

		if (audience.getCallStatus() == 2) {//电脑
			viewHolder.callingText.setVisibility(View.VISIBLE);
			viewHolder.callingText.setText("连麦中...");
			viewHolder.talkButton.setVisibility(View.GONE);
		} else if (audience.getCallStatus() == 1) {
			viewHolder.callingText.setVisibility(View.VISIBLE);
			viewHolder.callingText.setText("正在连麦...");
			viewHolder.talkButton.setVisibility(View.GONE);
		} else {
			viewHolder.callingText.setVisibility(View.GONE);
			viewHolder.talkButton.setVisibility(View.VISIBLE);
		}

		if (audience.getStopSpeak() == 1) {
			viewHolder.stop_speak.setText("已禁用");
		} else {
			viewHolder.stop_speak.setText("禁用");
		}

		if (uid != -1 && uidState != -1 && uid == audience.getUid()) {
			if (uidState == 1) {
				viewHolder.stop_speak.setText("已禁用");
			} else  {
				viewHolder.stop_speak.setText("禁用");
			}
		}

		viewHolder.stop_speak.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
					if (position == 0) {
						callbackListener.callback(true);
						return true;
					}
					return false;
				}else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					return false;
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
					return false;
				}else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
					if (audience.getCallStatus() == 2) {
						viewHolder.remove_person.setFocusable(true);
						viewHolder.remove_person.requestFocus();
						viewHolder.remove_person.setFocusableInTouchMode(true);
						viewHolder.stop_speak.setNextFocusRightId(R.id.remove_person);
					} else {
						viewHolder.talkButton.setFocusable(true);
						viewHolder.talkButton.requestFocus();
						viewHolder.talkButton.setFocusableInTouchMode(true);
					}
					viewHolder.stop_speak.setFocusable(false);
					return true;
				}else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					respontListencesStopSpeak.callback(position,audience.getUid());
					return true;
				}
				return false;
			}
		});


		viewHolder.talkButton.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					if (position == 0) {
						callbackListener.callback(true);
						return true;
					}
					return false;
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					return false;
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
					viewHolder.stop_speak.setFocusable(true);
					viewHolder.stop_speak.requestFocus();
					viewHolder.stop_speak.setFocusableInTouchMode(true);
					viewHolder.talkButton.setFocusable(false);
					return true;
				}else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
					viewHolder.remove_person.setFocusable(true);
					viewHolder.remove_person.requestFocus();
					viewHolder.remove_person.setFocusableInTouchMode(true);
					viewHolder.talkButton.clearFocus();
					viewHolder.talkButton.setFocusable(false);
					return true;
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					respontListences.callback(position,audience.getUid());
					return true;
				}
				return false;
			}
		});

		viewHolder.remove_person.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.i("mLt_cj",".,,,,,remove_person...keycode............"+keyCode + "------------------------"+event.getAction());
				if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					if (position == 0) {
						callbackListener.callback(true);
						return true;
					}
					return false;
				}else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					return false;
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
					if (audience.getCallStatus() == 2) {
						viewHolder.stop_speak.setFocusable(true);
						viewHolder.stop_speak.setFocusableInTouchMode(true);
						viewHolder.stop_speak.requestFocus();
						viewHolder.remove_person.setNextFocusLeftId(R.id.stop_speak);
					} else {
						viewHolder.talkButton.setFocusable(true);
						viewHolder.talkButton.setFocusableInTouchMode(true);
						viewHolder.talkButton.requestFocus();
					}
					viewHolder.remove_person.setFocusable(false);
					return true;
				}else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					Log.i("mLt_cj","....remove_person...KEYCODE_DPAD_RIGHT............"+keyCode);
					return false;
				}else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					respontListencesRemovePerson.callback(position,audience.getUid());
					return true;
				}

				return false;
			}
		});

		viewHolder.talkButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					viewHolder.talkButton.setTextColor(context.getResources().getColor(R.color.red));
				}else {
					viewHolder.talkButton.setTextColor(context.getResources().getColor(R.color.c_f62c64));
				}
			}
		});

		viewHolder.remove_person.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					viewHolder.remove_person.setTextColor(context.getResources().getColor(R.color.red));
				}else {
					viewHolder.remove_person.setTextColor(context.getResources().getColor(R.color.c_f62c64));
				}
			}
		});

		viewHolder.stop_speak.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					viewHolder.stop_speak.setTextColor(context.getResources().getColor(R.color.red));
				}else {
					viewHolder.stop_speak.setTextColor(context.getResources().getColor(R.color.c_f62c64));
				}
			}
		});

		return convertView;
	}

	class ViewHolder {
		LinearLayout audienceLayout;
		ImageView handsupImage, auditStatusImage, clientTypeImage;
		TextView nameText, callingText, postTypeNameText, talkButton,stop_speak,remove_person;
		Button checkButton;
	}

	public void setData(ArrayList<AudienceVideo> audiences) {
		this.audiences = audiences;
		Collections.sort(this.audiences, (o1, o2) -> {
			if (o1.isHandsUp() && !o2.isHandsUp()) {
				return -1;
			} else if (!o1.isHandsUp() && o2.isHandsUp()) {
				return 1;
			} else {
				return 0;
			}
		});
		notifyDataSetChanged();
	}

	public ArrayList<AudienceVideo> getData() {
		return audiences;
	}

	public void setUid (int uid,int uidState) {
		this.uid = uid;
		this.uidState = uidState;
		notifyDataSetChanged();
	}

}
