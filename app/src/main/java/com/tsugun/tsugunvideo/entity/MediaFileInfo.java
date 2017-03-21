package com.tsugun.tsugunvideo.entity;

/**
 * 媒体文件实体类
 * Created by shize on 2017/3/3.
 */

public class MediaFileInfo {

    private int id; // 视频id
    private String title; // 视频标题名称
    private String url; // 视频地址
    private String type; // 视频文件类型
    private long size; // 视频文件大小
    private long duration; // 视频总时长
    private long record; // 视频上次播放进度
    private float height; // 视频高度
    private float width; // 视频宽度
    private int rotation; // 录制视频的角度

    public MediaFileInfo() {
        size = 0;
        record = 0;
        duration = 0;
        height = 0;
        width = 0;
        rotation = 0;
    }

    public MediaFileInfo(int id, long record) {
        this.id = id;
        this.record = record;
    }

    public MediaFileInfo(int id, String title, String url, String type, long size,
                         long duration, long record, float height, float width, int rotation) {
        this();
        this.id = id;
        this.title = title;
        this.url = url;
        this.type = type;
        this.size = size;
        this.duration = duration;
        this.record = record;
        this.height = height;
        this.width = width;
        this.rotation = rotation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getRecord() {
        return record;
    }

    public void setRecord(long record) {
        this.record = record;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
}
