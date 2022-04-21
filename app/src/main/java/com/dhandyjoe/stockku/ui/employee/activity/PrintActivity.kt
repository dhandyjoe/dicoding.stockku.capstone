package com.dhandyjoe.stockku.ui.employee.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dhandyjoe.stockku.databinding.ActivityPrintBinding
import com.dhandyjoe.stockku.model.Product
import com.dhandyjoe.stockku.model.Users
import com.dhandyjoe.stockku.utils.COLLECTION_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity

private const val TAG = "PrintActivity"

class PrintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrintBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val firebaseDB = FirebaseFirestore.getInstance()
    private var nameBranch = ""

    private val cart by lazy {
        intent.getParcelableArrayListExtra<Product>("intent_cart") as ArrayList<Product>
    }
    private val totalPrice by lazy {
        intent.getIntExtra("intent_totalPrice", 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e(TAG, cart.size.toString())
        Log.e(TAG, totalPrice.toString())

        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "").get()
            .addOnSuccessListener {
                val data = it.toObject(Users::class.java)
                nameBranch = data!!.name
            }

        Printooth.init(this)

        binding.btnSearchPrinter.setOnClickListener {
            startActivityForResult(Intent(this, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
        }

        binding.btnPrint.setOnClickListener {
            print()
        }
    }

    private fun print() {
        var printables = arrayListOf<Printable>(
            TextPrintable.Builder()
                .setText("")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
                .setNewLinesAfter(2)
                .build(),
            TextPrintable.Builder()
                .setText("Sepatu Kakikaki")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setNewLinesAfter(2)
                .build(),
            TextPrintable.Builder()
                .setText("Salatiga")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_NORMAL)
                .setNewLinesAfter(1)
                .build(),
            TextPrintable.Builder()
                .setText("================================")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(3)
                .build()
        )

//        cart.forEach {
//            printables.add(
//                TextPrintable.Builder()
//                    .setText(it.name)
//                    .setNewLinesAfter(1)
//                    .build(),
//            )
//            printables.add(
//                TextPrintable.Builder()
//                    .setText("x${it.totalTransaction}")
//                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
//                    .setNewLinesAfter(1)
//                    .build(),
//            )
//            printables.add(
//                TextPrintable.Builder()
//                    .setText("${it.price.toInt()*it.totalTransaction}")
//                    .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
//                    .setNewLinesAfter(1)
//                    .build(),
//            )
//            printables.add(
//                TextPrintable.Builder()
//                    .setText("Ukuran : ${it.size}")
//                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
//                    .setNewLinesAfter(2)
//                    .build(),
//            )
//        }

        printables.add(
            TextPrintable.Builder()
                .setText("--------------------------------")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(2)
                .build(),
        )

        printables.add(
            TextPrintable.Builder()
                .setText("Total harga : $totalPrice")
                .setNewLinesAfter(2)
                .build(),
        )

        printables.add(
            TextPrintable.Builder()
                .setText("================================")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(2)
                .build(),
        )

        printables.add(
            TextPrintable.Builder()
                .setText("Cabang : $nameBranch")
                .setNewLinesAfter(6)
                .build(),
        )

        Printooth.printer().print(printables)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK) {
            Log.d("Printasdasdasdg", "Print successfully")
        }
    }
}