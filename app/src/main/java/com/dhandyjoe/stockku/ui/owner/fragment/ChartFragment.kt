package com.dhandyjoe.stockku.ui.owner.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.databinding.FragmentChartBinding
import com.dhandyjoe.stockku.model.Chart
import com.dhandyjoe.stockku.model.Transaction
import com.dhandyjoe.stockku.utils.*
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import com.mazenrashed.printooth.data.converter.ArabicConverter
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChartFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var thisContext: Context
    private lateinit var binding: FragmentChartBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val transaction = ArrayList<Transaction>()
    val xAxisLabel: ArrayList<String> = ArrayList()

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
        thisContext = container!!.context
        binding = FragmentChartBinding.inflate(inflater, container, false)
        transaction.clear()

        getTransactionList("njCJysmbC8bdjSNoLc2H8dyb1Fo2")
        getTransactionList("tvFiA8aXFaXOhoPC1CpYpmauTYB2")

        binding.btnDialogYear.setOnClickListener {
            dialogYear(transaction)
//            Toast.makeText(thisContext, "${subStringYear(transaction[0].name)}", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun getTransactionList(uid: String) {
        val doc = firebaseDB.collection(COLLECTION_USERS).document(uid)
            .collection(COLLECTION_TRANSACTION)
        doc.addSnapshotListener { snapshot, _ ->
            for(docItem in snapshot!!) {
                transaction.add(docItem.toObject(Transaction::class.java))
            }
        }
    }

    private fun barChart(data: Array<Float>) {
        val legend = binding.barChart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)

        val arrayList = ArrayList<BarEntry>()
        for (count in 0..11) {
            arrayList.add( BarEntry(count.toFloat(), data[count]) )
        }

        val barDataSet = BarDataSet (arrayList, "Penjualan Bulanan")
        barDataSet.color = R.color.second

        binding.barChart.description.isEnabled = false
        binding.barChart.setFitBars(true)
        binding.barChart.data = BarData( barDataSet )
        binding.barChart.animateXY(100, 500)
        binding.barChart.setDrawGridBackground( false )
        binding.barChart.axisRight.isEnabled = false


        binding.barChart.xAxis.setDrawGridLines(false)
        binding.barChart.xAxis.setDrawAxisLine(false)

        xAxisLabel.add("Januari")
        xAxisLabel.add("Februari")
        xAxisLabel.add("Maret")
        xAxisLabel.add("April")
        xAxisLabel.add("Mei")
        xAxisLabel.add("Juni")
        xAxisLabel.add("Juli")
        xAxisLabel.add("Agustus")
        xAxisLabel.add("September")
        xAxisLabel.add("Oktober")
        xAxisLabel.add("November")
        xAxisLabel.add("Desember")

        val xAxis: XAxis = binding.barChart.xAxis
        xAxis.valueFormatter = MyAxisFormatter()

        binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.barChart.xAxis.setDrawLabels(true)
        xAxis.granularity = 1f

//        val monthArray = AxisDateFormatter (monthValue.toArray(arrayOfNulls(monthValue.size)))
//        binding.barChart.xAxis.valueFormatter = monthArray
    }

    private fun dialogYear(data: ArrayList<Transaction>) {
        // current year
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        var year = currentYear

        val dialog = Dialog(thisContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_year)

        val dataFromYear = ArrayList<Transaction>()

        val yearPicker = dialog.findViewById<NumberPicker>(R.id.np_year)
        yearPicker.minValue = 1990
        yearPicker.maxValue = 2100
        yearPicker.wrapSelectorWheel = false
        yearPicker.value = currentYear

        yearPicker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener{
            override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
                year = p2
            }
        })

        val btnPickYear = dialog.findViewById<Button>(R.id.btn_pickYear)
        btnPickYear.setOnClickListener {
            dataFromYear.clear()
            data.forEach {
                if (subStringDate(it.name, 10, 14) == year.toString()) {
                    dataFromYear.add(it)
                }
            }
            barChart(ConvertToChartData(dataFromYear))
            binding.tvMonitorYear.text = year.toString()
            dialog.cancel()
        }

        dialog.show()
    }

    inner class MyAxisFormatter : IndexAxisValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index < xAxisLabel.size) {
                xAxisLabel[index]
            } else {
                ""
            }
        }
    }
}