package top.e404.boom.command

object Rain : WeatherCommand(
    "rain",
    Regex("(?i)rain"),
)