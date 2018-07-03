package com.aalife.android;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DayAdapter extends BaseAdapter {
	private Context context = null;
	private List<Map<String, String>> list = null;
	private LayoutInflater layout = null;
	private SimpleAdapter simpleAdapter = null;
	private ItemTableAccess itemAccess = null;
	private SQLiteOpenHelper sqlHelper = null;
	private SharedHelper sharedHelper = null;
	private DayActivity activity = null;
	private final int FIRST_REQUEST_CODE = 1;
	
	public DayAdapter(Context context, List<Map<String, String>> list) {
		this.context = context;
		this.list = list;
		this.layout = LayoutInflater.from(this.context);
		this.sqlHelper = new DatabaseHelper(this.context);
		this.sharedHelper = new SharedHelper(this.context);
		this.activity = (DayActivity) context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layout.inflate(R.layout.list_day, parent, false);
			holder.tvItemBuyDate = (TextView) convertView.findViewById(R.id.tv_day_itembuydate);
			holder.tvTotalPriceZhi = (TextView) convertView.findViewById(R.id.tv_day_totalprice_zhi);
			holder.tvTotalPriceShou = (TextView) convertView.findViewById(R.id.tv_day_totalprice_shou);
			holder.lvDayListSub = (MyListView) convertView.findViewById(R.id.list_day_sub);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		//标题变粗
		TextPaint textPaint = null;
		textPaint = holder.tvItemBuyDate.getPaint();
		textPaint.setFakeBoldText(true);
		textPaint = holder.tvTotalPriceZhi.getPaint();
		textPaint.setFakeBoldText(true);
		textPaint = holder.tvTotalPriceShou.getPaint();
		textPaint.setFakeBoldText(true);
		
		//取值赋值
		final String itemBuyDate = list.get(position).get("itembuydate");
		holder.tvItemBuyDate.setText(UtilityHelper.formatDate(itemBuyDate, "y-m-d-w2"));
		String totalPriceZhi = list.get(position).get("zhipricevalue");
		String totalPriceShou = list.get(position).get("shoupricevalue");
		holder.tvTotalPriceZhi.setText("- " + totalPriceZhi);
		try {
			if(Double.parseDouble(totalPriceShou) > 0) {
				holder.tvTotalPriceShou.setText("+ " + totalPriceShou);
			} else {
				holder.tvTotalPriceShou.setText("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//日期点击
		convertView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, DayDetailActivity.class);
		        intent.putExtra("date", itemBuyDate);
		        activity.startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
		
		//设置DayList
		itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		final List<Map<String, String>> subList = itemAccess.findItemByDate(itemBuyDate, 0);
		Map<String, String> totalMap = itemAccess.findAllMonth(itemBuyDate);
		itemAccess.close();

		//设置总计
		activity.setTotalData(itemBuyDate, totalMap.get("shourupricevalue"), totalMap.get("zhichupricevalue"), totalMap.get("jiecunpricevalue"));
		
		//设置CheckBox选中
		final boolean[][] reCheck = new boolean[2][subList.size()];
		for(int i=0; i < subList.size(); i++) {
			Map<String, String> map = subList.get(i);
			reCheck[0][i] = map.get("recommend").toString().equals("0") ? false : true;
			reCheck[1][i] = false;
		}

		//子ListView数据
		simpleAdapter = new SimpleAdapter(this.context, subList, R.layout.list_day_sub, new String[] { "itemid", "itemname", "itemremark", "zhiprice", "pricevalue", "shouprice", "itemtypevalue", "regionname" }, new int[] { R.id.tv_day_itemid, R.id.tv_day_itemname, R.id.tv_day_itemremark, R.id.tv_day_zhiprice, R.id.tv_day_pricevalue, R.id.tv_day_shouprice, R.id.tv_day_itemtypevalue, R.id.tv_day_regionname }) {
			@Override
			public View getView(int _position, View _convertView, ViewGroup _parent) {
				View view = null;
				if(_convertView != null) {
					view = _convertView;
					reCheck[1][_position] = false;
				} else {
					view = super.getView(_position, _convertView, _parent);
					reCheck[1][_position] = true;
				}

				if(_parent.getChildCount()==_position && reCheck[1][_position]) {
				    //System.out.println("parent:" + _parent.getChildCount() + ", position1:" + position + ", position2:" + _position);
					int ztId = Integer.parseInt(subList.get(_position).get("ztid"));
					int regionId = Integer.parseInt(subList.get(_position).get("regionid"));
					TextView tvItemId = (TextView) view.findViewById(R.id.tv_day_itemid);
					final int itemId = Integer.parseInt(tvItemId.getText().toString());
					TextView tvZhuanTi = (TextView) view.findViewById(R.id.tv_day_zhuanti);
					TextView tvRegionName = (TextView) view.findViewById(R.id.tv_day_regionname);
					tvZhuanTi.setVisibility(View.GONE);
					tvRegionName.setVisibility(View.GONE);
					if(ztId > 0) {
						tvZhuanTi.setVisibility(View.VISIBLE);
						tvRegionName.setVisibility(View.GONE);
					}
					if(regionId > 0) {
						tvZhuanTi.setVisibility(View.GONE);
						tvRegionName.setVisibility(View.VISIBLE);
					}
					
					//推荐CheckBox
					final CheckBox re = (CheckBox) view.findViewById(R.id.cb_day_recommend);
					re.setChecked(reCheck[0][_position]);
					re.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
							if(re.isChecked()) {
							    itemAccess.updateItemRecommend(itemId, 1);
							} else {
								itemAccess.updateItemRecommend(itemId, 0);
							}
							itemAccess.close();
							
							sharedHelper.setLocalSync(true);
				        	sharedHelper.setSyncStatus(context.getString(R.string.txt_home_hassync));
						}
					});	

					//如果有备注显示颜色
					TextView tvItemName = (TextView) view.findViewById(R.id.tv_day_itemname);
					TextView tvItemRemark = (TextView) view.findViewById(R.id.tv_day_itemremark);
					if(!tvItemRemark.getText().equals("")) {
						tvItemName.setTextColor(activity.getResources().getColor(R.color.color_back_main));
					} else {
						tvItemName.setTextColor(activity.getResources().getColor(android.R.color.secondary_text_light));
					}
				}
				
				return view;
			}
		};				
		holder.lvDayListSub.setAdapter(simpleAdapter);

		//列表点击
		holder.lvDayListSub.setOnItemClickListener(new OnItemClickListener(){
			TextView tvItemName = null;
			TextView tvZhiPrice = null;
			TextView tvShouPrice = null;
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
		        int itemId = Integer.parseInt(map.get("itemid"));
		        
		        tvItemName = (TextView) view.findViewById(R.id.tv_day_itemname);
		        tvItemName.setTextColor(activity.getResources().getColor(R.color.color_back_main));
		        tvZhiPrice = (TextView) view.findViewById(R.id.tv_day_zhiprice);
		        tvZhiPrice.setTextColor(activity.getResources().getColor(R.color.color_back_main));
		        tvShouPrice = (TextView) view.findViewById(R.id.tv_day_shouprice);
		        tvShouPrice.setTextColor(activity.getResources().getColor(R.color.color_back_main));
		        
		        Intent intent = new Intent(activity, AddActivity.class);
				intent.putExtra("itemid", itemId);
				activity.startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
						
		return convertView;
	}
		
	//更新数据源
	protected void updateData(List<Map<String, String>> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	
	class ViewHolder {
		private TextView tvItemBuyDate;
		private TextView tvTotalPriceZhi;
		private TextView tvTotalPriceShou;
		private MyListView lvDayListSub;
	}

}
