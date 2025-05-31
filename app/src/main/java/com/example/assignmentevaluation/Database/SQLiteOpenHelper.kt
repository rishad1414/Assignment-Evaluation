package com.example.assinment_evaluation.Database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.assignmentevaluation.Model.CourseStudent
import com.example.assinment_evaluation.Model.Assignment
import com.example.assinment_evaluation.Model.Course
import com.example.assinment_evaluation.Model.Student
import com.example.assinment_evaluation.Model.SubmitAssignment
import com.example.assinment_evaluation.Model.Teacher

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "assignment_manager.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {

        val createTeacherTable = """
    CREATE TABLE teacher (
        teacherId TEXT PRIMARY KEY,
        name TEXT,
        dept TEXT,
        des TEXT,
        email TEXT,
        profilePic BLOB,
        password TEXT
    );
""".trimIndent()

        val createStudentTable = """
    CREATE TABLE student (
        studentId TEXT PRIMARY KEY,
        name TEXT,
        email TEXT,
        dept TEXT,
        batch TEXT,
        sec TEXT,
        profilePic BLOB,
        password TEXT
    );
""".trimIndent()

        val createCourseTable = """
        CREATE TABLE IF NOT EXISTS course (
            courseId TEXT PRIMARY KEY,
            teacherId TEXT,
            title TEXT,
            sName TEXT,
            courseCode TEXT,
            accessKey TEXT,
            dept TEXT,
            lt TEXT,
            sec TEXT
        )
    """.trimIndent()


        val createAssignmentTable = """
        CREATE TABLE assignment (
            assignmentId TEXT PRIMARY KEY,
            courseId TEXT,
            title TEXT,
            des TEXT,
            deadline TEXT,
            totalMark TEXT
        );
    """.trimIndent()

        val createSubmitAssignmentTable = """
        CREATE TABLE submit_assignment (
            id TEXT PRIMARY KEY,
            assignmentId TEXT,
            studentId TEXT,
            submitTime TEXT,
            submitDate TEXT,
            status TEXT,
            feedBack TEXT,
            mark TEXT,
            file BLOB,
            fileType TEXT
        );
    """.trimIndent()

        val createCourseStudentTable = """
            CREATE TABLE course_student (
                courseId TEXT,
                studentId TEXT,
                PRIMARY KEY (courseId, studentId)
            );
        """.trimIndent()




        db.execSQL(createCourseStudentTable)
        db.execSQL(createAssignmentTable)
        db.execSQL(createSubmitAssignmentTable)
        db.execSQL(createCourseTable)
        db.execSQL(createTeacherTable)
        db.execSQL(createStudentTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS assignments")
        onCreate(db)
    }



    fun isEmailExists(email: String): Boolean {
        val db = readableDatabase
        val studentCursor = db.rawQuery("SELECT email FROM student WHERE email = ?", arrayOf(email))
        val teacherCursor = db.rawQuery("SELECT email FROM teacher WHERE email = ?", arrayOf(email))
        val exists = studentCursor.count > 0 || teacherCursor.count > 0
        studentCursor.close()
        teacherCursor.close()
        return exists
    }

    fun insertTeacher(teacher: Teacher): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("teacherId", teacher.teacherId)
            put("name", teacher.name)
            put("dept", teacher.dept)
            put("des", teacher.des)
            put("email", teacher.email)
            put("profilePic", teacher.profilePic)
            put("password", teacher.password)
        }
        val result = db.insert("teacher", null, values)
        return result != -1L
    }


    fun insertStudent(student: Student): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("studentId", student.studentId)
            put("name", student.name)
            put("email", student.email)
            put("dept", student.dept)
            put("batch", student.batch)
            put("sec", student.sec)
            put("profilePic", student.profilePic)
            put("password", student.password)
        }
        val result = db.insert("student", null, values)
        return result != -1L
    }

    fun insertCourse(course: Course): Boolean {
        return try {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put("courseId", course.courseId)
                put("teacherId", course.teacherId)
                put("title", course.title)
                put("sName", course.sName)
                put("courseCode", course.courseCode)
                put("accessKey", course.accessKey)
                put("dept", course.dept)
                put("lt", course.lt)
                put("sec", course.sec)
            }

            val result = db.insert("course", null, values)
            db.close()
            result != -1L
        } catch (e: Exception) {
            Log.d("DB_ERROR", "Insert course failed: ${e.message}")
            false
        }
    }

    fun insertAssignment(assignment: Assignment): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("assignmentId", assignment.assignmentId)
            put("courseId", assignment.courseId)
            put("title", assignment.title)
            put("des", assignment.des)
            put("deadline", assignment.deadline)
            put("totalMark", assignment.totalMark)
        }
        val result = db.insert("assignment", null, values)
        db.close()
        return result != -1L
    }

    fun insertSubmitAssignment(sa: SubmitAssignment): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("id", sa.id)
            put("assignmentId", sa.assignmentId)
            put("studentId", sa.studentId)
            put("submitTime", sa.submitTime)
            put("submitDate", sa.submitDate)
            put("status", sa.status)
            put("feedBack", sa.feedBack)
            put("mark", sa.mark)
            put("file", sa.file)
            put("fileType", sa.fileType)
        }
        val result = db.insert("submit_assignment", null, values)
        db.close()
        return result != -1L
    }




    // Method to get student by email
    @SuppressLint("Range")
    fun getStudentByEmail(email: String): Student? {
        val db = readableDatabase
        val cursor = db.query("student", null, "email = ?", arrayOf(email), null, null, null)
        return if (cursor != null && cursor.moveToFirst()) {
            val student = Student(
                studentId = cursor.getString(cursor.getColumnIndex("studentId")),
                name = cursor.getString(cursor.getColumnIndex("name")),
                email = cursor.getString(cursor.getColumnIndex("email")),
                dept = cursor.getString(cursor.getColumnIndex("dept")),
                batch = cursor.getString(cursor.getColumnIndex("batch")),
                sec = cursor.getString(cursor.getColumnIndex("sec")),
                password = cursor.getString(cursor.getColumnIndex("password"))
            )
            cursor.close()
            student
        } else {
            cursor.close()
            null
        }
    }

    @SuppressLint("Range")
    fun getStudentById(studentId: String): Student? {
        val db = readableDatabase
        val cursor = db.query("student", null, "studentId = ?", arrayOf(studentId), null, null, null)
        return if (cursor != null && cursor.moveToFirst()) {
            val student = Student(
                studentId = cursor.getString(cursor.getColumnIndex("studentId")),
                name = cursor.getString(cursor.getColumnIndex("name")),
                email = cursor.getString(cursor.getColumnIndex("email")),
                dept = cursor.getString(cursor.getColumnIndex("dept")),
                batch = cursor.getString(cursor.getColumnIndex("batch")),
                sec = cursor.getString(cursor.getColumnIndex("sec")),
                password = cursor.getString(cursor.getColumnIndex("password")),
                profilePic = cursor.getBlob(cursor.getColumnIndex("profilePic"))

            )
            cursor.close()
            student
        } else {
            cursor.close()
            null
        }
    }

    // Method to get teacher by email
    @SuppressLint("Range")
    fun getTeacherByEmail(email: String): Teacher? {
        val db = readableDatabase
        val cursor = db.query("teacher", null, "email = ?", arrayOf(email), null, null, null)
        return if (cursor != null && cursor.moveToFirst()) {
            val teacher = Teacher(
                teacherId = cursor.getString(cursor.getColumnIndex("teacherId")),
                name = cursor.getString(cursor.getColumnIndex("name")),
                dept = cursor.getString(cursor.getColumnIndex("dept")),
                des = cursor.getString(cursor.getColumnIndex("des")),
                email = cursor.getString(cursor.getColumnIndex("email")),
                password = cursor.getString(cursor.getColumnIndex("password")),
                profilePic = cursor.getBlob(cursor.getColumnIndex("profilePic"))
            )
            cursor.close()
            teacher
        } else {
            cursor.close()
            null
        }
    }

    @SuppressLint("Range")
    fun getTeacherById(teacherId: String): Teacher? {
        val db = readableDatabase
        val cursor = db.query("teacher", null, "teacherId = ?", arrayOf(teacherId), null, null, null)
        return if (cursor != null && cursor.moveToFirst()) {
            val teacher = Teacher(
                teacherId = cursor.getString(cursor.getColumnIndex("teacherId")),
                name = cursor.getString(cursor.getColumnIndex("name")),
                dept = cursor.getString(cursor.getColumnIndex("dept")),
                des = cursor.getString(cursor.getColumnIndex("des")),
                email = cursor.getString(cursor.getColumnIndex("email")),
                password = cursor.getString(cursor.getColumnIndex("password")),
                profilePic = cursor.getBlob(cursor.getColumnIndex("profilePic"))
            )
            cursor.close()
            teacher
        } else {
            cursor.close()
            null
        }
    }

    fun getCoursesByTeacherId(teacherId: String): List<Course> {
        val courseList = mutableListOf<Course>()
        val db = this.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM course WHERE teacherId = ?",
            arrayOf(teacherId)
        )

        if (cursor.moveToFirst()) {
            do {
                val course = Course(
                    courseId = cursor.getString(cursor.getColumnIndexOrThrow("courseId")),
                    teacherId = cursor.getString(cursor.getColumnIndexOrThrow("teacherId")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    sName = cursor.getString(cursor.getColumnIndexOrThrow("sName")),
                    courseCode = cursor.getString(cursor.getColumnIndexOrThrow("courseCode")),
                    accessKey = cursor.getString(cursor.getColumnIndexOrThrow("accessKey")),

                    dept = cursor.getString(cursor.getColumnIndexOrThrow("dept")),
                    lt = cursor.getString(cursor.getColumnIndexOrThrow("lt")),
                    sec = cursor.getString(cursor.getColumnIndexOrThrow("sec")),

                    )
                courseList.add(course)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return courseList
    }

    fun getCourseByCourseId(courseId: String): Course? {
        val db = this.readableDatabase
        var course: Course? = null

        val cursor = db.rawQuery(
            "SELECT * FROM course WHERE courseId = ?",
            arrayOf(courseId)
        )

        if (cursor.moveToFirst()) {
            // Get data from cursor and create Course object
            course = Course(
                courseId = cursor.getString(cursor.getColumnIndexOrThrow("courseId")),
                teacherId = cursor.getString(cursor.getColumnIndexOrThrow("teacherId")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                sName = cursor.getString(cursor.getColumnIndexOrThrow("sName")),
                courseCode = cursor.getString(cursor.getColumnIndexOrThrow("courseCode")),
                accessKey = cursor.getString(cursor.getColumnIndexOrThrow("accessKey")),
                dept = cursor.getString(cursor.getColumnIndexOrThrow("dept")),
                lt = cursor.getString(cursor.getColumnIndexOrThrow("lt")),
                sec = cursor.getString(cursor.getColumnIndexOrThrow("sec"))
            )
        }

        cursor.close()
        db.close()

        return course
    }
    fun getAssignmentsByCourseId(courseId: String): List<Assignment> {
        val assignmentList = mutableListOf<Assignment>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM assignment WHERE courseId = ?", arrayOf(courseId))

        if (cursor.moveToFirst()) {
            do {
                val assignment = Assignment(
                    assignmentId = cursor.getString(cursor.getColumnIndexOrThrow("assignmentId")),
                    courseId = cursor.getString(cursor.getColumnIndexOrThrow("courseId")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    des = cursor.getString(cursor.getColumnIndexOrThrow("des")),
                    deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline")),
                    totalMark = cursor.getString(cursor.getColumnIndexOrThrow("totalMark"))
                )
                assignmentList.add(assignment)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return assignmentList
    }


    fun getAssignmentById(assignmentId: String): Assignment? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM assignment WHERE assignmentId = ?", arrayOf(assignmentId))
        var assignment: Assignment? = null
        if (cursor.moveToFirst()) {
            assignment = Assignment(
                assignmentId = cursor.getString(cursor.getColumnIndexOrThrow("assignmentId")),
                courseId = cursor.getString(cursor.getColumnIndexOrThrow("courseId")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                des = cursor.getString(cursor.getColumnIndexOrThrow("des")),
                deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline")),
                totalMark = cursor.getString(cursor.getColumnIndexOrThrow("totalMark"))
            )
        }
        cursor.close()
        db.close()
        return assignment
    }







    fun deleteAssignmentById(assignmentId: String): Boolean {
        val db = writableDatabase
        val result = db.delete("assignment", "assignmentId = ?", arrayOf(assignmentId))
        db.close()
        return result > 0
    }

    fun insertCourseStudent(courseStudent: CourseStudent): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("courseId", courseStudent.courseId)
            put("studentId", courseStudent.studentId)
        }
        val result = db.insert("course_student", null, values)
        db.close()
        return result != -1L
    }

    fun getCoursesByStudentId(studentId: String): List<String> {
        val courseIds = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT courseId FROM course_student WHERE studentId = ?",
            arrayOf(studentId)
        )

        if (cursor.moveToFirst()) {
            do {
                courseIds.add(cursor.getString(cursor.getColumnIndexOrThrow("courseId")))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return courseIds
    }

    fun getStudentsByCourseId(courseId: String): List<String> {
        val studentIds = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT studentId FROM course_student WHERE courseId = ?",
            arrayOf(courseId)
        )

        if (cursor.moveToFirst()) {
            do {
                studentIds.add(cursor.getString(cursor.getColumnIndexOrThrow("studentId")))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return studentIds
    }

    fun isCourseStudentExists(courseId: String, studentId: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM course_student WHERE courseId = ? AND studentId = ?",
            arrayOf(courseId, studentId)
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }


    fun getSubmitAssignment(assignmentId: String, studentId: String): SubmitAssignment? {
        val db = readableDatabase
        var submitAssignment: SubmitAssignment? = null

        val cursor = db.rawQuery(
            "SELECT * FROM submit_assignment WHERE assignmentId = ? AND studentId = ?",
            arrayOf(assignmentId, studentId)
        )

        if (cursor.moveToFirst()) {
            submitAssignment = SubmitAssignment(
                id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                assignmentId = cursor.getString(cursor.getColumnIndexOrThrow("assignmentId")),
                studentId = cursor.getString(cursor.getColumnIndexOrThrow("studentId")),
                submitTime = cursor.getString(cursor.getColumnIndexOrThrow("submitTime")),
                submitDate = cursor.getString(cursor.getColumnIndexOrThrow("submitDate")),
                status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                feedBack = cursor.getString(cursor.getColumnIndexOrThrow("feedBack")),
                mark = cursor.getString(cursor.getColumnIndexOrThrow("mark")),
                file = cursor.getBlob(cursor.getColumnIndexOrThrow("file")),
                fileType = cursor.getString(cursor.getColumnIndexOrThrow("fileType"))
            )
        }

        cursor.close()
        db.close()

        return submitAssignment
    }

    fun getSubmitAssignmentId(id: String): SubmitAssignment? {
        val db = readableDatabase
        var submitAssignment: SubmitAssignment? = null

        val cursor = db.rawQuery(
            "SELECT * FROM submit_assignment WHERE id = ?",
            arrayOf(id)
        )

        if (cursor.moveToFirst()) {
            submitAssignment = SubmitAssignment(
                id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                assignmentId = cursor.getString(cursor.getColumnIndexOrThrow("assignmentId")),
                studentId = cursor.getString(cursor.getColumnIndexOrThrow("studentId")),
                submitTime = cursor.getString(cursor.getColumnIndexOrThrow("submitTime")),
                submitDate = cursor.getString(cursor.getColumnIndexOrThrow("submitDate")),
                status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                feedBack = cursor.getString(cursor.getColumnIndexOrThrow("feedBack")),
                mark = cursor.getString(cursor.getColumnIndexOrThrow("mark")),
                file = cursor.getBlob(cursor.getColumnIndexOrThrow("file")),
                fileType = cursor.getString(cursor.getColumnIndexOrThrow("fileType"))
            )
        }

        cursor.close()
        db.close()

        return submitAssignment
    }

    fun getSubmitAssignmentsByStudentId(studentId: String): List<SubmitAssignment> {
        val db = readableDatabase
        val submitAssignments = mutableListOf<SubmitAssignment>()

        val cursor = db.rawQuery(
            "SELECT * FROM submit_assignment WHERE studentId = ?",
            arrayOf(studentId)
        )

        if (cursor.moveToFirst()) {
            do {
                val submitAssignment = SubmitAssignment(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    assignmentId = cursor.getString(cursor.getColumnIndexOrThrow("assignmentId")),
                    studentId = cursor.getString(cursor.getColumnIndexOrThrow("studentId")),
                    submitTime = cursor.getString(cursor.getColumnIndexOrThrow("submitTime")),
                    submitDate = cursor.getString(cursor.getColumnIndexOrThrow("submitDate")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                    feedBack = cursor.getString(cursor.getColumnIndexOrThrow("feedBack")),
                    mark = cursor.getString(cursor.getColumnIndexOrThrow("mark")),
                    file = cursor.getBlob(cursor.getColumnIndexOrThrow("file")),
                    fileType = cursor.getString(cursor.getColumnIndexOrThrow("fileType"))
                )
                submitAssignments.add(submitAssignment)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return submitAssignments
    }



    fun getSubmitAssignmentsByAssignmentId(assignmentId: String): List<SubmitAssignment> {
        val db = readableDatabase
        val assignmentList = mutableListOf<SubmitAssignment>()

        val cursor = db.rawQuery(
            "SELECT * FROM submit_assignment WHERE assignmentId = ?",
            arrayOf(assignmentId)
        )

        if (cursor.moveToFirst()) {
            do {
                val submitAssignment = SubmitAssignment(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    assignmentId = cursor.getString(cursor.getColumnIndexOrThrow("assignmentId")),
                    studentId = cursor.getString(cursor.getColumnIndexOrThrow("studentId")),
                    submitTime = cursor.getString(cursor.getColumnIndexOrThrow("submitTime")),
                    submitDate = cursor.getString(cursor.getColumnIndexOrThrow("submitDate")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                    feedBack = cursor.getString(cursor.getColumnIndexOrThrow("feedBack")),
                    mark = cursor.getString(cursor.getColumnIndexOrThrow("mark")),
                    file = cursor.getBlob(cursor.getColumnIndexOrThrow("file")),
                    fileType = cursor.getString(cursor.getColumnIndexOrThrow("fileType"))
                )
                assignmentList.add(submitAssignment)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return assignmentList
    }



    // In MyDatabaseHelper.kt

    fun updateSubmitAssignment(submittedAssignment: SubmitAssignment): Boolean {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put("assignmentId", submittedAssignment.assignmentId)
        contentValues.put("studentId", submittedAssignment.studentId)
        contentValues.put("submitTime", submittedAssignment.submitTime)
        contentValues.put("submitDate", submittedAssignment.submitDate)
        contentValues.put("status", submittedAssignment.status)
        contentValues.put("feedBack", submittedAssignment.feedBack)
        contentValues.put("mark", submittedAssignment.mark)
        contentValues.put("file", submittedAssignment.file)  // The file is stored as BLOB
        contentValues.put("fileType", submittedAssignment.fileType)

        val result = db.update("submit_assignment", contentValues, "id = ?", arrayOf(submittedAssignment.id))

        db.close()
        return result > 0
    }

    fun deleteSubmitAssignmentsByAssignmentId(assignmentId: String): Boolean {
        val db = writableDatabase
        val result = db.delete("submit_assignment", "assignmentId = ?", arrayOf(assignmentId))
        db.close()
        return result > 0
    }


    fun deleteCourseById(courseId: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete("course", "courseId = ?", arrayOf(courseId))
        db.close()
        return result > 0
    }

    fun deleteCourseStudentByCourseId(courseId: String): Boolean {
        val db = writableDatabase
        val result = db.delete("course_student", "courseId = ?", arrayOf(courseId))
        db.close()
        return result > 0
    }


    fun updateAssignment(assignment: Assignment): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", assignment.title)
            put("des", assignment.des)
            put("deadline", assignment.deadline)
            put("totalMark", assignment.totalMark)
        }
        val result = db.update("assignment", values, "assignmentId = ?", arrayOf(assignment.assignmentId))
        db.close()
        return result > 0
    }


    fun updateCourse(course: Course): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("teacherId", course.teacherId)
            put("title", course.title)
            put("sName", course.sName)
            put("courseCode", course.courseCode)
            put("accessKey", course.accessKey)
            put("dept", course.dept)
            put("lt", course.lt)
            put("sec", course.sec)
        }

        val rowsAffected = db.update("course", values, "courseId = ?", arrayOf(course.courseId))

        return rowsAffected > 0
    }


    fun updateStudent(student: Student): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", student.name)
            put("email", student.email)
            put("dept", student.dept)
            put("batch", student.batch)
            put("sec", student.sec)
            put("profilePic", student.profilePic)
            put("password", student.password)
        }

        val result = db.update(
            "student",
            values,
            "studentId = ?",
            arrayOf(student.studentId)
        )

        db.close()
        return result > 0
    }

    fun updateTeacher(teacher: Teacher): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", teacher.name)
            put("email", teacher.email)
            put("dept", teacher.dept)
            put("des", teacher.des)
            put("profilePic", teacher.profilePic)
            put("password", teacher.password)
        }

        val result = db.update(
            "teacher",
            values,
            "teacherId = ?",
            arrayOf(teacher.teacherId)
        )

        db.close()
        return result > 0
    }




}
