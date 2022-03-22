package com.dhandyjoe.stockku.ui.owner.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.databinding.FragmentChartBinding
import com.dhandyjoe.stockku.databinding.FragmentTransactionOwnerBinding
import com.dhandyjoe.stockku.utils.AxisDateFormatter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChartFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentChartBinding
    private var monthValue = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartBinding.inflate(inflater, container, false)

        monthValue.add("Januari")
        monthValue.add("Februari")
        monthValue.add("Maret")
        monthValue.add("April")

        barChart()

        return binding.root
    }

    private fun barChart() {
        val legend = binding.barChart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)

        val arrayList = ArrayList<BarEntry>()
        arrayList.add( BarEntry(0f, 150F) )
        arrayList.add( BarEntry(1f, 80F) )
        arrayList.add( BarEntry(2f, 500F) )
        arrayList.add( BarEntry(3f, 180F) )


        val barDataSet = BarDataSet (arrayList, "Penjualan 2020")
        barDataSet.color = R.color.second

        binding.barChart.description.isEnabled = false
        binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.barChart.data = BarData( barDataSet )
        binding.barChart.animateXY(100, 500)
        binding.barChart.setDrawGridBackground( false )
//
//        val monthArray = AxisDateFormatter (monthValue.toArray(arrayOfNulls(monthValue.size)))
//        binding.barChart.xAxis.valueFormatter = monthArray

    }
}