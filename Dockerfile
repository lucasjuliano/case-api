
FROM clojure
COPY . /usr/src/app
WORKDIR /usr/src/app
ENV CASE_API_KEY  #INSERT HERE THE KEY
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" app-standalone.jar
EXPOSE 3000
CMD ["java", "-jar", "app-standalone.jar"]
