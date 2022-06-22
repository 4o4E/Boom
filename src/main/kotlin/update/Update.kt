package top.e404.boom.update

import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.eplugin.update.EUpdater

object Update : EUpdater(
    PL,
    url = "https://api.github.com/repos/4o4E/Boom/releases",
    mcbbs = "https://www.mcbbs.net/thread-1150139-1-1.html",
    github = "https://github.com/4o4E/Boom"
) {
    override fun enableUpdate() = Config.update
}