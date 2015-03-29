Anki
====

A tool to create Anki flash card from plain text files.

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


How to use
------

How to build
-----
