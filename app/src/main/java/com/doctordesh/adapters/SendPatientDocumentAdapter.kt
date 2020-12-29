package com.doctordesh.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.doctordesh.R


class SendPatientDocumentAdapter(var mContext: Context, var bitmapArrayList: ArrayList<Bitmap>) : RecyclerView.Adapter<SendPatientDocumentAdapter.SendPatientViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SendPatientViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_list_send_patient_document_images, parent,false)
        return SendPatientViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bitmapArrayList.size
    }



    override fun onBindViewHolder(holder: SendPatientViewHolder, position: Int) {
        holder.iv_image.setImageBitmap(bitmapArrayList.get(position))

    }


    inner class SendPatientViewHolder(view: View):RecyclerView.ViewHolder(view){

        var iv_image: ImageView

        init {
            iv_image = view.findViewById(R.id.iv_image)
        }


    }

}