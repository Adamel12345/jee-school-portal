-- ============================================================================
-- cascade.sql -- Lab 3, Step 8
-- Redefines the 5 foreign keys with ON DELETE CASCADE.
-- Run once in phpMyAdmin after schema.sql and seed.sql.
-- ============================================================================

USE school_portal;

-- 1. student_courses.student_id -> members.id
ALTER TABLE student_courses DROP FOREIGN KEY fk_sc_student;
ALTER TABLE student_courses
    ADD CONSTRAINT fk_sc_student
        FOREIGN KEY (student_id) REFERENCES members(id) ON DELETE CASCADE;

-- 2. teacher_subjects.teacher_id -> members.id
ALTER TABLE teacher_subjects DROP FOREIGN KEY fk_ts_teacher;
ALTER TABLE teacher_subjects
    ADD CONSTRAINT fk_ts_teacher
        FOREIGN KEY (teacher_id) REFERENCES members(id) ON DELETE CASCADE;

-- 3. parent_child.parent_id -> members.id
ALTER TABLE parent_child DROP FOREIGN KEY fk_pc_parent;
ALTER TABLE parent_child
    ADD CONSTRAINT fk_pc_parent
        FOREIGN KEY (parent_id) REFERENCES members(id) ON DELETE CASCADE;

-- 4. parent_child.student_id -> members.id
ALTER TABLE parent_child DROP FOREIGN KEY fk_pc_student;
ALTER TABLE parent_child
    ADD CONSTRAINT fk_pc_student
        FOREIGN KEY (student_id) REFERENCES members(id) ON DELETE CASCADE;

-- 5. attendance.student_id -> members.id
ALTER TABLE attendance DROP FOREIGN KEY fk_att_student;
ALTER TABLE attendance
    ADD CONSTRAINT fk_att_student
        FOREIGN KEY (student_id) REFERENCES members(id) ON DELETE CASCADE;