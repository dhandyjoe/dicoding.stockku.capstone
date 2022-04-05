package com.dhandyjoe.stockku.ui.employee.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.adapter.CategoryAdapter
import com.dhandyjoe.stockku.databinding.FragmentCategoryBinding
import com.dhandyjoe.stockku.model.Category
import com.dhandyjoe.stockku.ui.employee.activity.EXTRA_CATEGORY_ID
import com.dhandyjoe.stockku.ui.employee.activity.EXTRA_ITEM_CATEGORY
import com.dhandyjoe.stockku.ui.employee.activity.ProductActivity
import com.dhandyjoe.stockku.utils.*
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CategoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var thisContext: Context
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val database = Database()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var currentIdCategory = ""

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
        binding = FragmentCategoryBinding.inflate(inflater, container, false)

        getCategory()

        binding.favAddCategory.setOnClickListener {
            cartDialogCategory()
        }

        binding.ivDeleteCategory.setOnClickListener {
            database.deleteCategory(currentUser?.uid ?: "", currentIdCategory)
            database.deleteItemCategory(currentUser?.uid ?: "", currentIdCategory)
            binding.tvMonitorCategory.text = ""
            binding.ivDeleteCategory.visibility = View.GONE
            binding.tvStatusCategory.visibility = View.VISIBLE
        }

        return binding.root
    }

    private fun getCategory() {
        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY)
            .addSnapshotListener { snapshot, _ ->
                val categoryList = ArrayList<Category>()

                for(docItem in snapshot!!) {
                    categoryList.add(docItem.toObject(Category::class.java))
                }

                showListCategory(categoryList)
        }

    }

    private fun getItemCategory(documentId: String) {
        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY)
            .document(documentId)
            .collection(COLLECTION_ITEM_CATEGORY)
            .addSnapshotListener { snapshot, _ ->
                val itemCategoryList = ArrayList<Category>()

                for(docItem in snapshot!!) {
                    itemCategoryList.add(docItem.toObject(Category::class.java))
                }

                showListItemCategory(itemCategoryList)
            }

    }

    private fun showListCategory(data: ArrayList<Category>) {
        binding.rvCategory.layoutManager = LinearLayoutManager(context)
        val data = CategoryAdapter(data)
        binding.rvCategory.adapter = data

        data.setOnItemClickCallback(object : CategoryAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Category) {
                binding.ivDeleteCategory.visibility = View.VISIBLE
                binding.tvStatusCategory.visibility = View.GONE
                binding.rvItemCategory.visibility = View.VISIBLE
                binding.tvMonitorCategory.text = data.name
                getItemCategory(data.id)
                currentIdCategory = data.id
//                Toast.makeText(thisContext, data.id, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showListItemCategory(data: ArrayList<Category>) {
        binding.rvItemCategory.layoutManager = GridLayoutManager(context, 3)
        val data = CategoryAdapter(data)
        binding.rvItemCategory.adapter = data

        data.setOnItemClickCallback(object : CategoryAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Category) {
                val intent = Intent(requireContext(), ProductActivity::class.java)
                intent.putExtra(EXTRA_CATEGORY_ID, currentIdCategory)
                intent.putExtra(EXTRA_ITEM_CATEGORY, data)
                startActivity(intent)
            }
        })
    }

    private fun cartDialogCategory() {
        val cartDialog =  layoutInflater.inflate(R.layout.dialog_category, null)
        val dialog = Dialog(thisContext, R.style.CustomDialog)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
        }
        cartDialog.findViewById<MaterialCardView>(R.id.mv_category).setOnClickListener {
            dialog.cancel()
            cartDialogAddCategory()
        }
        cartDialog.findViewById<MaterialCardView>(R.id.mv_itemCategory).setOnClickListener {
            dialog.cancel()
            cartDialogAddItemCategory()
        }

        dialog.show()
    }

    private fun cartDialogAddCategory() {
        val cartDialog =  layoutInflater.inflate(R.layout.dialog_add_category, null)
        val dialog = Dialog(thisContext, R.style.CustomDialog)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
            setTitle("Tambah kategori")
        }
        cartDialog.findViewById<Button>(R.id.btn_addCategory).setOnClickListener {
            database.addCategory(
                currentUser?.uid ?: "",
                cartDialog.findViewById<EditText>(R.id.et_inputCategory).text.toString()
            )

            dialog.cancel()
//            categoryList.clear()
        }

        dialog.show()
    }

    private fun cartDialogAddItemCategory() {
        val listNameCategory = ArrayList<String>()
        val categoryList = ArrayList<Category>()

        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY)
            .addSnapshotListener { snapshot, _ ->

                for(docItem in snapshot!!) {
                    categoryList.add(docItem.toObject(Category::class.java))
                }

                categoryList.forEach {
                    listNameCategory.add(it.name)
                }
            }

        val cartDialog =  layoutInflater.inflate(R.layout.dialog_add_item_category, null)
        val dialog = Dialog(thisContext, R.style.CustomDialog)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
            setTitle("Tambah kategori")
        }

        val getValue = cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listCategory)
        getValue.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listNameCategory))

        cartDialog.findViewById<Button>(R.id.btn_addCategory).setOnClickListener {
            database.addItemCategory(
                currentUser?.uid ?: "",
                convertNameToIdCategory(getValue.text.toString(), categoryList),
                cartDialog.findViewById<EditText>(R.id.et_inputItemCategory).text.toString()
            )

            dialog.cancel()

//            itemCategoryList.clear()
//            Toast.makeText(requireContext(), currentItemCategory, Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }
}