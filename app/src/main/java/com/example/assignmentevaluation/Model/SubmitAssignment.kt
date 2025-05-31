package com.example.assinment_evaluation.Model

import java.sql.Time
import java.util.Date

class SubmitAssignment(
    var id: String?=null,
    var assignmentId: String?=null,
    var studentId: String?=null,
    var submitTime: String? = null,
    var submitDate: String? = null,
    var status: String?=null,
    var feedBack: String?=null,
    var mark: String?=null,
    var file: ByteArray? = null,
    var fileType: String?=null,
    ) {
}