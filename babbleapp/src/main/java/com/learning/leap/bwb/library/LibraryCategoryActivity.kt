package com.learning.leap.bwb.library

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.learning.leap.bwb.R
import com.learning.leap.bwb.model.BabbleTip
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_library_category.*

class LibraryCategoryActivity:Activity() {
    companion object {
        val SUB_CATEGORY = "SUB_CATEGORY"
        val HAS_SUB_CATEGORY = "HAS_SUB_CATEGORY"
    }

    val disposables = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_category)
        if(intent.getBooleanExtra(HAS_SUB_CATEGORY,false)){
            librarySubCategoryTextView.text = intent.getStringExtra(SUB_CATEGORY)
            librarySubCategoryTextView.visibility = View.VISIBLE
            libraryCategoryLinearLayout.visibility = View.GONE
            intent.getStringExtra(SUB_CATEGORY)?.let {
                getSubCategory(it)
            }
        }else{
            getAllCategories()
        }
    }

    private fun getAllCategories() {
        val hashMap = mutableMapOf<String, List<BabbleTip>>()
        Realm.getDefaultInstance().where(BabbleTip::class.java).findAllAsync().addChangeListener { collection, changeSet ->
            createCategoryList(false,collection, hashMap)
        }
    }

    private fun getSubCategory(category:String){
        val hashMap = mutableMapOf<String, List<BabbleTip>>()
        Realm.getDefaultInstance().where(BabbleTip::class.java).equalTo("category",category).findAllAsync().addChangeListener { collection, changeSet ->
            createSubCategoryList(collection,hashMap)
        }
    }

    private fun getCategoryString(isSubCategory: Boolean, babbleTip: BabbleTip):String{
        return if (isSubCategory) babbleTip.subcategory else babbleTip.category
    }

    private fun createCategoryList(isSubCategory:Boolean,collection: RealmResults<BabbleTip>?, hashMap: MutableMap<String, List<BabbleTip>>) {
        val disposable = Observable.fromIterable(collection).groupBy { it.category }.flatMapSingle {
            it.toList()
        }.subscribe({
            if (it.isNotEmpty()) {
                hashMap[it.get(0).category] = it
            }
        }, {
            it.printStackTrace()
        }, {
            createList(hashMap)
        })

        disposables.add(disposable)
    }

    private fun createSubCategoryList(collection: RealmResults<BabbleTip>?, hashMap: MutableMap<String, List<BabbleTip>>){
        val disposable = Observable.fromIterable(collection).groupBy { it.subcategory }.flatMapSingle {
            it.toList()
        }.subscribe({
            if (it.isNotEmpty()) {
                hashMap[it.get(0).subcategory] = it
            }
        }, {
            it.printStackTrace()
        }, {
            createList(hashMap)
        })

        disposables.add(disposable)
    }

    private fun createList(hashMap: MutableMap<String, List<BabbleTip>>) {
        var size = 0
        val recyclerViewList = mutableListOf<String>()
        val categoriesStrings = mutableListOf<String>()
        hashMap.forEach {
            if (it.key.isNotEmpty()) {
                size += it.value.size
                categoriesStrings.add(it.key)
                recyclerViewList.add("${it.key}(${it.value.size})")
            }
        }
        recyclerViewList.add(0, "All($size)")
        categoriesStrings.add(0,"All")
        val favoriteString = addFavoriteToList()
        favoriteString?.let {
            categoriesStrings.add(1,"Favorites")
            recyclerViewList.add(1,it)
        }
        val adapter = LibraryCategoryAdapter(recyclerViewList)
        libraryCategoryRecyclerView.adapter = adapter
        libraryCategoryRecyclerView.addItemDecoration(DividerItemDecoration(this,LinearLayoutManager.HORIZONTAL))
        libraryCategoryRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter.itemOnClick = {category,position ->
            if (category.contains("All")){
              if (intent.getBooleanExtra(HAS_SUB_CATEGORY,false)){
                  val categoryIntent = Intent(this,LibraryActivity::class.java)
                  categoryIntent.putExtra(LibraryActivity.IS_CATEGORY,true)
                  categoryIntent.putExtra(LibraryActivity.LIBRARY_CATEGORY,intent.getStringExtra(LibraryCategoryActivity.SUB_CATEGORY))
                  startActivity(categoryIntent)
              }else{
                  val allIntent = Intent(this,LibraryActivity::class.java)
                  allIntent.putExtra(LibraryActivity.IS_ALL,true)
                  startActivity(allIntent)
              }

            }else if ( category.contains("Favorites")){
                val favoriteIntent = Intent(this,LibraryActivity::class.java)
                favoriteIntent.putExtra(LibraryActivity.IS_FAVORITE,true)
                startActivity(favoriteIntent)
            } else {
                if (!intent.getBooleanExtra(HAS_SUB_CATEGORY,false)) {
                    val subCategoryIntent = Intent(this, LibraryCategoryActivity::class.java)
                    subCategoryIntent.putExtra(HAS_SUB_CATEGORY, true)
                    subCategoryIntent.putExtra(SUB_CATEGORY, categoriesStrings[position])
                    startActivity(subCategoryIntent)
                }else{
                    val subCategoryIntent = Intent(this,LibraryActivity::class.java)
                    subCategoryIntent.putExtra(LibraryActivity.IS_SUB_CATEGORY,true)
                    subCategoryIntent.putExtra(LibraryActivity.LIBRARY_SUB_CATEGORY,categoriesStrings[position])
                    startActivity(subCategoryIntent)
                }
            }
        }
    }

    private fun addFavoriteToList():String?{
        val results = Realm.getDefaultInstance().where(BabbleTip::class.java).equalTo("favorite",true).findAll()
        if (results.isEmpty() || intent.getBooleanExtra(HAS_SUB_CATEGORY,false)){
            return null
        }else{
            return "Favorites(${results.size})"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}