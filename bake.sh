#!/bin/bash

service_names=( partition ontology update query )

TARGET=${1:-all}

set -e

parse_version() {
  mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)'
}

container="aniketos"

if [ -n "$NOCACHE" ]; then
  nocache_option="--no-cache=true"
else
  nocache_option=""
fi

if [ -z "$DOCKER_REPOS" ]; then
  repos="quay.io/"
fi

if [ -n "$SUDO" ]; then
  sudo="sudo "
else
  sudo=""
fi

if [ -z "$TAG" ]; then
  docker_tag=$(parse_version)
else
  docker_tag=$TAG
fi

echo "Target Repos :: ${repos}"
echo "Target Tag :: ${docker_tag}"

if [ "$TARGET" == "all" ] || [ "$TARGET" == "service" ]; then
    for service in "${service_names[@]}"
    do
        #docker build -t "quay.io/daedafusion/$service" -f $service/$service-server/Dockerfile $service/$service-server
        # Build
        ${sudo} docker build ${nocache_option} -t "daedafusion/$service" -f $service/$service-server/Dockerfile $service/$service-server

        # Create tags
        for repo in `echo $repos`
        do
          ${sudo} docker tag "daedafusion/$service:latest" "${repo}daedafusion/$service:$docker_tag"
          ${sudo} docker tag "daedafusion/$service:latest" "${repo}daedafusion/$service:latest"
        done

        # Push to registry
        if [ "$PUSH" == "true" ]; then
          for repo in `echo $repos`
          do
            echo "Pushing to ${repo}"
            ${sudo} docker push "${repo}daedafusion/$service:$docker_tag"
            ${sudo} docker push "${repo}daedafusion/$service./b:latest"
          done
        fi
    done
fi

#if [ "$TARGET" == "all" ] || [ "$TARGET" == "config" ]; then
#    for service in "${service_names[@]}"
#    do
#        docker build -t "quay.io/daedafusion/$service-config:dev" -f config/$service/Dockerfile --build-arg ENVIRONMENT=dev config/$service
#    done
#fi