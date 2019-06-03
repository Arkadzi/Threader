package me.arkadzi.threader.model

interface QueueElement {
    val parent: QueueElement?
    val layer: Int

    fun getStates(): List<Page>
    fun process()
}