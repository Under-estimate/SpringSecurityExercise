# SpringSecurityExercise
An exercise project for spring security.

Based on: [Spring Guide : Securing a Web Application](https://spring.io/guides/gs/securing-web/)

Dependencies: `Spring MVC` `Spring Security` `Bootstrap` `Thymeleaf`

## Functions
+ Register account(with captcha verification)
+ Log on your account
+ Log out your account
+ Restricted access to page "/secured", unless previously logged on.
+ Change password after logged on.
+ Reset password without logged on, via answering the question you set in registration process.

## Unimplemented function
Deploy website to a server.

```text
Exception in thread "deploy-website" java.lang.NullPointerException : variable "webserver" is null
    at com.task.DeployWebsite.execute(DeployWebsite.java:233)
    at com.task.Task$4.run(Task.java:142)
    at com.task.TaskManager.doTask(TaskManager.java:85)
Caused by notjava.economy.InsufficientFundException : Cannot afford purchasing "web server"
    at notjava.economy.PurchaseManager.purchase(PurchaseManager.java:71)
    at notjava.economy.PurchaseManager.resolveRequirement(PurchaseManager.java:42)
    at com.task.Task$3.run(Task.java:173)
    at com.task.TaskManager.doTask(TaskManager.java:85)
```
