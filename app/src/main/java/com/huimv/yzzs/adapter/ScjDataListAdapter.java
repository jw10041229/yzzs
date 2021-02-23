package com.huimv.yzzs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huimv.yzzs.R;
import com.huimv.yzzs.adapter.base.BaseRecyclerAdapter;
import com.huimv.yzzs.db.entity.Da_scj;

/**
 * 手持机数据列表
 * Created by jw
 * on 2016/10/26.
 */

public class ScjDataListAdapter extends BaseRecyclerAdapter<Da_scj> implements BaseRecyclerAdapter.OnLoadingHeaderCallBack {


    public ScjDataListAdapter(Context context, int mode) {
        super(context, mode);
        setOnLoadingHeaderCallBack(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent) {
        return new HeaderViewHolder(mHeaderView);
    }

    @Override
    public void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ScjDataViewHolder(mInflater.inflate(R.layout.scj_data_list_item, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Da_scj item, int position) {
        ScjDataViewHolder vh = (ScjDataViewHolder) holder;

        TextView rfid = vh.tv_rfid;
        TextView tw = vh.tv_tw;
        TextView cjsj = vh.tv_cjsj;
        rfid.setText(item.getRfid());
        tw.setText(item.getTw());
        cjsj.setText(item.getCjsj());
    }

    private static class ScjDataViewHolder extends RecyclerView.ViewHolder {
        TextView tv_rfid,tv_tw,tv_cjsj;

        public ScjDataViewHolder(View itemView) {
            super(itemView);
            tv_rfid = (TextView) itemView.findViewById(R.id.tv_rfid);
            tv_tw = (TextView) itemView.findViewById(R.id.tv_tw);
            tv_cjsj = (TextView) itemView.findViewById(R.id.tv_cjsj);
        }
    }
}
