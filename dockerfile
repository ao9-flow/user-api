FROM amazoncorretto:11 
VOLUME /tmp
COPY target/flow-user-api-0.0.1-SNAPSHOT.jar UserAPI.jar 
ENTRYPOINT ["java","-jar","UserAPI.jar"] 