package com.sfzt.zlauncher_wf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
 
public class ShowAllAppList extends Activity {
 
	private final static String TAG = "WFUI";
    // ������¼Ӧ�ó������Ϣ
    List<AppsItemInfo> list;
 
    private GridView gridview;
    private static PackageManager pManager;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.app_list);
 
        Log.d(TAG, "ShowAllAppList");
        // ȡ��gridview
        gridview = (GridView) findViewById(R.id.GridViewAppList);
 
        // ��ȡͼƬ��Ӧ����������
        pManager = ShowAllAppList.this.getPackageManager();
        List<ResolveInfo> appList = getAllApps(ShowAllAppList.this);
 
        list = new ArrayList<AppsItemInfo>();
        
 
        for (int i = 0; i < appList.size(); i++) {
        	AppsItemInfo appItem = new AppsItemInfo();
    		appItem.appName = appList.get(i).loadLabel(getPackageManager()).toString();
    		appItem.icon = appList.get(i).loadIcon(getPackageManager());
    		appItem.packageName = appList.get(i).activityInfo.packageName;
    		appItem.className = appList.get(i).activityInfo.name;
    		if(!(appList.get(i).activityInfo.packageName.equals("com.dvr.android.dvr")))
//    		if(!(appList.get(i).activityInfo.packageName.equals("com.sfzt.uvcam")))
    			list.add(appItem);
        }
 
        // ����gridview��Adapter
        gridview.setAdapter(new baseAdapter());
        gridview.setSelection(0);
        // ���Ӧ��ͼ��ʱ��������Ӧ
        gridview.setOnItemClickListener(new ClickListener());
 
    }
    
    public static List<ResolveInfo> getAllApps(Context context) {
 
        List<ResolveInfo> apps = new ArrayList<ResolveInfo>();
        
        Object resolveIntent = new Intent(Intent.ACTION_MAIN, null);  
	    ((Intent)resolveIntent).addCategory(Intent.CATEGORY_LAUNCHER);  	    
	    List<ResolveInfo> resolveinfoList = pManager.queryIntentActivities(((Intent)resolveIntent), 0);
	    Collections.sort((List)resolveinfoList, new ResolveInfo.DisplayNameComparator(pManager));
	    
        for (int i = 0; i < resolveinfoList.size(); i++) {
        	ResolveInfo pak = (ResolveInfo) resolveinfoList.get(i);
 
            // �ж��Ƿ�Ϊ��ϵͳԤװ��Ӧ�ó���
            // ���ﻹ�������ϵͳ�Դ��ģ�������Ȳ�����ˣ��������Ҫ�����Լ����
            // if()���ֵ���<=0��Ϊ�Լ�װ�ĳ��򣬷���Ϊϵͳ�����Դ�
            //if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // ����Լ��Ѿ���װ��Ӧ�ó���
                apps.add(pak);
            //}
 
        }
        return resolveinfoList;
    }
    
    private class baseAdapter extends BaseAdapter {
        LayoutInflater inflater = LayoutInflater.from(ShowAllAppList.this);
 
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }
 
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }
 
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                // ʹ��View�Ķ���itemView��R.layout.item����
                convertView = inflater.inflate(R.layout.apps_item, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView
                        .findViewById(R.id.apps_image);
                holder.label = (TextView) convertView
                        .findViewById(R.id.apps_textview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            holder.icon.setImageDrawable(list.get(position).icon);
            if(list.get(position).packageName.equals("com.google.android.apps.maps")){
            	holder.label.setText(getResources().getText(R.string.googlemaps));
            }else{
            	holder.label.setText(list.get(position).appName);
            }
 
            return convertView;
 
        }
 
    }
 
    // ���û����Ӧ�ó���ͼ��ʱ�����������������Ӧ
    private class ClickListener implements OnItemClickListener {
 
        @Override
        public void onItemClick(AdapterView<?> viewGroup, View view, int position,
                long id) {
 
        	try {
    			String packageName = list.get(position).packageName;
    			String className = list.get(position).className;
    			ComponentName componetName = new ComponentName(packageName,className);
    			
    			Intent intent = new Intent(Intent.ACTION_MAIN);
    			intent.addCategory(Intent.CATEGORY_LAUNCHER); 
    			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			intent.setComponent(componetName);
    			startActivity(intent);
    		} catch (Exception e) {
    			Toast.makeText(ShowAllAppList.this, "Application Error", Toast.LENGTH_SHORT).show();
    		}
        }
 
    }
    
    private class ViewHolder {
        /** app图标 */
        ImageView icon;
        /** app默认文字 */
        TextView label;
    }
 
    // �Զ���һ�� AppsItemInfo �࣬�����洢Ӧ�ó���������Ϣ
    private class AppsItemInfo {
 
        private Drawable icon; // ���ͼƬ
        private String label; // ���Ӧ�ó�����
        private String packageName; // ���Ӧ�ó������
        private String className; // ���Ӧ�ó�������
        private String appName; // ���Ӧ�ó�������
 
        public Drawable getIcon() {
            return icon;
        }
 
        public void setIcon(Drawable icon) {
            this.icon = icon;
        }
 
        public String getLabel() {
            return label;
        }
 
        public void setLabel(String label) {
            this.label = label;
        }
 
        public String getAppName() {
            return appName;
        }
 
        public void setAppName(String appName) {
            this.appName = appName;
        }
        public String getPackageName() {
            return packageName;
        }
 
        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }
        
        public String getClassName() {
        	return className;
        }
        
        public void setClassName(String className) {
        	this.className = className;
        }
 
    }
 
}