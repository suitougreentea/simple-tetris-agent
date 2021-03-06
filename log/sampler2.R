data = read.csv("sample.log", header=F)
colors = c("red", "blue")
plot(data$V1[data$V4 == 0], data$V2[data$V4 == 0])#, col=colors[data$V4+1])#, #xlim=c(-2.0, -1.5))
# a = hist(data$V1[data$V4 == 0], xlim=c(-4, 0), breaks = 10)
reg = lm(data$V2[data$V4 == 0] ~ data$V1[data$V4 == 0])
print(summary(reg))
abline(reg)
