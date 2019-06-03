package me.arkadzi.threader.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import me.arkadzi.threader.R
import me.arkadzi.threader.model.Page

class PageHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_page, parent, false)) {
    val tvUrl: TextView
    val tvStatus: TextView
    init {

        tvUrl = itemView.findViewById(R.id.tvUrl)
        tvStatus = itemView.findViewById(R.id.tvStatus)
    }
    fun bind(page: Page) {
        tvUrl.text = page.url
        tvStatus.text = page.status.toString()
    }
}