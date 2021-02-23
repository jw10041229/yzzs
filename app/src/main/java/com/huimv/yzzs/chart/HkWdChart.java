package com.huimv.yzzs.chart;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.db.entity.Da_wendu;
import com.huimv.yzzs.support.general.IndexActivitySupport;
import com.huimv.yzzs.util.YzzsCommonUtil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;

/**
 * 温度图表
 * 
 * @author jiangwei
 * 
 */
public class HkWdChart extends AbstractDemoChart {
	public static String[] x;// x是用来显示的。xx是用来计算的
	private List<Da_wendu> wenduList;
	double[] xx;
	private double[] wenduY;
	private DecimalFormat df00 = new DecimalFormat(".00");
	private double max = 0;
	private double min = 0;
	private int cgqSx;
	public HkWdChart(int cgqSx) {//传感器顺序
		this.cgqSx = cgqSx;
	}
	public HkWdChart() {
	}
	@Override
	public LineChart execute(Context context) {
		wenduList = IndexActivitySupport.loadAllWenduListBySx(context,String.valueOf(cgqSx));
		String labelTitle = "未知地址";
		try {
			labelTitle = context.getResources().getStringArray(R.array.hk_sensor_item_array_location) 
					[Integer.valueOf(IndexActivity.CGQ_WZ[cgqSx - 1])];
		} catch (Exception e) {
		}
		x = new String[wenduList.size()];
		xx = new double[wenduList.size()];
		wenduY = new double[wenduList.size()];
		for (int i = 0; i < wenduList.size(); i++) {
			x[i] = (wenduList.get(i).getHksj());
			xx[i] = i;
			wenduY[i] = Double.valueOf(df00.format(Double.valueOf(wenduList
					.get(i).getHkwd())));
		}
		if (wenduY.length > 0) {
			max =YzzsCommonUtil.getMaxMin(wenduY, 1);
			min =YzzsCommonUtil.getMaxMin(wenduY, 0);
		}
		if (max == 0 || min == 0) {
			max = 1;
			min = 0;
		}
		String[] titles = new String[] { "温度—"+ labelTitle};
		List<double[]> xList = new ArrayList<double[]>();
		for (int i = 0; i < titles.length; i++) {
			xList.add(xx);
		}
		List<double[]> values = new ArrayList<double[]>();
		values.add(wenduY);

		int[] colors = new int[] { 0xFF3a8ecf };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
					.setFillPoints(true);
		}

		setChartSettings(renderer, "温度", "时间", "温度℃", 0, wenduList.size(), min -1,
				max + 1, Color.LTGRAY, Color.LTGRAY);
		renderer.setZoomLimits(new double[] { 0, 10,
				min - 1, max + 1 });// 设置缩放的范围
		renderer.setPanLimits(new double[] { 0, 10,
				min - 1, min + 1 });// 拖动时X，Y轴云许的最大最小值
		for (int i = 0; i < x.length; i++) {
			renderer.addXTextLabel(xx[i], x[i]); // 循环添加Xlabel其中显示的label放在years数组中
		}
		renderer.setXLabels(0);
		renderer.setYLabels(5);// 设置y轴显示10个点,根据setChartSettings的最大值和最小值自动计算点的间隔
		renderer.setYAxisMax(max + 1); // 设置Y轴最大值
		renderer.setYAxisMin(min - 1); // 设置Y轴最小值
		int size[] = context.getResources().getIntArray(R.array.chart_TextSize_array);
		renderer.setAxisTitleTextSize(size[0]); // 设置坐标轴标题文本大小
		renderer.setChartTitleTextSize(size[1]); // 设置图表标题文本大小
		renderer.setLabelsTextSize(size[2]); // 设置轴标签文本大小
		renderer.setLegendTextSize(size[3]); // 设置图例文本大小
		renderer.setLegendHeight(size[4]);//图列下面的留白
		renderer.setAxesColor(0X55000000);// 设置X.y轴颜色
		renderer.setTextTypeface(Typeface.SERIF);
		renderer.setGridColor(0X55000000);// 设置网格颜色
		renderer.setLabelsColor(0xff000000);// 设置轴刻度标题颜色
		renderer.setXLabelsColor(Color.BLACK);// 设置X轴刻度颜色
		renderer.setYLabelsColor(0, Color.BLACK);// 设置Y轴刻度颜色
		// renderer.setChartTitle(title);
		renderer.setMargins(context.getResources().getIntArray(R.array.chart_Margins_array)); // 设置4边留白
		renderer.setPanEnabled(true, true); // 设置x,y坐标轴不会因用户划动屏幕而移动
		renderer.setMarginsColor(Color.WHITE);// 设置4边留白透明
		renderer.setBackgroundColor(Color.WHITE); // 设置背景色透明
		renderer.setApplyBackgroundColor(true); // 使背景色生效
		renderer.setClickEnabled(true);
		renderer.setXLabelsAlign(Align.CENTER);// 刻度线与刻度标注之间的相对位置关系
		renderer.setYLabelsAlign(Align.LEFT);
		renderer.setZoomButtonsVisible(false);// 是否显示放大缩小按钮
		// renderer.setChartValuesTextSize(15);//柱体上方字的大小
		renderer.setShowGrid(true);// 是否显示网格
		renderer.setFitLegend(false);// 设置自动按比例缩放
		renderer.setSelectableBuffer(30);
		return new LineChart(buildDataset(titles, xList, values), renderer);
	}
}
