package com.huimv.yzzs.fragment.scj;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.android.basic.util.DateUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.adapter.ScjDataListAdapter;
import com.huimv.yzzs.adapter.base.BaseRecyclerAdapter;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.db.entity.Da_scj;
import com.huimv.yzzs.support.scj.ScjSsxsFragmentSupport;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.widget.RecyclerRefreshLayout;

import java.util.List;

@SuppressLint("DefaultLocale")
public class ScjDataListFragment extends YzzsBaseFragment implements OnClickListener,
		RecyclerRefreshLayout.SuperRefreshLayoutListener,
		BaseRecyclerAdapter.OnItemClickListener{
	private ScjDataListAdapter mScjDataListAdapter;
	private RecyclerView mRecyclerView;
	private ScjSsxsFragmentSupport mScj_ssxsFragmentSupport = new ScjSsxsFragmentSupport();
	RecyclerRefreshLayout mRefreshLayout;
	private boolean isRefreshing = false;
	private SearchView mSearchView;
	private int page = 1;
	private TextView tv_clean_data;
	private EditText et_RFID;
	private EditText et_temp;
	private Button btn_save;
	private TextView tv_time_set_data;
	private SharePreferenceUtil mSpUtil;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_scj_fragment_datalist, null);
		mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
		mRefreshLayout = (RecyclerRefreshLayout) view.findViewById(R.id.refreshLayout);
		mSearchView = (SearchView) view.findViewById(R.id.view_searcher);
		tv_clean_data = (TextView) view.findViewById(R.id.tv_clean_data);
		et_RFID = (EditText) view.findViewById(R.id.et_RFID);
		et_temp = (EditText) view.findViewById(R.id.et_temp);
		btn_save = (Button) view.findViewById(R.id.btn_save);
		tv_time_set_data = (TextView) view.findViewById(R.id.tv_time_set_data);
		initData();
		initListener();
		return view;
	}

	private void initListener() {
		btn_save.setOnClickListener(this);
		tv_clean_data.setOnClickListener(this);
		tv_time_set_data.setOnClickListener(this);
		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				mSearchView.clearFocus();
				return doSearch(query);
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (CommonUtil.isEmpty(newText)) {
					doSearch("");
				}
				return false;
			}
		});
		mSearchView.post(new Runnable() {
			@Override
			public void run() {
				mSearchView.setIconified(false);
			}
		});
	}

	private boolean doSearch(String query) {
		List <Da_scj> list;
		String mSearchText = query.trim();
		list = mScj_ssxsFragmentSupport.getScjByRfid(getActivity(),mSearchText);
		mScjDataListAdapter.resetItem(list);
		return true;
	}

	private void initData() {
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		page = 1;
		int mHeaderView = 1;
		int mode = mHeaderView == 0 ? BaseRecyclerAdapter.BOTH_HEADER_FOOTER : BaseRecyclerAdapter.ONLY_FOOTER;
		mScjDataListAdapter = new ScjDataListAdapter(getActivity(),mode);
		mScjDataListAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, false);
		mRecyclerView.setAdapter(mScjDataListAdapter);
		mScjDataListAdapter.setHeaderView(null);
		mScjDataListAdapter.setOnItemClickListener(this);
		mRefreshLayout.setSuperRefreshLayoutListener(this);
		mScjDataListAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, false);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (RecyclerView.SCROLL_STATE_DRAGGING == newState && getActivity() != null
						&& getActivity().getCurrentFocus() != null) {
					//TDevice.hideSoftKeyboard(getActivity().getCurrentFocus());
				}
			}
		});
		mRefreshLayout.setColorSchemeResources(
				R.color.swiperefresh_color1, R.color.swiperefresh_color2,
				R.color.swiperefresh_color3, R.color.swiperefresh_color4);
		List<Da_scj> alist = mScj_ssxsFragmentSupport.getAllScjLimit(getActivity(),page * 15);
		mScjDataListAdapter.addAll(alist);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_clean_data:
				cleanDataDialog();
				break;
			case R.id.btn_save:
				checkIntputData();
				break;
			case R.id.tv_time_set_data:
				showTimeSetDialog();
				break;
		}

	}

	private void checkIntputData() {
		String RFIDValue = et_RFID.getText().toString().trim();
		String tempValue = et_temp.getText().toString().trim();
		if (CommonUtil.isNotEmpty(RFIDValue) && CommonUtil.isNotEmpty(tempValue))
			saveDataIntoDB(RFIDValue, tempValue);
		else {
			ToastMsg(getActivity(),"输入数据不能为空");
		}
	}

	private void saveDataIntoDB(String RFIDValue, String tempValue) {
		Da_scj da_scj = new Da_scj();
		da_scj.setCjsj(DateUtil.parser(System.currentTimeMillis()));
		da_scj.setRfid(RFIDValue);
		da_scj.setTw(tempValue);
		mScj_ssxsFragmentSupport.insertScj(getActivity(),da_scj);
		onRefreshing();
		//mScjDataListAdapter.notifyDataSetChanged();
	}

	/**
	 * 清空数据
	 */
	private void cleanDataDialog () {
		new AlertDialog.Builder(getActivity()).setMessage("数据删除后不可恢复?").setTitle("删除数据提示")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						mScj_ssxsFragmentSupport.deleteAllScj(getActivity());
						ToastMsg(getActivity(),"数据已删除");
						mScjDataListAdapter.clear();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create().show();
	}
	@Override
	public void onRefreshing() {
		page = 1;
		isRefreshing = true;
		mRefreshLayout.setRefreshing(true);
		List<Da_scj> alist = mScj_ssxsFragmentSupport.getAllScjLimit(getActivity(),page * 15);
		mScjDataListAdapter.resetItem(alist);
		mRefreshLayout.onComplete();
		isRefreshing = false;
	}

	@Override
	public void onLoadMore() {
		page ++;
		mScjDataListAdapter.setState(isRefreshing ? BaseRecyclerAdapter.STATE_HIDE : BaseRecyclerAdapter.STATE_LOADING, true);
		final List list = mScj_ssxsFragmentSupport.getAllScjLimit(getActivity(),page * 15);
		new Handler().postDelayed(new Runnable(){
			public void run() {
				mScjDataListAdapter.resetItem(list);
				if (list.size() == mScj_ssxsFragmentSupport.getAllScj(getActivity()).size()) {
					mScjDataListAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
				} else {
					mScjDataListAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, true);
					mRefreshLayout.setCanLoadMore(true);
				}
				mRefreshLayout.onComplete();
			}
		}, 100);
	}

	@Override
	public void onItemClick(int position, long itemId) {

	}

	public void showTimeSetDialog() {
		final LinearLayout ll = new LinearLayout(getActivity());
		final EditText et_time_value = new EditText(getActivity());
		et_time_value.setInputType(InputType.TYPE_CLASS_PHONE);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(et_time_value);
		et_time_value.setGravity(Gravity.CENTER);
		et_time_value.setHint("时间设置(毫秒)");
		et_time_value.setHeight(200);
		et_time_value.setText(mSpUtil.getScjTime());
		new AlertDialog.Builder(getActivity()).setTitle("时间设置").setView(ll)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mSpUtil.setScjTime(et_time_value.getText().toString().trim());
						ToastMsg(getActivity(),"时间设置成功");
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create().show();
	}
}
