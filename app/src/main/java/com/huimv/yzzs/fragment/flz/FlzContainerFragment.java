package com.huimv.yzzs.fragment.flz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huimv.yzzs.R;
import com.huimv.yzzs.base.YzzsBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * FlzContainerFragment
 * 
 * @author jiangwei
 *
 */
public class FlzContainerFragment extends YzzsBaseFragment {
	//private final static String TAG = FlzContainerFragment.class.getSimpleName();
	/**
	 * viewPager
	 */
	private ViewPager vp_index;
	/**
	 * Fragment
	 */
	private Fragment flz_ssxs_fragment;
	private Fragment flz_gtlc_fragment;
	private Fragment flz_qtlc_fragment;
	/**
	 * fragment List
	 */
	private List<Fragment> viewList;// 把需要滑动的页卡添加到这个list中

	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_flz_index_fragment, null);
		initView(view);
		initData();
		return view;
	}

	private void initView(View view) {
		vp_index = (ViewPager) view.findViewById(R.id.vp_index);
	}

	private void initData() {
		viewList = new ArrayList<>();
		flz_ssxs_fragment = new FlzSsxsFragment();
		//flz_gtlc_fragment = new Flz_gtlcFragment();
		//flz_qtlc_fragment = new Flz_qtlcFragment();
		viewList.add(flz_ssxs_fragment);
		//viewList.add(flz_gtlc_fragment);
		//viewList.add(flz_qtlc_fragment);
		vp_index.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), viewList));
		vp_index.setCurrentItem(0);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> listFragments;
		public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> listFragments) {
			super(fm);
			this.listFragments = listFragments;
		}

		@Override
		public Fragment getItem(int position) {
			return listFragments.get(position);
		}

		@Override
		public int getCount() {
			return listFragments.size();
		}
		
		@Override
		public int getItemPosition(Object object) {
		return super.getItemPosition(object);
		}
	}
}
