package com.dhandyjoe.stockku.ui.employee.activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.adapter.CartAdapter
import com.dhandyjoe.stockku.databinding.ActivityCartBinding
import com.dhandyjoe.stockku.model.Transaction
import com.dhandyjoe.stockku.model.Item
import com.dhandyjoe.stockku.utils.Database
import com.dhandyjoe.stockku.utils.idrFormat
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val database = Database()
    private val firebaseDB = FirebaseFirestore.getInstance()
    private lateinit var docs: ArrayList<Item>
    private var totalPrice: Int = 0

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

        val doc = firebaseDB.collection("cart")
        doc.addSnapshotListener { snapshot, _ ->
            docs = ArrayList()
            for (document in snapshot!!) {
                docs.add(document.toObject(Item::class.java))
            }

            val data = CartAdapter(docs, this)
            showEmptyIndicator(data.isEmpty(), data)
        }

        binding.btnSaveTransaction.setOnClickListener {
            saveTransaction(docs)
            showPrintDialog()

            for (i in docs.indices) {
                database.deleteItemCart(docs[i])
            }
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
            intent.putExtra("intent_cart", docs)
            intent.putExtra("intent_totalPrice", totalPrice)
            startActivity(intent)
        })

        alert.setNegativeButton("Tidak") { dialog, which -> }
        alert.show()
    }

    private fun showRecycleView(adapter: CartAdapter) {
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
        val item = Transaction(docTransaction.id, "transaksi-$nameTransaction", dateTransaction, totalPrice)
        docTransaction.set(item)

        for (i in dataitem.indices) {
            database.saveTransaction(dataitem[i], docTransaction.id)
            database.updateStockItem(dataitem[i])
        }
    }

    private fun showEmptyIndicator(isEmpty: Boolean, adapter: CartAdapter) {
        if (isEmpty) {
            binding.ivIndicatorCart.visibility = View.VISIBLE
            binding.cvCart.visibility = View.GONE
            binding.rvListItemCart.visibility = View.GONE
        } else {
            binding.ivIndicatorCart.visibility = View.GONE
            binding.cvCart.visibility = View.VISIBLE
            showRecycleView(adapter)
            binding.rvListItemCart.visibility = View.VISIBLE
        }
    }

    fun liveTotal() {
        var total = 0
        for (item in docs) {
            total += item.price * item.totalTransaction
        }

        binding.tvLiveTotal.text = "Rp. ${idrFormat(total)}"
        totalPrice = total
    }
}