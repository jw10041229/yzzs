package com.huimv.yzzs.util.wheel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.huimv.yzzs.R;
import com.huimv.yzzs.db.entity.Da_mc;
import com.huimv.yzzs.support.general.BluetoothScanActivitySupport;
import com.huimv.yzzs.support.general.CreateZsmcActivitySupport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author jiangwei
 */
public class ScjMcDataSelectDialogUtil implements OnClickListener{
	private Button btnMcmc, btnZsmc;
	private Button btnSelectMcmc, btnSelectZsmc;
	private TextView tvJqid;
	private McZsItemSelectWheelUtil mcmcItemSelectWheelUtil;
	private McZsItemSelectWheelUtil zsmcItemSelectWheelUtil;
	private String[] mcmcArr;
	private String[] zsmcArr;
	private String selectMcmc; // 用于保存 wheel 中选择的牧场名称
	private String selectZsmc; // 用于保存 wheel 中选择的猪舍名称
	private String jqidFromDB; // 保存从数据库获取到的 机器ID（写入时使用）
	//private boolean isMcmcSelected = false; // 牧场名称被选择的标记位
	private Activity context;
	private String titleStr;
	private HashMap<String, String> paramsMap = new HashMap<String,String>();
	
	public ScjMcDataSelectDialogUtil(Activity context, String titleStr, int itemArrFlag, OnMcDataSelectConfirmListener mOnMcDataSelectConfirmListener) {
		this.context = context;
		this.titleStr = titleStr;
		this.mOnMcDataSelectConfirmListener = mOnMcDataSelectConfirmListener;
	}
	//创建接口  
    public interface OnMcDataSelectConfirmListener {
        void OnMcDataSelectConfirm(HashMap<String, String> paramsMap);
    } 
	 //声明接口对象  
    private OnMcDataSelectConfirmListener mOnMcDataSelectConfirmListener;  
    //设置监听器 也就是实例化接口 
    
	private void initView(View view) {
		btnMcmc = (Button) view.findViewById(R.id.btn_mcmc);
		btnZsmc = (Button) view.findViewById(R.id.btn_zsmc);
		btnSelectMcmc = (Button) view.findViewById(R.id.btn_select_mcmc);
		btnSelectZsmc = (Button) view.findViewById(R.id.btn_select_zsmc);
		btnSelectZsmc.setVisibility(View.GONE);
		btnSelectMcmc.setVisibility(View.GONE);
		tvJqid = (TextView) view.findViewById(R.id.tv_jqid);
		getMcmcFromDBInitMcmcArr();
	}
	
	private void initOnListeners() {
		btnMcmc.setOnClickListener(this);
		btnZsmc.setOnClickListener(this);

		mcmcItemSelectWheelUtil = new McZsItemSelectWheelUtil(context, "请选择牧场", mcmcArr,
				new McZsItemSelectWheelUtil.OnConfirmClickListener() {

					@Override
					public void onConfirm(int position) {
						//isMcmcSelected = true; // 牧场名称被选择
						selectMcmc = mcmcArr[position];
						btnMcmc.setText(selectMcmc); // 显示牧场名称
						// 选择完牧场后，让选择猪舍按钮可按
						btnSelectZsmc.setEnabled(true);
						// 初始化猪舍名称数组 zsmcArr 以显示 wheel
						initZsmcArrByMcmc(selectMcmc);
					}
				});
	}
	public void showDialog() {
		View view = context.getLayoutInflater().inflate(
				R.layout.activity_scj_upload_jqid_fragment, null);
		initView(view);
		initOnListeners();
		new AlertDialog.Builder(context).setView(view)
				.setTitle(titleStr)
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
	
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (mOnMcDataSelectConfirmListener != null) { 
									paramsMap.put("mcmc", selectMcmc);
									paramsMap.put("zsmc", selectZsmc);
									paramsMap.put("jqid", jqidFromDB);
									mOnMcDataSelectConfirmListener.OnMcDataSelectConfirm(paramsMap);
								}
								dialog.dismiss();
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
	
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						})
				.create().show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_mcmc:
				if (mcmcArr.length > 0) {
					mcmcItemSelectWheelUtil.showDialog();
				} else {
					Toast.makeText(context, "没有牧场数据，请重新登录用户", Toast.LENGTH_SHORT).show();
				}
				break;
		
			case R.id.btn_zsmc:
