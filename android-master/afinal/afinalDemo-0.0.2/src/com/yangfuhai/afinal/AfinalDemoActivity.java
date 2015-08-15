package com.yangfuhai.afinal;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinaActivity;
import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @title ���� afinal demo
 * @description afinal���ص�ַ http://code.google.com/p/afinal/
 * @company ̽�������繤����(www.tsz.net)
 * @author michael Young (www.YangFuhai.com)
 * @version 1.0
 * @created 2012-10-17
 */
public class AfinalDemoActivity extends FinaActivity {
	
	@ViewInject(id=R.id.btn_add,click="btnClick") Button BtnAdd;
	@ViewInject(id=R.id.btn_refresh,click="btnClick") Button BtnRefresh;
	
	@ViewInject(id=R.id.listView,itemClick="itemClick") ListView listView;
	
	@ViewInject(id=R.id.edit_name) EditText edit_name;
	@ViewInject(id=R.id.edit_email) EditText edit_email;
	
	List<User> mListViewData = new ArrayList<User>();
	
	FinalDb db;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        db = FinalDb.creat(this);
        listView.setAdapter(mListAdapter);
    }
    
    public void btnClick(View v){
    	if(v == BtnAdd){
	    	String name = edit_name.getText().toString();
	    	String email = edit_email.getText().toString();
	    	
	    	if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)){
	    		User user = new User();
	    		user.setEmail(email);
	    		user.setName(name);
	    		
	    		if(v.getTag()!=null){
	    			int id = Integer.valueOf(v.getTag().toString());
	    			user.setUserId(id);
	    			db.update(user);
	    			showToast("���³ɹ�");
	    			
	    		}else{
	    			db.save(user);
		    		showToast("��ӳɹ�");
	    		}
	    		
	    		edit_name.setText("");
	    		edit_email.setText("");
	    		BtnAdd.setTag(null);
	    		BtnAdd.setText("�������");
	    		
	    	}
    	}else if(v == BtnRefresh){
    		mListViewData.clear();
    		mListViewData.addAll(db.findAll(User.class));
    		mListAdapter.notifyDataSetChanged();
    	}
    }
    
    
    private View.OnClickListener itemButtonClick = new View.OnClickListener() {
		public void onClick(View v) {
			Object obj = v.getTag();
			if(obj instanceof Integer){
				int positon = Integer.valueOf(obj.toString());
				User user = mListViewData.get(positon);
				if(v.getId() == R.id.item_btn_del){ //listview��ɾ����ť
					
//					db.deleteById(User.class, user.getUserId());//Ҳ���������ַ�ʽɾ��
					db.deleteById(user);
					
					showToast("ɾ���ɹ�");
					BtnRefresh.performClick();//ˢ���б�
					
				}else if(v.getId() == R.id.item_btn_update){//listview�ĸ��°�ť
					edit_email.setText(user.getEmail());
					edit_name.setText(user.getName());
					
					BtnAdd.setText("��������");
					BtnAdd.setTag(user.getUserId());
				}
			}
		}
	};
    
	private void showToast(String strMsg){
		Toast.makeText(this, strMsg, 0).show();
	}
	
	
    
    private BaseAdapter mListAdapter = new BaseAdapter() {
		
		public View getView(int position, View convertView, ViewGroup parent) {
			View item = View.inflate(AfinalDemoActivity.this, R.layout.listitem, null);
			
			TextView tv_id = (TextView) item.findViewById(R.id.item_id);
			TextView tv_name = (TextView) item.findViewById(R.id.item_name);
			TextView tv_email = (TextView) item.findViewById(R.id.item_email);
			
			Button btn_del = (Button) item.findViewById(R.id.item_btn_del);
			Button btn_update = (Button) item.findViewById(R.id.item_btn_update);
			
			User user= mListViewData.get(position);
			tv_id.setText("id:"+user.getUserId()+"");
			tv_name.setText("���֣�"+user.getName());
			tv_email.setText("����:"+user.getEmail());
			
			btn_del.setTag(position);
			btn_update.setTag(position);
			btn_del.setOnClickListener(itemButtonClick);
			btn_update.setOnClickListener(itemButtonClick);
			
			return item;
		}
		
		public long getItemId(int position) {
			return position;
		}
		
		public Object getItem(int position) {
			return mListViewData.get(position);
		}
		
		public int getCount() {
			return mListViewData.size();
		}
	};
    
    
    
}