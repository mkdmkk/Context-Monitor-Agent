package net.infidea.cma;

import java.util.ArrayList;
import net.infidea.cma.monitor.ContextMonitor;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ContextListAdapter extends BaseAdapter implements OnClickListener {

	private ArrayList<ViewGroup> contextViewList = null;
	
	private ContextMonitor contextMonitor = null;
	
	public ContextListAdapter(Context context, ContextMonitor contextMonitor) {
		// TODO Auto-generated constructor stub
		this.contextMonitor = contextMonitor;
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return contextViewList.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return contextViewList.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return contextViewList.get(position);
	}

	public void setContextList(ArrayList<ViewGroup> contextViewList) {
		this.contextViewList = contextViewList;
		for(ViewGroup contextView:contextViewList) {
			contextView.setOnClickListener(this);
		}
	}
	
	public void clear() {
		contextViewList.clear();
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		int contextType = (Integer) v.getTag(); 
		contextMonitor.displayContextInfo(contextType);
	}
}
