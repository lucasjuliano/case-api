
FROM clojure
COPY . /usr/src/app
WORKDIR /usr/src/app
ENV CASE_API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrVERSeXlQcG5QQnJEemQiLCJhY2NvdW50LWlkIjoiYmIzNTI1ZWUtM2NhMi00NmQ1LTlhYTItYTkxNmZmODhlNTBmIn0.xPsGazEv0n22QQiTs_ccKhJGUA9A08t4Kzx8kQFTUMc"
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" app-standalone.jar
EXPOSE 3000
CMD ["java", "-jar", "app-standalone.jar"]
