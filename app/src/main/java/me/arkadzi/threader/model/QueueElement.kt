package me.arkadzi.threader.model

interface QueueElement {
    var parent: QueueElement?
    var layer: Int

    fun getStates(): List<QueueElement>
    fun process()
}