# task-tracker
This is a side project I started in 2018 to take down a few birds with one stone:
1. Get more experience with Spring Boot apps as a new developer on a Spring Boot project at work.
2. Become more familiar with AWS services.
3. Use Docker to manage an application image.
4. Give myself a tool to help manage how I spend my time on my hobbies and any important tasks I need to complete. I haven't been entirely pleased with all the todo/habit building applications out there and wanted to try my hand at building *exactly* what I needed.


# Current status
The webapp at pasquatch.com/web/ allows users to create an account and then use it to create and edit task subscriptions assigned to their users with a period of either daily, weekly, or monthly. These tasks can be assigned a weight that helps the user to compare relative importance between tasks. One-time tasks are also supported.
There's an Android client application that I've developed as the primary consumption point for the application: [task tracker android client](https://github.com/pasquatch913/taskTrackerAndroidClient)
I don't currently have plans to develop an iOS client application.

# Features awaiting development on the webapp
1. Enhanced analytics view that will allow the user to view how well they're keeping up with each of their commitments.
2. Planning view that will allow the user to set/view target completion dates on a weekly-ish calendar.
3. Homepage with screenshots detailing application usage and better readme.
4. Moving off AWS RDS to self-managed EC2 postgres instance to reduce monthly billing.
5. Possibly a notes field for a subscription or completion event. My thoughts are less clear on this use-case at the moment.
6. Possibly a CI/CD pipeline server run locally and integration with github if any additional developers want to contribute. 