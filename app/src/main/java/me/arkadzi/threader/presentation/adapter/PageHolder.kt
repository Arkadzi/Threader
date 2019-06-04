package me.arkadzi.threader.presentation.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import me.arkadzi.threader.R
import me.arkadzi.threader.domain.model.Page
import me.arkadzi.threader.domain.model.Status

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
        tvStatus.text = page.getStatusText()
    }

    private fun Page.getStatusText() = if (status == Status.FAILURE) {
        "$status $error"
    } else {
        status.toString()
    }
}