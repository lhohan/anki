Anki
====

A cmd line tool to create Anki flash card from plain text files.

For those who prefer plain text over Excel or the other Anki supported formats.

File format
---

### Basic cards

The most basic card is just 2 lines, first line is the front of the card, second is the back of the card.

```
Eleanor Roosevelt said: "Do one thing every day that ______."
scares you
```

This should be sufficient to get started. Just start creating sections of 2 lines. The first ine contains the front of the card, the second the back of the card. For more advanced options see further down below.


If there are more than two lines, the first one is considered to be containing the front of the card. All the other lines will be put on the back.

### Cards are separated by at least one line break (empty line).

```
Eleanor Roosevelt said: "Do one thing every day that ______."
scares you

Aristotle: "We are what we repeatedly do. Excellence, then, _______."
is not an act, but a habit
```

### Comments

Comment lines start with double back slashes: `\\`.

```
\\ This will not be part of the card.
Eleanor Roosevelt said: "Do one thing every day that ______."
scares you
```

### More options:

You can add:

- a hint to the *front* of the card => prefix the line with `,`
- a source to the *back* of the card => prefix the line with `#`
- more details to the *back* of the card => prefix the line with `.`


If no prefixes the first line (the question) will be added to the front, the rest to the back.

Note: the order does not matter as long as the lines are not separated by an empty line.

#### Examples:


Example:

```
#Tip 33 The little book of talent
To learn from a book, ______
close it
,What can you with a book?
.'learning is reaching' : <br>instead of reading 10 pages 4 times and trying to memorize them, read the 10 pages once and try to write a 1 page summary (people remember 50% more material)
```

Content of the front of the card will be:

> To learn from a book, ______
>
> What can do with a book?

Content of the back of the card will be:

> close it
>
> 'learning is reaching' : <br>instead of reading 10 pages 4 times and trying to memorize them, read the 10 pages once and try to write a 1 page summary (people remember 50% more material)
>
> Tip 33 The little book of talent


Another example where only some more details will be added to the back of the card:

```
What are the two simple concepts behind Anki?
Active recall testing and spaced repetition
."Active recall testing" is a practical implementation of 'reaching' an important concept for trying to remember the answer when asked a question.
.This is in contrast to passive study, where we read, watch or listen to something without pausing to consider if we know the answer.
.<br>"Spaced Repetition": use (information) or loose it
```

A full sample is [here](src/test/resources/sample_input.txt).


How to use
------

Once you have the main artifact (it should be called `anki.jar`, see section 'How To Build') you can run it using standard java like:

```
java -jar anki.jar <input file> <output file>
```

For example, if you would run in where the `anki.jar` is generated (for me with still Scala 2.10: `anki/target/scala-2.10`) it may look like:

```
$ cp ../../src/test/resources/sample_input.txt .
$ java -jar anki.jar sample_input.txt sample_output.text
Cards written: 4 to sample_output.text

$ cat sample_output.text
Eleanor Roosevelt said: "Do one thing every day that ______."	scares you
Aristotle: "We are what we repeatedly do. Excellence, then, _______."	is not an act, but a habit
To learn from a book, ______	close it	'learning is reaching' : <br>instead of reading 10 pages 4 times and trying to memorize them, read the 10 pages once and try to write a 1 page summary (people remember 50% more material)	Tip 33 The little book of talent	What can you with a book?
What are the two simple concepts behind Anki?	Active recall testing and spaced repetition	"Active recall testing" is a practical implementation of 'reaching' an important concept for trying to remember the answer when asked a question. This is in contrast to passive study, where we read, watch or listen to something without pausing to consider if we know the answer. <br>"Spaced Repetition": use (information) or loose it

$
```

If no output file is specified one will be generated. It will be printed in the output of the program.


How to build
-----

The build tool used is `sbt`. If you want to build from source you will need it.

Command to generate the artifact:

```
sbt assembly
```

This will generate the `anki.jar` file in your target folder.
See usage for how to run it.
