package me.arkadzi.threader.presentation.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import me.arkadzi.threader.domain.model.Page

class Adapter(
        val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<PageHolder>() {
    var data = emptyList<Page>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        return PageHolder(layoutInflater, parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) {
        holder.bind(data[position])
    }

    fun updateData(data: List<Page>) {
//        val productDiffUtilCallback = PageDiffUtilCallback(this.data, data)
//        val productDiffResult = DiffUtil.calculateDiff(productDiffUtilCallback)
        this.data = data
        notifyDataSetChanged()
//        productDiffResult.dispatchUpdatesTo(this)
    }
}