/*				if (isMcmcSelected) {
	
				} else {
					Toast.makeText(context, "在选择猪舍前，请先选择对应牧场", 500).show();
				}*/
				if (zsmcArr.length > 0) {
					zsmcItemSelectWheelUtil.showDialog();
				} else {
					Toast.makeText(context, "该牧场下没有猪舍", Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}
	/**
	 * 通过选择的牧场名称，初始化 猪舍名称数组
	 * 
	 * @param selectMcmc
	 */
	private void initZsmcArrByMcmc(String selectMcmc) {
		// 根据选择的牧场名称，查询该牧场名称下面所有的猪舍列表，初始化 zsmcArr 以显示 wheel
		List<String> zslbList = CreateZsmcActivitySupport.getZslbByMcmc(context, selectMcmc);
		if (zslbList.size() == 0) {
			Toast.makeText(context, "该牧场下没有猪舍", Toast.LENGTH_SHORT).show();
		} else {
			zsmcArr = new String[zslbList.size()];
			for (int i = 0; i < zslbList.size(); i++) {
				zsmcArr[i] = zslbList.get(i);
			}
		}
		// 显示猪舍名称的 wheel
		zsmcItemSelectWheelUtil = new McZsItemSelectWheelUtil(context, "请选择猪舍", zsmcArr,
				new McZsItemSelectWheelUtil.OnConfirmClickListener() {

					@Override
					public void onConfirm(int position) {
						selectZsmc = zsmcArr[position];
						btnZsmc.setText(selectZsmc); // 显示猪舍名称
						// 根据猪舍名称获取机器id
						jqidFromDB = CreateZsmcActivitySupport.getJqidByZsmc(context, selectZsmc);
						if ("".equals(jqidFromDB)) {
							tvJqid.setText("该猪舍没有对应的机器ID");
						} else {
							tvJqid.setText(jqidFromDB);
						}
					}
				});
	}
	/**
	 * 从数据库中读取牧场名称，并显示在界面上
	 */
	private void getMcmcFromDBInitMcmcArr() {
		// 得到牧场表的所有不同的牧场名称
		List<Da_mc> daMcList = BluetoothScanActivitySupport.getAllMc(context);
		if (daMcList.size() == 0) {
			Toast.makeText(context, "该账户下没有牧场数据，请检查账户信息", Toast.LENGTH_SHORT).show();
			btnMcmc.setEnabled(false);
			btnZsmc.setEnabled(false);
			mcmcArr = new String[0];
		} else {
			btnMcmc.setEnabled(true);
			btnZsmc.setEnabled(true);
			List<String> mcmcList = new ArrayList<String>();
			for (Da_mc daMc : daMcList) {
				mcmcList.add(daMc.getMcmc());
			}
			// 取出 mcidList中不重复牧场名称数据保存在 set中
			HashSet<String> set = new HashSet<String>(mcmcList);
			mcmcList.clear(); // 将去重的牧场名称重新放进 mcmcList 中
			for (String mcmc : set) {
				mcmcList.add(mcmc);
			}
			// 初始化 牧场名称 数组吗，以显示 wheel
			mcmcArr = new String[mcmcList.size()];
			for (int i = 0; i < set.size(); i++) {
				mcmcArr[i] = mcmcList.get(i);
			}
			if (mcmcArr.length > 0) {
				String mcmc = mcmcArr[0];
				btnMcmc.setText(mcmc); // 显示牧场名称
				List<String> zslbList = CreateZsmcActivitySupport.getZslbByMcmc(context, mcmc);
				if (zslbList.size() > 0) {
					String zsmc = zslbList.get(0);
					btnZsmc.setText(zsmc);
					btnZsmc.setEnabled(true);
					String jqid = CreateZsmcActivitySupport.getJqidByZsmc(context, zsmc);
					selectMcmc = mcmc;
					selectZsmc = zsmc;
					jqidFromDB = jqid;
					initZsmcArrByMcmc(selectMcmc);
				} else {
					btnZsmc.setEnabled(false);
				}
			} 
		}
	}
}
