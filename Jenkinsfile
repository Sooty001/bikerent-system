pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'Java21'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Pulling code from GitHub...'
            }
        }

        stage('Build Shared Libs') {
            steps {
                echo 'Building Events Contract...'
                sh 'mvn -B -f events-contract/pom.xml clean install'

                echo 'Building API Contract...'
                sh 'mvn -B -f bikerent-api/pom.xml clean install'
            }
        }

        stage('Build & Test Services') {
            parallel {
                stage('Rest Service') {
                    steps {
                        sh 'mvn -B -f bikerent-rest/pom.xml clean package -DskipTests'
                    }
                }
                stage('Stats Service') {
                    steps {
                        sh 'mvn -B -f statistics-service/pom.xml clean package -DskipTests'
                    }
                }
                stage('Notify Service') {
                    steps {
                        sh 'mvn -B -f notification-service/pom.xml clean package -DskipTests'
                    }
                }
            }
        }

        /*
        stage('Deploy (Docker)') {
            steps {
                echo 'Rebuilding containers...'
                sh 'docker compose up -d --build'
            }
        }
        */
    }
}