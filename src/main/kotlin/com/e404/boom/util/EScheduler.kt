package com.e404.boom.util

import com.e404.boom.Boom
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask

object EScheduler {
    private val tasks = ArrayList<BukkitTask>()
    private val scheduler = Bukkit.getScheduler()

    fun reload() {
        while (tasks.isNotEmpty()) tasks.removeAt(0).cancel()
    }

    private fun add(record: Boolean, task: BukkitTask) {
        if (record) tasks.add(task)
    }

    /**
     * 设置重复任务
     *
     * @param delay 延迟
     * @param period 间隔
     * @param runnable 任务
     */
    fun scheduleRepeat(record: Boolean, delay: Long = 0, period: Long, runnable: Runnable) {
        add(record, scheduler.runTaskTimer(Boom.instance, runnable, delay, period))
    }

    /**
     * 设置一次性任务
     *
     * @param delay 延迟
     * @param runnable 任务
     */
    fun schedule(record: Boolean, delay: Long = 0, runnable: Runnable) {
        add(record, scheduler.runTaskLater(Boom.instance, runnable, delay))
    }

    /**
     * 计划一次性异步任务
     *
     * @param delay 延迟
     * @param runnable 任务
     */
    fun scheduleAsync(record: Boolean, delay: Long = 0, runnable: Runnable) {
        add(record, scheduler.runTaskLaterAsynchronously(Boom.instance, runnable, delay))
    }

    /**
     * 计划异步任务
     *
     * @param delay 延迟
     * @param period 间隔
     * @param runnable 任务
     */
    fun scheduleAsyncRepeat(record: Boolean, delay: Long = 0, period: Long, runnable: Runnable) {
        add(record, scheduler.runTaskTimerAsynchronously(Boom.instance, runnable, delay, period))
    }
}