FROM maven:3.8.3-jdk-17

ADD . /usr/src/finder
WORKDIR /usr/src/finder
EXPOSE 4567
ENTRYPOINT ["mvn", "clean", "verify", "exec:java"]