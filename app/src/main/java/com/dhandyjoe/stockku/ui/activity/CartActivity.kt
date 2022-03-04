package com.dhandyjoe.stockku.ui.activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.adapter.CartAdapter
import com.dhandyjoe.stockku.databinding.ActivityCartBinding
import com.dhandyjoe.stockku.model.Transaction
import com.dhandyjoe.stockku.model.Item
import com.dhandyjoe.stockku.util.COLLECTION_CART
import com.dhandyjoe.stockku.util.Database
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val database = Database()
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val adapter = CartAdapter()

//    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == AddItemTransactionActivity.RESULT_CODE && result.data != null) {
//            val selectedValue = result.data?.getParcelableExtra<Item>(AddItemTransactionActivity.EXTRA_SELECTED_VALUE)
//            if (selectedValue != null) {
//                val list = ArrayList<Item>()
//                list.add(selectedValue)
//                adapter.updateItem(list)
//            }
//        }
//        showEmptyIndicator(adapter.isEmpty())
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Keranjang"

//        binding.ivAddItemTransaction.setOnClickListener {
//            val moveForResultIntent = Intent(this, AddItemTransactionActivity::class.java)
//            resultLauncher.launch(moveForResultIntent)
//        }

        firebaseDB.collection(COLLECTION_CART).get()
            .addOnSuccessListener {
                val docs = ArrayList<Item>()
                for (document in it) {
                    docs.add(document.toObject(Item::class.java))
                }

                adapter.updateItem(docs)
                showEmptyIndicator(adapter.isEmpty())
            }

        binding.btnSaveTransaction.setOnClickListener {
            saveTransaction(adapter.listItemCart)
            showPrintDialog()
//            finish()
//            Toast.makeText(this, "Transaksi berhasil disimpan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPrintDialog() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Transaksi berhasil disimpan!")
        alert.setMessage("Apakah anda ingin mencetak struk?")
        alert.setPositiveButton("Print", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(this, PrintActivity::class.java)
            startActivity(intent)
        })

        alert.setNegativeButton("Tutup") { dialog, which ->

        }
        alert.show()
    }

    private fun showRecycleView() {
        binding.rvListItemCart.layoutManager = LinearLayoutManager(this)
        binding.rvListItemCart.adapter = adapter
    }

    private fun saveTransaction(dataitem: ArrayList<Item>) {
        val patternNameTransaction = "yyyyMMddHHmm"
        val simpleDateFormat1 = SimpleDateFormat(patternNameTransaction)
        val nameTransaction: String = simpleDateFormat1.format(Date())

        val patternDateTransaction = "dd MMMM yyyy hh:mm:ss"
        val simpleDateFormat2 = SimpleDateFormat(patternDateTransaction)
        val dateTransaction: String = simpleDateFormat2.format(Date())

        val docTransaction = firebaseDB.collection("transaksi").document()
        val item = Transaction(docTransaction.id, "transaksi-$nameTransaction", dateTransaction)
        docTransaction.set(item)

        for (i in dataitem.indices) {
            database.addItemTransaction(dataitem[i], docTransaction.id)
            database.updateStockItem(dataitem[i])
        }
    }

    private fun showEmptyIndicator(isEmpty: Boolean) {
        if (isEmpty) {
            binding.ivIndicatorCart.visibility = View.VISIBLE
            binding.btnSaveTransaction.visibility = View.GONE
            binding.rvListItemCart.visibility = View.GONE
        } else {
            binding.ivIndicatorCart.visibility = View.GONE
            binding.btnSaveTransaction.visibility = View.VISIBLE
            showRecycleView()
            binding.rvListItemCart.visibility = View.VISIBLE
        }
    }
}