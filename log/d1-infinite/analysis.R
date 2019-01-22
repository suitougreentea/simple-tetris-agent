# ScoreSampler1.kt
data = read.csv("summary.csv", header=F)
currentEval = data$V3
allMoves = data$V2
finalScore = data$V8
#moves = log(data$V2 - data$V1 + 1)
moves = data$V2 - data$V1 + 1
#plot(currentEval, moves, log="y", xlab="eval function", ylab="remaining moves")
plot(currentEval, moves, xlab="eval function", ylab="remaining moves")
reg = lm(moves ~ currentEval)
print(summary(reg))
#abline(reg)
a = 6.157
b = 1.396
x = seq(-4, 1, 0.01)
y = exp(b * x  + a)
lines(x, y)

print(mean(finalScore/allMoves))
#x = data.frame("currentEval" = seq(-3, 1, 0.01))
#y = predict(reg, x)
#lines(x$currentEval, y)