package com.dzg.bootchange;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dzg.bootchange.bean.StepBean;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static String PCKAGE_NAME="com.dzg.bootchange";
    private int mStep=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getBoot();
        editSP();
        save();
        if (mStep>0)
            Toast.makeText(this,"步数已增加"+mStep*2+"步",Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getBoot(){
        Process process = null;
        DataOutputStream os = null;
        File dir=new File("/data/data/com.dzg.bootchange/shared_prefs/");
        if (!dir.exists()){
            dir.mkdirs();
        }
        String cmd="chmod 777 " + PCKAGE_NAME;
        String cmd2="cp /data/data/com.eg.android.AlipayGphone/shared_prefs/NewPedoMeter_private.xml /data/data/com.dzg.bootchange/shared_prefs/NewPedoMeter_private.xml";
        String cmd3="cp  /data/data/com.eg.android.AlipayGphone/shared_prefs/NewPedoMeter.xml /data/data/com.dzg.bootchange/shared_prefs/NewPedoMeter.xml";
        String cmd4="chmod 666 /data/data/com.dzg.bootchange/shared_prefs/NewPedoMeter_private.xml";
        String cmd5="chmod 666 /data/data/com.dzg.bootchange/shared_prefs/NewPedoMeter.xml";
        try {
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes(cmd2+"\n");
            os.writeBytes(cmd3+"\n");
            os.writeBytes(cmd4+"\n");
            os.writeBytes(cmd5+"\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (os!=null)
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            process.destroy();
        }
    }
    public void save(){
        Process process = null;
        DataOutputStream os = null;
        String cmd="chmod 777 " + PCKAGE_NAME;
        String cmd2="cp /data/data/com.dzg.bootchange/shared_prefs/NewPedoMeter_private.xml /data/data/com.eg.android.AlipayGphone/shared_prefs/NewPedoMeter_private.xml";
        String cmd3="chmod 666 /data/data/com.eg.android.AlipayGphone/shared_prefs/NewPedoMeter_private.xml";
        try {
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes(cmd2+"\n");
            os.writeBytes(cmd3+"\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }
    }
    public void editSP(){
        SharedPreferences sp =getSharedPreferences("NewPedoMeter_private", Context.MODE_PRIVATE);
        SharedPreferences sp2 =getSharedPreferences("NewPedoMeter", Context.MODE_PRIVATE);
        String last_stepinfo=sp2.getString("last_stepinfo_today","");
        String firstStep=sp.getString("firstStep","");
        StepBean firstBean=  JSON.parseObject(firstStep.replaceAll("&quot;","\""),StepBean.class);
        if (!last_stepinfo.equals("")){
            StepBean lastBean=JSON.parseObject(last_stepinfo.replaceAll("&quot;","\""),StepBean.class);
            firstBean.setTime(lastBean.getTime());
        }
        long timespan=new Date().getTime()-firstBean.getTime();
        mStep = (int) (timespan/1000);
        int span=100;
        List<StepBean> myList=new ArrayList<>();
        Random random=new Random();
        for (int i=0;i<mStep;i++){
            StepBean stepBean=new StepBean();
            i=i+random.nextInt(span);
            stepBean.setSteps(firstBean.getSteps()+i*2);
            stepBean.setTime(firstBean.getTime()+i*1000+random.nextInt(1000));
            stepBean.setBiz(firstBean.getBiz());
            myList.add(stepBean);
        }
        String jsonString=JSON.toJSONString(myList);
        sp.edit().putString("stepRecord",jsonString).commit();
    }
}
