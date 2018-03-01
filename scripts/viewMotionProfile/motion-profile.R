if (!suppressMessages(require("plotly"))) install.packages("plotly");
if (!suppressMessages(require("webshot"))) install.packages("webshot");
if(!htmlwidgets:::pandoc_available()) {
  if (!suppressMessages(require("installr"))) install.packages("installr");
  install.pandoc()
}

mydata = read.csv("left.csv")
head(mydata, n=3)

p <- plot_ly(mydata, x = ~x, y = ~y, type = 'scatter', mode = 'lines')
htmlwidgets::saveWidget(p, "index.html")
browseURL("index.html")
