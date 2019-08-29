#Summer Research on Static Analysis of Android Apps

##Environment Requirements
1. Java SE Development Kit Version 8 downloaded at [https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html]
2. IntelliJ IDEA Community Edition

##Libraries Used
<br>The libraries are downloaded and included inside the repository. They may need to be set up inside the **Project Structure** of Intellij settings.
1. **Soot 3.3.0**
<br>For the analysis of individual jimple bodies of the loaded classes in the scene. 
2. **FlowDroid 2.7.1**
<br> For the construction of **DummyMainClass** and **DummyMainMethods** for Android applications, and construction of call graphs and setting Pointer Transformation using SPARK algorithm.
3. **Gson 2.8.5**
<br>For parsing JSON into Java objects.

##How to use the application
1. Go to **Project Structure** in IntelliJ and Build artifact. <br> It will generate a **jar** file with the dependencies.
2. Run on Command Line via **java -jar <<x>options> <<x>folder or diectory name> <<x>Android platforms folder>**

##Future Improvements
1. Reflection Handling
<br>Exploring DroidRA but giving casting error traced back to ASMMethodSource's getBody method
2. Solve SPARK transformation error with some APKs