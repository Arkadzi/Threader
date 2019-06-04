package me.arkadzi.threader.domain.processing

import java.util.concurrent.ThreadFactory

class ProcessPriorityThreadFactory(private val threadPriority: Int) : ThreadFactory {

    override fun newThread(r: Runnable): Thread {
        val thread = Thread(r)
        thread.priority = threadPriority
        return thread
    }
}