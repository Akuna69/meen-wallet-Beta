FROM --platform=linux/amd64 ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive
ENV GO_VERSION=1.21.5
ENV JAVA_VERSION=17
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV ANDROID_NDK_HOME=/opt/android-sdk/ndk/25.2.9519653

RUN apt-get update && apt-get install -y \
    build-essential \
    curl \
    git \
    openjdk-17-jdk \
    unzip \
    wget \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Instalar Go
RUN wget https://go.dev/dl/go${GO_VERSION}.linux-amd64.tar.gz \
    && tar -C /usr/local -xzf go${GO_VERSION}.linux-amd64.tar.gz \
    && rm go${GO_VERSION}.linux-amd64.tar.gz

ENV PATH=/usr/local/go/bin:$PATH
ENV GOPATH=/go
ENV PATH=$GOPATH/bin:$PATH

WORKDIR /src
COPY . .

RUN tools/bootstrap-gomobile.sh \
    && (cd libwallet; go clean) \
    && ./gradlew :android:apolloui:clean \
    && ./gradlew :android:libwallet:build \
    && ./gradlew :android:apolloui:assembleProdRelease
