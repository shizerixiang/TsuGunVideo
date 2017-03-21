package com.tsugun.tsugunvideo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsugun.tsugunvideo.R;
import com.tsugun.tsugunvideo.entity.MediaFileInfo;
import com.tsugun.tsugunvideo.util.ConverterUtil;
import com.tsugun.tsugunvideo.util.TsuGunThumbLoader;

import java.util.List;

/**
 * 视频列表recyclerView的适配器
 * Created by shize on 2017/3/5.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.RecyclerViewViewHolder> {

    private LayoutInflater mInflater;
    private List<MediaFileInfo> mData;
    private TsuGunThumbLoader mTsuGunThumbLoader; // 缩略图加载器
    private Context mContext;
    private OnVideoItemClickListener mOnClickListener;

    public VideoListAdapter(Context context, List<MediaFileInfo> data) {
        mContext = context;
        mData = data;
        mInflater = LayoutInflater.from(context);
        mTsuGunThumbLoader = new TsuGunThumbLoader(mData);
    }

    /**
     * 设置点击事件
     */
    public void setOnVideoItemClickListener(OnVideoItemClickListener onVideoItemClickListener) {
        mOnClickListener = onVideoItemClickListener;
    }

    @Override
    public RecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewViewHolder(mInflater.inflate(R.layout.item_video_path, parent, false));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerViewViewHolder holder, final int position) {
        holder.mVideoTitle.setText(mData.get(position).getTitle());
        holder.mVideoSize.setText(ConverterUtil.getConvertedSize(
                mData.get(position).getSize()));
        holder.mVideoRecord.setText(ConverterUtil.getConvertedTime(
                (int) mData.get(position).getDuration()));
        holder.mVideoThumbnail.setImageDrawable(mContext.getDrawable(R.drawable.img_vector_loading));
        // 添加验证缩略图标签，防止错位
        holder.mVideoThumbnail.setTag(mData.get(position).getUrl());
        mTsuGunThumbLoader.getVideoThumb(mData.get(position).getUrl(), holder.mVideoThumbnail);

        if (mOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnClickListener.onItemLongClick(holder.itemView, holder.getAdapterPosition());
                    return false;
                }
            });
        }
    }

    /**
     * 添加一个item
     *
     * @param position 添加位置
     * @param info     添加的视频信息
     */
    public void addVideoItem(int position, MediaFileInfo info) {
        mData.add(position, info);
        notifyItemInserted(position);
    }

    /**
     * 删除一个item
     *
     * @param position item位置
     */
    public void removeVideoItem(int position) {
        if (mData.size() > position) {
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * 清除所有数据
     */
    public void clearVideoItems() {
        for (int i = mData.size(); i >= 0; i--) {
            removeVideoItem(i);
        }
    }

    class RecyclerViewViewHolder extends RecyclerView.ViewHolder {

        private TextView mVideoTitle;
        private TextView mVideoSize;
        private TextView mVideoRecord;
        private ImageView mVideoThumbnail;

        RecyclerViewViewHolder(View convertView) {
            super(convertView);
            mVideoTitle = (TextView) convertView.findViewById(R.id.id_main_item_path);
            mVideoThumbnail = (ImageView) convertView.findViewById(
                    R.id.id_main_video_image);
            mVideoSize = (TextView) convertView.findViewById(R.id.id_main_item_size);
            mVideoRecord = (TextView) convertView.findViewById(R.id.id_main_item_record);
        }
    }

    public interface OnVideoItemClickListener {
        void onItemClick(View v, int position);

        void onItemLongClick(View v, int position);
    }

}
