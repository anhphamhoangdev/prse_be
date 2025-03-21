package com.hcmute.prse_be.rest;

import com.cloudinary.Api;
import com.hcmute.prse_be.constants.*;
import com.hcmute.prse_be.dtos.*;
import com.hcmute.prse_be.entity.*;
import com.hcmute.prse_be.request.*;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.*;
import com.hcmute.prse_be.util.ConvertUtils;
import com.hcmute.prse_be.util.JsonUtils;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApiPaths.INSTRUCTOR_API)
public class InstructorAPI {

    private final StudentService studentService;

    private final InstructorService instructorService;

    private final CloudinaryService cloudinaryService;

    private final CourseService courseService;

    private final WebSocketService webSocketService;

    private final WithdrawService withdrawService;

    public InstructorAPI(StudentService studentService, InstructorService instructorService, CloudinaryService cloudinaryService, CourseService courseService, WebSocketService webSocketService, WithdrawService withdrawService) {
        this.studentService = studentService;
        this.instructorService = instructorService;
        this.cloudinaryService = cloudinaryService;
        this.courseService = courseService;
        this.webSocketService = webSocketService;
        this.withdrawService = withdrawService;
    }


    @GetMapping(ApiPaths.GET_PROFILE)
    public ResponseEntity<JSONObject> getProfile(Authentication authentication) {
        LogService.getgI().info("[InstructorAPI] getInstructorProfile username: "+authentication.getName() );

        try {

            String username = authentication.getName();

            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());

            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // set total student and total course
            instructor.setTotalStudent(ConvertUtils.toInt(instructorService.getTotalStudentOfInstructor(instructor.getId())));
            instructor.setTotalCourse(ConvertUtils.toInt(instructorService.getTotalCourseOfInstructor(instructor.getId())));

            JSONObject response = new JSONObject();
            response.put("instructor", instructor);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
        }
    }
    @GetMapping(ApiPaths.INSTRUCTOR_POPULAR_POSITION)
    public ResponseEntity<JSONObject> popularPosition()
    {
        LogService.getgI().info("[InstructorAPI] getListPopularPosition");
        try{
            if(instructorService.getAllTitles()==null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.success(new JSONObject()));
            }
            List <InstructorCommonTitleEntity> listPopular= instructorService.getAllTitles();
            JSONObject response = new JSONObject();
            response.put("instructor_common_titles", listPopular);
            return ResponseEntity.ok(Response.success(response));
        }
        catch(Exception err)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));

        }
    }

    @GetMapping(ApiPaths.INSTRUCTOR_GET_COURSES)
    public ResponseEntity<JSONObject> getCourses(Authentication authentication) {
        LogService.getgI().info("[InstructorAPI] getCoursesOfInstructor username: "+authentication.getName() );

        try {

            String username = authentication.getName();

            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());

            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.INSTRUCTOR_DOES_NOT_EXIST));
            }

            List<CourseEntity> courses = courseService.getCoursesByInstructorId(instructor.getId());

            JSONObject response = new JSONObject();
            response.put("courses", courses);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
        }
    }

    @GetMapping(ApiPaths.REVENUE)
    public ResponseEntity<JSONObject> getRevenueStatistics(
            @RequestParam(defaultValue = "6") int monthsCount,
            Authentication authentication
    ) {
        LogService.getgI().info("[InstructorAPI] getRevenueStatistics username: "+authentication.getName() + "months: "+monthsCount);
        try {
            // Verify student
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            // Verify instructor
            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error(ErrorMsg.INSTRUCTOR_DOES_NOT_EXIST));
            }

            // Get revenue statistics
            List<RevenueStatisticsDTO> statistics = instructorService.getRevenueStatistics(
                    instructor.getId(),
                    monthsCount
            );

            // Build response
            JSONObject response = new JSONObject();
            response.put("revenue_statistics", statistics);

            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
        }
    }

    @GetMapping(ApiPaths.INSTRUCTOR_GET_RECENT_ENROLL)
    public ResponseEntity<?> getRecentEnrollments(Authentication authentication) {
        LogService.getgI().info("[InstructorAPI] getRecentEnrollments Of " +authentication.getName() );
        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            List<RecentEnrollmentDTO> enrollments = instructorService.getRecentEnrollments(instructor.getId());

            JSONObject response = new JSONObject();
            response.put("recent_enrollments", enrollments);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
        }
    }

    @PostMapping(ApiPaths.INSTRUCTOR_UPLOAD_COURSE)
    public ResponseEntity<JSONObject> uploadCourse(
            @RequestParam("image") MultipartFile image,
            @RequestParam("data") String data,
            Authentication authentication) {
        LogService.getgI().info("[InstructorAPI] uploadCourse username: "+authentication.getName() );
        try {

            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error(ErrorMsg.INSTRUCTOR_DOES_NOT_EXIST));
            }

            // Xử lý dữ liệu form
            CourseFormDataRequest courseFormData = JsonUtils.DeSerialize(data, CourseFormDataRequest.class);

            // tao khoa hoc
            CourseEntity course = courseService.createCourse(courseFormData, instructor);

            // upload anh
            String imagePath =
                    cloudinaryService.uploadImage(image, ImageFolderName.COURSE+"/"+course.getId());


            // luu anh
            course.setImageUrl(imagePath);
            courseService.saveCourse(course);

            // gui thong bao cho giao vien
            // ban socket cho giáo viên
            WebSocketMessage message = WebSocketMessage.uploadStarted(
                    "Khóa học: " + course.getTitle(),
                    "Preview video đang được upload..."

            );
            webSocketService.sendToInstructor(instructor.getId(), "/uploads", message);

            JSONObject response = new JSONObject();
            response.put("course", course);


            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi tải lên khóa học"));
        }
    }

    @PostMapping(ApiPaths.INSTRUCTOR_UPLOAD_PREVIEW_VIDEO)
    public ResponseEntity<JSONObject> uploadVideo(@RequestParam("video") MultipartFile file,
                                                  @RequestParam("courseId") String courseId,
                                                  Authentication authentication) {
        LogService.getgI().info("[InstructorAPI] UploadPreviewVideo courseId: " +courseId +" Instructor: "+ authentication.getName() );
        try {
            LogService.getgI().info("Uploading video... : uploadVideo");
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Lấy thông tin khóa học
            CourseEntity course = courseService.getCourse(ConvertUtils.toLong(courseId));
            if (course == null || !course.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }


            // Tạo một UploadingVideoDetail mới
            String threadId = UUID.randomUUID().toString();
            UploadingVideoDetail uploadStatus = new UploadingVideoDetail(threadId, "PENDING");
            uploadStatus.setInstructorId(instructor.getId());
            uploadStatus.setTitle("Preview video của khóa học: " + course.getTitle());
            uploadStatus.setImageUrl(course.getImageUrl());

            // Lưu vào cache
            UploadingVideoCache.getInstance().getUploadingVideo().put(threadId, uploadStatus);

            // Tên thư mục : course/{courseId}
            String folderName = ImageFolderName.COURSE + "/" + course.getId();
            CompletableFuture.supplyAsync(() -> {
                try {
                    // Thực hiện upload video
                    Map uploadResult = cloudinaryService.uploadVideo(file, folderName);
                    LogService.getgI().info("Video uploaded successfully. URL: " + JsonUtils.Serialize(uploadResult));
                    uploadStatus.setStatus("COMPLETED");

                    String videoUrl = (String) uploadResult.get("secure_url");
                    uploadStatus.setUploadResult(uploadResult);

                    // Cập nhật thông tin khóa học ở đây
                    course.setPreviewVideoUrl(videoUrl);
                    courseService.saveCourse(course);

                    // Gửi thông báo cho giáo viên
                    WebSocketMessage messageSuccess = WebSocketMessage.uploadComplete(
                            "Khóa học: " + course.getTitle(),
                            "Preview video đã được upload thành công"

                    );
                    webSocketService.sendToInstructor(instructor.getId(), "/uploads", messageSuccess);

                    return uploadResult;
                } catch (Exception e) {
                    uploadStatus.setStatus("FAILED");
                    uploadStatus.setErrorMessage(e.getMessage());
                    return null;
                } finally {
                    UploadingVideoCache.getInstance().getUploadingVideo().remove(threadId);
                }
            });



            // Trả về phản hồi ngay lập tức
            JSONObject response = new JSONObject();
            response.put("success", true);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi tải lên khóa học"));
        }
    }


    @GetMapping(ApiPaths.INSTRUCTOR_UPLOAD_STATUS)
    public ResponseEntity<JSONObject> getUploadStatusesByInstructor(Authentication authentication) {
        LogService.getgI().info("[InstructorAPI] getUploadStatusesByInstructor: "+authentication.getName());
        try {
            // Kiểm tra thông tin người dùng
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            // Kiểm tra thông tin giảng viên
            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Lấy danh sách trạng thái upload của instructor
            UploadingVideoCache cache = UploadingVideoCache.getInstance();
            List<UploadingVideoDetail> statuses = cache.getUploadingVideo().values().stream()
                    .filter(status -> instructor.getId().equals(status.getInstructorId()))
                    .collect(Collectors.toList());

            // Trả về response thành công
            JSONObject response = new JSONObject();
            response.put("uploadStatuses", statuses);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @GetMapping(ApiPaths.INSTRUCTOR_GET_COURSE_ID)
    public ResponseEntity<JSONObject> getCourse(Authentication authentication, @PathVariable("courseId") Long courseId) {
        LogService.getgI().info("[InstructorAPI] getCourseId: "+ courseId + " username: "+authentication.getName());
        try {

            Long id = ConvertUtils.toLong(courseId);
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(id);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            // Trả về response thành công
            JSONObject response = new JSONObject();
            response.put("course", courseEntity);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @PutMapping(ApiPaths.INSTRUCTOR_UPDATE_COURSE_ID)
    public ResponseEntity<JSONObject> updateCourse(Authentication authentication,
                                                   @PathVariable("courseId") Long courseId,
                                                   @RequestBody UpdateCourseRequest updateCourseRequest) {
        LogService.getgI().info("[InstructorAPI] getCourseId: "+ courseId + " "+ updateCourseRequest.toString() + " username: "+ authentication.getName());

        try {

            Long id = ConvertUtils.toLong(courseId);
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(id);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            courseEntity.setTitle(updateCourseRequest.getCourse().getTitle());
            courseEntity.setDescription(updateCourseRequest.getCourse().getDescription());
            courseEntity.setShortDescription(updateCourseRequest.getCourse().getShortDescription());
            courseEntity.setImageUrl(updateCourseRequest.getCourse().getImageUrl());
            courseEntity.setLanguage(updateCourseRequest.getCourse().getLanguage());
            courseEntity.setIsPublish(updateCourseRequest.getCourse().getIsPublish());
            courseEntity.setPreviewVideoUrl(updateCourseRequest.getCourse().getPreviewVideoUrl());
            courseEntity.setPreviewVideoDuration(updateCourseRequest.getCourse().getPreviewVideoDuration());
            courseEntity.setOriginalPrice(updateCourseRequest.getCourse().getOriginalPrice());
            courseService.saveCourse(courseEntity);
            // Trả về response thành công
            JSONObject response = new JSONObject();
            response.put("course", courseEntity);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @GetMapping(ApiPaths.INSTRUCTOR_GET_COURSE_CURRICULUM)
    public ResponseEntity<JSONObject> getCurriculum(Authentication authentication, @PathVariable("courseId") Long courseId) {
        LogService.getgI().info("[InstructorAPI] getCourseCurriculum: "+ courseId + " "+authentication.getName());

        try {

            Long id = ConvertUtils.toLong(courseId);
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(id);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            List<ChapterEntity> chapters = courseService.getChaptersByCourseId(id);


            // create chapter id
            List<ChapterInstructorEditDTO> chapterInstructorEditDTOS  = new ArrayList<>();

            for (ChapterEntity chapter : chapters) {
                ChapterInstructorEditDTO chapterInstructorEditDTO = new ChapterInstructorEditDTO();
                chapterInstructorEditDTO.setId(chapter.getId());
                chapterInstructorEditDTO.setTitle(chapter.getTitle());
                chapterInstructorEditDTO.setOrderIndex(chapter.getOrderIndex());
                chapterInstructorEditDTOS.add(chapterInstructorEditDTO);
            }

            // find lesson by chapter
            for (ChapterInstructorEditDTO chapterInstructorEditDTO : chapterInstructorEditDTOS) {
                List<LessonInstructorEditDTO> lessonInstructorEditDTOS = new ArrayList<>();
                for (ChapterEntity chapter : chapters) {
                    if (chapter.getId().equals(chapterInstructorEditDTO.getId())) {
                        List<LessonEntity> lessons = courseService.getAllLessonByChapterId(chapter.getId());
                        for (LessonEntity lesson : lessons) {
                            LessonInstructorEditDTO lessonInstructorEditDTO = new LessonInstructorEditDTO();
                            lessonInstructorEditDTO.setId(lesson.getId());
                            lessonInstructorEditDTO.setTitle(lesson.getTitle());
                            lessonInstructorEditDTO.setType(lesson.getType());
                            lessonInstructorEditDTO.setPublish(lesson.getIsPublish());
                            lessonInstructorEditDTO.setOrderIndex(lesson.getOrderIndex());
                            lessonInstructorEditDTOS.add(lessonInstructorEditDTO);
                        }
                    }
                }
                chapterInstructorEditDTO.setLessons(lessonInstructorEditDTOS);
            }
            // Trả về response thành công
            JSONObject response = new JSONObject();
            response.put("chapters", chapterInstructorEditDTOS);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @GetMapping(ApiPaths.INSTRUCTOR_GET_CHAPTER_ID)
    public ResponseEntity<JSONObject> getChapterInfo(Authentication authentication, @PathVariable("courseId") Long courseId, @PathVariable("chapterId") Long chapterId) {
        LogService.getgI().info("[InstructorAPI] getChapter username:"+ authentication.getName()+ " courseId: "+ courseId + "chapterId: "+chapterId);

        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(courseId);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            ChapterEntity chapterEntity = courseService.getChapterById(chapterId);

            if(chapterEntity == null || !chapterEntity.getCourseId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin chương"));
            }

            ChapterInstructorEditDTO chapterInstructorEditDTO = new ChapterInstructorEditDTO();
            chapterInstructorEditDTO.setId(chapterEntity.getId());
            chapterInstructorEditDTO.setTitle(chapterEntity.getTitle());
            chapterInstructorEditDTO.setOrderIndex(chapterEntity.getOrderIndex());

            List<LessonInstructorEditDTO> lessonInstructorEditDTOS = new ArrayList<>();
            List<LessonEntity> lessons = courseService.getAllLessonByChapterId(chapterId);
            for (LessonEntity lesson : lessons) {
                LessonInstructorEditDTO lessonInstructorEditDTO = new LessonInstructorEditDTO();
                lessonInstructorEditDTO.setId(lesson.getId());
                lessonInstructorEditDTO.setTitle(lesson.getTitle());
                lessonInstructorEditDTO.setType(lesson.getType());
                lessonInstructorEditDTO.setPublish(lesson.getIsPublish());
                lessonInstructorEditDTO.setOrderIndex(lesson.getOrderIndex());
                lessonInstructorEditDTOS.add(lessonInstructorEditDTO);
            }
            chapterInstructorEditDTO.setLessons(lessonInstructorEditDTOS);

            // Trả về response thành công
            JSONObject response = new JSONObject();
            response.put("chapter", chapterInstructorEditDTO);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @GetMapping(ApiPaths.INSTRUCTOR_GET_LESSON_ID)
    public ResponseEntity<JSONObject> getLessonInfo(Authentication authentication, @PathVariable("courseId") Long courseId, @PathVariable("chapterId") Long chapterId, @PathVariable("lessonId") Long lessonId){
        LogService.getgI().info("[InstructorAPI] getLesson username: "+ authentication.getName()+ " courseId: "+ courseId + "chapterId: "+chapterId + " lessonId"+ lessonId);

        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(courseId);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            ChapterEntity chapterEntity = courseService.getChapterById(chapterId);

            if(chapterEntity == null || !chapterEntity.getCourseId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin chương"));
            }


            List<LessonEntity> lessons = courseService.getAllLessonByChapterId(chapterId);
            for (LessonEntity lesson : lessons) {
                if (lesson.getId().equals(lessonId)) {
                    JSONObject response = new JSONObject();
                    response.put("lesson", lesson);
                    return ResponseEntity.ok(Response.success(response));
                }
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Không tìm thấy thông tin"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @PutMapping(ApiPaths.INSTRUCTOR_UPDATE_LESSON_ID)
    public ResponseEntity<JSONObject> changeIsPublish(Authentication authentication, @PathVariable("courseId") Long courseId, @PathVariable("chapterId") Long chapterId, @PathVariable("lessonId") Long lessonId){
        LogService.getgI().info("[InstructorAPI] updateLesson username: "+ authentication.getName()+ " courseId: "+ courseId + "chapterId: "+chapterId+ " lessonId: "+lessonId);
        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(courseId);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            ChapterEntity chapterEntity = courseService.getChapterById(chapterId);

            if(chapterEntity == null || !chapterEntity.getCourseId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin chương"));
            }


            List<LessonEntity> lessons = courseService.getAllLessonByChapterId(chapterId);
            for (LessonEntity lesson : lessons) {
                if (lesson.getId().equals(lessonId)) {
                    JSONObject response = new JSONObject();
                    lesson.setIsPublish(!lesson.getIsPublish());
                    courseService.saveLesson(lesson);
                    response.put("lesson", lesson);
                    return ResponseEntity.ok(Response.success(response));
                }
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Không tìm thấy thông tin"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @GetMapping(ApiPaths.INSTRUCTOR_GET_LESSON_VIDEO)
    public ResponseEntity<JSONObject> getVideoLessonInfo(Authentication authentication, @PathVariable("courseId") Long courseId, @PathVariable("chapterId") Long chapterId, @PathVariable("lessonId") Long lessonId){
        LogService.getgI().info("[InstructorAPI] getVideoLesson username: "+ authentication.getName()+ " courseId: "+ courseId + "chapterId: "+chapterId+ " lessonId: "+lessonId);
        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(courseId);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            ChapterEntity chapterEntity = courseService.getChapterById(chapterId);

            if(chapterEntity == null || !chapterEntity.getCourseId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin chương"));
            }


            List<LessonEntity> lessons = courseService.getAllLessonByChapterId(chapterId);
            for (LessonEntity lesson : lessons) {
                if (lesson.getId().equals(lessonId)) {
                    JSONObject response = new JSONObject();
                    VideoLessonEntity videoLessonEntity = courseService.getVideoLesson(courseId, chapterId, lessonId);
                    response.put("video", videoLessonEntity);
                    return ResponseEntity.ok(Response.success(response));
                }
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Không tìm thấy thông tin"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @PostMapping(ApiPaths.INSTRUCTOR_POST_LESSON_INFOR)
    public ResponseEntity<JSONObject> addNewLesson(Authentication authentication, @PathVariable("courseId") Long courseId, @PathVariable("chapterId") Long chapterId, @RequestBody AddNewLessonRequest addLessonRequest) {
        LogService.getgI().info("[InstructorAPI] createLesson username: "+ authentication.getName()+" courseId: "+ courseId + "chapterId: "+chapterId+ addLessonRequest.toString());
        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(courseId);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            ChapterEntity chapterEntity = courseService.getChapterById(chapterId);

            if(chapterEntity == null || !chapterEntity.getCourseId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin chương"));
            }


            List<LessonEntity> lessons = courseService.getLessonsByChapterId(chapterId);

            LessonEntity lessonEntity = new LessonEntity();
            lessonEntity.setTitle(addLessonRequest.getLesson().getTitle());
            lessonEntity.setType(addLessonRequest.getLesson().getType());
            lessonEntity.setChapterId(chapterId);
            lessonEntity.setOrderIndex(lessons.size() + 1);
            lessonEntity.setIsPublish(addLessonRequest.getLesson().isPublish());
            courseService.saveLesson(lessonEntity);
            // Trả về response thành công
            return ResponseEntity.ok(Response.success());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi"));
        }
    }

    @PostMapping(ApiPaths.INSTRUCTOR_UPLOAD_LESSON_VIDEO)
    public ResponseEntity<JSONObject> uploadLessonVideo(
            @RequestParam("video") MultipartFile file,
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @PathVariable Long lessonId,
            Authentication authentication) {
        LogService.getgI().info("[InstructorAPI] uploadLessonVideo username: "+ authentication.getName()+ " courseId: "+courseId+" chapterId: "+chapterId+" lessonId: "+lessonId);
        try {
            LogService.getgI().info("[InstructorAPI] uploadLessonVideo courseId: "  +courseId +" chapterId: "+chapterId+" lessonId: "+lessonId);
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Validate course ownership
            CourseEntity course = courseService.getCourse(ConvertUtils.toLong(courseId));
            if (course == null || !course.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không có quyền truy cập khóa học này"));
            }

            // Validate chapter
            ChapterEntity chapterEntity = courseService.getChapterById(chapterId);
            if(chapterEntity == null || !chapterEntity.getCourseId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin chương"));
            }

            List<LessonEntity> lessons = courseService.getAllLessonByChapterId(chapterId);
            for (LessonEntity lesson : lessons) {
                if (lesson.getId().equals(lessonId)) {
                    JSONObject response = new JSONObject();

                    // lay ra duoc video lesson roi => bat dau luu tru
                    VideoLessonEntity videoLessonEntity = courseService.getVideoLesson(courseId, chapterId, lessonId);

                    if(videoLessonEntity == null) {
                        videoLessonEntity = new VideoLessonEntity();
                    }

                    // Check if video is already uploaded
                    String threadId = UUID.randomUUID().toString();
                    UploadingVideoDetail uploadStatus = new UploadingVideoDetail(threadId, "PENDING");
                    uploadStatus.setInstructorId(instructor.getId());
                    uploadStatus.setLessonId(lessonId);
                    uploadStatus.setTitle("Video cho bài học: " + lesson.getTitle());


                    // save to cache
                    UploadingVideoCache.getInstance().getUploadingVideo().put(threadId, uploadStatus);
                    // Folder structure: course/{courseId}/chapter/{chapterId}/lesson/{lessonId}
                    String folderName = String.format("%s/%s/chapter/%s/lesson/%s",
                            ImageFolderName.COURSE,
                            course.getId(),
                            chapterId,
                            lessonId);

                    LogService.getgI().info("Uploading video... : " + folderName);

                    VideoLessonEntity finalVideoLessonEntity = videoLessonEntity;
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            // Start progress simulation in separate thread
                            Thread progressSimulator = new Thread(() -> {
                                try {
                                    double progress = 0;
                                    while (progress < 95 && "UPLOADING".equals(uploadStatus.getStatus())) {
                                        // Simulate slower progress as we get closer to 95%
                                        double increment = (95 - progress) / 20;
                                        progress += increment;
                                        uploadStatus.setProgress(progress);
                                        LogService.getgI().info("Progress: " + progress);
                                        Thread.sleep(1000); // Update every second
                                    }
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            });

                            // Start progress simulation
                            uploadStatus.setStatus("UPLOADING");
                            progressSimulator.start();

                            // Actual upload process
                            LogService.getgI().info("PASSED 0");
                            Map uploadResult = cloudinaryService.uploadVideo(file, folderName);

                            // Upload completed
                            uploadStatus.setStatus("COMPLETED");
                            uploadStatus.setProgress(100.0);
                            LogService.getgI().info("PASSED 1");

                            String videoUrl = (String) uploadResult.get("secure_url");
                            LogService.getgI().info("PASSED 2");
                            uploadStatus.setUploadResult(uploadResult);
                            LogService.getgI().info("PASSED 3");

                            // Update course information
                            finalVideoLessonEntity.setLessonId(lessonId);
                            finalVideoLessonEntity.setDuration(ConvertUtils.toDouble(uploadResult.get("duration")));
                            finalVideoLessonEntity.setVideoUrl(videoUrl);
                            courseService.saveVideoLesson(finalVideoLessonEntity);

                            // Send notification to teacher
                            WebSocketMessage messageSuccess = WebSocketMessage.uploadComplete(
                                    "Khóa học: " + course.getTitle(),
                                    "Video " + lesson.getTitle() + " đã được upload thành công"
                            );
                            webSocketService.sendToInstructor(instructor.getId(), "/uploads", messageSuccess);

                            return uploadResult;
                        } catch (Exception e) {
                            uploadStatus.setStatus("FAILED");
                            uploadStatus.setProgress(0.0);
                            uploadStatus.setErrorMessage(e.getMessage());
                            return null;
                        } finally {
                            UploadingVideoCache.getInstance().getUploadingVideo().remove(threadId);
                        }
                    });
                    return ResponseEntity.ok(Response.success(response));
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Không tìm thấy thông tin"));
        } catch (Exception e) {
            LogService.getgI().error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi tải lên video"));
        }
    }

    @PostMapping(ApiPaths.INSTRUCTOR_CREATE_CHAPTER)
    public ResponseEntity<JSONObject> addChapter(
            @PathVariable Long courseId,
            @RequestBody CreateChapterRequest request,
            Authentication authentication) {
        LogService.getgI().info("[InstructorAPI] createChapter username: "+ authentication.getName()+ " courseId: "+courseId + " "+request.toString());
        try {
            // Validate user
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            // Validate instructor
            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Validate course ownership
            CourseEntity course = courseService.getCourse(courseId);
            if (course == null || !course.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không có quyền truy cập khóa học này"));
            }

            // Create new chapter
            ChapterEntity chapter = new ChapterEntity();
            chapter.setCourseId(courseId);
            chapter.setTitle(request.getTitle());
            chapter.setOrderIndex(request.getOrderIndex());

            // Save to database
            ChapterEntity savedChapter = courseService.saveChapter(chapter);

            // Map to response
            JSONObject response = new JSONObject();
            ChapterInstructorEditDTO chapterInstructorEditDTO = new ChapterInstructorEditDTO();
            chapterInstructorEditDTO.setId(savedChapter.getId());
            chapterInstructorEditDTO.setTitle(savedChapter.getTitle());
            chapterInstructorEditDTO.setOrderIndex(savedChapter.getOrderIndex());
            chapterInstructorEditDTO.setLessons(Collections.emptyList());  // Chapter mới không có lessons


            response.put("chapter", chapterInstructorEditDTO);

            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            LogService.getgI().error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi thêm chương mới"));
        }
    }

    @PutMapping(ApiPaths.INSTRUCTOR_UPDATE_CHAPTER)
    public ResponseEntity<JSONObject> updateChapter(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @RequestBody UpdateChapterRequest request,
            Authentication authentication) {
        LogService.getgI().info("[InstructorAPI] updateChapter username: "+ authentication.getName()+ " courseId:"+courseId+" chapterId: "+chapterId + " " + authentication.getName() +" "+ request.toString());
        try {
            // Validate user
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            // Validate instructor
            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Validate course ownership
            CourseEntity course = courseService.getCourse(courseId);
            if (course == null || !course.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không có quyền truy cập khóa học này"));
            }

            // Get and validate chapter
            ChapterEntity chapter = courseService.getChapterById(chapterId);
            if (chapter == null || !chapter.getCourseId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin chương"));
            }

            // Update chapter
            chapter.setTitle(request.getChapter().getTitle());
            chapter.setOrderIndex(request.getChapter().getOrderIndex());
            courseService.saveChapter(chapter);
            return ResponseEntity.ok(Response.success());

        } catch (Exception e) {
            LogService.getgI().error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi cập nhật chương"));
        }
    }

    @GetMapping(ApiPaths.INSTRUCTOR_CHECK_UPLOAD_STATUS)
    public ResponseEntity<JSONObject> videoUploadStatus(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @PathVariable Long lessonId,
            Authentication authentication) {
        LogService.getgI().info("[InstructorAPI] checkUploadStatus courseId: "+courseId+ " chapterId: "+ chapterId + " lessonId: "+lessonId);
        try {

            // Validate user
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            // Validate instructor
            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Validate course ownership
            CourseEntity course = courseService.getCourse(courseId);
            if (course == null || !course.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không có quyền truy cập khóa học này"));
            }

            // Lấy instance của UploadingVideoCache
            JSONObject status = new JSONObject();
            UploadingVideoCache cache = UploadingVideoCache.getInstance();

            // Kiểm tra có video nào đang upload với lessonId này không
            boolean isUploading = cache.getUploadingVideo().values().stream()
                    .anyMatch(detail -> detail.getLessonId().equals(lessonId));

            if (isUploading) {
                double progress = cache.getUploadingVideo().values().stream()
                        .filter(detail -> detail.getLessonId().equals(lessonId))
                        .findFirst()
                        .map(UploadingVideoDetail::getProgress)
                        .orElse(0.0);

                status.put("isUploading", true);
                status.put("progress", progress );
            } else {

                status.put("isUploading", false);
                status.put("progress", 0.0);  // Set progress = 0 khi không uploading
            }

            return ResponseEntity.ok(Response.success(status));
        } catch (Exception e) {
            LogService.getgI().error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @PostMapping(ApiPaths.INSTRUCTOR_WITHDRAW_STUDENT)
    public ResponseEntity<JSONObject> withdrawStudentAccount(
            @ModelAttribute WithdrawRequest request,
            Authentication authentication) {
        LogService.getgI().info("[InstructorAPI] withDrawStudent :"+ authentication.getName() +" "+ request.toString());
        try {
            String username = authentication.getName();

            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());

            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            if (request.getAmount() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error("Số tiền rút phải lớn hơn 0"));
            }

            if (request.getAmount() > instructor.getMoney()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error("Số dư không đủ để rút"));
            }

            instructor.setMoney(instructor.getMoney() - request.getAmount());
            instructorService.saveInstructor(instructor);

            student.setMoney(student.getMoney() + request.getAmount());
            studentService.save(student);

            // withdraw entity
            WithDrawEntity withdrawEntity = new WithDrawEntity();
            withdrawEntity.setInstructorId(instructor.getId());
            withdrawEntity.setAmount(request.getAmount());
            withdrawEntity.setType(WithDrawType.STUDENT_ACCOUNT);
            withdrawEntity.setStatus(WithDrawStatus.APPROVED);
            withdrawService.saveWithdraw(withdrawEntity);

            return ResponseEntity.ok(Response.success());

        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi rút tiền"));
        }
    }

    @PostMapping(ApiPaths.INSTRUCTOR_WITHDRAW_BANK)
    public ResponseEntity<JSONObject> withdrawBank(
            @ModelAttribute WithdrawRequest request,
            Authentication authentication) {
        LogService.getgI().info("withDrawBank :"+ authentication.getName() +" "+ request.toString());

        try {
            String username = authentication.getName();

            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());

            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            if (request.getAmount() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error("Số tiền rút phải lớn hơn 0"));
            }

            if (request.getAmount() > instructor.getMoney()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error("Số dư không đủ để rút"));
            }

            instructor.setMoney(instructor.getMoney() - request.getAmount());
            instructorService.saveInstructor(instructor);

            // withdraw entity
            WithDrawEntity withdrawEntity = new WithDrawEntity();
            withdrawEntity.setInstructorId(instructor.getId());
            withdrawEntity.setAmount(request.getAmount());
            withdrawEntity.setType(WithDrawType.BANK);
            withdrawEntity.setStatus(WithDrawStatus.PENDING);
            withdrawEntity.setBankCode(request.getBankCode());
            withdrawEntity.setBankName(request.getBankName());
            withdrawEntity.setAccountHolder(request.getAccountHolder());
            withdrawEntity.setAccountNumber(request.getAccountNumber());
            withdrawService.saveWithdraw(withdrawEntity);

            return ResponseEntity.ok(Response.success());

        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi rút tiền"));
        }
    }
}
