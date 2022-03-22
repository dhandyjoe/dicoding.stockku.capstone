package com.dhandyjoe.stockku.ui.owner.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.adapter.TransactionAdapter
import com.dhandyjoe.stockku.databinding.FragmentTransactionOwnerBinding
import com.dhandyjoe.stockku.model.Transaction
import com.dhandyjoe.stockku.ui.owner.activity.DetailTransactionOwnerActivity
import com.dhandyjoe.stockku.utils.COLLECTION_TRANSACTION
import com.dhandyjoe.stockku.utils.COLLECTION_USERS
import com.dhandyjoe.stockku.utils.idrFormat
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.VerticalAlignment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TransactionOwnerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentTransactionOwnerBinding
    private lateinit var thisContext: Context
    private val firebaseDB = FirebaseFirestore.getInstance()
    private var listItemSearch = ArrayList<Transaction>()

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
        binding = FragmentTransactionOwnerBinding.inflate(inflater, container, false)

        spinnerTransaction()

        return binding.root
    }

    private fun spinnerTransaction() {
        val list = resources.getStringArray(R.array.spinner_transaction)
        val adapter = ArrayAdapter(thisContext, android.R.layout.simple_spinner_item, list)
        binding.spTransaction.adapter = adapter

        binding.spTransaction.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    getTransactionList("njCJysmbC8bdjSNoLc2H8dyb1Fo2", "Kemiri")
                } else if (position == 1) {
                    getTransactionList("tvFiA8aXFaXOhoPC1CpYpmauTYB2", "Tegalrejo")
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun getTransactionList(uid: String, branch: String) {
        val doc = firebaseDB.collection(COLLECTION_USERS).document(uid)
            .collection(COLLECTION_TRANSACTION)
        doc.addSnapshotListener { snapshot, _ ->
            val user = ArrayList<Transaction>()

            for(docItem in snapshot!!) {
                user.add(docItem.toObject(Transaction::class.java))
            }

            if (user.size > 0) {
                showRecycleView(user, uid)
            } else {
                binding.rvListTransaction.visibility = View.GONE
            }

            binding.favPrintPDF.setOnClickListener {
                printPDF(branch, user)
//                Toast.makeText(thisContext, "${user.size}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRecycleView(data: ArrayList<Transaction>, uid: String) {
        binding.rvListTransaction.layoutManager = LinearLayoutManager(context)
        val data = TransactionAdapter(data)
        binding.rvListTransaction.adapter = data
        binding.rvListTransaction.visibility = View.VISIBLE

        data.setOnItemClickCallback(object : TransactionAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Transaction) {
                val intent = Intent(activity, DetailTransactionOwnerActivity::class.java)
                intent.putExtra(DetailTransactionOwnerActivity.EXTRA_DATA, data)
                intent.putExtra(DetailTransactionOwnerActivity.EXTRA_UID, uid)
                startActivity(intent)
            }
        })
    }

    private fun printPDF(branch: String, data: ArrayList<Transaction>) {
        // formatting name PDF
        val patternNameTransaction = "yyyyMMdd"
        val simpleDateFormat1 = SimpleDateFormat(patternNameTransaction)
        val nameDocument: String = simpleDateFormat1.format(Date())

        val file = File(thisContext.getExternalFilesDir("/"), "$nameDocument.pdf")

        val writer = PdfWriter(file)
        val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(writer)
        val document = Document(pdfDocument)

        var counter = 1

        // Table
        val table = Table(floatArrayOf(40F, 180F, 160F, 120F))
            .setHorizontalAlignment(HorizontalAlignment.CENTER)

        // New line
        val newline = Paragraph(Text("\n"))

        val paragraph1 = Paragraph("SEPATU KAKIKAKI")
            .setTextAlignment(TextAlignment.CENTER)
            .setBold()
            .setFontSize(20F)

        val paragraph2 = Paragraph("Report Transaksi")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(17F)

        val paragraph3 = Paragraph("Cabang : $branch")
            .setTextAlignment(TextAlignment.RIGHT)
            .setItalic()
            .setFontSize(15F)

        // Line-separator
        val line = LineSeparator(SolidLine())

        val cell1 = Cell(1, 1)
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .add(Paragraph("No"))
        val cell2 = Cell(1, 1)
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .add(Paragraph("No. Transaksi"))
        val cell3 = Cell(1, 1)
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .add(Paragraph("Tanggal Transaksi"))
        val cell4 = Cell(1, 1)
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .add(Paragraph("Total"))

        table.addCell(cell1)
        table.addCell(cell2)
        table.addCell(cell3)
        table.addCell(cell4)

        data.forEach {
            table.addCell(Cell(1, 1)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .add(Paragraph("$counter")))
            table.addCell(Cell(1, 1)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .add(Paragraph(it.name)))
            table.addCell(Cell(1, 1)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .add(Paragraph(it.date)))
            table.addCell(Cell(1, 1)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .add(Paragraph("Rp. ${idrFormat(it.totalPrice)}")))

            counter++
        }


        // add layout in PDF
        document.add(paragraph1)
        document.add(paragraph2)
        document.add(newline)
        document.add(paragraph3)
        document.add(line)
        document.add(newline)
        document.add(table)
        document.close()
        Toast.makeText(thisContext, "PDF created!", Toast.LENGTH_SHORT).show()
    }
}