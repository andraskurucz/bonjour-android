package com.akurucz.bonjour

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class ServicesAdapter(private val onItemClicked: (BonjourService) -> Unit) :
    ListAdapter<BonjourService, ServicesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.discovered_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindTo(item, onItemClicked)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindTo(item: BonjourService, onItemClicked: (BonjourService) -> Unit) {
            itemView.setOnClickListener { onItemClicked(item) }
            itemView.findViewById<TextView>(R.id.name).text = item.name
            itemView.findViewById<TextView>(R.id.address).text = item.address
        }
    }

    internal class DiffCallback : DiffUtil.ItemCallback<BonjourService>() {
        override fun areItemsTheSame(oldItem: BonjourService, newItem: BonjourService): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: BonjourService, newItem: BonjourService): Boolean {
            return oldItem == newItem
        }
    }
}