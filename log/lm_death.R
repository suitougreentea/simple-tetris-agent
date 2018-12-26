data = read.csv("lm_death_sample.log", header=F)
colors = c("red", "blue")
#plot(data$V1[data$V4 == 0], data$V2[data$V4 == 0])#, col=colors[data$V4+1])#, #xlim=c(-2.0, -1.5))
plot(data$V1, data$V2, col=colors[data$V4+1])
reg = lm(data$V2[data$V4 == 0] ~ data$V1[data$V4 == 0])
print(summary(reg))
abline(reg)


density.dead = density(data$V1[data$V4 == 0], from=-4, to=1)
density.alive = density(data$V1[data$V4 == 1], from=-4, to=1)
plot(density.dead, col=colors[1], ylim=c(0, 2))
lines(density.alive, col=colors[2])
density.alive.
lines(density.alive/(density.dead+density.alive), col="green")