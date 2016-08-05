package com.example.qzl.liaotianjiqiren;

/**
 * 提问和回答的对象
 * Created by Qzl on 2016-08-05.
 */
public class TalkBean {
    public String content;//谈话内容
    public boolean isAsk;//标记是否是提问
    public int imageId;//回答图片的id

    public TalkBean(String content, boolean isAsk, int imageId) {
        this.content = content;
        this.isAsk = isAsk;
        this.imageId = imageId;
    }
}
