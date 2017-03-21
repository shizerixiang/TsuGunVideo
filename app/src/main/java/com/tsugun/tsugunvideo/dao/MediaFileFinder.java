package com.tsugun.tsugunvideo.dao;

import com.tsugun.tsugunvideo.entity.MediaFileInfo;

import java.util.List;

/**
 * 媒体文件数据库调用接口
 * Created by shize on 2017/3/3.
 */

public interface MediaFileFinder {

    /**
     * 获取所有文件信息
     */
    List<MediaFileInfo> getAllFileInfo();

    /**
     * 删除所有文件信息
     */
    void deleteAllFileInfo();

    /**
     * 条件删除文件信息
     *
     * @param url 在媒体文件信息中添加需要删除文件的路径
     */
    void deleteFileInfo(String url);

    /**
     * 添加单个文件信息
     *
     * @param fileInfo 需要添加的文件信息
     */
    void addFileInfo(MediaFileInfo fileInfo);

    /**
     * 添加多个文件信息
     *
     * @param fileInfo 需要添加的文件信息集合
     */
    void addFileInfo(List<MediaFileInfo> fileInfo);

    /**
     * 根据文件路径搜索单个文件信息
     *
     * @param url 文件的绝对路径
     * @return 返回文件信息
     */
    MediaFileInfo getFileInfoByUrl(String url);

    /**
     * 根据文件类型获取多个文件信息
     *
     * @param type 文件类型
     * @return 文件信息集合
     */
    List<MediaFileInfo> getFileInfoByType(String type);

    /**
     * 根据文件夹路径获取多个文件信息
     *
     * @param folderUrl 文件夹路径
     * @return 文件信息集合
     */
    List<MediaFileInfo> getFileInfoByFolderUrl(String folderUrl);

    /**
     * 修改单个文件播放进度记录
     *
     * @param mediaFileInfo 修改后的文件信息
     * @return 是否修改成功
     */
    boolean updateFileInfo(MediaFileInfo mediaFileInfo);

}
