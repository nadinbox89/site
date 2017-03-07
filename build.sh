#!/usr/bin/env bash
java -Dusername=??? -Dpassword=??? -jar mvn-classloader-1.8.jar  com.github.igor-suhorukov:maven-cli:3.3.9 com.github.igorsuhorukov.maven.MavenRunner clean package scm-publish:publish-scm
