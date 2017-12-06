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
    // 用来记录应用程序的信息
    List<AppsItemInfo> list;
 
    private GridView gridview;
    private static PackageManager pManager;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.app_list);
 
        Log.d(TAG, "ShowAllAppList");
        // 取得gridview
        gridview = (GridView) findViewById(R.id.GridViewAppList);
 
        // 获取图片、应用名、包名
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
 
        // 设置gridview的Adapter
        gridview.setAdapter(new baseAdapter());
        gridview.setSelection(0);
        // 点击应用图标时，做出响应
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
 
            // 判断是否为非系统预装的应用程序
            // 这里还可以添加系统自带的，这里就先不添加了，如果有需要可以自己添加
            // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
            //if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // 添加自己已经安装的应用程序
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
                // 使用View的对象itemView与R.layout.item关联
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
 
    // 当用户点击应用程序图标时，将对这个类做出响应
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
        /** app鍥炬爣 */
        ImageView icon;
        /** app榛樿鏂囧瓧 */
        TextView label;
    }
 
    // 自定义一个 AppsItemInfo 类，用来存储应用程序的相关信息
    private class AppsItemInfo {
 
        private Drawable icon; // 存放图片
        private String label; // 存放应用程序名
        private String packageName; // 存放应用程序包名
        private String className; // 存放应用程序类名
        private String appName; // 存放应用程序类名
 
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