package com.e404.boom.event

import com.e404.boom.Boom
import org.bukkit.Bukkit
import org.bukkit.event.Listener

interface IListener : Listener {
    fun register() = Bukkit.getPluginManager().registerEvents(this, Boom.instance)
}