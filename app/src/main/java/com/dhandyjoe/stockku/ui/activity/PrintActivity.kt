package com.dhandyjoe.stockku.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dhandyjoe.stockku.databinding.ActivityPrintBinding
import com.dhandyjoe.stockku.model.Item
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity

private const val TAG = "PrintActivity"

class PrintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrintBinding
    private val cart by lazy {
        intent.getParcelableArrayListExtra<Item>("intent_cart") as ArrayList<Item>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e(TAG, cart.size.toString())

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

        cart.forEach {
            printables.add(
                TextPrintable.Builder()
                    .setText(it.name)
                    .setNewLinesAfter(1)
                    .build(),
            )
            printables.add(
                TextPrintable.Builder()
                    .setText("x${it.totalTransaction}")
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setNewLinesAfter(1)
                    .build(),
            )
            printables.add(
                TextPrintable.Builder()
                    .setText("${it.price.toInt()*it.totalTransaction}")
                    .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                    .setNewLinesAfter(1)
                    .build(),
            )
            printables.add(
                TextPrintable.Builder()
                    .setText("Ukuran : ${it.size}")
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setNewLinesAfter(2)
                    .build(),
            )
        }

        printables.add(
            TextPrintable.Builder()
                .setText("--------------------------------")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(2)
                .build(),
        )

        printables.add(
            TextPrintable.Builder()
                .setText("Total harga : ")
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
                .setText("Cabang : ")
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