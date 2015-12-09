# fragments
Fragments is a combination of wikipedia and twitter, as it shall consist of articles that explain and define anything you want, but its size and contents are limited. Such an article is called a fragment.
A fragment consists of the following parts:
* a header (mandatory, max. 100 characters)
* a text (mandatory, max. 2000 characters)
* an image (optional)
* a source URL (optional)

## project layout

The fragment itself and a manager that is responsible to store and retrieve fragments are contained in the `fragments-core` project, but only as interfaces. Other projects will implement these interfaces, and can be combined to get a fully working system.
This way it is possible to have different UIs for displaying fragments or different layers to persist fragments (or even just put them into memory, losing everything once the system stops).

## okay, fine ... but why?

I have several reasons for this project, but let's consider the most important ones.

### 1. i want to learn

The project layout allows to try different technologies to solve the same problem. The task the project tries to solve is simple, yet challenging enough to be more than the common "Hello, world!" example. So I am forced to struggle more with the technologies I want to try than to copy and paste some code or configuration snippets from a tutorial page (at least I hope so).

### 2. it should be useful

Some projects that are set up to learn starve because they are ... well, just set up to learn. Once done or even before reaching the end of the learn process, they are aborted because of lack of interest. At least mine.
I need a project that is useful for myself. And I figured out, I sometimes need a tool for saving short explanations of topics or solutions to problems. That is a little different from just taking notes, because fragments can also save images and can be related to each other.
