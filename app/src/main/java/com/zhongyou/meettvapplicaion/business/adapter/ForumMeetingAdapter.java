package com.zhongyou.meettvapplicaion.business.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.PageData;
import com.zhongyou.meettvapplicaion.utils.BitmapUtil;
import com.zhongyou.meettvapplicaion.utils.StringUtils;
import com.squareup.picasso.Picasso;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Dongce
 * create time: 2018/11/20
 */
public class ForumMeetingAdapter extends RecyclerView.Adapter<ForumMeetingAdapter.ForumMeetingHolder> {

    private Context mContext;
    private LinkedList<PageData> forums = new LinkedList<>();
    private OnItemClickListener listener;

    public ForumMeetingAdapter(Context context, OnItemClickListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    public void addDataAtFirst(LinkedList<PageData> forums) {
        forums = cleaningData(forums);

        this.forums.addAll(0, forums);
        notifyItemRangeInserted(0, forums.size());
    }

    public void addDataAtLast(LinkedList<PageData> forums) {
        forums = cleaningData(forums);

        this.forums.add(forums.get(0));
        notifyItemInserted(this.forums.size() - 1);
    }

    public void clearData() {
        this.forums.clear();
        notifyDataSetChanged();
    }

    public void revocationData() {
        PageData pageData = this.forums.getLast();
        pageData.setMsgType(PageData.MSGTYPE_WITHDRAW);
        pageData.setContent(mContext.getResources().getString(R.string.forun_revocation_message));

        notifyItemChanged(this.forums.size() - 1);
    }

    /**
     * 2018.11.28根据后台新增功能：处理消息类型：msgType属于聚合数据的情况，属于聚合数据的，从数据列表中移除掉
     *
     * @param forums
     * @return
     */
    private LinkedList<PageData> cleaningData(LinkedList<PageData> forums) {
        for (Iterator<PageData> pageDataIterator = forums.iterator(); pageDataIterator.hasNext(); ) {
            PageData pageData = pageDataIterator.next();
            if (pageData.getMsgType() == PageData.MSGTYPE_AGGREGATION) {
                pageDataIterator.remove();
            }
        }
        return forums;
    }

    @Override
    public int getItemCount() {
        return forums.size();
    }

    @NonNull
    @Override
    public ForumMeetingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_forum_msg_layout, parent, false);
        return new ForumMeetingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForumMeetingHolder viewHolder, int position) {
        final PageData pageData = forums.get(position);

        ForumMeetingHolder holder = (ForumMeetingHolder) viewHolder;

        if (pageData.getMsgType() == PageData.MSGTYPE_WITHDRAW) {
            //消息类型
            switch (pageData.getMsgType()) {
                case PageData.MSGTYPE_NORMAL:
                    break;
                case PageData.MSGTYPE_WITHDRAW:
                    holder.item_forum_content.setVisibility(View.VISIBLE);
                    holder.item_forum_image.setVisibility(View.GONE);

                    holder.item_forum_content.setText(StringUtils.textColoring(mContext, "", mContext.getString(R.string.forun_revocation_message), R.color.c_FF7FBAFF));
                    break;
                case PageData.MSGTYPE_AGGREGATION:
                    break;
            }
        } else {
            //内容类型
            switch (pageData.getType()) {
                case PageData.TYPE_TEXT:
                    holder.item_forum_content.setVisibility(View.VISIBLE);
                    holder.item_forum_image.setVisibility(View.GONE);
                    holder.item_forum_content.setText(pageData.getContent());
                    break;
                case PageData.TYPE_IMAGE:
                    holder.item_forum_content.setVisibility(View.GONE);
                    holder.item_forum_image.setVisibility(View.VISIBLE);

                    Picasso.with(mContext)
                            .load(pageData.getContent())
                            .transform(BitmapUtil.getTransformation(holder.item_forum_image))
                            .placeholder(R.drawable.item_forum_img_loading)
                            .error(R.drawable.item_forum_img_error)
                            .config(Bitmap.Config.RGB_565)
                            .into(holder.item_forum_image);
                    break;
            }
        }
        holder.item_forum_username.setText(pageData.getUserName() + "：");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(v, pageData);
                }
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(View view, PageData pageData);
    }

    public class ForumMeetingHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView item_forum_username, item_forum_content;
        ImageView item_forum_image;

        ForumMeetingHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            item_forum_username = itemView.findViewById(R.id.item_forum_username);
            item_forum_image = itemView.findViewById(R.id.item_forum_image);
            item_forum_content = itemView.findViewById(R.id.item_forum_content);
        }
    }
}
