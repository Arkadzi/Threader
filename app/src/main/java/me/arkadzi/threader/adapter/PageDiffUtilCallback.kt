package me.arkadzi.threader.adapter

import android.support.v7.util.DiffUtil
import me.arkadzi.threader.model.Page

class PageDiffUtilCallback(
        private val oldList: List<Page>,
        private val newList: List<Page>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldProduct = oldList[oldItemPosition]
        val newProduct = newList[newItemPosition]
        return oldProduct.url == newProduct.url
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldProduct = oldList[oldItemPosition]
        val newProduct = newList[newItemPosition]
        return oldProduct.status == newProduct.status
    }
}