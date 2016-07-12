package com.example.groupsend;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class GroupSend extends Activity {
	
	EditText numbers, content;
	Button select, send;
	@SuppressWarnings("deprecation")
	SmsManager smsManager;
	ArrayList<String> sendList = new ArrayList<String>(); //记录需要群发的号码列表

    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        smsManager = SmsManager.getDefault();
        numbers = (EditText)findViewById(R.id.numbers);
        content = (EditText)findViewById(R.id.content);
        select = (Button)findViewById(R.id.select);
        send = (Button)findViewById(R.id.send);
        
        send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for (String number : sendList)
				{
					//创建一个PendingIntent对象
					PendingIntent pendingIntent = PendingIntent.getActivity(GroupSend.this, 0, new Intent(), 0);
					smsManager.sendTextMessage(number, null, content.getText().toString(), pendingIntent, null);//发送短信
				}
				Toast.makeText(GroupSend.this, "群发短信完成", Toast.LENGTH_LONG).show(); //提示群发短息完成	
			}
		});
        
        select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//查询联系人的电话号码
				final Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
						null, null, null, null);
				
				BaseAdapter adapter = new BaseAdapter() 
				{
					
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						// TODO Auto-generated method stub
						cursor.moveToPosition(position);
						CheckBox checkBox = new CheckBox(GroupSend.this);
						//获取联系人的电话号码，并去掉中间的中划线、空格
						String number = cursor.getString(cursor.getColumnIndex
								(ContactsContract.CommonDataKinds.Phone.NUMBER))
								.replace("_", "")
								.replace(" ", "");
						checkBox.setText(number);
						//如果该号码已经被加入黑名单、默认勾选该号码
						if (isChecked(number))
						{
							checkBox.setChecked(true);
						}
						return checkBox;
					}
					
					@Override
					public long getItemId(int position) {
						// TODO Auto-generated method stub
						return position;
					}
					
					@Override
					public Object getItem(int position) {
						// TODO Auto-generated method stub
						return position;
					}
					
					@Override
					public int getCount() {
						// TODO Auto-generated method stub
						return cursor.getCount();
					}
				};
				
				//加载list.xml布局文件对应的View
				View selectView = getLayoutInflater().inflate(R.layout.list, null);
				//获取selectView中的名为list的ListView组件
				final ListView listView = (ListView)selectView.findViewById(R.id.list);
				listView.setAdapter(adapter);
				new AlertDialog.Builder(GroupSend.this).setView(selectView).setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								sendList.clear();//清空sendList集合
								//遍历listView组件的每个列表项
								for (int i = 0; i < listView.getCount(); i++)
								{
									CheckBox checkBox2 = (CheckBox)listView.getChildAt(i);
									//如果该列表项被勾选
									if (checkBox2.isChecked())
									{
										sendList.add(checkBox2.getText().toString()); //添加该列表项的电话号码
									}
								}
								numbers.setText(sendList.toString());
							}
						}).show();
			}
		});
        
    }
    
    public boolean isChecked(String phone)
    {
    	for (String s1 : sendList)
    	{
    		if (s1.equals(phone))
    		{
    			return true;
    		}
    	}
    	
		return false;
    }
    
}
