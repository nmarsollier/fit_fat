# Fit Fat

A program to control body fat mass in android.

This is a sample project to try new things in android, there are many branches, not all 
branches are functional, but they are designed with different architectures in mind.

master branch is usually the last work in progress

## Domain Driven Design Architecture

Architecture is DDD, with many modules.

On the UI side, this is a custom MVI/Redux Architecture.

Uses Koin as dependency injection framework.
Firebase to store data in cloud.

It stores everything locally, but also uses Firebase to backup data.

### Main Aggregates :

UserSettings and Measures

### Services 

There are some services also in the same folder as Aggregates are.

### Event Driven Architecture

There is a service related with FirebaseConnection, when the connection is stablished, everything 
should be synchronized.
