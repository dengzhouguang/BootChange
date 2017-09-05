package com.dzg.bootchange;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dzg.bootchange.bean.StepBean;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView;
    private Button mButton;
    private EditText mEditText;
    public static String PCKAGE_NAME="com.dzg.bootchange";
    private boolean isRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getBoot();
        editSP();
        save();
        finish();
    }

    private void save() {
        try {
            File file=new File("/data/data/com.dzg.bootchange/shared_prefs/NewPedoMeter_private.xml");
            FileInputStream fis=new FileInputStream(file);
            byte[] bs=new byte[8*1024*1024];
            int len=-1;
            len = fis.read(bs);
            String sp=new String(bs,0,len,"utf-8").replace("\"","\\\"");
            Exec(sp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getBoot(){
        Process process = null;
        DataOutputStream os = null;
        DataInputStream is;
        File dir=new File("/data/data/com.dzg.bootchange/shared_prefs/");
        if (!dir.exists()){
            dir.mkdirs();
        }
        String cmd="chmod 777 " + PCKAGE_NAME;
        String editString="cat /data/data/com.eg.android.AlipayGphone/shared_prefs/NewPedoMeter_private.xml";
        try {
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            is=new DataInputStream(process.getInputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes(editString+"\n");
            os.writeBytes("exit\n");
            os.flush();
            byte[] bs=new byte[8*1024*1024];
            int len=-1;
            len=is.read(bs);
            File file=new File("/data/data/com.dzg.bootchange/shared_prefs/NewPedoMeter_private.xml");
            FileWriter writer=new FileWriter(file);
            if(len>0){
            writer.write(new String(bs,0,len-1,"utf-8"));}
            writer.flush();
            writer.close();
            process.waitFor();
            os.close();
            is.close();
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
        String cmd2="chmod 777 " + PCKAGE_NAME;
        String editString2="cat /data/data/com.eg.android.AlipayGphone/shared_prefs/NewPedoMeter.xml";
        try {
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            is=new DataInputStream(process.getInputStream());
            os.writeBytes(cmd2 + "\n");
            os.writeBytes(editString2+"\n");
            os.writeBytes("exit\n");
            os.flush();
            byte[] bs=new byte[8*1024*1024];
            int len=-1;
            len=is.read(bs);
            File file=new File("/data/data/com.dzg.bootchange/shared_prefs/NewPedoMeter.xml");
            FileWriter writer=new FileWriter(file);
            if(len>0){
                writer.write(new String(bs,0,len-1,"utf-8"));}
            writer.flush();
            writer.close();
            process.waitFor();
            os.close();
            is.close();
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
    public void Exec(String string){
        Process process = null;
        DataOutputStream os = null;
        String cmd="chmod 777 " + PCKAGE_NAME;
        String editString="echo \""+string+"\">"+"/data/data/com.eg.android.AlipayGphone/shared_prefs/NewPedoMeter_private.xml";
        try {
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes(editString+"\n");
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
        int step = (int) (timespan/1000);
        int span=30;
        List<StepBean> myList=new ArrayList<>();
        Random random=new Random();
        for (int i=0;i<step;i++){
            StepBean stepBean=new StepBean();
            i=i+random.nextInt(span);
            stepBean.setSteps(firstBean.getSteps()+i*2);
            stepBean.setTime(firstBean.getTime()+i*1000+random.nextInt(1000));
            stepBean.setBiz(firstBean.getBiz());
            myList.add(stepBean);
        }
        String jsonString=JSON.toJSONString(myList);
        sp.edit().putString("stepRecord",jsonString).commit();
        Log.e("getEBoot",jsonString);
    }
}
