# VERY IMPORTANT!!!
# This file is used by build-release.yml
FROM --platform=linux/amd64 openjdk:17-jdk-slim@sha256:aaa3b3cb27e3e520b8f116863d0580c438ed55ecfa0bc126b41f68c3f62f9774 AS muun_android_builder

ENV NDK_VERSION=22.0.7026061 \
    ANDROID_PLATFORM_VERSION=28 \
    ANDROID_BUILD_TOOLS_VERSION=28.0.3 \
    GO_VERSION=1.26.0 \
    GOTOOLCHAIN=local \
    CGO_ENABLED=1

# Sobrescribir repositorios para usar unicamente el archivo funcional de Bullseye
RUN echo "deb http://archive.debian.org/debian bullseye main" > /etc/apt/sources.list \
    && apt-get update --allow-releaseinfo-change -o Acquire::Check-Valid-Until=false -o Acquire::Retries=5 \
    && apt-get install --yes --no-install-recommends \
        unzip \
        wget \
        ca-certificates \
        curl \
        git \
        zip \
    && rm -rf /var/lib/apt/lists/*

# install golang
RUN curl -L "https://golang.org/dl/go${GO_VERSION}.linux-amd64.tar.gz" \
-o go${GO_VERSION}.linux-amd64.tar.gz && \
tar -xvf go${GO_VERSION}.linux-amd64.tar.gz -C /usr/local/ && \
rm -rf go${GO_VERSION}.linux-amd64.tar.gz

ENV GOPATH=/go
ENV PATH=$GOPATH/bin:/usr/local/go/bin:$PATH

# install android sdk
ENV ANDROID_HOME=/opt/android-sdk-linux
ENV ANDROID_SDK_ROOT=/opt/android-sdk-linux

RUN cd /opt \
    && wget -q https://dl.google.com/android/repository/commandlinetools-linux-6609375_latest.zip -O android-sdk-tools.zip \
    && mkdir -p ${ANDROID_HOME}/cmdline-tools \
    && unzip -q android-sdk-tools.zip -d ${ANDROID_HOME}/cmdline-tools \
    && rm android-sdk-tools.zip

ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/tools/bin:${ANDROID_HOME}/platform-tools

# Accept licenses before installing components
RUN yes | sdkmanager --licenses

# Platform tools
RUN sdkmanager "platform-tools"

RUN yes | sdkmanager --update --channel=3

# Please keep these in descending order!
RUN yes | sdkmanager \
    "platforms;android-${ANDROID_PLATFORM_VERSION}" \
    "build-tools;${ANDROID_BUILD_TOOLS_VERSION}"

RUN sdkmanager --install "ndk;${NDK_VERSION}"

ENV ANDROID_NDK_HOME=${ANDROID_HOME}/ndk/${NDK_VERSION}

RUN mkdir -p /root/Android && ln -s /opt/android-sdk-linux /root/Android/Sdk

FROM --platform=linux/amd64 rust:1.84@sha256:738ae99a3d75623f41e6882566b4ef37e38a9840244a47efd4a0ca22e9628b88 AS rust_builder

ARG RUSTUP_TOOLCHAIN=nightly-2024-12-16
ARG TARGETS="aarch64-unknown-linux-musl x86_64-unknown-linux-musl aarch64-linux-android armv7-linux-androideabi i686-linux-android x86_64-linux-android"

RUN rustup toolchain install ${RUSTUP_TOOLCHAIN} \
    && rustup override set ${RUSTUP_TOOLCHAIN} \
    && rustup target add --toolchain ${RUSTUP_TOOLCHAIN} $TARGETS \
    && rustup component add clippy \
    && rustup component add rustfmt

FROM rust_builder AS librs_build

WORKDIR /src
COPY . /src

RUN TARGETS="$TARGETS" USE_HOST_CARGO=true libwallet/librs/makelibs.sh

FROM muun_android_builder AS build

WORKDIR /src
COPY . /src
COPY --from=librs_build /src/libwallet/librs/libs /src/libwallet/librs/libs

RUN tools/bootstrap-gomobile.sh \
    && (cd libwallet; go clean) \
    && ./gradlew :android:apolloui:clean \
    && ./gradlew :android:libwallet:build \
    && ./gradlew :android:apolloui:assembleProdRelease

FROM scratch

COPY --from=build /src/android/apolloui/build/outputs/apk/prod/release/apolloui-prod-arm64-v8a-release-unsigned.apk apolloui-prod-arm64-v8a-release-unsigned.apk
COPY --from=build /src/android/apolloui/build/outputs/apk/prod/release/apolloui-prod-armeabi-v7a-release-unsigned.apk apolloui-prod-armeabi-v7a-release-unsigned.apk
COPY --from=build /src/android/apolloui/build/outputs/apk/prod/release/apolloui-prod-x86-release-unsigned.apk apolloui-prod-x86-release-unsigned.apk
COPY --from=build /src/android/apolloui/build/outputs/apk/prod/release/apolloui-prod-x86_64-release-unsigned.apk apolloui-prod-x86_64-release-unsigned.apk
COPY --from=build /src/android/apolloui/build/outputs/mapping/prodRelease/mapping.txt mapping.txt
