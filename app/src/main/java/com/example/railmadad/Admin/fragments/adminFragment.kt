package com.example.railmadad.Admin.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.railmadad.Admin.adminSettings
import com.example.railmadad.Admin.viewModel.adminViewmodel
import com.example.railmadad.R
import com.example.railmadad.profileInfo
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.button.MaterialButton


class adminFragment : Fragment() {

    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart
    val viewModel: adminViewmodel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pieChart = view.findViewById(R.id.pieChart)
        barChart = view.findViewById(R.id.barChart)
        val btn = view.findViewById<Button>(R.id.btnRefreshChart)
        val total = view.findViewById<TextView>(R.id.tvTotalComplaints)
        val pending = view.findViewById<TextView>(R.id.tvActiveComplaints)
        val resolved = view.findViewById<TextView>(R.id.tvResolvedComplaints)
        val progress = view.findViewById<TextView>(R.id.tvProgressComplaints)
        val btnSettings = view.findViewById<ImageView>(R.id.btnAdminSettings)
        val btn_export = view.findViewById<MaterialButton>(R.id.btnExport)

        viewModel.getComplaintByStats(requireContext())

        viewModel.totalStat.observe(viewLifecycleOwner) { totalCount ->
            total.text = totalCount
        }
        viewModel.pendingStat.observe(viewLifecycleOwner) { pendingCount ->
            pending.text = pendingCount
        }
        viewModel.resolvedStat.observe(viewLifecycleOwner) { resolvedCount ->
            resolved.text = resolvedCount
        }

        viewModel.progressStat.observe(viewLifecycleOwner) { progressCount ->
            progress.text = progressCount
        }

        btn.setOnClickListener {
            viewModel.sortCategory() // Refresh on button click
            viewModel.getComplaintByStats(requireContext()) // Refresh stats
        }

        btn_export.setOnClickListener {
            Toast.makeText(requireContext(), "Coming Soon...", Toast.LENGTH_SHORT).show()
        }

        btnSettings.setOnClickListener {
            val intent = Intent(requireContext(), adminSettings::class.java)
            intent.putExtra("role", "Admin")
            startActivity(intent)
        }


        observeData()
        viewModel.sortCategory()
    }

     fun observeData() {
        val observer = { _: Any ->
            updateChart()
            updateBarChart()
        }

        viewModel.arrayPD.observe(viewLifecycleOwner, observer)
        viewModel.arrayFood.observe(viewLifecycleOwner, observer)
        viewModel.arrayClean.observe(viewLifecycleOwner, observer)
        viewModel.arrayStaff.observe(viewLifecycleOwner, observer)
        viewModel.arrayTech.observe(viewLifecycleOwner, observer)
        viewModel.arrayTicket.observe(viewLifecycleOwner, observer)
        viewModel.arraySafe.observe(viewLifecycleOwner, observer)
    }


    private fun updateChart() {
        val pd = viewModel.arrayPD.value?.size ?: 0
        val food = viewModel.arrayFood.value?.size ?: 0
        val clean = viewModel.arrayClean.value?.size ?: 0
        val staff = viewModel.arrayStaff.value?.size ?: 0
        val tech = viewModel.arrayTech.value?.size ?: 0
        val ticket = viewModel.arrayTicket.value?.size ?: 0
        val safe = viewModel.arraySafe.value?.size ?: 0

        val total = pd + food + clean + staff + tech + ticket + safe
        if (total == 0) return

        val entries = mutableListOf<PieEntry>()

        fun addEntryIfNotZero(value: Int, label: String) {
            if (value > 0) {
                val percent = (value * 100f) / total
                entries.add(PieEntry(percent, label))
            }
        }

        addEntryIfNotZero(pd, "Punctuality & Delays")
        addEntryIfNotZero(clean, "Cleanliness & Hygiene")
        addEntryIfNotZero(tech, "Technical Issues")
        addEntryIfNotZero(food, "Food & Catering")
        addEntryIfNotZero(staff, "Train Operation & Scheduling")
        addEntryIfNotZero(ticket, "Ticketing & Seat Allocation")
        addEntryIfNotZero(safe, "Safety & Security")

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val data = PieData(dataSet)
        data.setValueTextSize(14f)
        data.setValueTextColor(Color.WHITE)

        pieChart.legend.textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
        pieChart.legend.isWordWrapEnabled = true
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.centerText = "Complaints"
        pieChart.setCenterTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
        pieChart.setCenterTextSize(18f)
        pieChart.animateY(1000)
        pieChart.invalidate() // 🔁 Refresh
    }

    private fun updateBarChart() {
        val pd = viewModel.arrayPD.value?.size ?: 0
        val food = viewModel.arrayFood.value?.size ?: viewModel.arrayStaff.value?.size?.let { 0 + it }
        ?: 0
        val clean = viewModel.arrayClean.value?.size ?: 0
        val tech = viewModel.arrayTech.value?.size ?: 0
        val ticket = viewModel.arrayTicket.value?.size ?: 0
        val safe = viewModel.arraySafe.value?.size ?: 0


        val barEntries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        var index = 0

        fun addBar(value: Int, label: String) {

                barEntries.add(BarEntry(index.toFloat(), value.toFloat()))
                labels.add(label)
                index++

        }

        addBar(pd, "Operating Department")
        addBar(clean, "Mechanical (C&W) Department")
        addBar(tech, "IT & Electrical Department")
        addBar(food, "Catering Department")
        addBar(ticket, "Commercial Department")
        addBar(safe, "Railway Protection Force (RPF)")

        val dataSet = BarDataSet(barEntries, "Complaint Categories")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f

        val data = BarData(dataSet)
        data.barWidth = 0.9f

        barChart.data = data
        barChart.setFitBars(true)
        barChart.setDrawValueAboveBar(true)
        barChart.xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
        barChart.axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
        barChart.legend.textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
        // Setup X-axis labels
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.labelRotationAngle = -45f // ✅ tilt labels for readability
        xAxis.labelCount = labels.size
        xAxis.setLabelRotationAngle(-45f) // You can go -90f for vertical

// Optional: increase spacing
        xAxis.textSize = 10f
        barChart.setExtraBottomOffset(16f) // Gives space for rotated labels

        barChart.legend.isWordWrapEnabled = true
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.animateY(1000)
        barChart.invalidate()
    }


}