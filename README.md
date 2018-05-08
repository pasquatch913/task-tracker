# task-tracker
Used to assign and track completion of recurring tasks.

# spec
idea:
weekly task tracker app

tasks should be created with rules
example:
play guitar 30 min -> occurs daily, 1 pt per completion  
code 30 min -> occurs daily, 1 pt per completion  
work out -> occurs daily with max of 3x per week, 1 pt per completion  
cook something new -> occurs 1x per week, 3 pts per completion  

weekly pts get aggregated and can be reviewed on another view with a total graph
each week has a notes section
each task has a notes section that is included in the weekly notes section for the relevant week


# walking skeleton:
1 tasks view
  this view must show appropriate tasks for the week/day
  this view must differentiate between completed and active tasks
1 set of tasks written to db
1 table containing record

features to follow in order of priority:
1. task creation ui
2. notes section
3. tasks review section (pts review, notes available in UI)
4. port to mobile
5. notifications to encourage action on incomplete tasks
