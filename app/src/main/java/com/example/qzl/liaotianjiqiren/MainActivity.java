package com.example.qzl.liaotianjiqiren;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ListView mLvList;
    private StringBuffer mVoiceBuffer;
    private ArrayList<TalkBean> mTalkBeen = new ArrayList<>();//用来存交谈内容
    private VoiceAdapter mAdapter;

    private String[] mAnswers = new String[]{"约吗？","等你吆！！！","没有更多美女了！","这是最后一张了！","不要再要了！","人家害羞呀！"};
    private int[] mAnswerPics = new int[]{R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化语音配置对象
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "= 572ef5b8");
        mLvList = (ListView) findViewById(R.id.lv_li_listview);
        mAdapter = new VoiceAdapter();
        mLvList.setAdapter(mAdapter);
    }

    public void startVoice(View view) {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, null);
        //2.设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        mVoiceBuffer = new StringBuffer();
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean islast) {
                //输出是一个json数据
                System.out.println(recognizerResult.getResultString());
                String voiceStr = printResult(recognizerResult.getResultString());
                mVoiceBuffer.append(voiceStr);
                if (islast){
                    String askStr = mVoiceBuffer.toString();
//                    Toast.makeText(getApplicationContext(),askStr,Toast.LENGTH_SHORT).show();

                    //添加提问对象
                    TalkBean ask = new TalkBean(askStr,true,-1);
                    mTalkBeen.add(ask);
                    //初始化回答对象
                    String answerStr = "没听清";
                    int imageId = -1;
                    if (askStr.contains("你好")){//判断里面有没有你好字样
                        answerStr = "你好呀！！！";
                    }else if (askStr.contains("你是谁")){
                        answerStr = "我是你的小助手";
                    }else if (askStr.contains("美女")){
                        Random random = new Random();
                        //随机回答
                        int strPos = random.nextInt(mAnswers.length);
                        answerStr = mAnswers[strPos];

                        //随机图片
                        int imagePos = random.nextInt(mAnswerPics.length);
                        imageId = mAnswerPics[imagePos];
                    }else if (askStr.contains("天王盖地虎")){
                        answerStr = "小鸡炖蘑菇";
                        imageId = R.drawable.m;
                    }
                    TalkBean answer = new TalkBean(answerStr,false,imageId);
                    mTalkBeen.add(answer);
                    //刷新listView
                    mAdapter.notifyDataSetChanged();
                    //让listV定位到最后一个item
                    mLvList.setSelection(mTalkBeen.size() - 1);
                    startClick(answerStr);
                }
            }

            @Override
            public void onError(SpeechError speechError) {

            }
        });
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    public void startClick(String content) {
        //1.创建 SpeechSynthesizer 对象, 第二个参数：本地合成时传 InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(this, null);
        //2.合成参数设置，详见《MSC Reference Manual》SpeechSynthesizer 类
        //设置发音人（更多在线发音人，用户可参见 附录13.2
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); //设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端引擎
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
        //仅支持保存为 pcm 和 wav 格式，如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
        //3.开始合成
        mTts.startSpeaking(content,null);

    }

    //解析语音json
    private String printResult(String json) {
        Gson gson = new Gson();
        VoiceBean voiceBean = gson.fromJson(json,VoiceBean.class);
        StringBuffer sb = new StringBuffer();
        ArrayList<VoiceBean.WsBean> ws = voiceBean.ws;
        for (VoiceBean.WsBean wsBean : ws){
            String w = wsBean.cw.get(0).w;
            sb.append(w);//拼起来
        }
        return sb.toString();
    }

    class VoiceAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mTalkBeen.size();
        }

        @Override
        public TalkBean getItem(int position) {
            return mTalkBeen.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(getApplicationContext(),R.layout.list_item,null);
                holder = new ViewHolder();
                holder.mTvAsk = (TextView) convertView.findViewById(R.id.tv_li_ask);
                holder.mTvAnswer = (TextView) convertView.findViewById(R.id.tv_li_answer);
                holder.mIvPic = (ImageView) convertView.findViewById(R.id.iv_li_pic);
                holder.mLlAnswer = (LinearLayout) convertView.findViewById(R.id.ll_li_answer);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            TalkBean item = getItem(position);
            if (item.isAsk){
                //提问
                holder.mTvAsk.setVisibility(View.VISIBLE);
                holder.mLlAnswer.setVisibility(View.GONE);
                holder.mTvAsk.setText(item.content);
            }else {
                //回答
                holder.mTvAsk.setVisibility(View.GONE);
                holder.mLlAnswer.setVisibility(View.VISIBLE);
                holder.mTvAnswer.setText(item.content);
                //有图片
                if (item.imageId > 0){
                    holder.mIvPic.setVisibility(View.VISIBLE);
                    holder.mIvPic.setImageResource(item.imageId);
                }else {
                    holder.mIvPic.setVisibility(View.GONE);
                }
            }
            return convertView;
        }
    }
    static class ViewHolder{
        public TextView mTvAsk;
        public TextView mTvAnswer;
        public ImageView mIvPic;
        public LinearLayout mLlAnswer;
    }
}
