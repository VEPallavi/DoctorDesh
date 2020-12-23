package com.doctordesh.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.doctordesh.R
import android.R.attr.fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.doctordesh.fragments.SearchFragment
import kotlinx.android.synthetic.main.activity_design.*
import java.util.Map


class DesignActivity : AppCompatActivity() {


    var itemMap = LinkedHashMap<String, List<String>>()
    var mapSet: MutableSet<MutableMap.MutableEntry<String, List<String>>>? = null
    var itemCount = 1;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_design)


        var item1 = ArrayList<String>()
        item1.add("Slice")
        item1.add("18 inch")

        itemMap.put("Choice of size", item1)

        var item2 = ArrayList<String>()
        item2.add("Paneer")
        item2.add("Pepperoni")
        item2.add("Extra cheese")

        itemMap.put("Choice Toppings", item2)

        mapSet = itemMap!!.entries

        iv_minus.setOnClickListener(View.OnClickListener {
            if (itemCount != 1)
                itemCount -= itemCount
            updateItemCount()
        })

        iv_add.setOnClickListener(View.OnClickListener {
            if (itemCount != 10)
                itemCount += itemCount
            updateItemCount()

        })






        rv_item_ingredient.setHasFixedSize(true)
        rv_item_ingredient.layoutManager = LinearLayoutManager(this)
        rv_item_ingredient.adapter = ItemListAdapter()
    }


    fun updateItemCount() {
        tv_item_counter.text = itemCount.toString()
        tv_order_quantity.text = "Add " + itemCount + " to cart"
    }


    inner class ItemListAdapter : RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
            return ItemListViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_design,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {

            return itemMap.size

        }

        override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {


            val elementAt5 = mapSet!!.toTypedArray()[position] as Map.Entry<String, List<String>>




            holder.tv_header!!.text = elementAt5.key

            holder.tv_header!!.setOnClickListener(View.OnClickListener {

                if (holder.rv_sub_items!!.visibility == View.GONE)
                    holder.rv_sub_items!!.visibility = View.VISIBLE
                else
                    holder.rv_sub_items!!.visibility = View.GONE

            })


            holder.rv_sub_items!!.adapter = SubItemListAdapter(elementAt5.value)
        }


        inner class ItemListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var tv_header: TextView? = null
            var rv_sub_items: RecyclerView? = null

            init {
                tv_header = view.findViewById(R.id.tv_header)
                rv_sub_items = view.findViewById(R.id.rv_sub_items)
                rv_sub_items!!.setHasFixedSize(true)
                rv_sub_items!!.layoutManager = LinearLayoutManager(this@DesignActivity)

            }

        }
    }

    inner class SubItemListAdapter(var subItemList: List<String>) :
        RecyclerView.Adapter<SubItemListAdapter.SubItemListViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubItemListViewHolder {
            return SubItemListViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_credentials,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {

            return subItemList.size

        }

        override fun onBindViewHolder(holder: SubItemListViewHolder, position: Int) {


            holder.tv_header!!.text = subItemList.get(position)

        }


        inner class SubItemListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var tv_header: TextView? = null

            init {
                tv_header = view.findViewById(R.id.tv_credential)

            }

        }
    }
}
