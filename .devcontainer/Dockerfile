FROM debian:bookworm-slim

RUN apt-get update && apt-get install -y \
    git \
    curl \
    wget \
    procps \
    sudo \
    unzip \
    openjdk-17-jdk \
    && rm -rf /var/lib/apt/lists/*

# Gradle (binary distribution; apt's version is too old for libGDX 1.12)
ENV GRADLE_VERSION=8.7
RUN wget -q https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -O /tmp/gradle.zip \
    && unzip -q /tmp/gradle.zip -d /opt \
    && ln -s /opt/gradle-${GRADLE_VERSION}/bin/gradle /usr/local/bin/gradle \
    && rm /tmp/gradle.zip
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

RUN curl -fsSL https://deb.nodesource.com/setup_lts.x | bash - \
    && apt-get install -y nodejs \
    && rm -rf /var/lib/apt/lists/*

RUN npm install -g @anthropic-ai/claude-code

RUN useradd -m -s /bin/bash claudeuser \
    && echo "claudeuser ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers
