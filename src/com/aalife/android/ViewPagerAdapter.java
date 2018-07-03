package com.aalife.android;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ViewPagerAdapter extends PagerAdapter {
    private List<Map<String, String>> list = null;
    private LayoutInflater inflater = null;
	private SQLiteOpenHelper sqlHelper = null;
	private Context context = null;
	private final int FIRST_REQUEST_CODE = 1;
	
    public ViewPagerAdapter(Context context, List<Map<String, String>> list) {
    	this.list = list;
    	this.inflater = LayoutInflater.from(context);
		sqlHelper = new DatabaseHelper(context);
		this.context = context;
    }
    
	@Override
	public void destroyItem(ViewGroup view, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager)view).removeView((View)object);
	}

	@Override
	public void finishUpdate(View container) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		// TODO Auto-generated method stub
		View layMonthPager = inflater.inflate(R.layout.list_month_pager, view, false);
		Map<String, String> map = list.get(position);
		String date = map.get("datevalue");
		final MonthActivity activity = ((MonthActivity)context);

		//标题变粗
		TextPaint textPaint = null;
		TextView tvTitleZhiChu = (TextView) layMonthPager.findViewById(R.id.tv_title_zhichuprice);
		textPaint = tvTitleZhiChu.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleShouRu = (TextView) layMonthPager.findViewById(R.id.tv_title_shouruprice);
		textPaint = tvTitleShouRu.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleDate = (TextView) layMonthPager.findViewById(R.id.tv_title_date);
		textPaint = tvTitleDate.getPaint();
		textPaint.setFakeBoldText(true);
		
		ListView listMonth = (ListView) layMonthPager.findViewById(R.id.list_month);
		listMonth.setDivider(null);
		ItemTableAccess itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		List<Map<String, String>> list = itemAccess.findMonthByDate(date);
		itemAccess.close();
		SimpleAdapter adapter = new SimpleAdapter(context, list, R.layout.list_month, new String[] { "zhichuprice", "shouruprice", "date" }, new int[] { R.id.tv_month_zhichuprice, R.id.tv_month_shouruprice, R.id.tv_month_date });
		listMonth.setAdapter(adapter);
		//UtilityHelper.setListViewHeight(context, listMonth, adapter.getCount());

		//列表点击
		listMonth.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
		        String date = map.get("datevalue");

		        TextView tvZhiChuPrice = (TextView) view.findViewById(R.id.tv_month_zhichuprice);
		        tvZhiChuPrice.setBackgroundColor(context.getResources().getColor(R.color.color_tran_main));
		        TextView tvShouRuPrice = (TextView) view.findViewById(R.id.tv_month_shouruprice);
		        tvShouRuPrice.setBackgroundColor(context.getResources().getColor(R.color.color_tran_main));
		        TextView tvDate = (TextView) view.findViewById(R.id.tv_month_date);
		        tvDate.setBackgroundColor(context.getResources().getColor(R.color.color_tran_main));
		        
		        Intent intent = new Intent(context, DayDetailActivity.class);
		        intent.putExtra("date", date);
		        activity.startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
		
		((ViewPager)view).addView(layMonthPager, 0);
		return layMonthPager;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list != null ? list.size() : 0;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
		// TODO Auto-generated method stub
	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View container) {
		// TODO Auto-generated method stub
	}

}
