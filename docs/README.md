# Gary User Guide

Meow! Gary is your sea-snail task companion. Manage tasks, deadlines, events,
and contacts in one desktop chat window.

[Getting started](#getting-started) · [Tasks](#tasks) · [Contacts](#contacts) ·
[Saving your data](#saving-your-data) · [Troubleshooting](#troubleshooting)

## Getting started

1. Install **Java 25**. Run `java -version` in a terminal to check your version.
2. Put `gary.jar` in a folder where you can save files. Open a terminal in that folder.
3. Start Gary:

   ```shell
   java -jar "gary.jar"
   ```

4. Type `help` into the text box and press **Enter** or click **Send**.
5. Try `todo read book`, then `list`.

Packaged versions are on the [releases page](https://github.com/Azora04/ip/releases).
Older releases may not include every feature in this guide. To build the current
version, download or clone the [repository](https://github.com/Azora04/ip), open a
terminal in the project folder, and run `gradlew.bat shadowJar` on Windows or
`./gradlew shadowJar` on macOS/Linux. The result is `build/libs/gary.jar`.

Resize the window to suit your screen. Use the mouse wheel to scroll through the
conversation, including over the input bar while typing. Errors appear in a
contrasting warning style; correct the command and submit it again.

### Command basics

- Replace uppercase placeholders such as `DESCRIPTION` and `NUMBER` with your values.
- Commands and markers such as `/by` ignore case; descriptions keep your casing.
- Dates use **DD-MM-YYYY**, for example `25-12-2026`. Times are not supported.
- Numbers start at **1**. Use the current number from `list` or `contact list`.
- Send one command at a time. Do not put quotation marks around descriptions.

## Tasks

### Add a task

| Type | Format | Example |
| --- | --- | --- |
| To-do, without a date | `todo DESCRIPTION` | `todo read book` |
| Deadline | `deadline DESCRIPTION /by DD-MM-YYYY` | `deadline submit report /by 30-09-2026` |
| Event | `event DESCRIPTION /from DD-MM-YYYY /to DD-MM-YYYY` | `event project meeting /from 20-09-2026 /to 21-09-2026` |

Descriptions and dates must not be empty. An event may start and end on the same
date, but its end date cannot be earlier than its start date.

Adding a to-do to an empty list produces:

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

### View and manage tasks

| Command | What it does |
| --- | --- |
| `list` | Show all tasks and their current numbers. |
| `mark 1` | Mark task 1 as done. |
| `unmark 1` | Mark task 1 as not done. |
| `delete 1` | Permanently remove task 1. There is no undo command. |
| `find book` | Find tasks whose descriptions contain `book`, ignoring case. |

`[T]`, `[D]`, and `[E]` mean to-do, deadline, and event.
`[X]` means done; `[ ]` means not done.

```text
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] submit report (by: 30-09-2026)
3.[E][ ] project meeting (from: 20-09-2026 to: 21-09-2026)
```

Search results retain the original task numbers. Deleting a task renumbers later
tasks, so run `list` again before acting on another number. `find` searches the
description, not the date. With no matches, only the search heading is displayed.

## Contacts

Add a contact with a name, phone number, and email address. All three are required,
and `/phone` must come before `/email`:

```text
contact add Alice Tan /phone 91234567 /email alice@example.com
```

Gary confirms the details and the new contact count.

| Command | What it does |
| --- | --- |
| `contact list` | Show all contacts and their numbers. |
| `contact find Alice` | Search names, phone numbers, and emails, ignoring case. |
| `contact delete 1` | Permanently remove contact 1. |

Contact numbers are separate from task numbers. Search results keep the original
numbers; deletion renumbers the contacts after the deleted entry. A search with
no matches displays only the search heading.

## Help and goodbye

Type `help` for the command guide. Type `bye` for Gary's farewell.
In the desktop interface, lowercase `bye` disables the input; close the window
with its close button. Restart Gary to begin another conversation.

## Saving your data

Gary automatically saves changes in `data/gary.txt` and `data/contacts.txt`,
relative to the folder from which you launched the app. It loads those files
the next time you start it from that folder.

- On the first run, missing files mean empty lists. Gary creates the folder and
  files when you add data.
- To move Gary, copy the JAR **and the `data` folder**.
- Back up the `data` folder before editing or replacing its files.
- Avoid a vertical bar (`|`) surrounded by spaces in descriptions and contact fields. Avoid editing
  data files while Gary is running; malformed records may be skipped on loading.

> If Gary cannot save, your latest changes may exist only in the current session.
> Keep a copy of the details and resolve the folder access problem before closing.

## Troubleshooting

| Problem | What to try |
| --- | --- |
| Java is not recognized, or its version is unsupported | Install Java 25, reopen the terminal, and check `java -version`. |
| Unable to access the JAR | Open the terminal in its folder and run `java -jar "gary.jar"`. |
| Unknown command | Type `help`. Use `contact list` for contacts and `list` for tasks. |
| Missing information or invalid format | Follow Gary's suggested syntax, including the required markers. |
| Invalid date | Use a real date with a two-digit day and month, such as `05-01-2027`. |
| Invalid task or contact number | Run the relevant list command and use a current positive number. |
| Lists are unexpectedly empty | Check you launched Gary from the usual folder and its `data` folder is present. |
| Unable to load or save | Check folder permissions and that data paths are files, not directories. Back up existing data before repairs. Load errors may appear in the terminal. |

Still stuck? [Report an issue](https://github.com/Azora04/ip/issues) with the command,
error message, and Java version. Omit private task and contact details.
