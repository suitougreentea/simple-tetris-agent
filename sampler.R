# 1: 20 fwd
# 2: 30 fwd
# 3: 50 fwd
# 4: 5 fwd
# 5: 15 fwd
# 6: 20 fwd; lines
# 7: 100 fwd; lines
# 8: 200 fwd; lines
# 9: 200 fwd; lines + short episodes
# 10: 200 fwd; lines + short episodes
# 11: 200 fwd + short episodes
# 12: 20 fwd + short episodes
# 13: 100 fwd + short episodes
data = read.csv("sample_13.log", header=F)
library(rgl)
#plot3d(data)
colors = c("red", "blue")
#plot(data$V1, data$V2, col=colors[(data$V4 == 100) + 1])
a = hist(data$V1[data$V4 == 100], breaks=seq(-4, 0, 0.5))
b = hist(data$V1[data$V4 != 100], breaks=seq(-4, 0, 0.5))
c = a$counts / (a$counts + b$counts + 0.0001)
plot(seq(-3.75, -0.25, 0.5), c)
#reg = lm(data$V2 ~ data$V1 + data$V2)
reg = lm(data$V2 ~ data$V1)
print(summary(reg))
abline(reg)
#coefs <- coef(reg)
#planes3d(coefs[2], coefs[3], -1, coefs[1], col="blue", alpha=0.5)