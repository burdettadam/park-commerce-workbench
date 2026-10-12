# Local development build. Image digest pinning and scanning are release gates.
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
ARG SERVICE
RUN case "$SERVICE" in catalog-service|pricing-service|booking-service) ;; *) echo 'Invalid SERVICE' >&2; exit 2;; esac \
 && mvn -B -ntp -pl "$SERVICE" -am -DskipTests package

FROM eclipse-temurin:21-jre-jammy
RUN apt-get update && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/* \
 && groupadd --gid 10001 app && useradd --uid 10001 --gid app --no-create-home app
WORKDIR /app
ARG SERVICE
COPY --from=build --chown=app:app /workspace/${SERVICE}/target/app.jar /app/app.jar
USER 10001:10001
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70.0 -Duser.timezone=UTC"
ENTRYPOINT ["java","-jar","/app/app.jar"]
