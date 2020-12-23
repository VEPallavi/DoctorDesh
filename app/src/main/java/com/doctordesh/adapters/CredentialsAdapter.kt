package com.doctordesh.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.doctordesh.R
import com.doctordesh.models.CredentialModel

class CredentialsAdapter(mContext: Context, var credentialsList: List<CredentialModel>) : BaseAdapter() {

    internal var inflater: LayoutInflater? = null


    init {
        if (inflater == null) {
            inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
    }


    override fun getView(pos: Int, view: View?, viewGroup: ViewGroup?): View {


        var convertView = view
        var viewHolder: CredentialViewHolder
        if (convertView == null) {
            convertView = inflater!!.inflate(R.layout.item_credentials, viewGroup, false)
            viewHolder = CredentialViewHolder()

            viewHolder.tvCredential = convertView.findViewById(R.id.tv_credential)

            convertView.setTag(viewHolder)
        } else {
            viewHolder = convertView.tag as CredentialViewHolder
        }

        viewHolder.tvCredential.text = credentialsList.get(pos).credential_type
        return convertView!!
    }

    override fun getItem(pos: Int): Any {

        return credentialsList.get(pos)

    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return credentialsList.size
    }


    class CredentialViewHolder {
        lateinit var tvCredential: TextView
    }


}