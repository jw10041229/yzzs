package com.huimv.yzzs.fragment.hk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huimv.yzzs.R;
import com.huimv.yzzs.base.YzzsBaseFragment;

/**
 * Created by jiangwei-pc on 2017/2/8.
 */

public class TestFragment extends YzzsBaseFragment {
    @Override
    public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test_fragment, null);
    }
}
