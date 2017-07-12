package com.dzg.bootchange;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        mTextView = (TextView) findViewById(R.id.id_tv);
        mEditText = (EditText) findViewById(R.id.id_et);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRoot){
                    Toast.makeText(MainActivity.this,"没有ROOT权限，请先授予本应用ROOT权限",Toast.LENGTH_LONG).show();
                    return;
                }
                putToSP();
                save();
                Toast.makeText(MainActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
            }
        });
        getBoot(false);
        showEdit();
    }

    private void save() {
        try {
            File file=new File("/data/data/com.dzg.bootchange/shared_prefs/NewPedoMeter.xml");
            FileInputStream fis=new FileInputStream(file);
            byte[] bs=new byte[2*1024*1024];
            int len=-1;
            try {
                len=fis.read(bs);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            String sp=new String(bs,0,len,"utf-8").replace("\"","\\\"");
            Exec(sp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showEdit() {
        if (!isRoot){
            mTextView.setText("尚未安装支付宝或没有ROOT权限");
            return;
        }
        SharedPreferences sp =getSharedPreferences("NewPedoMeter", Context.MODE_PRIVATE);
        String boot=sp.getString("last_stepinfo_today","0");
        String baseStep=sp.getString("baseStep","0");
        if (!boot.equals("0"))
        boot=boot.substring(boot.indexOf("steps\":")+"steps\":".length(),boot.lastIndexOf(","));
        if (!baseStep.equals("0"))
        baseStep=baseStep.substring(baseStep.indexOf(":")+1,baseStep.indexOf(","));
        if (boot.contains(";")){
            mTextView.setText("总步数：0");
        }else
        mTextView.setText("总步数："+boot);
        mEditText.setText(baseStep);
        mEditText.setSelection(baseStep.length());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBoot(true);
        showEdit();
    }

    public void getBoot(boolean reload){
        Process process = null;
        DataOutputStream os = null;
        DataInputStream is=null;
        File dir=new File("/data/data/com.dzg.bootchange/shared_prefs/");
        if (!dir.exists()){
            dir.mkdirs();
        }
        String cmd="chmod 777 " + PCKAGE_NAME;
        String editString="cat /data/data/com.eg.android.AlipayGphone/shared_prefs/NewPedoMeter.xml";

        try {
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            is=new DataInputStream(process.getInputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes(editString+"\n");
            os.writeBytes("exit\n");
            os.flush();
            byte[] bs=new byte[2*1024*1024];
            int len=-1;
            len=is.read(bs);
            if (!reload){
            File file=new File("/data/data/com.dzg.bootchange/shared_prefs/NewPedoMeter.xml");
            FileWriter writer=new FileWriter(file);
            if(len>0){
            writer.write(new String(bs,0,len-1,"utf-8"));}
            writer.flush();
            writer.close();
            }else {
                if (len==-1){
                    isRoot=false;
                    return;
                }else
                    isRoot=true;
                String str=new String(bs,0,len,"utf-8");
                String startCount=str.substring(str.indexOf("{&quot;steps&quot;:")+"{&quot;steps&quot;:".length());
                startCount=startCount.substring(0,startCount.indexOf(","));
                String count=str.substring(str.indexOf(",&quot;steps&quot;:")+",&quot;steps&quot;:".length());
                count=count.substring(0,count.indexOf(","));
                SharedPreferences sp =getSharedPreferences("NewPedoMeter", Context.MODE_PRIVATE);
                String boot=sp.getString("last_stepinfo_today","");
                String userId=sp.getString("userId","0");
                String head="";
                String end="";
                if (!boot.equals("")) {
                    head= boot.substring(0, boot.indexOf("steps\":") + "steps\":".length());
                    end= boot.substring(boot.lastIndexOf(","), boot.length());
                }else
                {
                    head="{&quot;biz&quot;:&quot;alipay&quot;,&quot;steps&quot;:";
                    end=",&quot;time&quot;:"+ new Date().getTime()+"}";
                }
                String baseStep=sp.getString("baseStep","");
                String baseHead=null;
                String baseEnd=null;
                if(!baseStep.equals("")) {
                    baseHead= baseStep.substring(0, baseStep.indexOf(":") + 1);
                    baseEnd= baseStep.substring(baseStep.indexOf(","), baseStep.length());
                }else{
                    baseHead=">{&quot;steps&quot;:";
                    Date date=new Date();
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                    String time=sdf.format(date);
                    try {
                        date=sdf.parse(time);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    baseEnd=",&quot;time&quot;:"+date.getTime()+"}";
                }
                sp.edit().clear().commit();
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("baseStep",baseHead+startCount+baseEnd);
                editor.putBoolean("checkuser",true);
                editor.putBoolean("startup",true);
                editor.putString("last_stepinfo_today",head+count+end);
                editor.putString("userId",userId);
                editor.commit();
            }
            process.waitFor();
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (os!=null)
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            process.destroy();
        }
    }
    public void putToSP(){
        String count= mEditText.getText().toString();
        String startCount= mEditText.getText().toString();
        SharedPreferences sp =getSharedPreferences("NewPedoMeter", Context.MODE_PRIVATE);
        String userId=sp.getString("userId","0");
        String boot=sp.getString("last_stepinfo_today","");
        String head="";
        String end="";
        if (!boot.equals("")) {
            head= boot.substring(0, boot.indexOf("steps\":") + "steps\":".length());
            end= boot.substring(boot.lastIndexOf(","), boot.length());
        }else
        {
            head="{&quot;biz&quot;:&quot;alipay&quot;,&quot;steps&quot;:";
            end=",&quot;time&quot;:"+ new Date().getTime()+"}";
        }
        String baseStep=sp.getString("baseStep","");
        String baseHead=null;
        String baseEnd=null;
        if(!baseStep.equals("")) {
            baseHead= baseStep.substring(0, baseStep.indexOf(":") + 1);
            baseEnd= baseStep.substring(baseStep.indexOf(","), baseStep.length());
        }else{
            baseHead=">{&quot;steps&quot;:";
            Date date=new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String time=sdf.format(date);
            try {
                date=sdf.parse(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
            baseEnd=",&quot;time&quot;:"+date.getTime()+"}";
        }
        sp.edit().clear().commit();
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("baseStep",baseHead+startCount+baseEnd);
        editor.putBoolean("checkuser",true);
        editor.putBoolean("startup",true);
        editor.putString("last_stepinfo_today",head+count+end);
        editor.putString("userId",userId);
        editor.commit();
    }
    public void Exec(String string){
        Process process = null;
        DataOutputStream os = null;
        String cmd="chmod 777 " + PCKAGE_NAME;
        String editString="echo \""+string+"\">"+"/data/data/com.eg.android.AlipayGphone/shared_prefs/NewPedoMeter.xml";
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
}
