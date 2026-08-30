# UI Test Plan

The entry point is `gary.Gary`. Each test case runs in a fresh process using Java 25. `Inputs` and `Expected outputs` are parallel JSON lists with one expected response per command. An expected response can be a string or a list of output lines. Test cases are isolated unless they specify the same `Storage group`, in which case their processes share one temporary working directory.

The runner compares the response printed between the two standard divider lines after each command. Line endings and surrounding blank lines are normalized; response text and internal whitespace are otherwise compared exactly. Startup output and all divider lines remain visible in the console-session record.

## UI-01: Task lifecycle

**Aim:** Verify typed tasks can be added, marked, listed, deleted, and listed again while preserving their details and status.

### Inputs

```json
[
  "todo read book",
  "deadline return book /by 2019-12-02",
  "event project meeting /from 2019-08-06 /to 2019-08-07",
  "mark 2",
  "list",
  "delete 1",
  "list",
  "bye"
]
```

### Expected outputs

```json
[
  [
    "Got it. I've added this task:",
    "  [T][ ] read book",
    "Now you have 1 tasks in the list."
  ],
  [
    "Got it. I've added this task:",
    "  [D][ ] return book (by: Dec 2 2019)",
    "Now you have 2 tasks in the list."
  ],
  [
    "Got it. I've added this task:",
    "  [E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)",
    "Now you have 3 tasks in the list."
  ],
  [
    "Nice! I've marked this task as done:",
    "  [D][X] return book (by: Dec 2 2019)"
  ],
  [
    "Here are the tasks in your list:",
    "1.[T][ ] read book",
    "2.[D][X] return book (by: Dec 2 2019)",
    "3.[E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)"
  ],
  [
    "Noted. I've removed this task:",
    "  [T][ ] read book",
    "Now you have 2 tasks in the list."
  ],
  [
    "Here are the tasks in your list:",
    "1.[D][X] return book (by: Dec 2 2019)",
    "2.[E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)"
  ],
  "Bye. Hope to see you again soon!"
]
```

## UI-02: Invalid input

**Aim:** Verify incomplete, out-of-range, and unknown commands report errors without terminating the session.

### Inputs

```json
[
  "todo",
  "deadline submit report /by tomorrow",
  "event meeting /from 2019-12-02 /to Tuesday",
  "delete 1",
  "blah",
  "bye"
]
```

### Expected outputs

```json
[
  "Error: The description of a todo cannot be empty",
  "Error: The deadline date must be in yyyy-MM-dd format",
  "Error: The event dates must be in yyyy-MM-dd format",
  "Error: The task number is invalid",
  "Invalid command",
  "Bye. Hope to see you again soon!"
]
```

## UI-03A: Save tasks

**Aim:** Verify todo, deadline, and event tasks and their completion status are saved before the chatbot exits.

**Storage group:** level-7-persistence

### Inputs

```json
[
  "todo read book",
  "deadline return book /by 2019-12-02",
  "event project meeting /from 2019-08-06 /to 2019-08-07",
  "mark 2",
  "bye"
]
```

### Expected outputs

```json
[
  [
    "Got it. I've added this task:",
    "  [T][ ] read book",
    "Now you have 1 tasks in the list."
  ],
  [
    "Got it. I've added this task:",
    "  [D][ ] return book (by: Dec 2 2019)",
    "Now you have 2 tasks in the list."
  ],
  [
    "Got it. I've added this task:",
    "  [E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)",
    "Now you have 3 tasks in the list."
  ],
  [
    "Nice! I've marked this task as done:",
    "  [D][X] return book (by: Dec 2 2019)"
  ],
  "Bye. Hope to see you again soon!"
]
```

## UI-03B: Load tasks

**Aim:** Verify a new chatbot process reloads the previously saved task types, details, and completion status.

**Storage group:** level-7-persistence

### Inputs

```json
[
  "list",
  "delete 1",
  "bye"
]
```

### Expected outputs

```json
[
  [
    "Here are the tasks in your list:",
    "1.[T][ ] read book",
    "2.[D][X] return book (by: Dec 2 2019)",
    "3.[E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)"
  ],
  [
    "Noted. I've removed this task:",
    "  [T][ ] read book",
    "Now you have 2 tasks in the list."
  ],
  "Bye. Hope to see you again soon!"
]
```

## UI-04: Find tasks

**Aim:** Verify find returns only descriptions containing the keyword, ignores case, preserves task numbers, and rejects an empty keyword.

### Inputs

```json
[
  "todo read book",
  "deadline return book /by 2019-12-02",
  "todo buy bread",
  "find BOOK",
  "find",
  "bye"
]
```

### Expected outputs

```json
[
  [
    "Got it. I've added this task:",
    "  [T][ ] read book",
    "Now you have 1 tasks in the list."
  ],
  [
    "Got it. I've added this task:",
    "  [D][ ] return book (by: Dec 2 2019)",
    "Now you have 2 tasks in the list."
  ],
  [
    "Got it. I've added this task:",
    "  [T][ ] buy bread",
    "Now you have 3 tasks in the list."
  ],
  [
    "Here are the matching tasks in your list:",
    "1.[T][ ] read book",
    "2.[D][ ] return book (by: Dec 2 2019)"
  ],
  "Error: The keyword for a find cannot be empty",
  "Bye. Hope to see you again soon!"
]
```
