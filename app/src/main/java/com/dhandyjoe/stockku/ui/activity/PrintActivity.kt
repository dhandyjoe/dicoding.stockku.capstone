package com.dhandyjoe.stockku.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dhandyjoe.stockku.databinding.ActivityPrintBinding
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.ui.ScanningActivity

class PrintActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrintBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Printooth.init(this)

        binding.btnSearchPrinter.setOnClickListener {
            startActivityForResult(Intent(this, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
        }

        binding.btnPrint.setOnClickListener {
            var printables = ArrayList<Printable>()
            var printable = TextPrintable.Builder()
                .setText("Hello World")
            printables.add(printable.build())
            Printooth.printer().print(printables)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK) {
            Log.d("Printasdasdasdg", "Print successfully")
        }
    }
}