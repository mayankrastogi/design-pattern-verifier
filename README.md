# Homework 2
### Description: object-oriented design and implementation of an annotation framework and a verifier for a design pattern.
### Grade: 7% + bonus up to 3%
#### You can obtain this Git repo using the command git clone git clone https://bitbucket.org/cs474_fall2019/homework2.git.

## Preliminaries
As part of  homework assignment you will gain experience with design patterns and the annotation framework and the reflection Java package. This homework combines two separate topics: design patterns for object-oriented programming and Java annotation processing Application Programming Interface (API) to determine if a Java program implements a given design pattern correctly. That is, you will determine what program entities (e.g., classes and their methods) are needed as well as specific constraints on the implementation of these program entities (e.g., inheritance among classes or method calls in a certain sequence) as determined by your assigned design pattern. Next, you will create a set of annotations for your pattern that implement the labeling of program entities and relations among them. Finally, you will create a verifier that will use the annotation processing API and Java reflection package to determine if a design pattern is used correctly.

If you have not done so, you must create your account at [BitBucket](https://bitbucket.org/), a Git repo management system. It is imperative that you use your UIC email account that has the extension @uic.edu. Once you create an account with your UIC address, BibBucket will assign you an academic status that allows you to create private repos. Bitbucket users with free accounts cannot create private repos, which are essential for submitting your homeworks and the course project. Your instructor created a team for this class named [cs474_Fall2019](https://bitbucket.org/cs474_fall2019/). Please contact your TA, [Mr. Aditya Gupta](agupt24@uic.edu) using your UIC.EDU email account and he will add you to the team repo as developers, since Mr.Gupta already has the admin privileges. Please use your emails from the class registration roster to add you to the team and you will receive an invitation from BitBucket to join the team. Since it is a large class, please use your UIC email address for communications or Piazza and avoid emails from other accounts like funnybunny1992@gmail.com. If you don't receive a response within 12 hours, please contact us via Piazza, it may be a case that your direct emails went to the spam folder.

Next, if you haven't done so, you will install [IntelliJ](https://www.jetbrains.com/student/) with your academic license, the JDK, the Scala runtime and the IntelliJ Scala plugin, the [Simple Build Toolkit (SBT)](https://www.scala-sbt.org/1.x/docs/index.html) or the [Gradle build tool](https://gradle.org/) and make sure that you can create, compile, and run Java and Scala programs. Please make sure that you can run [various Java tools from your chosen JDK](https://docs.oracle.com/en/java/javase/index.html).

As before, you will use logging and configuration management frameworks. You will comment your code extensively and supply logging statements at different logging levels (e.g., TRACE, INFO, ERROR) to record information at some salient points in the executions of your programs. All input and configuration variables must be supplied through configuration files -- hardcoding these values in the source code is generally prohibited and will be punished by taking a large percentage of points from your total grade! You are expected to use [Logback](https://logback.qos.ch/) and [SLFL4J](https://www.slf4j.org/) for logging and [Typesafe Conguration Library](https://github.com/lightbend/config) for managing configuration files. These and other libraries should be imported into your project using your script [build.sbt](https://www.scala-sbt.org/1.0/docs/Basic-Def-Examples.html) or [gradle script](https://docs.gradle.org/current/userguide/writing_build_scripts.html). These libraries and frameworks are widely used in the industry, so learning them is the time well spent to improve your resumes.

Even though you can implement your homework in Java, you can also use Scala, for which you will receive an additional bonus of up to 3% for fully pure functional (not imperative) implementation. You will be expected to learn Scala as you go - all mandatory reading that you have done includes in-depth Scala programming exercises. As you see from the StackOverflow survey, knowledge of Scala is highly paid and in great demand, and it is expected that you pick it relatively fast, especially since it is tightly integrated with Java. I recommend using the book on Programming in Scala: Updated for Scala 2.12 published on May 10, 2016 by Martin Odersky and Lex Spoon. This book is available to you using the academic subscription on Safari Books Online. There are many other books and resources available on the Internet to learn Scala. Those who know more about functional programming can use the book on Functional Programming in Scala published on Sep 14, 2014 by Paul Chiusano and Runar Bjarnason.

To receive your bonus for writing your implementation in Scala, you should avoid using **var**s and while/for loops that iterate over collections using [induction variables](https://en.wikipedia.org/wiki/Induction_variable). Instead, you should learn to use collection methods **map**, **flatMap**, **foreach**, **filter** and many others with lambda functions, which make your code linear and easy to understand. Also, avoid mutable variables at all cost. Points will be deducted for having many **var**s and inductive variable loops without explanation why mutation is needed in your code - you can always do without it.

## Overview and Functionality
For example, for the class Singleton below that implements the pattern Singleton your chosen annotations may be the following.
```java
@SingletonGoF.OneInstance(CreationTime=SingletonGoF.LAZY)
public class Singleton {
	private static Singleton INSTANCE = null;
	@SingletonGoF.MainMethod
	public static Singleton getInstance() { 
		if(INSTANCE == null) INSTANCE = new Singleton();
		return INSTANCE;}
}
```
You will design and implement your annotation package around classes and interfaces that are used to implement your chosen pattern. For example, for the pattern Singleton, your annotation design may be the following.
```java
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SingletonGoF {
	public @interface OneInstance {
		CreationTime createtime default CreationTime.EAGER
	}
	public @interface MainMethod {}
	public enum CreationTime { LAZY, EAGER }
}
```
You will take a program that uses your assigned design pattern - you can implement this program or find a program that implements this pattern in an open-source repository or on the Internet - and annotate it using your own annotations that you designed and implemented for this pattern. Next, you will implement your pattern usage verifier where you will use Java Annotation Processing API calls with Java Reflection to obtain the information about the annotated program entities and issue warnings about the violations of the rules for the usage of your chosen design pattern. For example, if there is a class that is annotated with @SingletonGoF.OneInstance and a method of this class that is annotated with @SingletonGoF.MainMethod is not static, then your verifier may issue an error message that demands that there must be a static method in the singleton class that creates an instance. Alternatively, such annotated method may exist and the class Singleton is instantiated, but your analyzer will determine that this method is not invoked anywhere in a program and will issue a warning about it. Designing an annotation system for your assigned pattern and implementing its verifier is the essence of this homework.

From the lectures we already learned a few patterns in the context of using OO polymorphism. Each of you will select one of the following design patterns from the GoF book on Design Patterns: Elements of Reusable Object-Oriented Software published in 1994. You do not need to buy this book, [it is available for free](https://w3sdesign.com/GoF_Design_Patterns_Reference0100.pdf).
0. Abstract factory groups object factories that have a common theme.
1. Builder constructs complex objects by separating construction and representation.
2. Factory method creates objects without specifying the exact class to create.
3. Prototype creates objects by cloning an existing object.
4. Adapter allows classes with incompatible interfaces to work together by wrapping its own interface around that of an already existing class.
5. Bridge decouples an abstraction from its implementation so that the two can vary independently.
6. Composite composes zero-or-more similar objects so that they can be manipulated as one object.
7. Decorator dynamically adds/overrides behaviour in an existing method of an object.
8. Facade provides a simplified interface to a large body of code.
9. Flyweight reduces the cost of creating and manipulating a large number of similar objects.
10. Proxy provides a placeholder for another object to control access, reduce cost, and reduce complexity.
11. Chain of responsibility delegates commands to a chain of processing objects.
12. Command creates objects which encapsulate actions and parameters.
13. Interpreter implements a specialized language.
14. Iterator accesses the elements of an object sequentially without exposing its underlying representation.
15. Mediator allows loose coupling between classes by being the only class that has detailed knowledge of their methods.
16. Memento provides the ability to restore an object to its previous state (undo).
17. Observer is a publish/subscribe pattern which allows a number of observer objects to see an event.
18. State allows an object to alter its behavior when its internal state changes.
19. Strategy allows one of a family of algorithms to be selected on-the-fly at runtime.
20. Template method defines the skeleton of an algorithm as an abstract class, allowing its subclasses to provide concrete behavior.
21. Visitor separates an algorithm from an object structure by moving the hierarchy of methods into one object.

To determine your assigned pattern for this homework, take the modulo operation and your UIN as an integer and compute the modulus (UIN mod 22) - it is the number of your assigned pattern. First, you should study your pattern, create a Java project with an example of the implementation of the assigned pattern, and think of its program entities and constraints. Explicitly state rules that are important to hold for a given pattern to hold its essential properties. For example, instantiating new Singleton multiple times in a program defeats the purpose of having a unique Singleton object. The quality of the constraints and rules for your assigned pattern is a major component of your grade for this homework.

To verify that a program that uses your annotations abides by the rules of the pattern, you will create your own concrete Annotation Processor that is derived from the abstract class AbstractProcessor that is specified in the JDK. There are many useful examples on the Internet in different programming forums and blogs that show how to create concrete annotation processors. You will go through a prototyping exercise where you start with some simple annotations and write your annotation processor to obtain these annotations from the bytecodes. To implement some more complex verification rules you may need to use additional Java reflection package capabilities.

Your submission must include the following components: the design document that describes a model of your assigned pattern, its program entities, constraints and rules, your annotation package, example program(s) that implement your assigned design pattern whose program entities are annotated with your annotations, and the verifier with test cases that verify its behavior.

This homework script is written using a retroscripting technique, in which the homework outlines are generally and loosely drawn, and the individual students improvise to create the implementation that fits their refined objectives. In doing so, students are expected to stay within the basic requirements of the homework and they are free to experiments. That is, it is impossible that two non-collaborating students will submit similar homeworks! Asking questions is important, so please ask away at Piazza!

Your homework can be divided roughly into five steps. First, you learn how design pattern works, its components, constrains and usage rules. I suggest that you write several implementations of some simple program with the implemented pattern in IntelliJ and explore its classes, interfaces, and dependencies and how it works for clients. Second, you create your own model that describes the rules and constraints of the given design pattern. You will create your annotation framework. Next, you will create an implementation of your design with the annotated program entities - you may use relevant examples from the [GoF book](http://wiki.c2.com/?DesignPatternsBook). Fourth, you will create multiple unit tests using [JUnit framework](https://junit.org/junit5/). Finally, you will run your program, collect execution logs, and show how your verifier works for different implementations.. 

## Baseline
To be considered for grading, your project should include at least one of your programs written in Java, your project should be buildable using the SBT or the Gradle, and your documentation must specify how you create and evaluate your design pattern verification. Your documentation must include your design and model, the reasoning about pros and cons, explanations of your implementation and the chosen design pattern with its constraints and rules, and the results of your runs, the explanations of your error/warning messages. Simply copying some open-source Java programs from examples and modifying them a bit (e.g., rename some variables) will result in desk-rejecting your submission.

## Piazza collaboration
You can post questions and replies, statements, comments, discussion, etc. on Piazza. For this homework, feel free to share your ideas, mistakes, code fragments, commands from scripts, and some of your technical solutions with the rest of the class, and you can ask and advise others using Piazza on where resources and sample programs can be found on the internet, how to resolve dependencies and configuration issues. When posting question and answers on Piazza, please select the appropriate folder, i.e., hw2 to ensure that all discussion threads can be easily located. Active participants and problem solvers will receive bonuses from the big brother :-) who is watching your exchanges on Piazza (i.e., your class instructor and your TA). However, *you must not describe your design or specific details related how your construct your models!*

## Git logistics
**This is an individual homework.** Separate repositories will be created for each of your homeworks and for the course project. You will find a corresponding entry for this homework at  https://bitbucket.org/cs474_fall2019/homework2.git. You will fork this repository and your fork will be private, no one else besides you, the TA and your course instructor will have access to your fork. Please remember to grant a read access to your repository to your TA and your instructor. In future, for the team homeworks and the course project, you should grant the write access to your forkmates, but NOT for this homework. You can commit and push your code as many times as you want. Your code will not be visible and it should not be visible to other students (except for your forkmates for a team project, but not for this homework). When you push the code into the remote repo, your instructor and the TA will see your code in your separate private fork. Making your fork public or inviting other students to join your fork for an individual homework will result in losing your grade. For grading, only the latest push timed before the deadline will be considered. **If you push after the deadline, your grade for the homework will be zero**. For more information about using the Git and Bitbucket specifically, please use this [link as the starting point](https://confluence.atlassian.com/bitbucket/bitbucket-cloud-documentation-home-221448814.html). For those of you who struggle with the Git, I recommend a book by Ryan Hodson on Ry's Git Tutorial. The other book called Pro Git is written by Scott Chacon and Ben Straub and published by Apress and it is [freely available](https://git-scm.com/book/en/v2/). There are multiple videos on youtube that go into details of the Git organization and use.

Please follow this naming convention while submitting your work : "Firstname_Lastname_hw2" without quotes, where you specify your first and last names **exactly as you are registered with the University system**, so that we can easily recognize your submission. I repeat, make sure that you will give both your TA and the course instructor the read/write access to your *private forked repository* so that we can leave the file feedback.txt with the explanation of the grade assigned to your homework.

## Discussions and submission
As it is mentioned above, you can post questions and replies, statements, comments, discussion, etc. on Piazza. Remember that you cannot share your code and your solutions privately, but you can ask and advise others using Piazza and StackOverflow or some other developer networks where resources and sample programs can be found on the Internet, how to resolve dependencies and configuration issues. Yet, your implementation should be your own and you cannot share it. Alternatively, you cannot copy and paste someone else's implementation and put your name on it. Your submissions will be checked for plagiarism. **Copying code from your classmates or from some sites on the Internet will result in severe academic penalties up to the termination of your enrollment in the University**. When posting question and answers on Piazza, please select the appropriate folder, i.e., hw1 to ensure that all discussion threads can be easily located.


## Submission deadline and logistics
Thursday, October 31 at 11PM CST via the bitbucket repository. Your submission will include the code for the program, your documentation with instructions and detailed explanations on how to assemble and deploy your program along with the results of your verification runs and a document that explains these results based on the your rules and constraints, and what the limitations of your implementation are. Again, do not forget, please make sure that you will give both your TAs and your instructor the read access to your private forked repository. Your name should be shown in your README.md file and other documents. Your code should compile and run from the command line using the commands **sbt clean compile test** and **sbt clean compile run** or the corresponding commands for Gradle. Also, you project should be IntelliJ friendly, i.e., your graders should be able to import your code into IntelliJ and run from there. Use .gitignore to exlude files that should not be pushed into the repo.


## Evaluation criteria
- the maximum grade for this homework is 7% with the bonus up to 3%. Points are subtracted from this maximum grade: for example, saying that 2% is lost if some requirement is not completed means that the resulting grade will be 7%-2% => 5%; if the core homework functionality does not work, no bonus points will be given;
- only some POJO classes are created and nothing else is done: up to 7% lost;
- having less than five unit and/or integration tests: up to 5% lost;
- missing comments and explanations from the submitted program: up to 5% lost;
- logging is not used in your programs: up to 3% lost;
- hardcoding the input values in the source code instead of using the suggested configuration libraries: up to 4% lost;
- no instructions in README.md on how to install and run your program: up to 5% lost;
- the program crashes without completing the core functionality: up to 3% lost;
- no design and rule/constraint documentation exists that explains your choices: up to 6% lost;
- the deployment documentation exists but it is insufficient to understand how you assembled and deployed all components of the program: up to 5% lost;
- the minimum grade for this homework cannot be less than zero.

That's it, folks!