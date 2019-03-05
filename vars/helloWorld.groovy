#!/usr/bin/env groovy

// def call(def server, def port) {
//     println("server is:" + server)
//     println("port is:" + port)
// }


def call(String name = 'human') {
  println "Hello, ${name}."
}