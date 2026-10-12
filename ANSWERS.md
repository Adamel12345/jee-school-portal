# ANSWERS.md — Lab 3 (JDBC)

**Nom :** [Ton nom]
**Date :** [Date]

---

## Step 5 — Table 6 : Where did the enrollment go?

### Before the fix

| # | Action | Ali's courses on dashboard | Ali's rows in `student_courses` |
|---|--------|---------------------------|----------------------------------|
| 1 | Sign in as Ali | Web Development | (3, Web Development) |
| 2 | Enroll in JEE | Web Development, JEE | (3, Web Development) ⚠️ JEE missing |
| 3 | Sign out + sign in | Web Development (JEE lost) | (3, Web Development) |

### After the fix (add `dao.save(member)` in EnrollServlet)

| # | Action | Ali's courses on dashboard | Ali's rows in `student_courses` |
|---|--------|---------------------------|----------------------------------|
| 1 | Sign in as Ali | Web Development, JEE | (3, Web Development), (3, JEE) |
| 2 | Sign out + sign in | Web Development, JEE | (3, Web Development), (3, JEE) |
| 3 | Restart Tomcat + sign in | Web Development, JEE | (3, Web Development), (3, JEE) |

### Questions

**Q1 — In Lab 2, where was Ali's list of courses stored, and which object did EnrollServlet modify? Why was that enough?**

In Lab 2, the list of courses was stored in the `ConcurrentHashMap` of `MemberDaoImpl` (in memory). `EnrollServlet` modified the `Student` object returned by `findByEmail()` — and because that object was the SAME as the one in the map (shared reference), the modification was persistent as long as Tomcat was running.

**Q2 — In Lab 3, when Ali signs in, `findByEmail` builds a Student object from the rows. Which object does EnrollServlet modify now? Does any line of code write the new course into the database?**

In Lab 3, `findByEmail()` reads the SQL rows and builds a NEW `Student` object on every call. `EnrollServlet` modifies this object in memory only — it is a COPY of the rows. No line of code writes the new course into `student_courses`, so the modification is lost as soon as the page is reloaded or the user signs out.

**Q3 — At action 3, where does the dashboard's list come from?**

At action 3, the list comes from the DATABASE: `findByEmail()` re-reads the rows of `student_courses` for Ali, which only contains `Web Development`. The in-memory object that had `JEE` has been replaced by a fresh `Student` reloaded from the database.

---

## Step 6 — Parents

**Q — You can register as the parent of any student whose email you know. Describe in two or three sentences how a real school portal should prevent it. Do not implement it.**

In a real school portal, the parent–student link should NOT be created directly from the registration form based only on the student's email. It should require either:
1. An invitation sent by the school administration (with a unique, time-limited link), OR
2. A verification code given to the parent by the school, OR
3. The student's explicit consent (since the parent would see private data like attendance and grades).

Simply knowing a student's email is not proof of a family relationship — this is a real privacy and security issue.

---

## Step 8 — Deleting a member

### Error before cascade

First line of the stack trace:

**Which rows of which tables still point to Ali?**
- `student_courses` (his 2 courses: Web Development, JEE)
- `parent_child`    (Karim → Ali)
- `attendance`      (his 3 attendance records)

**Why does MySQL refuse to delete him?**
Because of the FOREIGN KEY constraints. Deleting Ali's row in `members` would leave orphan rows in the child tables pointing to a non-existent member. MySQL refuses to break referential integrity.

**Why is this refusal a good thing?**
It protects the database from inconsistency. Without it, we could have courses / attendance records referring to a student that no longer exists — data corruption. The FK constraint is a safety net that forces the application to handle deletion properly.

**Can we delete Karim's parent account? Why?**
Yes — Karim is only a parent; no table has a foreign key pointing to him (except `parent_child.parent_id`, but Karim IS the parent side of that row, so CASCADE would remove only that link). Deleting Karim removes his `parent_child` row too — no orphans left.

### After running `sql/cascade.sql`

**What disappeared with Ali?**
- `members`:         Ali's row
- `student_courses`: Ali's 2 courses (Web Development, JEE)
- `parent_child`:    Karim → Ali relation
- `attendance`:      Ali's 3 attendance records

**What happens to Karim's account when Ali is deleted?**
Karim's account remains in `members`, but his link to Ali in `parent_child` disappears (CASCADE). So Karim is now a parent with no children.

**What happens to Ali's account if Karim is deleted?**
Ali's account remains. Only the `parent_child` row (Karim → Ali) disappears.

**Is this what a school would want?**
No. Real systems use soft-delete (mark inactive) instead of CASCADE, because attendance history is important and parents shouldn't be silently unlinked. CASCADE is technically convenient but policy-wise dangerous. A real portal would require confirmation, an audit trail, or a soft-delete.