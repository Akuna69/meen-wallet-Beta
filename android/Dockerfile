FROM --platform=linux/amd64 ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive
ENV GO_VERSION=1.21.5
ENV JAVA_VERSION=17
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV ANDROID_HOME=/opt/android-sdk
ENV ANDROID_NDK_HOME=/opt/android-sdk/ndk/25.2.9519653

# 1. Instalar dependencias del sistema
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

# 2. Instalar Go
RUN wget https://go.dev/dl/go${GO_VERSION}.linux-amd64.tar.gz \
    && tar -C /usr/local -xzf go${GO_VERSION}.linux-amd64.tar.gz \
    && rm go${GO_VERSION}.linux-amd64.tar.gz

ENV PATH=/usr/local/go/bin:$PATH
ENV GOPATH=/go
ENV PATH=$GOPATH/bin:$PATH

# 3. Instalar Android Command Line Tools y el NDK requerido
RUN mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools \
    && wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O /tmp/cmdline-tools.zip \
    && unzip -q /tmp/cmdline-tools.zip -d ${ANDROID_SDK_ROOT}/cmdline-tools \
    && mv ${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools ${ANDROID_SDK_ROOT}/cmdline-tools/latest \
    && rm /tmp/cmdline-tools.zip

ENV PATH=${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${ANDROID_SDK_ROOT}/platform-tools:$PATH

# Acepta licencias e instala NDK 25.2.9519653
RUN yes | sdkmanager --licenses > /dev/null \
    && sdkmanager "ndk;25.2.9519653" "platforms;android-34" "build-tools;34.0.0"

# Enlace simbólico para engañar la ruta por defecto de gomobile
RUN mkdir -p /root/Android && ln -s /opt/android-sdk /root/Android/Sdk

WORKDIR /src
COPY . .

RUN tools/bootstrap-gomobile.sh \
    && (cd libwallet; go clean) \
    && ./gradlew :android:apolloui:clean \
    && ./gradlew :android:libwallet:build \
    && ./gradlew :android:apolloui:assembleProdRelease
