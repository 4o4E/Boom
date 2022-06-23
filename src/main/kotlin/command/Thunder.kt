package top.e404.boom.command

object Thunder : WeatherCommand(
    "thunder",
    Regex("(?i)thunder"),
)