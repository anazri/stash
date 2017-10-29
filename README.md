# Stash Backend

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

Stash Backend is a BA project written by Gábor Pintér for the
Web Development BA program at [Copenhagen School of Design
and Technology](http://www.kea.dk). The application is a general purpuse backend demonstrating some of the 
core features of the [Dropwizard](http://www.dropwizard.io) framework. 

The purpose of the application is to serve as an out-of-the-box backend which can be
easily run locally or be deployed to a provider of your choice 
(e.g.: [Heroku](https://www.heroku.com/), [AWS](https://aws.amazon.com/), 
[Docker Cloud](https://cloud.docker.com/)). Perfect for frontend web- and mobile
developers.

## Features

- **App service**
  - Create multiple apps
  - Authenticate apps using JWT
- **User service**
  - Register users
  - Authenticate (log in / out) users
  - Update / delete user profiles
- **Document service**
  - Store, update and delete *json* objects in database
  - Set document ownership 
  - Query documents
- **File service**
  - Upload / delete files
  - Set file publicity and accessibility
- **Stash Dashboard**
  - Overview and settings of your backend in the browser

## Documentation & support

- [See Wiki](https://github.com/gaboratorium/stash/wiki)
- [PRs Welcome - issues you can help with](https://github.com/gaboratorium/stash/issues?q=is%3Aissue+is%3Aopen+label%3A%22help+wanted%22)
