# Fit Fat

A program to control body fat mass in android.

This is a sample project to try new things in android, there are many branches, not all 
branches are functional, but they are designed with different architectures in mind.

master branch is usually the last work in progress

## Domain Driven Design Architecture

Main branch is architecture as DDD, with many modules, to check how it behaves with modules sharing.

Also it uses Koin as dependency injection framework.


It stores everything locally, but also uses Firebase to backup data.
### Main Aggregates :

UserSettings and Measures


### Services 

There are some services also in the same folder as Aggregates are.

### Event Driven Architecture

There is a service related with FirebaseConnection, when the connection is stablished, everything 
should be synchronized.
