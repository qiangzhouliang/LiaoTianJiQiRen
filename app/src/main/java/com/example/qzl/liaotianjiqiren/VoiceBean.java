package com.example.qzl.liaotianjiqiren;

import java.util.ArrayList;

/**
 * Created by Qzl on 2016-08-05.
 */
public class VoiceBean {
    public ArrayList<WsBean> ws;
    public class WsBean{
        public ArrayList<CwBean> cw;
    }
    public class CwBean{
        public String w;
    }
}
