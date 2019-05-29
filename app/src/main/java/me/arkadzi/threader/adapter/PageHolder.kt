package me.arkadzi.threader.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import me.arkadzi.threader.R
import me.arkadzi.threader.model.Page

class PageHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_page, parent, false)) {
    fun bind(page: Page) {}
}