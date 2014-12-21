# What is Jerminal?
Jerminal is a Java library for creating embedded command-line interfaces.
It is mostly intended for creating debug consoles.<br>
The main feature of Jerminal is it's auto complete suggestions (more info below).

## Terminology
First of all, some quick terminology.<br>
This terminology may not be accurately used (with real world counterparts), but this is the terminology adopted by Jerminal.

* **Shell** - The software that parses and processes the command line and sends results to be displayed. The logic.
* **Command** - A piece of code that can be executed by the Shell.
* **File System** - A hierarchy of Commands. Commands can be grouped under directories, starting from a *root directory*.
* **Global Command** - A command that doesn't belong to any directory and can be executed from any directory.
* **Display Driver** - A component that can display the events generated by the Shell, the screen.
* **Terminal** - A Display Driver that displays everything as text.
* **Command Line Driver** - A component that can read from and write to the command line.
* **Console** - The complete package - a Shell, a Terminal and a Command Line Driver.

## More In Depth
Jerminal aims to remove the burden of creating command line interfaces (that will from now on be referred to as 'consoles')
  by providing all the necessary tools out of the box.<br>
All that is required to create a console is:

1. Provide the command hierarchy that will act as the Shell's File System.
2. Implement Jerminal's SPI (Service provider interfaces): Display Driver and Command Line Driver.
3. Hooks calls to Jerminal's API.

### 1. The Command Hierarchy
The basic unit of Jerminal is a command. In a file system structure, a command can be thought of as a file.

Commands can be grouped under directories. The Shell's File System contains a *root directory* which can contain
 child commands or directories. Directories can be nested to unlimited depth.

The Shell operates as would be expected from a standard command line interface - it maintains a *current working directory*
 and supports calling commands both relatively from the *current working directory* and with an absolute path from the *root*.

Jerminal comes with built-in control commands - *cd*, *ls* and *man*. These are implemented as Global Commands.<br>
Global Commands are commands that aren't bound to any parent directory. They can be executed from any *current working directory*.<br>
Adding custom global commands is supported for commands whose names don't clash with the built-in commands.

#### 1.1 Command Parameters
Commands may take 0 or more parameters of the types string, boolean, int, double, flag.<br>
Any parameter may be declared as optional, in which case a default value will be provided if it isn't supplied.<br>

* String parameters may either be constrained to a set of possible values, or be allowed any value.
 Furthermore, if constrained, the values may either be pre-determined or calculated at runtime through a value supplier.
 Values consisting of more then 1 word (separated by whitespace) can be surrounded with either single '' or double "" quotes.
* Boolean parameters only accept the values *true* or *false*.
* Numerical parameters (int and double) cannot be auto completed and don't (currently) support value constraints.
* Flags are special boolean parameters that are always optional and default to *false* if not provided.
 They can be set to *true* only by specifying their name. For example, the control command *ls* has a flag *-r* which makes
 it recurse into sub-directories. 'ls -r' is valid, but 'ls true' is not.

### 2. Display Driver and Command Line Driver
The DisplayDriver and CommandLineDriver are the 2 Service Provider Interfaces that must be implemented in order to integrate
Jerminal into another application.

The DisplayDriver is a higher-level interface which can display the different events generated by the Shell.
 This can be implemented if a more graphically sophisticated UI is desired.<br>
There is also a lower-level interface, a Terminal, which displays all the events generated by the Shell as text.
 This should be enough for most cases.

The CommandLineDriver is a component that can read from and write to the command line. The command line is the text input
area.
TBD: Mention that this is not the only way to implement a console.

Jerminal currently comes with implementations for these SPI's for libGdx and JavaFX.<br>
Any contributions will be certainly welcome :)

### 3. Hook calls to Jerminal
TBD.

# TBD
* Auto complete
* Annotations
* Programmatic creation
* Call parameters by name or by position