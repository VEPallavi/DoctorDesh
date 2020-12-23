package com.doctordesh.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.doctordesh.R
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_search_fragment.*
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SearchFragmentr.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SearchFragmentr.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        var tabs = view.findViewById<TabLayout>(R.id.tabs)
        var viewpager = view.findViewById<ViewPager>(R.id.viewpager)
        var viewpagerAdapter = ViewPagerAdapter(activity!!.supportFragmentManager)
        viewpagerAdapter.addFragment(OrderFragments(), "PAST ORDER")
        viewpagerAdapter.addFragment(OrderFragments(), "UPCOMING")
        viewpager.adapter = viewpagerAdapter
        tabs.setupWithViewPager(viewpager)


        /* var rvCategories = view.findViewById<RecyclerView>(R.id.rv_categories)
         rvCategories.setHasFixedSize(true)
         rvCategories.layoutManager = GridLayoutManager(activity, 2)
         rvCategories.adapter = CategoriesListAdapter()
 */

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragmentr.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            SearchFragment()
    }

    class CategoriesListAdapter() : RecyclerView.Adapter<CategoriesListAdapter.CategoriesViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
            return CategoriesViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_categories_list,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {

            return 10
        }

        override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        }


        inner class CategoriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        }
    }


    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList.get(position)
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList.get(position)
        }
    }

    internal class OrderFragments : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            val view = inflater.inflate(R.layout.fragment_order, container, false)

            var rv_order_list = view.findViewById<RecyclerView>(R.id.rv_order_list)
            rv_order_list.setHasFixedSize(true)
            rv_order_list.layoutManager = LinearLayoutManager(activity)
            rv_order_list.adapter = OrderListAdapter()


            return view
        }


        class OrderListAdapter() : RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
                return OrderListViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_order_list,
                        parent,
                        false
                    )
                )
            }

            override fun getItemCount(): Int {

                return 10
            }

            override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
            }


            inner class OrderListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            }
        }


    }

}
